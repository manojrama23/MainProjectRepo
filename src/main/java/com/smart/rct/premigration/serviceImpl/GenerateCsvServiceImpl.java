package com.smart.rct.premigration.serviceImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.GenerateConstantsModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.AuditConstants;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.dto.UseCaseBuilderDto;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.migration.service.UploadFileService;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.postmigration.entity.AuditConstantsEntity;
import com.smart.rct.postmigration.repository.AuditConstantsRepository;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.Ip;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.repository.GenerateRepository;
import com.smart.rct.premigration.repository.GrowConstantsRepository;
import com.smart.rct.premigration.repositoryImpl.GenerateCsvRepositoryImpl;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CSVUtils;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class GenerateCsvServiceImpl implements GenerateCsvService {
	final static Logger logger = LoggerFactory.getLogger(GenerateCsvServiceImpl.class);

	@Autowired
	GenerateRepository objGenerateCsvRepository;
	@Autowired
	UseCaseBuilderDto useCaseBuilderDto;
	@Autowired
	FileUploadRepository fileUploadRepository;
	@Autowired
	RunTestRepository runTestRepository;
	@Autowired
	RunTestService runTestService;
	@Autowired
	CustomerService customerService;
	@Autowired
	GrowConstantsRepository growConstantsRepository;
	@Autowired
	NeMappingService neMappingService;
	@Autowired
	UseCaseBuilderService useCaseBuilderService;
	@Autowired
	UploadFileService uploadFileService;
	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;
	@Autowired
	GenerateCsvRepositoryImpl rep;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	FileUploadService fileUploadService;
	@Autowired
	AuditConstantsRepository auditConstantsRepository;
	@Autowired
	CBandGrowTemplate cBandGrowTemplate;
	@Autowired
	DSSGrowTemplate dSSGrowTemplate;
	@Autowired
	dSSGrowTemplateAu dSSGrowTemplateAu;
	@Autowired
	dSSGrowTemplateAuPf dSSGrowTemplateAuPf;
	@Autowired
	PreMigrationToOV PreMigrationToOV;

	public static String time = null;
	private  static boolean ip_present = true;
	
	private synchronized void setIpPresent(boolean flag) {
		ip_present = flag;
	}

	public static boolean getIpPresent() {
		return ip_present;
	}

	@Override
	public boolean csvFileGeneration(String ciqFileName, String pathCsv, Integer customerId, Integer networkTypeId,
			String lsmVersion, String sessionId) {
		// TODO Auto-generated method stub
		boolean status = false;
		StringBuilder objPathBuilder = new StringBuilder();
		String dateAppend = new SimpleDateFormat("yyymmddhhmmss").format(new Date());

		try {
			List<CIQDetailsModel> listCIQDetailsModel = objGenerateCsvRepository.findAll(ciqFileName);

			if (listCIQDetailsModel != null && listCIQDetailsModel.size() > 0) {
				objPathBuilder.setLength(0);
				objPathBuilder.append(pathCsv);
				objPathBuilder.append(File.separator);
				objPathBuilder.append(ciqFileName.substring(0, ciqFileName.lastIndexOf('.')));
				objPathBuilder.append(dateAppend);
				objPathBuilder.append(".csv");
				FileWriter writer = new FileWriter(objPathBuilder.toString());
				boolean data = false;
				for (CIQDetailsModel objModel : listCIQDetailsModel) {
					LinkedHashMap<String, CiqMapValuesModel> objMap = objModel.getCiqMap();
					if (objMap != null && objMap.size() > 0) {
						List<String> objList = objMap.entrySet().stream().map(x -> x.getValue().getHeaderValue())
								.collect(Collectors.toList());
						if (!data) {
							List<String> objKeyList = objMap.entrySet().stream().map(x -> x.getKey())
									.collect(Collectors.toList());
							CSVUtils.writeLine(writer, objKeyList);
							data = true;
						}

						CSVUtils.writeLine(writer, objList);

					}

				}
				status = true;

			}

		} catch (Exception e) {
			status = false;
			logger.info(
					"Exception in csvFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			StringBuilder objNewBuilder = new StringBuilder();
			objNewBuilder.setLength(0);
			objNewBuilder.append(ciqFileName.substring(0, ciqFileName.lastIndexOf('.')));
			objNewBuilder.append(dateAppend);
			objNewBuilder.append(".csv");
			GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity();
			if (status) {
				objInfo.setFilePath(pathCsv);
				objInfo.setFileName(objNewBuilder.toString());

				CustomerEntity objCustomerEntity = new CustomerEntity();
				objCustomerEntity.setId(customerId);

				NetworkTypeDetailsEntity objNetworkTypeDetailsEntity = new NetworkTypeDetailsEntity();
				objNetworkTypeDetailsEntity.setId(networkTypeId);
				objInfo.setGenerationDate(new Date());
				objInfo.setGeneratedBy(user.getUserName());
			} else {
				objInfo.setFilePath(pathCsv);
				objInfo.setFileName(objPathBuilder.toString());
				CustomerEntity objCustomerEntity = new CustomerEntity();
				objCustomerEntity.setId(customerId);
				NetworkTypeDetailsEntity objNetworkTypeDetailsEntity = new NetworkTypeDetailsEntity();
				objNetworkTypeDetailsEntity.setId(networkTypeId);
				objInfo.setGenerationDate(new Date());
				objInfo.setGeneratedBy(user.getUserName());

			}

			objGenerateCsvRepository.saveCsvAudit(objInfo);

		}
		return status;
	}

	@Override
	public Map<String, Object> getCsvAuditDetails(GenerateInfoAuditModel csvModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = null;
		try {

			objMap = objGenerateCsvRepository.getCsvAuditDetails(csvModel, page, count);

		} catch (Exception e) {
			// TODO: handle exception

			logger.error("getCsvAuditDetails() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	@Override
	public List<GenerateInfoAuditModel> getCsvFilesList(int customerId) {
		// TODO Auto-generated method stub
		List<GenerateInfoAuditModel> objList = null;
		try {

			objList = objGenerateCsvRepository.getCsvFilesList(customerId);

		} catch (Exception e) {
			// TODO: handle exception

			logger.error("getCsvAuditDetails() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return objList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject transferCiqFile(List<GenerateInfoAuditModel> csvInfo, int iLsmId, JSONObject resultMap) {

		String sLocalFile = null;
		String sHostIP = null;
		int sHostPort;
		String sHostUserName = null;
		String sHostPwd = null;
		String sDestinationPath = null;
		com.jcraft.jsch.Session session = null;
		com.jcraft.jsch.Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			LsmEntity lsmEntity = objGenerateCsvRepository.getLsmById(iLsmId);

			sHostIP = lsmEntity.getLsmIp();
			sHostUserName = lsmEntity.getLsmUserName();
			sHostPwd = lsmEntity.getLsmPassword();
			sHostPort = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").toString());
			sDestinationPath = LoadPropertyFiles.getInstance().getProperty("DESTINATION_PATH").toString();

			for (int i = 0; i < csvInfo.size(); i++) {
				sLocalFile = csvInfo.get(i).getFilePath() + csvInfo.get(i).getFileName();
				JSch jsch = new JSch();
				session = jsch.getSession(sHostUserName, sHostIP, sHostPort);
				session.setPassword(sHostPwd);
				java.util.Properties config = new java.util.Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config);
				session.connect();
				channel = session.openChannel("sftp");
				channel.connect();
				channelSftp = (ChannelSftp) channel;
				channelSftp.cd(sDestinationPath);
				File f = new File(sLocalFile);
				channelSftp.put(new FileInputStream(f), f.getName());
				resultMap.put("status", Constants.SUCCESS);
			}

		} catch (JSchException e) {
			logger.error(
					"Exception transferCiqFile() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.AUTH_FAIL));
		} catch (FileNotFoundException e) {
			logger.error(
					"Exception transferCiqFile() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILE_NOT_FOUND));
		} catch (Exception e) {
			logger.error(
					"Exception in transferCiqFile()   GenerateCsvServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_PRE_GROW));
		} finally {
			try {
				channelSftp.exit();
				channel.disconnect();
				session.disconnect();
			} catch (Exception e) {
				return resultMap;
			}
		}
		return resultMap;
	}

	@Override
	public boolean ciqFileValidation(String ciqFileName, String pathCsv, Integer customerId, Integer networkTypeId,
			String lsmVersion, String sessionId) {
		// TODO Auto-generated method stub
		boolean status = false;

		try {
			List<CIQDetailsModel> listCIQDetailsModel = objGenerateCsvRepository.findAll(ciqFileName);

			for (CIQDetailsModel objModel : listCIQDetailsModel) {
				LinkedHashMap<String, CiqMapValuesModel> objMap = objModel.getCiqMap();
				if (objMap != null && objMap.size() > 0) {
					List<String> objList = objMap.entrySet().stream().map(x -> x.getValue().getHeaderValue())
							.collect(Collectors.toList());
					List<String> objKeyList = objMap.entrySet().stream().map(x -> x.getKey())
							.collect(Collectors.toList());

					// need to write validation details

				}

			}

		} catch (Exception e) {
			status = false;
			logger.info(
					"Exception in ciqFileValidation() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<UseCaseBuilderModel> getUseCaseDetails(Integer customerId) {
		List<UseCaseBuilderModel> useCaseBuilderModelList = new ArrayList<>();
		try {
			List<UseCaseBuilderEntity> useCaseBuilderEntityList = objGenerateCsvRepository
					.getUseCaseDetails(customerId);
			if (!useCaseBuilderEntityList.isEmpty())
				useCaseBuilderModelList = useCaseBuilderDto
						.convertUseCaseBuilderEntityToModel(useCaseBuilderEntityList);

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderSearchDetails service : " + ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderModelList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject transferCiqFileAndGrow(EnbPreGrowAuditModel csvInfo, JSONObject resultMap) {

		String sLocalFile = null;
		String sHostIP = null;
		int sHostPort;
		String sHostUserName = null;
		String sHostPwd = null;
		String sDestinationPath = null;
		com.jcraft.jsch.Session session = null;
		com.jcraft.jsch.Channel channel = null;
		ChannelSftp channelSftp = null;
		try {
			LsmEntity lsmEntity = objGenerateCsvRepository.getLsmById(csvInfo.getSmId());

			sHostIP = lsmEntity.getLsmIp();
			sHostUserName = lsmEntity.getLsmUserName();
			sHostPwd = lsmEntity.getLsmPassword();
			sHostPort = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty("HOST_PORT").toString());
			sDestinationPath = LoadPropertyFiles.getInstance().getProperty("DESTINATION_PATH").toString();

			sLocalFile = csvInfo.getCsvFilePath() + csvInfo.getCsvFileName();
			JSch jsch = new JSch();
			session = jsch.getSession(sHostUserName, sHostIP, sHostPort);
			session.setPassword(sHostPwd);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(sDestinationPath);
			File f = new File(sLocalFile);
			channelSftp.put(new FileInputStream(f), f.getName());
			resultMap.put("status", Constants.SUCCESS);

		} catch (JSchException e) {
			logger.error(
					"Exception transferCiqFile() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.AUTH_FAIL));
		} catch (FileNotFoundException e) {
			logger.error(
					"Exception transferCiqFile() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FILE_NOT_FOUND));
		} catch (Exception e) {
			logger.error(
					"Exception in transferCiqFile()   GenerateCsvServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_PRE_GROW));
		} finally {
			try {
				channelSftp.exit();
				channel.disconnect();
				session.disconnect();
			} catch (Exception e) {
				return resultMap;
			}
		}
		return resultMap;
	}

	private boolean preMigRunTest(EnbPreGrowAuditModel csvInfo, LsmEntity lsmEntity) {
		boolean status = false;

		try {
			JSONObject runTestParams = new JSONObject();
			RunTestEntity runTestEntity = new RunTestEntity();
			runTestEntity.setCreationDate(new Date());
			runTestEntity.setLsmName(csvInfo.getSmName());
			runTestEntity.setLsmVersion(lsmEntity.getLsmVersion());
			runTestEntity.setTestName(csvInfo.getGrowingName());
			runTestEntity.setTestDescription(csvInfo.getDescription());
			// runTestEntity.setNwType(lsmEntity.getNetworkTypeDetailsEntity().getNetworkType());
			runTestEntity.setUseCase(csvInfo.getUseCaseName());
			runTestEntity.setStatus("PASS");
			runTestEntity.setCustomerId(Integer.valueOf(csvInfo.getCustomerId()));

			RunTestEntity runTestEntity1 = runTestRepository.createRunTest(runTestEntity);

			List<LinkedHashMap> useCaseList = new ArrayList<>();
			Map objLinkedHashMap = new LinkedHashMap();
			objLinkedHashMap.put("useCaseId", csvInfo.getUseCaseId());
			objLinkedHashMap.put("executionSequence", 1);
			objLinkedHashMap.put("useCaseName", csvInfo.getUseCaseName());
			// runTestService.getRuntestExecResult(useCaseList, runTestEntity1, "CURRENT");
			status = true;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(
					"Exception in preMigRunTest()   GenerateCsvServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	@Override
	public Map<String, Object> generateFilesListSearch(GenerateInfoAuditModel objCsvInfoAuditModel, int customerId,
			int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = null;

		try {
			objMap = objGenerateCsvRepository.generateFilesListSearch(objCsvInfoAuditModel, customerId, page, count);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("Exception in generateFilesListSearch()   GenerateCsvServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	// method for fsu nemapping independent
	@Override
	public JSONObject envFileGenerationForFsu(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,String ovType, String fileType,
			String remarks,String neVersion, String fsuType, List<CIQDetailsModel> listCIQDetailsModel) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String envFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		try {
			
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			envFilename = "ENV_" + enbName + dateString + ".txt";
			StringBuffer sb = new StringBuffer();
			
			
			
			if (fileType.equalsIgnoreCase("ENV") || ovType.equalsIgnoreCase("ENV_FSU") ) {
				

				String s1 = "setenv p __BOOTUP_FLAG__ 0000";
				String s2 = "setenv p PNP_MODE 0";
				String s3 = "setenv p MAX_PNP_CLEAR_COUNT 1";
				String s4 = "setenv p REBOOT_TIMER 15";
				String s5 = "setenv p BOOTMODE static";
				String s6 = "setenv p IPV6_ENABLE 1";
				String s7 = "setenv p PORT_0_0_0_IPVER 6";
				String s8 = "setenv p BOOTPORT PORT_0_0_0";
				String s9 = "setenv p PNP_CLEAR_COUNT 0";
				String s10 = "setenv p PROTECTION_MODE 0";
				
				CIQDetailsModel cIQDetailsModel = listCIQDetailsModel.get(3);
				String common = "setenv p" + " ";
				String AUTH = common + "AUTH " + "yes";
				String NE_TYPE = common + "NE_TYPE " + "FSU";
				String NE_ID = common + "NE_ID " + cIQDetailsModel.getCiqMap().get("NE_ID").getHeaderValue();
				String RS_IP = common + "RS_IP " + cIQDetailsModel.getCiqMap().get("RS_IP").getHeaderValue();
				/*
				 * String PORT_0_0_0_IPV6_NM
				 * =common+"PORT_0_0_0_IPV6_NM="+cIQDetailsModel.getCiqMap().get(
				 * "PORT_0_0_0_IPV6_NM").getHeaderValue(); String PORT_0_0_0_IPV6_IP
				 * =common+"PORT_0_0_0_IPV6_IP="+cIQDetailsModel.getCiqMap().get(
				 * "PORT_0_0_0_IPV6_IP").getHeaderValue(); String PORT_0_0_0_IPV6_GW
				 * =common+"PORT_0_0_0_IPV6_GW="+cIQDetailsModel.getCiqMap().get(
				 * "PORT_0_0_0_IPV6_GW").getHeaderValue();
				 */
				String PORT_0_0_0_IPV6_NM = common + "PORT_0_0_0_IPV6_NM "
						+ cIQDetailsModel.getCiqMap().get("Subnet").getHeaderValue();
				String PORT_0_0_0_IPV6_IP = common + "PORT_0_0_0_IPV6_IP "
						+ cIQDetailsModel.getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue();
				String PORT_0_0_0_IPV6_GW = common + "PORT_0_0_0_IPV6_GW "
						+ cIQDetailsModel.getCiqMap().get("FSU_Gateway_IP_Address").getHeaderValue();
				String VLAN_ID = common + "PORT_0_0_0_" + "VLANID "
						+ cIQDetailsModel.getCiqMap().get("VLAN_ID").getHeaderValue();
				if(neVersion.equals("21.A.0") || neVersion.equals("21.B.0") || neVersion.equals("21.C.0") || neVersion.equals("21.D.0") ||  neVersion.equals("22.A.0")) {
					sb.append(s5).append("\n");
					sb.append(s1).append("\n");
					sb.append(s6).append("\n");
					sb.append(AUTH);
					sb.append(System.getProperty("line.separator"));
					sb.append(NE_TYPE);
					sb.append(System.getProperty("line.separator"));
					sb.append(s8).append("\n");
					sb.append(s7).append("\n");
					sb.append(PORT_0_0_0_IPV6_IP);
					sb.append(System.getProperty("line.separator"));
					sb.append(PORT_0_0_0_IPV6_NM);
					sb.append(System.getProperty("line.separator"));
					sb.append(PORT_0_0_0_IPV6_GW);
					sb.append(System.getProperty("line.separator"));
					sb.append(VLAN_ID);
                                        sb.append(System.getProperty("line.separator"));
					sb.append(s2).append("\n");
					sb.append(s3).append("\n");
					sb.append(s4).append("\n");
					sb.append(s10).append("\n");
					sb.append(s9).append("\n");
					sb.append(NE_ID);
					sb.append(System.getProperty("line.separator"));
					sb.append(RS_IP);
					
					
					
					
					
				}else {
				sb.append(s1).append("\n");
				sb.append(s2).append("\n");
				sb.append(s3).append("\n");
				sb.append(s4).append("\n");
				sb.append(s5).append("\n");
				sb.append(s6).append("\n");
				sb.append(s7).append("\n");
				sb.append(s8).append("\n");
				sb.append(s9).append("\n");
				sb.append(s10).append("\n");
				sb.append(AUTH);
				sb.append(System.getProperty("line.separator"));
				sb.append(NE_TYPE);
				sb.append(System.getProperty("line.separator"));
				sb.append(NE_ID);
				sb.append(System.getProperty("line.separator"));
				sb.append(RS_IP);
				sb.append(System.getProperty("line.separator"));
				sb.append(PORT_0_0_0_IPV6_NM);
				sb.append(System.getProperty("line.separator"));
				sb.append(PORT_0_0_0_IPV6_IP);
				sb.append(System.getProperty("line.separator"));
				sb.append(PORT_0_0_0_IPV6_GW);
				sb.append(System.getProperty("line.separator"));
				sb.append(VLAN_ID);
				}
				// String str;
				// str = sb.toString();
				// str.replaceAll("[\n]", "");
				// sb.toString().replaceAll("(?m)^[\t]*\r?\n","");

				// StringBuilder sb1 = new StringBuilder();
				// String equal = "=";
				// String consd = "setenv p ";
				// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("NE_ID").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("NE_ID").getHeaderValue().toString());
				// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_IP").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_IP").getHeaderValue().toString());
				// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_NM").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_NM").getHeaderValue().toString());
				// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_GW").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_GW").getHeaderValue().toString());
				// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("VLAN_ID").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("VLAN_ID").getHeaderValue().toString());
				// sb.append(sb1);
			}

			else {
				logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
						+ Constants.ENB_MENU_TEMPLATE);
			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + envFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", envFilename);
			/*
			 * User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
			 * { String fileSavePath = fileBuilder.toString(); fileSavePath =
			 * fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			 * , ""); objInfo.setFilePath(fileSavePath);
			 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
			 * objInfo.setRemarks(remarks); objInfo.setFileType(Constants.FILE_TYPE_ENV);
			 * objInfo.setFileName(envFilename); objInfo.setGenerationDate(new Date());
			 * objInfo.setGeneratedBy(user.getUserName()); CustomerDetailsEntity
			 * programDetailsEntity = new CustomerDetailsEntity();
			 * programDetailsEntity.setId(programId);
			 * objInfo.setProgramDetailsEntity(programDetailsEntity);
			 * objGenerateCsvRepository.saveCsvAudit(objInfo); }
			 */
		}
		return fileGenerateResult;
	}

	@Override
	public JSONObject envFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String fileType, NeMappingEntity neMappingEntity, String remarks, Boolean supportCA) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String envFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		try {
			logger.error("Inside env file generation in generate csv service impl");
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			// envFilename = "ENV_" + enbName + "_" + timeStamp + ".txt";
			envFilename = "ENV_" + enbName + dateString + ".txt";
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("env")) {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			if (listCIQDetailsModel != null && listCIQDetailsModel.size() > 0) {

				GenerateConstantsModel generateConstantsModel = new GenerateConstantsModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				generateConstantsModel.setProgramDetailsEntity(programDetailsEntity);
				generateConstantsModel.setLabel(Constants.ORAN_ENV_STATIC_PART);
				List<GrowConstantsEntity> entities = growConstantsRepository
						.getGrowConstantsDetails(generateConstantsModel);

				StringBuffer sb = new StringBuffer();

				if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
					if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")
							|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0")
							|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0")
							|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
										.equals("22.A.0")) {
						sb.append("setenv p RASKIP=1");
						sb.append(System.getProperty("line.separator"));
					}
					
					for (GrowConstantsEntity templateEntity : entities) {
						String value = templateEntity.getValue().replaceAll("\\n",
								System.getProperty("line.separator"));
						String[] splitData = value.split(System.getProperty("line.separator"));
						for (String line : splitData) {
							sb.append(line);
							sb.append(System.getProperty("line.separator"));
						}
					}

				} else {
					logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
							+ ": " + Constants.ENB_MENU_TEMPLATE);
				}
				// code for fsu dynamic

				generateConstantsModel.setProgramDetailsEntity(programDetailsEntity);
				// if(generateConstantsModel.getProgramDetailsEntity().getId()!=38)
				// {
				if (!neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("19.A.0")) {
					generateConstantsModel.setLabel(Constants.ORAN_ENV_DYNAMIC_PART);
					entities = growConstantsRepository.getGrowConstantsDetails(generateConstantsModel);

					if (CommonUtil.isValidObject(entities) && entities.size() > 0) {

						for (GrowConstantsEntity templateEntity : entities) {
							String[] splitData = templateEntity.getValue().split("\n");
							for (String line : splitData) {
								String[] splitVar = line.split("\\$");
								sb.append(splitVar[0]);
								String dynamicValue = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
										Constants.ORAN_SPRINT_SHEET_IPPLAN, enbId, splitVar[1]);
								if (splitVar[1].trim()
										.equalsIgnoreCase(Constants.ORAN_SPRINT_COMM_SCRIPT_eNB_OAM_IP_eNB_S_B_IP)
										|| splitVar[1].trim().equalsIgnoreCase(
												Constants.ORAN_SPRINT_COMM_SCRIPT_eNB_OAM_GW_IP_eNB_S_B_GW_IP)) {
									sb.append(CommonUtil.formatIPV6Address(dynamicValue));
									sb.append(System.lineSeparator());
								} else if (splitVar[1].contains(Constants.ORAN_ENV_RS_IP)) {
									dynamicValue = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
									sb.append(CommonUtil.formatIPV6Address(dynamicValue));
									sb.append(System.lineSeparator());
								} else {
									sb.append(dynamicValue);
									sb.append(System.getProperty("line.separator"));
								}
							}
						}
						sb.append(System.getProperty("line.separator"));//adding extra lines as per Arun 25th Oct,2022
						sb.append(System.getProperty("line.separator"));
					}
				}
				// }
				else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
						.equals("19.A.0")) {
					// sb.toString().replaceAll("setenv p TEMPERATURE_THRESHOLD_CPU=100", "");
					// sb.toString().replaceAll("setenv p _MemorySize_OF_SE=3840", "");
					// sb.toString().replaceAll("setenv p FTPTELNET_ON=no", "");
					// sb.toString().replaceAll("setenv p _CoreNumber_OF_SE=9", "");
					// StringBuilder sb1 = new StringBuilder();
					// sb1.append(sb);
					// sb.delete(0, sb.length());
					// sb.append(sb1);
					sb.setLength(0);
					// StringBuilder sb1 = new StringBuilder();
					/*
					 * String s = "setenv p TEMPERATURE_THRESHOLD_CPU=100"; delete(sb,s);
					 * delete(sb,"setenv p _MemorySize_OF_SE=3840");
					 * delete(sb,"setenv p FTPTELNET_ON=no");
					 * delete(sb,"setenv p _CoreNumber_OF_SE=9");
					 * 
					 * replaceAll(sb, "setenv p PORT_0_0_1_IPVER=6", "setenv p PORT_0_0_0_IPVER=6");
					 * replaceAll(sb, "setenv p BOOTPORT=PORT_0_0_1",
					 * "setenv p BOOTPORT=PORT_0_0_0");
					 */
					String s1 = "setenv p __BOOTUP_FLAG__ 0000";
					String s2 = "setenv p PNP_MODE 0";
					String s3 = "setenv p MAX_PNP_CLEAR_COUNT 1";
					String s4 = "setenv p REBOOT_TIMER 15";
					String s5 = "setenv p BOOTMODE static";
					String s6 = "setenv p IPV6_ENABLE 1";
					String s7 = "setenv p PORT_0_0_0_IPVER 6";
					String s8 = "setenv p BOOTPORT PORT_0_0_0";
					String s9 = "setenv p PNP_CLEAR_COUNT 0";
					String s10 = "setenv p PROTECTION_MODE 0";
					sb.append(s1).append("\n");
					sb.append(s2).append("\n");
					sb.append(s3).append("\n");
					sb.append(s4).append("\n");
					sb.append(s5).append("\n");
					sb.append(s6).append("\n");
					sb.append(s7).append("\n");
					sb.append(s8).append("\n");
					sb.append(s9).append("\n");
					sb.append(s10).append("\n");
					CIQDetailsModel cIQDetailsModel = listCIQDetailsModel.get(3);
					String common = "setenv p" + " ";
					String AUTH = common + "AUTH " + "yes";
					String NE_TYPE = common + "NE_TYPE " + "FSU";
					String NE_ID = common + "NE_ID " + cIQDetailsModel.getCiqMap().get("NE_ID").getHeaderValue();
					String RS_IP = common + "RS_IP " + cIQDetailsModel.getCiqMap().get("RS_IP").getHeaderValue();
					/*
					 * String PORT_0_0_0_IPV6_NM
					 * =common+"PORT_0_0_0_IPV6_NM="+cIQDetailsModel.getCiqMap().get(
					 * "PORT_0_0_0_IPV6_NM").getHeaderValue(); String PORT_0_0_0_IPV6_IP
					 * =common+"PORT_0_0_0_IPV6_IP="+cIQDetailsModel.getCiqMap().get(
					 * "PORT_0_0_0_IPV6_IP").getHeaderValue(); String PORT_0_0_0_IPV6_GW
					 * =common+"PORT_0_0_0_IPV6_GW="+cIQDetailsModel.getCiqMap().get(
					 * "PORT_0_0_0_IPV6_GW").getHeaderValue();
					 */
					String PORT_0_0_0_IPV6_NM = common + "PORT_0_0_0_IPV6_NM "
							+ cIQDetailsModel.getCiqMap().get("Subnet").getHeaderValue();
					String PORT_0_0_0_IPV6_IP = common + "PORT_0_0_0_IPV6_IP "
							+ cIQDetailsModel.getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue();
					String PORT_0_0_0_IPV6_GW = common + "PORT_0_0_0_IPV6_GW "
							+ cIQDetailsModel.getCiqMap().get("FSU_Gateway_IP_Address").getHeaderValue();
					String VLAN_ID = common + "PORT_0_0_0_" + "VLANID "
							+ cIQDetailsModel.getCiqMap().get("VLAN_ID").getHeaderValue();
					sb.append(AUTH);
					sb.append(System.getProperty("line.separator"));
					sb.append(NE_TYPE);
					sb.append(System.getProperty("line.separator"));
					sb.append(NE_ID);
					sb.append(System.getProperty("line.separator"));
					sb.append(RS_IP);
					sb.append(System.getProperty("line.separator"));
					sb.append(PORT_0_0_0_IPV6_NM);
					sb.append(System.getProperty("line.separator"));
					sb.append(PORT_0_0_0_IPV6_IP);
					sb.append(System.getProperty("line.separator"));
					sb.append(PORT_0_0_0_IPV6_GW);
					sb.append(System.getProperty("line.separator"));
					sb.append(VLAN_ID);
					// String str;
					// str = sb.toString();
					// str.replaceAll("[\n]", "");
					// sb.toString().replaceAll("(?m)^[\t]*\r?\n","");

					// StringBuilder sb1 = new StringBuilder();
					// String equal = "=";
					// String consd = "setenv p ";
					// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("NE_ID").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("NE_ID").getHeaderValue().toString());
					// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_IP").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_IP").getHeaderValue().toString());
					// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_NM").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_NM").getHeaderValue().toString());
					// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_GW").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("PORT_0_0_0_IPV6_GW").getHeaderValue().toString());
					// sb1.append(consd).append(listCIQDetailsModel.get(3).getCiqMap().get("VLAN_ID").getHeaderName().toString()).append(equal).append(listCIQDetailsModel.get(3).getCiqMap().get("VLAN_ID").getHeaderValue().toString());
					// sb.append(sb1);
				}

				else {
					logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
							+ ": " + Constants.ENB_MENU_TEMPLATE);
				}

				if (CommonUtil.isValidObject(sb)) {
					logger.error("Writing env file");
					FileWriter fileWriter = new FileWriter(fileBuilder.toString() + envFilename);
					BufferedWriter bw = null;
					bw = new BufferedWriter(fileWriter);
					try {
						bw.write(sb.toString());
						sb.delete(0, sb.length());
						status = true;
						logger.error("Done writing, status = true");
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						bw.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
			}} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", envFilename);
			/*
			 * User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
			 * { String fileSavePath = fileBuilder.toString(); fileSavePath =
			 * fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			 * , ""); objInfo.setFilePath(fileSavePath);
			 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
			 * objInfo.setRemarks(remarks); objInfo.setFileType(Constants.FILE_TYPE_ENV);
			 * objInfo.setFileName(envFilename); objInfo.setGenerationDate(new Date());
			 * objInfo.setGeneratedBy(user.getUserName()); CustomerDetailsEntity
			 * programDetailsEntity = new CustomerDetailsEntity();
			 * programDetailsEntity.setId(programId);
			 * objInfo.setProgramDetailsEntity(programDetailsEntity);
			 * objGenerateCsvRepository.saveCsvAudit(objInfo); }
			 */
		}
		return fileGenerateResult;
	}
	
	//env generation for file with dummy suffix
	@Override
	public JSONObject envFileGenerationDummyIP(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String fileType, NeMappingEntity neMappingEntity, String remarks, Boolean supportCA) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String envFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		try {
			logger.error("Inside env file generation DummyIP in generate csv service impl");
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			// envFilename = "ENV_" + enbName + "_" + timeStamp + ".txt";
			envFilename = "ENV_" + enbName + dateString + "_Dummy" + ".txt";
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("envDummy")) {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			if (listCIQDetailsModel != null && listCIQDetailsModel.size() > 0) {

				GenerateConstantsModel generateConstantsModel = new GenerateConstantsModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				generateConstantsModel.setProgramDetailsEntity(programDetailsEntity);
				generateConstantsModel.setLabel(Constants.ORAN_ENV_STATIC_PART);
				List<GrowConstantsEntity> entities = growConstantsRepository
						.getGrowConstantsDetails(generateConstantsModel);

				StringBuffer sb = new StringBuffer();

				if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
					//if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) {
						sb.append("setenv p RASKIP=1");
						sb.append(System.getProperty("line.separator"));
					//}
					for (GrowConstantsEntity templateEntity : entities) {
						String value = templateEntity.getValue().replaceAll("\\n",
								System.getProperty("line.separator"));
						String[] splitData = value.split(System.getProperty("line.separator"));
						for (String line : splitData) {
							sb.append(line);
							sb.append(System.getProperty("line.separator"));
						}
					}

				} else {
					logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
							+ ": " + Constants.ENB_MENU_TEMPLATE);
				}

				generateConstantsModel.setProgramDetailsEntity(programDetailsEntity);
				// if(generateConstantsModel.getProgramDetailsEntity().getId()!=38)
				// {
				if (!neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("19.A.0")) {
					generateConstantsModel.setLabel(Constants.ORAN_ENV_DYNAMIC_PART);
					entities = growConstantsRepository.getGrowConstantsDetails(generateConstantsModel);

					if (CommonUtil.isValidObject(entities) && entities.size() > 0) {

						for (GrowConstantsEntity templateEntity : entities) {
							String[] splitData = templateEntity.getValue().split("\n");
							for (String line : splitData) {
								String[] splitVar = line.split("\\$");
								sb.append(splitVar[0]);
								String dynamicValue = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
										Constants.ORAN_SPRINT_SHEET_IPPLAN, enbId, splitVar[1]);
								if (splitVar[1].trim()
										.equalsIgnoreCase(Constants.ORAN_SPRINT_COMM_SCRIPT_eNB_OAM_IP_eNB_S_B_IP)
										|| splitVar[1].trim().equalsIgnoreCase(
												Constants.ORAN_SPRINT_COMM_SCRIPT_eNB_OAM_GW_IP_eNB_S_B_GW_IP)) {
									if(splitVar[1].equals("eNB_OAM_IP&eNB_S&B_IP")) {
										String str = CommonUtil.formatIPV6Address(dynamicValue);
										int x = str.lastIndexOf(":");
										String ipD = "fff1";
										String mnp = str.substring(0,x+1);
										str = mnp + ipD;
										sb.append(str);
									} else {
										sb.append(CommonUtil.formatIPV6Address(dynamicValue));
									}
									sb.append(System.lineSeparator());
								} else if (splitVar[1].contains(Constants.ORAN_ENV_RS_IP)) {
									dynamicValue = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
									sb.append(CommonUtil.formatIPV6Address(dynamicValue));
									sb.append(System.lineSeparator());
								} else {
									if(splitVar[1].equals("eNB_OAM_VLAN")) {
										sb.append("410");
									} else {
										sb.append(dynamicValue);
									}									
									sb.append(System.getProperty("line.separator"));
								}
							}
							sb.append(System.getProperty("line.separator"));//adding extra line as per Arun 25th Oct,2022
							sb.append(System.getProperty("line.separator"));
						}

					}
				}
				else {
					logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
							+ ": " + Constants.ENB_MENU_TEMPLATE);
				}
				if (CommonUtil.isValidObject(sb)) {
					logger.error("Writing dummy env file");
					FileWriter fileWriter = new FileWriter(fileBuilder.toString() + envFilename);
					BufferedWriter bw = null;
					bw = new BufferedWriter(fileWriter);
					try {
						bw.write(sb.toString());
						sb.delete(0, sb.length());
						status = true;
						logger.error("Done writing, status = true");
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						bw.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
			}} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", envFilename);
		}
		return fileGenerateResult;
	}

	// fsu method for nemapping independent
	@Override
	public JSONObject csvFileGenerationForFsu(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,String ovFileType, String fileType,
			String remarks, String neVersion, String fsuType, List<CIQDetailsModel> listCIQDetailsModel) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String csvFilename = "";
		fileBuilder.setLength(0);
		fileBuilder.append(filePath);
		JSONObject fileGenerateResult = new JSONObject();
		boolean addQuotes = true;
		try {
			csvFilename = "FSU_TEMPLATE_" + enbName + dateString + ".csv";
			StringBuilder csvBuilder = new StringBuilder();

			if (fileType.equalsIgnoreCase("fsu")|| (CommonUtil.isValidObject(ovFileType) && ovFileType.equalsIgnoreCase("CSV_FSU"))) {
				
				if(neVersion.equals("22.C.0") || neVersion.equals("21.A.0") || neVersion.equals("21.B.0") || neVersion.equals("21.C.0") || neVersion.equals("21.D.0") || neVersion.equals("22.A.0"))
				{
					csvBuilder = getENBStringForFsuV21(ciqFileName, neVersion, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel);
				}else {
					csvBuilder = getENBStringForFsuV9(ciqFileName, neVersion, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel);
				}
			}
			if (CommonUtil.isValidObject(csvBuilder)) {

				// FileWriter fileWriter = new FileWriter(fileBuilder.toString() + csvFilename);
				// BufferedWriter bw = null;
				// bw = new BufferedWriter(fileWriter);
				// try {
				// bw.write(csvBuilder.toString());
				// csvBuilder.delete(0, csvBuilder.length());
				// status = true;
				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + csvFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					String csvString = csvBuilder.toString();
					if (addQuotes) {
						csvString = "\"" + csvString.replaceAll(",", "\",\"").replaceAll("\n", "\"\n\"");
						csvString = csvString.substring(0, csvString.length() - 2);
					}
					bw.write(csvString);
					csvBuilder.delete(0, csvBuilder.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", csvFilename);

		}
		return fileGenerateResult;
	}

	// 5g methods

	// env method for 5G
	@Override
	public JSONObject envFileGenerationFor5G(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String remarks) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String envFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			envFilename = "ENV_AU_" + version + "_" + enbName + "_" + timeStamp + ".txt";
			StringBuffer sb = new StringBuffer();
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String bootup_flag = Constants.bootup_flag;
			String bootport_port = Constants.bootport_port;
			String protection_mode = Constants.protection_mode;
			String thresold_cpu = Constants.temperature_thresold_cpu;
			String thresold_modem = Constants.temperature_thresold_modem;
			String bootmode_static = Constants.bootmode_static;
			String ne_type = Constants.ne_type;
			String pnp_mode = Constants.pnp_mode;
			String max_pnp_clear_count = Constants.max_pnp_clear_count;
			String swdisable_reboot_cnt = Constants.swdisable_reboot_cnt;
			String ftp_type = Constants.ftp_type;
			String pingskip = Constants.pingskip;
			String isolation_map = Constants.isolation_map;
			String rlc_core = Constants.number_of_rlc_core;
			String hugepages = Constants.hugepages;
			String corenumber_of_se = Constants.corenumber_of_se;
			String memorysize_of_se = Constants.memorysize_of_se;
			String auth = Constants.auth;
			String ipv6_enable = Constants.ipv6_enable;
			String reboot_timer = Constants.reboot_timer;
			String pnp_clear_count = Constants.pnp_clear_count;
			String port_0_0_0_type = Constants.port_0_0_0_type;
			String port_0_0_0_ipver6 = Constants.port_0_0_0_ipver6;
			String neid = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
			String nm = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Prefix_Length").getHeaderValue();
			String vlan = listCIQDetailsModel.get(0).getCiqMap().get("OAM_VLAN_ID").getHeaderValue();
			String ip = listCIQDetailsModel.get(0).getCiqMap().get("OAM_IP").getHeaderValue();
			String gw = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Gateway").getHeaderValue();
			String rsip = listCIQDetailsModel.get(0).getCiqMap().get("EMS_IP").getHeaderValue();
			String common = "setenv p" + " ";
			String NEID = common + "NE_ID " + neid;
			// nm.replace("/", "");
			String PORT_0_0_0_IPV6_NM = common + "PORT_0_0_0_IPV6_NM " + nm.replace("/", "");
			String PORT_0_0_0_VLANID = common + "PORT_0_0_0_VLANID " + vlan;
			String PORT_0_0_0_IPV6_IP = common + "PORT_0_0_0_IPV6_IP " + ip;
			String PORT_0_0_0_IPV6_GW = common + "PORT_0_0_0_IPV6_GW " + gw;
			String RS_IP = common + "RS_IP " + rsip;
			if (version.equals("19A")) {
				envFilename = "ENV_AU_" + version + "_" + enbName + "_" + timeStamp + ".txt";

				sb.append(bootup_flag).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(protection_mode).append("\n");
				sb.append(thresold_cpu).append("\n");
				sb.append(thresold_modem).append("\n");
				sb.append(bootmode_static).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(pnp_mode).append("\n");
				sb.append(max_pnp_clear_count).append("\n");
				sb.append(swdisable_reboot_cnt).append("\n");
				sb.append(ftp_type).append("\n");
				sb.append(pingskip).append("\n");
				sb.append(isolation_map).append("\n");
				sb.append(rlc_core).append("\n");
				sb.append(hugepages).append("\n");
				sb.append(corenumber_of_se).append("\n");
				sb.append(memorysize_of_se).append("\n");
				sb.append(auth).append("\n");
				sb.append(NEID).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(reboot_timer).append("\n");
				sb.append(pnp_clear_count).append("\n");
				sb.append(port_0_0_0_type).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				sb.append(RS_IP);

			}
			// else if(fileType.equalsIgnoreCase("ENV") && version.equals("20A")) {
			// envFilename = "ENV_AU_"+version+"_"+"39Ghz_"+enbName+dateString+".txt";
			//
			//
			// sb.append(bootup_flag).append("\n");
			// sb.append(bootport_port).append("\n");
			// sb.append(protection_mode).append("\n");
			// sb.append(thresold_cpu).append("\n");
			// sb.append(thresold_modem).append("\n");
			// sb.append(bootmode_static).append("\n");
			// sb.append(ne_type).append("\n");
			// sb.append(pnp_mode).append("\n");
			// sb.append(max_pnp_clear_count).append("\n");
			// sb.append(swdisable_reboot_cnt).append("\n");
			// sb.append(ftp_type).append("\n");
			// sb.append(pingskip).append("\n");
			// sb.append(isolation_map).append("\n");
			// sb.append(rlc_core).append("\n");
			// sb.append(hugepages).append("\n");
			// sb.append(auth).append("\n");
			// sb.append(NEID).append("\n");
			// sb.append(ipv6_enable).append("\n");
			// sb.append(reboot_timer).append("\n");
			// sb.append(pnp_clear_count).append("\n");
			// sb.append(port_0_0_0_ipver6).append("\n");
			// sb.append(PORT_0_0_0_IPV6_NM).append("\n");
			// sb.append(PORT_0_0_0_VLANID).append("\n");
			// sb.append(port_0_0_0_type).append("\n");
			// sb.append(PORT_0_0_0_IPV6_IP).append("\n");
			// sb.append(PORT_0_0_0_IPV6_GW).append("\n");
			// sb.append(RS_IP).append("\n");
			//
			//
			//
			// }
			else {
				logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
						+ Constants.ENB_MENU_TEMPLATE);
			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + envFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", envFilename);
			/*
			 * User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
			 * { String fileSavePath = fileBuilder.toString(); fileSavePath =
			 * fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			 * , ""); objInfo.setFilePath(fileSavePath);
			 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
			 * objInfo.setRemarks(remarks); objInfo.setFileType(Constants.FILE_TYPE_ENV);
			 * objInfo.setFileName(envFilename); objInfo.setGenerationDate(new Date());
			 * objInfo.setGeneratedBy(user.getUserName()); CustomerDetailsEntity
			 * programDetailsEntity = new CustomerDetailsEntity();
			 * programDetailsEntity.setId(programId);
			 * objInfo.setProgramDetailsEntity(programDetailsEntity);
			 * objGenerateCsvRepository.saveCsvAudit(objInfo); }
			 */
		}
		return fileGenerateResult;
	}

	// method for 39mhz env temporary puropse, once we know 28,39 mhz from where we
	// are getting then we ll include this method in above method only

	@Override
	public JSONObject envFileGenerationFor5G28ghz(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String remarks,String programName) {
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String envFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		try {
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			StringBuffer sb = new StringBuffer();
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String nr_band = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND").getHeaderValue();
			
			
			String auType="";
			
			if (programName.contains("VZN-5G-MM")) {
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
				{
					auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue().trim();
				}

				/*if ("GEN1".equalsIgnoreCase(auType)) {
					version = "20A";
				} else if ("GEN2".equalsIgnoreCase(auType)) {
					version = "20C";
				}*/

			}
			
			if (nr_band.equalsIgnoreCase("n261")) {
				envFilename = "ENV_AU_" + version + "_"+auType+"_" + "28Ghz_" + enbName + "_" + timeStamp + ".txt";
			}
			if (nr_band.equalsIgnoreCase("n260")) {
				envFilename = "ENV_AU_" + version + "_"+auType+"_" + "37Ghz_" + enbName + "_" + timeStamp + ".txt";
			}
			
			//Daisy Chain Implementation
			boolean daisychainbool = false;
			String siteName = null;
			Set<String> daisychainSet = new HashSet<>();
			List<CIQDetailsModel> data1 = null;
			Set<String> gnodebIdList = new HashSet<>();
			List<NeMappingEntity> data = neMappingService.getSiteName(enbId);
			siteName = data.get(0).getSiteName();
			data1 = fileUploadRepository.getCiqDetailsForRuleValidationsheet(siteName, dbcollectionFileName, "5GNRCIQAU", "siteName");
			if(!ObjectUtils.isEmpty(data1)){
				for(CIQDetailsModel ciqDetails : data1) {
					if(ciqDetails.getCiqMap().containsKey("DaisyChain")) {
						daisychainSet.add(ciqDetails.getCiqMap().get("DaisyChain").getHeaderValue());
					}
					gnodebIdList.add(ciqDetails.geteNBId());
				}
			}
			if(daisychainSet.size() == gnodebIdList.size()) {
				if(daisychainSet.contains("M") && daisychainSet.contains("S1")) {
					daisychainbool = true;
				}
			}
			if(daisychainbool) {
				String daisychainvalue = listCIQDetailsModel.get(0).getCiqMap().get("DaisyChain").getHeaderValue();
				if((daisychainSet.size()==3 && (daisychainvalue.equals("M") || daisychainvalue.equals("S1")))
						|| (daisychainSet.size()==2 && daisychainvalue.equals("M"))) {
					daisychainbool = true;
				} else {
					daisychainbool = false;
				}
			}
			

/*setenv p SECURE_STORAGE=1
setenv p VPN0_IPVER=6
 
Also please change line:
 
FROM:
setenv p FEATURE_DOT1X 0
 
TO:
setenv p FEATURE_DOT1X=OFF*/
 
			
			String bootup_flag = Constants.bootup_flag;
			String bootport_port = Constants.bootport_port;
			String protection_mode = Constants.protection_mode;
			String thresold_cpu = Constants.temperature_thresold_cpu;
			String thresold_modem = Constants.temperature_thresold_modem;
			String bootmode_static = Constants.bootmode_static;
			String ne_type = Constants.ne_type;
			String pnp_mode = Constants.pnp_mode;
			String max_pnp_clear_count = Constants.max_pnp_clear_count;
			String swdisable_reboot_cnt = Constants.swdisable_reboot_cnt;
			String ftp_type = Constants.ftp_type;
			String pingskip = Constants.pingskip;
			String isolation_map = Constants.isolation_map;
			String rlc_core = Constants.number_of_rlc_core;
			String hugepages = Constants.hugepages;
			String corenumber_of_se = Constants.corenumber_of_se;
			String memorysize_of_se = Constants.memorysize_of_se;
			String auth = Constants.auth;
			String ipv6_enable = Constants.ipv6_enable;
			String reboot_timer = Constants.reboot_timer;
			String pnp_clear_count = Constants.pnp_clear_count;
			String port_0_0_0_type = Constants.port_0_0_0_type;
			String port_0_0_0_ipver6 = Constants.port_0_0_0_ipver6;
			String cert_enable = Constants.CERT_ENABLE;
			String cmp_dn_domain = Constants.CMP_DN_DOMAIN;
			String default_ip_ver = Constants.DEFAULT_IPVER;
			String envupdate_enable = Constants.ENVUPDATE_ENABLE;
			String feature_dotix = Constants.FEATURE_DOT1X;
			String secure_storage = Constants.SECURE_STORAGE;
			String port_0_0_1_type = Constants.port_0_0_1_type;
			String cmpv2_inside = Constants.CMPV2_INSIDE_SEGW;
			String dh_group = Constants.DH_GROUP;
			String fallback_to_dhcp = Constants.FALLBACK_TO_DHCP;
			String feature_dot1x0 = Constants.FEATURE_DOT1X0;
			String ftptelnet_on = Constants.FTPTELNET_ON;
			String hash_alg = Constants.HASH_ALG;
			String ike_sa_lifetime = Constants.IKE_SA_LIFETIME;
			String ipsec_sa_lifetime = Constants.IPSEC_SA_LIFETIME;
			String no_crl = Constants.NO_CRL;
			String VPN0_IPVER = Constants.VPN0_IPVER;
			String feature_dot1xoff = Constants.FEATURE_DOT1XOFF;
			
			String siteType = "";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("Site_Type")) {
				siteType = listCIQDetailsModel.get(0).getCiqMap().get("Site_Type").getHeaderValue();
			}
			String neid = listCIQDetailsModel.get(0).getCiqMap().get("NE ID AU").getHeaderValue();
			String nm = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Prefix_Length").getHeaderValue();
			String vlan = listCIQDetailsModel.get(0).getCiqMap().get("OAM_VLAN_ID").getHeaderValue();
			String ip = listCIQDetailsModel.get(0).getCiqMap().get("OAM_IP").getHeaderValue();
			String gw = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Gateway").getHeaderValue();
			String rsip = listCIQDetailsModel.get(0).getCiqMap().get("EMS_IP").getHeaderValue();
			String common = "setenv p" + " ";
			String NEID = common + "NE_ID " + neid;
			// nm.replace("/", "");
			String PORT_0_0_0_IPV6_NM = common + "PORT_0_0_0_IPV6_NM " + nm.replace("/", "");
			String PORT_0_0_0_VLANID = common + "PORT_0_0_0_VLANID " + vlan;
			String PORT_0_0_0_IPV6_IP = common + "PORT_0_0_0_IPV6_IP " + ip;
			String PORT_0_0_0_IPV6_GW = common + "PORT_0_0_0_IPV6_GW " + gw;
			String RS_IP = common + "RS_IP " + rsip;

			if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))) { //&& version.equals("20C")) {
				sb.append(bootmode_static).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(cert_enable).append("\n");
				sb.append(cmp_dn_domain).append("\n");
				sb.append(cmpv2_inside).append("\n");
				sb.append(default_ip_ver).append("4\n");
				sb.append(dh_group).append("\n");
				sb.append(fallback_to_dhcp).append("\n");
				if(version.equals("21D") || version.equals("22A")) { //22A ENV
					sb.append(feature_dot1xoff).append("\n");	
				}else {
					sb.append(feature_dot1x0).append("\n");
				}
				sb.append(ftptelnet_on).append("\n");
				sb.append(hash_alg).append("\n");
				sb.append(ike_sa_lifetime).append("\n");
				sb.append(ipsec_sa_lifetime).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(no_crl).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(NEID).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				if(version.equals("21D") || version.equals("22A")) { //22A ENV
				sb.append(RS_IP).append("\n");
				sb.append(secure_storage).append("\n");//VPN0_IPVER
				sb.append(VPN0_IPVER);
				}else {
					sb.append(RS_IP);
				}
				
			} else if (version.equals("20A") && nr_band.equalsIgnoreCase("n261")) {

				sb.append(bootup_flag).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(protection_mode).append("\n");
				sb.append(thresold_cpu).append("\n");
				sb.append(thresold_modem).append("\n");
				sb.append(bootmode_static).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(pnp_mode).append("\n");
				sb.append(max_pnp_clear_count).append("\n");
				sb.append(swdisable_reboot_cnt).append("\n");
				sb.append(ftp_type).append("\n");
				sb.append(pingskip).append("\n");
				sb.append(isolation_map).append("\n");
				sb.append(rlc_core).append("\n");
				sb.append(hugepages).append("\n");
				sb.append(auth).append("\n");
				sb.append(NEID).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(reboot_timer).append("\n");
				sb.append(pnp_clear_count).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(port_0_0_0_type).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				sb.append(RS_IP).append("\n");
				sb.append(corenumber_of_se).append("\n");
				sb.append(memorysize_of_se);
				if(daisychainbool) {
					sb.append("\n").append(port_0_0_1_type);
				}
			} else if (version.equals("20A") && nr_band.equalsIgnoreCase("n260")) {
				sb.append(bootup_flag).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(protection_mode).append("\n");
				sb.append(thresold_cpu).append("\n");
				sb.append(thresold_modem).append("\n");
				sb.append(bootmode_static).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(pnp_mode).append("\n");
				sb.append(max_pnp_clear_count).append("\n");
				sb.append(swdisable_reboot_cnt).append("\n");
				sb.append(ftp_type).append("\n");
				sb.append(pingskip).append("\n");
				sb.append(isolation_map).append("\n");
				sb.append(rlc_core).append("\n");
				sb.append(hugepages).append("\n");
				sb.append(auth).append("\n");
				sb.append(NEID).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(reboot_timer).append("\n");
				sb.append(pnp_clear_count).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(port_0_0_0_type).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				sb.append(RS_IP);
				if(daisychainbool) {
					sb.append("\n").append(port_0_0_1_type);
				}
			}else if ((version.equals("20C") ||  version.equals("21A") //22A ENV
					||  version.equals("21B") ||  version.equals("21C")||  version.equals("21D") || version.equals("22A") || version.equals("22C"))&& nr_band.equalsIgnoreCase("n261")) {
				sb.append(bootup_flag).append("\n");
				sb.append(auth).append("\n");
				sb.append(bootmode_static).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(cert_enable).append("\n");
				sb.append(cmp_dn_domain).append("\n");
				sb.append(default_ip_ver).append("\n");
				sb.append(envupdate_enable).append("\n");
				sb.append(feature_dotix).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(pnp_clear_count).append("\n");
				sb.append(reboot_timer).append("\n");
				sb.append(secure_storage).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(NEID).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				sb.append(RS_IP).append("\n");
				if(daisychainbool) {
					sb.append(port_0_0_1_type);
				}
			}else if ((version.equals("20C") ||  version.equals("21A") //22A ENV
					||  version.equals("21B") ||  version.equals("21C")||  version.equals("21D") || version.equals("22A")|| version.equals("22C")) && nr_band.equalsIgnoreCase("n260")) {
				sb.append(bootup_flag).append("\n");
				sb.append(auth).append("\n");
				sb.append(bootmode_static).append("\n");
				sb.append(bootport_port).append("\n");
				sb.append(cert_enable).append("\n");
				sb.append(cmp_dn_domain).append("\n");
				sb.append(default_ip_ver).append("\n");
				sb.append(envupdate_enable).append("\n");
				sb.append(feature_dotix).append("\n");
				sb.append(ipv6_enable).append("\n");
				sb.append(ne_type).append("\n");
				sb.append(pnp_clear_count).append("\n");
				sb.append(reboot_timer).append("\n");
				sb.append(secure_storage).append("\n");
				sb.append(port_0_0_0_ipver6).append("\n");
				sb.append(PORT_0_0_0_IPV6_NM).append("\n");
				sb.append(PORT_0_0_0_VLANID).append("\n");
				sb.append(NEID).append("\n");
				sb.append(PORT_0_0_0_IPV6_IP).append("\n");
				sb.append(PORT_0_0_0_IPV6_GW).append("\n");
				sb.append(RS_IP).append("\n");
				if(daisychainbool) {
					sb.append(port_0_0_1_type);
				}
			}

			else {
				logger.info(GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
						+ Constants.ENB_MENU_TEMPLATE);
			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + envFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.envFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", envFilename);
		}
		return fileGenerateResult;
	}

	@Override
	public JSONObject generateTemplates(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String remarksm,
			String releaseVer, NeMappingEntity neMappingEntity) {
		boolean status = false;
		ArrayList<String> fileNames = new ArrayList<>();
		JSONObject fileGenerateResult = new JSONObject();
		try {
			StringBuffer sb = new StringBuffer();
			ArrayList<String> ciqData = new ArrayList<String>();
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String[] data = { "Market", "GNODEB_AU_ID", "NE ID AU", "NR FREQ BAND", "OAM_VLAN_ID", "OAM_Prefix_Length",
					"OAM_IP", "RAN_IP_Address", "RAN_Gateway", "RAN_Prefix_Length", "RAN_Route_Prefix1",
					"RAN_Route_Prefix2", "Remote_IP_Address", "VZW Site Name", "AU_MIMO_Configuration", "Network",
					"GNODEBID", "NR PCI", "PRACH ROOT SEQUENCE INDEX", "RAN_VLAN_ID", "CC0 Cell Num", "CC0 NR ARFCN",
					"CC0 NR Bandwidth", "CC1 Cell Num", "CC1 NR ARFCN", "CC1 NR Bandwidth", "CC2 Cell Num",
					"CC2 NR ARFCN", "CC2 NR Bandwidth", "CC3 Cell Num", "CC3 NR ARFCN", "CC3 NR Bandwidth",
					"CC4 Cell Num", "CC4 NR ARFCN", "CC4 NR Bandwidth", "CC5 Cell Num", "CC5 NR ARFCN",
					"CC5 NR Bandwidth", "CC6 Cell Num", "CC6 NR ARFCN", "CC6 NR Bandwidth", "CC7 Cell Num",
					"CC7 NR ARFCN", "CC7 NR Bandwidth", "NE_ID ACPF", "NE_ID_ACPF Name", "NE_ID_AUPF",
					"NE_ID_AUPF Name", "GNB_CU Name", "Site_Type", "AU_Latitude", "AU_Longitude", "Height", 
					"Latitude", "Longitude", "Rad_Height","Clock_Source","AU_Type", "MarketType", "GNB AU Name", "IAU_Mount_Type","RAN_IP_Address"};
			for (int i = 0; i < data.length; i++) {
				try {
					if (i == 0) {
						if (listCIQDetailsModel.get(0).getCiqMap().get(data[i]).getHeaderValue().isEmpty()) {
							sb.append("TBD");
						} else {
							sb.append(listCIQDetailsModel.get(0).getCiqMap().get(data[i]).getHeaderValue());
						}						
					} else {
						if (listCIQDetailsModel.get(0).getCiqMap().get(data[i]).getHeaderValue().isEmpty()) {
							sb.append("," + "TBD");
						} else {
							sb.append("," + listCIQDetailsModel.get(0).getCiqMap().get(data[i]).getHeaderValue());
						}
						
					}
				} catch (NullPointerException e) {
					logger.info("GenerateCsvServiceImpl.generateTemplates() " + data[i] + " data is not Present");
					sb.append("," + "TBD");
				}
			}
			//sb.append("," + enbName);
			if (CommonUtil.isValidObject(sb)) {

				ciqData.add(
						"MARKET,GNODEB_AUID,NE_ID_AU,NR_FREQ_BAND,OAMVLANID,OAMPREFIXLENGTH,OAMIP,RANIPADDRESS,RANGATEWAY,RANPREFIXLENGTH,"
						+ "RANROUTEPREFIX1,RANROUTEPREFIX2,REMOTEIPADDRESS,VZW_SITE_NAME,AUMIMOCONFIGURATION,NETWORK,GNODEB_ID,NR_PCI,"
						+ "PRACH_ROOT_SEQUENCE_INDEX,RANVLANID,CC0_Cell_Num,CC0_NR_ARFCN,CC0_NR_Bandwidth,CC1_Cell_Num,CC1_NR_ARFCN,CC1_NR_Bandwidth,"
						+ "CC2_Cell_Num,CC2_NR_ARFCN,CC2_NR_Bandwidth,CC3_Cell_Num,CC3_NR_ARFCN,CC3_NR_Bandwidth,CC4_Cell_Num,CC4_NR_ARFCN,"
						+ "CC4_NR_Bandwidth,CC5_Cell_Num,CC5_NR_ARFCN,CC5_NR_Bandwidth,CC6_Cell_Num,CC6_NR_ARFCN,CC6_NR_Bandwidth,CC7_Cell_Num,"
						+ "CC7_NR_ARFCN,CC7_NR_Bandwidth,NE_ID_ACPF,NE_ID_ACPF_Name,NE_ID_AUPF,NE_ID_AUPF_Name,GNB_CU_Name,Site Type,AU Latitude,"
						+ "AU Longitude,AU RadCenter Height (ft),Latitude,Longitude,Height,Clock_Source,AU_Type,Market_Type,GNB_AU_Name,IAU_Mount_Type,RAN_IP_Address");
				ciqData.add(sb.toString());
				String auType="";
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
				{
					auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue().trim();
				}
				String siteType = "";
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("Site_Type")) {
					siteType = listCIQDetailsModel.get(0).getCiqMap().get("Site_Type").getHeaderValue();
				}
				//Release-Version Fix
				String marketType="";
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("MarketType") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("MarketType").getHeaderValue()))
				{
					marketType=listCIQDetailsModel.get(0).getCiqMap().get("MarketType").getHeaderValue();
				}
				/*String tempRelVer = releaseVer;
				if(!marketType.equalsIgnoreCase("SNAP")) {
					releaseVer = getReleaseVersion(programId, neMappingEntity, releaseVer);
				}
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))) {
					releaseVer = tempRelVer;
				}*/
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("release_version")) {
					releaseVer = listCIQDetailsModel.get(0).getCiqMap().get("release_version").getHeaderValue();
				}
				ArrayList<String> cellTemplatefn = (new CellTemplate_5G()).generateCellTemplate(ciqData, version,
						filePath, enbId,auType);
				
				cellTemplatefn.forEach(x -> fileNames.add(x));

				fileNames.add((new AuTemplate()).generateAuTemplate(ciqData, version, filePath, enbId, releaseVer,auType));

				fileNames.add(mergeGrowTemplates(filePath, fileNames, enbName,auType));

				fileNames
						.add((new ACPF_Template()).generateAcpfTemplate(ciqData, version, filePath, enbId, releaseVer));

				fileNames
						.add((new AUPF_Template()).generateAupfTemplate(ciqData, version, filePath, enbId, releaseVer));

				status = true;

			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.generateTemplates() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", fileNames);
		}
		return fileGenerateResult;
	}

	public String mergeGrowTemplates(String filePath, ArrayList<String> fileNames, String enbName,String auType) throws IOException {
		StringBuilder mergeFileName = new StringBuilder();
		StringBuilder sreader = new StringBuilder();
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String mfileName = "pnp_macro_indoor_dist_" +auType+"_"+ enbName + dateString + ".csv";
		mergeFileName.append(filePath).append(mfileName);
		File enbPath = new File(filePath + fileNames.get(1));
		File cellPath = new File(filePath + fileNames.get(0));
		File mergePath = new File(mergeFileName.toString());
		BufferedReader breader1 = new BufferedReader(new FileReader(enbPath));
		BufferedReader breader2 = new BufferedReader(new FileReader(cellPath));
		String reader = breader1.readLine();
		String str = reader;
		sreader.append(str);
		reader = breader1.readLine();
		while (reader != null) {
			str = "\n" + reader;
			sreader.append(str);
			reader = breader1.readLine();
		}
		String reader2 = breader2.readLine();
		while (reader2 != null) {
			str = "\n" + reader2;
			sreader.append(str);
			reader2 = breader2.readLine();
		}
		breader1.close();
		breader2.close();
		FileWriter fileWriter = new FileWriter(mergePath.toString());
		fileWriter.write(sreader.toString());
		fileWriter.close();
		return mfileName;
	}

	@Override
	public String folderName(String gnbidd) {

		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String folderName = "ENDC_X2_" + gnbidd + "_" + timeStamp;

		return folderName;

	}

	@Override
	public JSONObject endcTemplate(String nrfreq, String folderName, String enbid, String version, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, Integer programId, String filePath,
			String sessionId, String fileType, NeMappingEntity neMappingEntity, String remarks, String validation) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		StringBuilder endctemp = new StringBuilder();

		String arfcn_dl = null;
		String arfcn_ul = null;
		List<String> gnbid = new ArrayList<String>();
		List<String> ip = new ArrayList<String>();
		Set<CIQDetailsModel> ciqDetails = new LinkedHashSet<>();
		// List<String> arfcn_ul=new ArrayList<String>();
		String enbid1 = null;
		Set<String> enb_cellid = new LinkedHashSet<String>();
		String sheetName = "4GLTE5GNRX2";
		List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableSheetDetails(ciqFileName, sheetName,
				enbid, dbcollectionFileName);

		if (listCIQDetailsModel.size() == 0) {
			if (enbid.startsWith("0"))
				enbid1 = enbid.substring(1, enbid.length());
			else
				enbid1 = "0" + enbid;

			listCIQDetailsModel = fileUploadRepository.getEnbTableSheetDetails(ciqFileName, sheetName, enbid1,
					dbcollectionFileName);

		}

		sheetName = "4GLTE5GNRRelation";

		List<CIQDetailsModel> listCIQDetailsModell = fileUploadRepository.getEnbTableSheetDetailss(ciqFileName,
				sheetName, enbid, dbcollectionFileName);

		for (int i = 0; i < listCIQDetailsModel.size(); i++) {

			if (listCIQDetailsModel.get(i).getCiqMap().containsKey("GNODEB ID")) {
				String gnb = listCIQDetailsModel.get(i).getCiqMap().get("GNODEB ID").getHeaderValue();
				if (ciqFileName.contains("Boston")) {
					gnb = "0" + gnb;
				}
				gnbid.add(gnb);
			}
		}

		for (int i = 0; i < listCIQDetailsModel.size(); i++) {
			if (listCIQDetailsModel.get(i).getCiqMap().containsKey("Remote_IPAddress")) {
				ip.add(listCIQDetailsModel.get(i).getCiqMap().get("Remote_IPAddress").getHeaderValue());
			}
		}
		if (listCIQDetailsModell.size() > 0) {
			if (listCIQDetailsModell.get(0).getCiqMap().containsKey("ARFCN_UL")) {
				arfcn_ul = (listCIQDetailsModell.get(0).getCiqMap().get("ARFCN_UL").getHeaderValue());
			}
			if (listCIQDetailsModell.get(0).getCiqMap().containsKey("ARFCN_DL")) {
				arfcn_dl = (listCIQDetailsModell.get(0).getCiqMap().get("ARFCN_DL").getHeaderValue());
			}

			for (int i = 0; i < listCIQDetailsModell.size(); i++) {
				if (listCIQDetailsModell.get(0).getCiqMap().containsKey("eNB_Cell_ID")) {
					String id = listCIQDetailsModell.get(i).getCiqMap().get("eNB_Cell_ID").getHeaderValue();
					if (!enb_cellid.contains(id)) {
						enb_cellid.add(listCIQDetailsModell.get(i).getCiqMap().get("eNB_Cell_ID").getHeaderValue());
					}
				}
			}
		}

		version = "20B";

		if (enbid.startsWith("0"))
			enbid = enbid.substring(1, enbid.length());
		/*
		 * String neversion=null; int c=0;
		 * 
		 * List<NeMappingEntity> neVersion=rep.getVersion(enbid); for(int
		 * i=0;i<neVersion.size();i++) { String programName=
		 * neVersion.get(i).getProgramDetailsEntity().getProgramName();
		 * if(programName.equals("VZN-4G-USM-LIVE")) try{ {
		 * if(neVersion.get(i).getNetworkConfigEntity()!=null) { if(c==0) { neversion =
		 * neVersion.get(i).getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
		 * ; c++; } }
		 * 
		 * }} catch(Exception e){ System.out.println("No ne mapping found");
		 * 
		 * } } try { if(neversion .contains("9")) version = "9";
		 * if(neversion.contains("20.A")) version = "20A";
		 * if(neversion.contains("20.B")) version = "20B";
		 * 
		 * } catch(Exception e) { System.out.println("No ne version found"); }
		 * 
		 * if(neversion==null) { version = "20A"; }
		 */
		String f = null;
		List<String> ls = new ArrayList<String>(Arrays.asList(version + "_0_ENDC_" + enbid + "_" + timeStamp + ".xml"));
		List<String> ls1 = new ArrayList<String>(Arrays.asList(version + "_0_ENDC_" + enbid + ".xml"));
		List<String> fileNames = new ArrayList<String>();
		f = Constants.ENDC;

		if (f != null && (ls.size()) > 0) {
			try {

				fileBuilder.setLength(0);
				fileBuilder.append(filePath.toString());
				if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("XML")) {
					temp.append(fileBuilder);
					for (int i = 0; i < ls.size(); i++) {
						String endcFileName1 = ls.get(i);
						if (i > 0) {
							fileBuilder.setLength(0);
							fileBuilder.append(temp);

						}
						String endctempp = fileBuilder.toString();
						status = endcGeneration(nrfreq, f, gnbid, ip, arfcn_ul, arfcn_dl, enb_cellid,
								dbcollectionFileName, listCIQDetailsModel, endctempp, endcFileName1, folderName,
								ls1.get(i), validation);
					}

					fileNames.add(folderName);

				}

			}

			catch (Exception e) {
				logger.error("endcGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
			}

			finally {
				fileGenerateResult.put("status", status);
				fileGenerateResult.put("fileName", fileNames);

			}
		}
		return fileGenerateResult;

	}

	public boolean endcGeneration(String nrfreq, String f, List<String> gnbid, List<String> ip, String arfcn_ul,
			String arfcn_dl, Set<String> enb_cellid, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String fileName, String folderName,
			String endcUseCaseFileName, String validation)
			throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException {

		boolean status = false;
		List<Integer> cell_id = new ArrayList<Integer>();
		for (String x : enb_cellid) {
			if (!(x.equals("") || x.equals("NA"))) {
				Integer cellid = Integer.parseInt(x);
				cell_id.add(cellid);
			}
		}
		// Collections.sort(cell_id);
		int c = gnbid.size();
		int k = cell_id.size();
		Integer count = 0;

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();

		Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
		doc.appendChild(nc_rpc);

		Element nc_edit_config = doc.createElement("nc:edit-config");
		nc_rpc.appendChild(nc_edit_config);

		Element nc_target = doc.createElement("nc:target");
		nc_edit_config.appendChild(nc_target);

		Element running = doc.createElement("nc:running");
		nc_target.appendChild(running);
		running.setTextContent(" ");

		Element nc_default_operation = doc.createElement("nc:default-operation");
		nc_edit_config.appendChild(nc_default_operation);
		nc_default_operation.appendChild(doc.createTextNode("none"));

		Element nc_config = doc.createElement("nc:config");
		nc_edit_config.appendChild(nc_config);

		Element mid_managed_element = doc.createElementNS(
				"http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist", "mid:managed-element");
		nc_config.appendChild(mid_managed_element);

		Element mid_enb_function = doc.createElement("mid:enb-function");
		mid_managed_element.appendChild(mid_enb_function);

		if (!fileName.startsWith("20B")) {
			while (c != 0) {

				Element mid_term_point_to_gnb = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:term-point-to-gnb");
				mid_term_point_to_gnb.setAttribute("nc:operation", "merge");
				mid_enb_function.appendChild(mid_term_point_to_gnb);

				Element mid_neighbor_gnb_index = doc.createElement("mid:neighbor-gnb-index");
				mid_neighbor_gnb_index.setTextContent(count.toString());
				mid_term_point_to_gnb.appendChild(mid_neighbor_gnb_index);
				Element mid_no_x2 = doc.createElement("mid:no-x2");
				mid_no_x2.setTextContent("false");
				mid_term_point_to_gnb.appendChild(mid_no_x2);

				Element mid_gnb_id_bit_length = doc.createElement("mid:gnb-id-bit-length");
				mid_gnb_id_bit_length.setTextContent("22");
				mid_term_point_to_gnb.appendChild(mid_gnb_id_bit_length);

				Element mid_gnb_id = doc.createElement("mid:gnb-id");
				mid_gnb_id.setTextContent(gnbid.get(count));
				mid_term_point_to_gnb.appendChild(mid_gnb_id);

				Element mid_mcc = doc.createElement("mid:mcc");
				mid_mcc.setTextContent("311");
				mid_term_point_to_gnb.appendChild(mid_mcc);

				Element mid_mnc = doc.createElement("mid:mnc");
				mid_mnc.setTextContent("480");
				mid_term_point_to_gnb.appendChild(mid_mnc);
				Element mid_ip_ver = doc.createElement("mid:ip-ver");

				mid_ip_ver.setTextContent("ipv6");
				mid_term_point_to_gnb.appendChild(mid_ip_ver);

				Element mid_neighbor_gnb_ipv6 = doc.createElement("mid:neighbor-gnb-ipv6");
				mid_neighbor_gnb_ipv6.setTextContent(ip.get(count++));
				mid_term_point_to_gnb.appendChild(mid_neighbor_gnb_ipv6);

				c--;
			}

			Element mid_enb_function2 = doc.createElement("mid:enb-function");
			mid_managed_element.appendChild(mid_enb_function2);

			Element mid_carrier_aggregation = doc.createElement("mid:carrier-aggregation");
			mid_enb_function2.appendChild(mid_carrier_aggregation);

			Element mid_ca_requested_frequency_band_info = doc.createElement("mid:ca-requested-frequency-band-info");
			mid_ca_requested_frequency_band_info.setAttribute("nc:operation", "merge");
			mid_carrier_aggregation.appendChild(mid_ca_requested_frequency_band_info);

			Element mid_requested_nr_band1 = doc.createElement("mid:requested-nr-band1");
			mid_requested_nr_band1.setTextContent("261");
			mid_ca_requested_frequency_band_info.appendChild(mid_requested_nr_band1);

			Element mid_requested_nr_band2 = doc.createElement("mid:requested-nr-band2");
			mid_requested_nr_band2.setTextContent("0");
			mid_ca_requested_frequency_band_info.appendChild(mid_requested_nr_band2);

//			Element mid_enb_function3 = doc.createElement("mid:enb-function");
//			mid_managed_element.appendChild(mid_enb_function3);
//
//			Element mid_new_radio_network = doc.createElement("mid:new-radio-network");
//			mid_enb_function3.appendChild(mid_new_radio_network);
//			Integer cntt;
//			cntt = 2;
//			for (Integer i = 0; i < cntt; i++) {
//
//				Element mid_dc_control_param = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
//						"mid:dc-control-param");
//				mid_dc_control_param.setAttribute("nc:operation", "merge");
//				mid_new_radio_network.appendChild(mid_dc_control_param);
//
//				Element mid_dc_index = doc.createElement("mid:dc-index");
//				mid_dc_index.setTextContent(i.toString());
//				mid_dc_control_param.appendChild(mid_dc_index);
//
//				Element mid_bearer_type_usage = doc.createElement("mid:bearer-type-usage");
//				mid_bearer_type_usage.setTextContent("use");
//				mid_dc_control_param.appendChild(mid_bearer_type_usage);
//
//				Element mid_qci = doc.createElement("mid:qci");
//				if (i == 0)
//					mid_qci.setTextContent("8");
//				if (i == 1)
//					mid_qci.setTextContent("9");
//				mid_dc_control_param.appendChild(mid_qci);
//
//				Element mid_arp = doc.createElement("mid:arp");
//
//				mid_arp.setTextContent("15");
//				mid_dc_control_param.appendChild(mid_arp);
//
//				Element mid_bearer_type = doc.createElement("mid:bearer-type");
//				mid_bearer_type.setTextContent("sn-term-split");
//				mid_dc_control_param.appendChild(mid_bearer_type);
//
//				Element mid_nr_band_freq = doc.createElement("mid:nr-band-freq");
//				mid_nr_band_freq.setTextContent("qciarp-fr2");
//				mid_dc_control_param.appendChild(mid_nr_band_freq);
//
//				Element mid_priority = doc.createElement("mid:priority");
//				mid_priority.setTextContent("7");
//				mid_dc_control_param.appendChild(mid_priority);
//			}

			int a = 0;
			while (k != 0) {

				Element mid_enb_function1 = doc.createElement("mid:enb-function");
				mid_managed_element.appendChild(mid_enb_function1);

				Element mid_eutran_generic_cell = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell);

				Element mid_eutran_cell_fdd_tdd = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell.appendChild(mid_eutran_cell_fdd_tdd);

				Element mid_cell_num = doc.createElement("mid:cell-num");
				mid_cell_num.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd.appendChild(mid_cell_num);

				Element mid_cell_endc_function = doc.createElement("mid:cell-endc-function");
				mid_eutran_cell_fdd_tdd.appendChild(mid_cell_endc_function);

				Element mid_endc_cell_info = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:endc-cell-info");
				mid_endc_cell_info.setAttribute("nc:operation", "merge");
				mid_cell_endc_function.appendChild(mid_endc_cell_info);

				Element mid_endc_support = doc.createElement("mid:endc-support");
				mid_endc_support.setTextContent("true");
				mid_endc_cell_info.appendChild(mid_endc_support);

				Element mid_endc_operation_mode = doc.createElement("mid:endc-operation-mode");
				mid_endc_operation_mode.setTextContent("endc-mode2");
				mid_endc_cell_info.appendChild(mid_endc_operation_mode);

				Element mid_freq_distribute_option = doc.createElement("mid:freq-distribute-option");
				mid_freq_distribute_option.setTextContent("lte-nr-freq-specific");
				mid_endc_cell_info.appendChild(mid_freq_distribute_option);

				Element mid_num_lte_freq_for_fr1 = doc.createElement("mid:num-lte-freq-for-fr1");
				mid_num_lte_freq_for_fr1.setTextContent("5");
				mid_endc_cell_info.appendChild(mid_num_lte_freq_for_fr1);

				Element mid_num_lte_freq_for_fr2 = doc.createElement("mid:num-lte-freq-for-fr2");
				mid_num_lte_freq_for_fr2.setTextContent("1");
				mid_endc_cell_info.appendChild(mid_num_lte_freq_for_fr2);

				Element mid_harmonics_imd_nr_fr = doc.createElement("mid:harmonics-imd-nr-fr");
				if (fileName.contains("20A"))
					mid_harmonics_imd_nr_fr.setTextContent("imd-na");
				else
					mid_harmonics_imd_nr_fr.setTextContent("imd-fr1");
				mid_endc_cell_info.appendChild(mid_harmonics_imd_nr_fr);

				Element mid_endc_anchor_type = doc.createElement("mid:endc-anchor-type");
				mid_endc_anchor_type.setTextContent("endc-anchor");
				mid_endc_cell_info.appendChild(mid_endc_anchor_type);

				Element mid_gnb_conf_volte = doc.createElement("mid:gnb-conf-volte");
				mid_gnb_conf_volte.setTextContent("true");
				mid_endc_cell_info.appendChild(mid_gnb_conf_volte);

				Element mid_data_traffic_threshold = doc.createElement("mid:data-traffic-threshold");
				mid_data_traffic_threshold.setTextContent("2100");
				mid_endc_cell_info.appendChild(mid_data_traffic_threshold);

				Element mid_eutran_cell_fdd_tdd2 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell.appendChild(mid_eutran_cell_fdd_tdd2);

				Element mid_cell_num2 = doc.createElement("mid:cell-num");
				mid_cell_num2.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd2.appendChild(mid_cell_num2);

				Element mid_nr_frequency_relation = doc.createElement("mid:nr-frequency-relation");
				mid_eutran_cell_fdd_tdd2.appendChild(mid_nr_frequency_relation);

				Element mid_nr_fa_information = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:nr-fa-information");
				mid_nr_fa_information.setAttribute("nc:operation", "merge");
				mid_nr_frequency_relation.appendChild(mid_nr_fa_information);

				Element mid_fa_index = doc.createElement("mid:fa-index");
				mid_fa_index.setTextContent("0");
				mid_nr_fa_information.appendChild(mid_fa_index);

				Element mid_eutran_generic_cell2 = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell2);

				Element mid_eutran_cell_fdd_tdd3 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell2.appendChild(mid_eutran_cell_fdd_tdd3);

				Element mid_cell_num3 = doc.createElement("mid:cell-num");
				mid_cell_num3.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd3.appendChild(mid_cell_num3);

				Element mid_nr_frequency_relation2 = doc.createElement("mid:nr-frequency-relation");
				mid_eutran_cell_fdd_tdd3.appendChild(mid_nr_frequency_relation2);

				Element mid_nr_fa_information2 = doc.createElement("mid:nr-fa-information");
				mid_nr_frequency_relation2.appendChild(mid_nr_fa_information2);

				Element mid_fa_index2 = doc.createElement("mid:fa-index");
				if (nrfreq.equals("n261"))
					mid_fa_index2.setTextContent("0");
				else
					mid_fa_index2.setTextContent("1");

				mid_nr_fa_information2.appendChild(mid_fa_index2);
				Element mid_nr_fa_prior_info_func = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:nr-fa-prior-info-func");
				mid_nr_fa_prior_info_func.setAttribute("nc:operation", "merge");
				mid_nr_fa_information2.appendChild(mid_nr_fa_prior_info_func);
				Element mid_duplex_type = doc.createElement("mid:duplex-type");
				mid_duplex_type.setTextContent("tdd");
				mid_nr_fa_prior_info_func.appendChild(mid_duplex_type);
				Element mid_arfcn_nr_dl = doc.createElement("mid:arfcn-nr-dl");
				mid_arfcn_nr_dl.setTextContent(arfcn_dl);
				mid_nr_fa_prior_info_func.appendChild(mid_arfcn_nr_dl);
				Element mid_arfcn_nr_ul = doc.createElement("mid:arfcn-nr-ul");
				mid_arfcn_nr_ul.setTextContent(arfcn_ul);
				mid_nr_fa_prior_info_func.appendChild(mid_arfcn_nr_ul);
				Element mid_mtc_ssb_nr_period = doc.createElement("mid:mtc-ssb-nr-period");
				mid_mtc_ssb_nr_period.setTextContent("ssb-period-sf20");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_period);
				Element mid_mtc_ssb_nr_offset = doc.createElement("mid:mtc-ssb-nr-offset");
				mid_mtc_ssb_nr_offset.setTextContent("5");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_offset);
				Element mid_mtc_ssb_nr_duration = doc.createElement("mid:mtc-ssb-nr-duration");
				mid_mtc_ssb_nr_duration.setTextContent("ssb-duration-sf3");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_duration);
				Element mid_sub_carrier_spacing_ssb = doc.createElement("mid:sub-carrier-spacing-ssb");
				mid_sub_carrier_spacing_ssb.setTextContent("scs-240khz");
				mid_nr_fa_prior_info_func.appendChild(mid_sub_carrier_spacing_ssb);

				Element mid_abs_thresh_rs_index_rsrp = doc.createElement("mid:abs-thresh-rs-index-rsrp");
				mid_abs_thresh_rs_index_rsrp.setTextContent("35");
				mid_nr_fa_prior_info_func.appendChild(mid_abs_thresh_rs_index_rsrp);
				Element mid_abs_thresh_rs_index_rsrp2 = doc.createElement("mid:abs-thresh-rs-index-rsrq");
				mid_abs_thresh_rs_index_rsrp2.setTextContent("8");
				mid_nr_fa_prior_info_func.appendChild(mid_abs_thresh_rs_index_rsrp2);
				Element mid_abs_thresh_rs_index_sinr = doc.createElement("mid:abs-thresh-rs-index-sinr");
				mid_abs_thresh_rs_index_sinr.setTextContent("32");
				mid_nr_fa_prior_info_func.appendChild(mid_abs_thresh_rs_index_sinr);
				Element mid_max_rs_index_cell_qual = doc.createElement("mid:max-rs-index-cell-qual");
				mid_max_rs_index_cell_qual.setTextContent("2");
				mid_nr_fa_prior_info_func.appendChild(mid_max_rs_index_cell_qual);
				Element mid_quantity_config_set = doc.createElement("mid:quantity-config-set");
				mid_quantity_config_set.setTextContent("1");
				mid_nr_fa_prior_info_func.appendChild(mid_quantity_config_set);

				Element mid_nr_band = doc.createElement("mid:nr-band");
				if (nrfreq.equals("n261"))
					mid_nr_band.setTextContent("261");
				else
					mid_nr_band.setTextContent("260");
				mid_nr_fa_prior_info_func.appendChild(mid_nr_band);

				Element mid_offset_freq = doc.createElement("mid:offset-freq");
				mid_offset_freq.setTextContent("0");
				mid_nr_fa_prior_info_func.appendChild(mid_offset_freq);

				Element mid_preference0 = doc.createElement("mid:preference0");
				mid_preference0.setTextContent("preferred-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference0);
				Element mid_preference1 = doc.createElement("mid:preference1");
				mid_preference1.setTextContent("not-allowed-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference1);
				Element mid_preference2 = doc.createElement("mid:preference2");
				mid_preference2.setTextContent("not-allowed-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference2);
				Element mid_preference3 = doc.createElement("mid:preference3");
				mid_preference3.setTextContent("not-allowed-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference3);
				Element mid_preference4 = doc.createElement("mid:preference4");
				mid_preference4.setTextContent("not-allowed-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference4);
				Element mid_preference5 = doc.createElement("mid:preference5");
				mid_preference5.setTextContent("not-allowed-preference");
				mid_nr_fa_prior_info_func.appendChild(mid_preference5);
				Element mid_min_nrt_ratio_carrier = doc.createElement("mid:min-nrt-ratio-carrier");
				mid_min_nrt_ratio_carrier.setTextContent("10");
				mid_nr_fa_prior_info_func.appendChild(mid_min_nrt_ratio_carrier);

				Element mid_eutran_generic_cell3 = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell3);

				Element mid_eutran_cell_fdd_tdd4 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell3.appendChild(mid_eutran_cell_fdd_tdd4);

				Element mid_cell_num4 = doc.createElement("mid:cell-num");
				mid_cell_num4.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd4.appendChild(mid_cell_num4);

				Element mid_ue_measurement_control = doc.createElement("mid:ue-measurement-control");
				mid_eutran_cell_fdd_tdd4.appendChild(mid_ue_measurement_control);

				Element mid_nr_b1_criteria_info = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:nr-b1-criteria-info");
				mid_nr_b1_criteria_info.setAttribute("nc:operation", "merge");
				mid_ue_measurement_control.appendChild(mid_nr_b1_criteria_info);

				Element mid_purpose = doc.createElement("mid:purpose");
				mid_purpose.setTextContent("en-dc-addition");
				mid_nr_b1_criteria_info.appendChild(mid_purpose);

				Element mid_fa_index1 = doc.createElement("mid:fa-index");
				mid_fa_index1.setTextContent("0");
				mid_nr_b1_criteria_info.appendChild(mid_fa_index1);

				Element mid_qci_group_index = doc.createElement("mid:qci-group-index");
				mid_qci_group_index.setTextContent("0");
				mid_nr_b1_criteria_info.appendChild(mid_qci_group_index);

				Element mid_b1_enable = doc.createElement("mid:b1-enable");
				mid_b1_enable.setTextContent("enable");
				mid_nr_b1_criteria_info.appendChild(mid_b1_enable);

				Element mid_b1_threshold_nr_rsrp = doc.createElement("mid:b1-threshold-nr-rsrp");
				mid_b1_threshold_nr_rsrp.setTextContent("51");
				mid_nr_b1_criteria_info.appendChild(mid_b1_threshold_nr_rsrp);

				Element mid_hysteresis = doc.createElement("mid:hysteresis");
				mid_hysteresis.setTextContent("0");
				mid_nr_b1_criteria_info.appendChild(mid_hysteresis);

				Element mid_time_to_trigger = doc.createElement("mid:time-to-trigger");
				mid_time_to_trigger.setTextContent("128ms");
				mid_nr_b1_criteria_info.appendChild(mid_time_to_trigger);

				Element mid_trigger_quantity = doc.createElement("mid:trigger-quantity");
				mid_trigger_quantity.setTextContent("nr-rsrp");
				mid_nr_b1_criteria_info.appendChild(mid_trigger_quantity);

				Element mid_eutran_generic_cell4 = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell4);

				Element mid_eutran_cell_fdd_tdd5 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell4.appendChild(mid_eutran_cell_fdd_tdd5);

				Element mid_cell_num5 = doc.createElement("mid:cell-num");
				mid_cell_num5.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd5.appendChild(mid_cell_num5);

				Element mid_cell_endc_function1 = doc.createElement("mid:cell-endc-function");
				mid_eutran_cell_fdd_tdd5.appendChild(mid_cell_endc_function1);

				for (int i = 0; i < 2; i++) {

					Element mid_en_dc_meas_priority = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:en-dc-meas-priority");
					mid_en_dc_meas_priority.setAttribute("nc:operation", "merge");
					mid_cell_endc_function1.appendChild(mid_en_dc_meas_priority);

					Element mid_non_gbr_qci = doc.createElement("mid:non-gbr-qci");
					if (i == 0)
						mid_non_gbr_qci.setTextContent("8");
					if (i == 1)
						mid_non_gbr_qci.setTextContent("9");
					mid_en_dc_meas_priority.appendChild(mid_non_gbr_qci);

					Element mid_nr_band_freq = doc.createElement("mid:nr-band-freq");
					mid_nr_band_freq.setTextContent("fr2");
					mid_en_dc_meas_priority.appendChild(mid_nr_band_freq);

					Element mid_priority = doc.createElement("mid:priority");
					mid_priority.setTextContent("7");
					mid_en_dc_meas_priority.appendChild(mid_priority);
				}
				Element mid_eutran_generic_cell5 = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell5);

				Element mid_eutran_cell_fdd_tdd6 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell5.appendChild(mid_eutran_cell_fdd_tdd6);

				Element mid_cell_num6 = doc.createElement("mid:cell-num");
				mid_cell_num6.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd6.appendChild(mid_cell_num6);

				for (int i = 0; i < 2; i++) {

					Element mid_cell_plmn_info = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:cell-plmn-info");
					mid_cell_plmn_info.setAttribute("nc:operation", "merge");
					mid_eutran_cell_fdd_tdd6.appendChild(mid_cell_plmn_info);

					Element mid_plmn_index = doc.createElement("mid:plmn-index");
					if (i == 0) {
						mid_plmn_index.setTextContent("0");
					}
					if (i == 1) {
						mid_plmn_index.setTextContent("1");
					}
					mid_cell_plmn_info.appendChild(mid_plmn_index);

					Element mid_plmn_usage = doc.createElement("mid:plmn-usage");
					mid_plmn_usage.setTextContent("enable");
					mid_cell_plmn_info.appendChild(mid_plmn_usage);

					Element mid_upper_layer_indication = doc.createElement("mid:upper-layer-indication");
					mid_upper_layer_indication.setTextContent("enable");
					mid_cell_plmn_info.appendChild(mid_upper_layer_indication);
				}

				Element mid_eutran_generic_cell6 = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function1.appendChild(mid_eutran_generic_cell6);

				Element mid_eutran_cell_fdd_tdd7 = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell6.appendChild(mid_eutran_cell_fdd_tdd7);

				Element mid_cell_num7 = doc.createElement("mid:cell-num");
				mid_cell_num7.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd7.appendChild(mid_cell_num7);

				Element mid_eutran_cell_info = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:eutran-cell-info");
				mid_eutran_cell_info.setAttribute("nc:operation", "merge");
				mid_eutran_cell_fdd_tdd7.appendChild(mid_eutran_cell_info);

				Element mid_plmn_info_list_r15_usage = doc.createElement("mid:plmn-info-list-r15-usage");
				mid_plmn_info_list_r15_usage.setTextContent("use");
				mid_eutran_cell_info.appendChild(mid_plmn_info_list_r15_usage);

				k--;
				a++;
			}
		}

		else {

			while (c != 0) {

				Element mid_term_point_to_gnb = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"mid:term-point-to-gnb");
				mid_term_point_to_gnb.setAttribute("nc:operation", "merge");
				mid_enb_function.appendChild(mid_term_point_to_gnb);

				Element mid_neighbor_gnb_index = doc.createElement("mid:neighbor-gnb-index");
				mid_neighbor_gnb_index.setTextContent(count.toString());
				mid_term_point_to_gnb.appendChild(mid_neighbor_gnb_index);
				Element mid_no_x2 = doc.createElement("mid:no-x2");
				mid_no_x2.setTextContent("false");
				mid_term_point_to_gnb.appendChild(mid_no_x2);

				Element mid_gnb_id_bit_length = doc.createElement("mid:gnb-id-bit-length");
				mid_gnb_id_bit_length.setTextContent("22");
				mid_term_point_to_gnb.appendChild(mid_gnb_id_bit_length);

				Element mid_gnb_id = doc.createElement("mid:gnb-id");
				mid_gnb_id.setTextContent(gnbid.get(count));
				mid_term_point_to_gnb.appendChild(mid_gnb_id);

				Element mid_mcc = doc.createElement("mid:mcc");
				mid_mcc.setTextContent("311");
				mid_term_point_to_gnb.appendChild(mid_mcc);

				Element mid_mnc = doc.createElement("mid:mnc");
				mid_mnc.setTextContent("480");
				mid_term_point_to_gnb.appendChild(mid_mnc);
				Element mid_ip_ver = doc.createElement("mid:ip-ver");

				mid_ip_ver.setTextContent("ipv6");
				mid_term_point_to_gnb.appendChild(mid_ip_ver);

				Element mid_neighbor_gnb_ipv6 = doc.createElement("mid:neighbor-gnb-ipv6");
				mid_neighbor_gnb_ipv6.setTextContent(ip.get(count++));
				mid_term_point_to_gnb.appendChild(mid_neighbor_gnb_ipv6);

				c--;
			}

			Element mid_enb_function2 = doc.createElement("mid:enb-function");
			mid_managed_element.appendChild(mid_enb_function2);

			Element mid_carrier_aggregation = doc.createElement("mid:carrier-aggregation");
			mid_enb_function2.appendChild(mid_carrier_aggregation);

			Element mid_ca_requested_frequency_band_info = doc.createElement("mid:ca-requested-frequency-band-info");
			mid_ca_requested_frequency_band_info.setAttribute("nc:operation", "merge");
			mid_carrier_aggregation.appendChild(mid_ca_requested_frequency_band_info);

			Element mid_requested_nr_band1 = doc.createElement("mid:requested-nr-band1");
			mid_requested_nr_band1.setTextContent("261");
			mid_ca_requested_frequency_band_info.appendChild(mid_requested_nr_band1);

			Element mid_requested_nr_band2 = doc.createElement("mid:requested-nr-band2");
			mid_requested_nr_band2.setTextContent("0");
			mid_ca_requested_frequency_band_info.appendChild(mid_requested_nr_band2);

//			Element mid_enb_function3 = doc.createElement("mid:enb-function");
//			mid_managed_element.appendChild(mid_enb_function3);
//
//			Element mid_new_radio_network = doc.createElement("mid:new-radio-network");
//			mid_enb_function3.appendChild(mid_new_radio_network);
//			Integer cntt;
//			cntt = 2;
//			for (Integer i = 0; i < cntt; i++) {
//
//				Element mid_dc_control_param = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
//						"mid:dc-control-param");
//				mid_dc_control_param.setAttribute("nc:operation", "merge");
//				mid_new_radio_network.appendChild(mid_dc_control_param);
//
//				Element mid_dc_index = doc.createElement("mid:dc-index");
//				mid_dc_index.setTextContent(i.toString());
//				mid_dc_control_param.appendChild(mid_dc_index);
//
//				Element mid_bearer_type_usage = doc.createElement("mid:bearer-type-usage");
//				mid_bearer_type_usage.setTextContent("use");
//				mid_dc_control_param.appendChild(mid_bearer_type_usage);
//
//				Element mid_qci = doc.createElement("mid:qci");
//				if (i == 0)
//					mid_qci.setTextContent("8");
//				if (i == 1)
//					mid_qci.setTextContent("9");
//				mid_dc_control_param.appendChild(mid_qci);
//
//				Element mid_arp = doc.createElement("mid:arp");
//
//				mid_arp.setTextContent("15");
//				mid_dc_control_param.appendChild(mid_arp);
//
//				Element mid_bearer_type = doc.createElement("mid:bearer-type");
//				mid_bearer_type.setTextContent("sn-term-split");
//				mid_dc_control_param.appendChild(mid_bearer_type);
//
//				Element mid_nr_band_freq = doc.createElement("mid:nr-band-freq");
//				mid_nr_band_freq.setTextContent("qciarp-fr2");
//				mid_dc_control_param.appendChild(mid_nr_band_freq);
//
//				Element mid_priority = doc.createElement("mid:priority");
//				mid_priority.setTextContent("7");
//				mid_dc_control_param.appendChild(mid_priority);
//			}

			Element mid_enb_function4 = doc.createElement("mid:enb-function");
			mid_managed_element.appendChild(mid_enb_function4);

			int a = 0;
			while (k != 0) {

				Element mid_eutran_generic_cell = doc.createElement("mid:eutran-generic-cell");
				mid_enb_function4.appendChild(mid_eutran_generic_cell);

				Element mid_eutran_cell_fdd_tdd = doc.createElement("mid:eutran-cell-fdd-tdd");
				mid_eutran_generic_cell.appendChild(mid_eutran_cell_fdd_tdd);

				Element mid_cell_num = doc.createElement("mid:cell-num");
				mid_cell_num.setTextContent(cell_id.get(a).toString());
				mid_eutran_cell_fdd_tdd.appendChild(mid_cell_num);

				Element mid_cell_endc_function = doc.createElement("mid:cell-endc-function");
				mid_eutran_cell_fdd_tdd.appendChild(mid_cell_endc_function);

				Element mid_endc_cell_info = doc.createElement("mid:endc-cell-info");
				mid_endc_cell_info.setAttribute("nc:operation", "merge");
				mid_cell_endc_function.appendChild(mid_endc_cell_info);

				Element mid_endc_support = doc.createElement("mid:endc-support");
				mid_endc_support.setTextContent("true");
				mid_endc_cell_info.appendChild(mid_endc_support);

				Element mid_endc_operation_mode = doc.createElement("mid:endc-operation-mode");
				mid_endc_operation_mode.setTextContent("endc-mode2");
				mid_endc_cell_info.appendChild(mid_endc_operation_mode);

				for (int i = 0; i < 2; i++) {

					Element mid_en_dc_meas_priority = doc.createElement("mid:en-dc-meas-priority");
					mid_en_dc_meas_priority.setAttribute("nc:operation", "merge");
					mid_cell_endc_function.appendChild(mid_en_dc_meas_priority);

					Element mid_non_gbr_qci = doc.createElement("mid:non-gbr-qci");
					if (i == 0)
						mid_non_gbr_qci.setTextContent("8");
					if (i == 1)
						mid_non_gbr_qci.setTextContent("9");
					mid_en_dc_meas_priority.appendChild(mid_non_gbr_qci);

					Element mid_nr_band_freq = doc.createElement("mid:nr-band-freq");
					mid_nr_band_freq.setTextContent("fr1-fr2");
					mid_en_dc_meas_priority.appendChild(mid_nr_band_freq);

					Element mid_priority = doc.createElement("mid:priority");
					mid_priority.setTextContent("7");
					mid_en_dc_meas_priority.appendChild(mid_priority);
				}

				Element mid_cell_plmn_info = doc.createElement("mid:cell-plmn-info");
				mid_cell_plmn_info.setAttribute("nc:operation", "merge");
				mid_eutran_cell_fdd_tdd.appendChild(mid_cell_plmn_info);

				Element mid_plmn_index = doc.createElement("mid:plmn-index");
				mid_plmn_index.setTextContent("0");
				mid_cell_plmn_info.appendChild(mid_plmn_index);

				Element mid_plmn_usage = doc.createElement("mid:plmn-usage");
				mid_plmn_usage.setTextContent("enable");
				mid_cell_plmn_info.appendChild(mid_plmn_usage);

				Element mid_upper_layer_indication = doc.createElement("mid:upper-layer-indication");
				mid_upper_layer_indication.setTextContent("enable");
				mid_cell_plmn_info.appendChild(mid_upper_layer_indication);

				Element mid_eutran_cell_info = doc.createElement("mid:eutran-cell-info");
				mid_eutran_cell_info.setAttribute("nc:operation", "merge");
				mid_eutran_cell_fdd_tdd.appendChild(mid_eutran_cell_info);

				Element mid_cell_size = doc.createElement("mid:cell-size");
				mid_cell_size.setTextContent("large");
				mid_eutran_cell_info.appendChild(mid_cell_size);

				Element mid_plmn_info_list_r15_usage = doc.createElement("mid:plmn-info-list-r15-usage");
				mid_plmn_info_list_r15_usage.setTextContent("use");
				mid_eutran_cell_info.appendChild(mid_plmn_info_list_r15_usage);

				Element mid_ue_measurement_control = doc.createElement("mid:ue-measurement-control");
				mid_eutran_cell_fdd_tdd.appendChild(mid_ue_measurement_control);

				for (Integer i = 0; i < 2; i++) {
					Element mid_nr_b1_criteria_info = doc.createElement("mid:nr-b1-criteria-info");
					mid_nr_b1_criteria_info.setAttribute("nc:operation", "merge");
					mid_ue_measurement_control.appendChild(mid_nr_b1_criteria_info);

					Element mid_purpose = doc.createElement("mid:purpose");
					mid_purpose.setTextContent("en-dc-addition");
					mid_nr_b1_criteria_info.appendChild(mid_purpose);

					Element mid_fa_index1 = doc.createElement("mid:fa-index");
					mid_fa_index1.setTextContent(i.toString());
					mid_nr_b1_criteria_info.appendChild(mid_fa_index1);

					Element mid_qci_group_index = doc.createElement("mid:qci-group-index");
					mid_qci_group_index.setTextContent("0");
					mid_nr_b1_criteria_info.appendChild(mid_qci_group_index);

					Element mid_b1_enable = doc.createElement("mid:b1-enable");
					mid_b1_enable.setTextContent("enable");
					mid_nr_b1_criteria_info.appendChild(mid_b1_enable);

					Element mid_b1_threshold_nr_rsrp = doc.createElement("mid:b1-threshold-nr-rsrp");
					mid_b1_threshold_nr_rsrp.setTextContent("51");
					mid_nr_b1_criteria_info.appendChild(mid_b1_threshold_nr_rsrp);

					Element mid_b1_threshold_nr_rsrq = doc.createElement("mid:b1-threshold-nr-rsrq");
					mid_b1_threshold_nr_rsrq.setTextContent("24");
					mid_nr_b1_criteria_info.appendChild(mid_b1_threshold_nr_rsrq);

					Element mid_b1_threshold_nr_sinr = doc.createElement("mid:b1-threshold-nr-sinr");
					mid_b1_threshold_nr_sinr.setTextContent("50");
					mid_nr_b1_criteria_info.appendChild(mid_b1_threshold_nr_sinr);

					Element mid_hysteresis = doc.createElement("mid:hysteresis");
					mid_hysteresis.setTextContent("0");
					mid_nr_b1_criteria_info.appendChild(mid_hysteresis);

					Element mid_time_to_trigger = doc.createElement("mid:time-to-trigger");
					mid_time_to_trigger.setTextContent("128ms");
					mid_nr_b1_criteria_info.appendChild(mid_time_to_trigger);

					Element mid_trigger_quantity = doc.createElement("mid:trigger-quantity");
					mid_trigger_quantity.setTextContent("nr-rsrp");
					mid_nr_b1_criteria_info.appendChild(mid_trigger_quantity);

					Element mid_max_report_rs_index = doc.createElement("mid:max-report-rs-index");
					mid_max_report_rs_index.setTextContent("7");
					mid_nr_b1_criteria_info.appendChild(mid_max_report_rs_index);

				}

				Element mid_nr_frequency_relation2 = doc.createElement("mid:nr-frequency-relation");
				mid_eutran_cell_fdd_tdd.appendChild(mid_nr_frequency_relation2);

				Element mid_nr_fa_information2 = doc.createElement("mid:nr-fa-information");
				mid_nr_fa_information2.setAttribute("nc:operation", "merge");
				mid_nr_frequency_relation2.appendChild(mid_nr_fa_information2);

				Element mid_fa_index2 = doc.createElement("mid:fa-index");
				if (nrfreq.equals("n261"))
					mid_fa_index2.setTextContent("0");
				else
					mid_fa_index2.setTextContent("1");

				mid_nr_fa_information2.appendChild(mid_fa_index2);
				Element mid_nr_fa_prior_info_func = doc.createElement("mid:nr-fa-prior-info-func");
				mid_nr_fa_prior_info_func.setAttribute("nc:operation", "merge");
				mid_nr_fa_information2.appendChild(mid_nr_fa_prior_info_func);
				Element mid_duplex_type = doc.createElement("mid:duplex-type");
				mid_duplex_type.setTextContent("tdd");
				mid_nr_fa_prior_info_func.appendChild(mid_duplex_type);
				Element mid_arfcn_nr_dl = doc.createElement("mid:arfcn-nr-dl");
				mid_arfcn_nr_dl.setTextContent(arfcn_dl);
				mid_nr_fa_prior_info_func.appendChild(mid_arfcn_nr_dl);
				Element mid_arfcn_nr_ul = doc.createElement("mid:arfcn-nr-ul");
				mid_arfcn_nr_ul.setTextContent(arfcn_ul);
				mid_nr_fa_prior_info_func.appendChild(mid_arfcn_nr_ul);
				Element mid_mtc_ssb_nr_period = doc.createElement("mid:mtc-ssb-nr-period");
				mid_mtc_ssb_nr_period.setTextContent("ssb-period-sf20");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_period);
				Element mid_mtc_ssb_nr_offset = doc.createElement("mid:mtc-ssb-nr-offset");
				mid_mtc_ssb_nr_offset.setTextContent("5");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_offset);
				Element mid_mtc_ssb_nr_duration = doc.createElement("mid:mtc-ssb-nr-duration");
				mid_mtc_ssb_nr_duration.setTextContent("ssb-duration-sf3");
				mid_nr_fa_prior_info_func.appendChild(mid_mtc_ssb_nr_duration);
				Element mid_sub_carrier_spacing_ssb = doc.createElement("mid:sub-carrier-spacing-ssb");
				mid_sub_carrier_spacing_ssb.setTextContent("scs-240khz");
				mid_nr_fa_prior_info_func.appendChild(mid_sub_carrier_spacing_ssb);
				Element mid_nr_band = doc.createElement("mid:nr-band");
				if (nrfreq.equals("n261"))
					mid_nr_band.setTextContent("261");
				else
					mid_nr_band.setTextContent("260");

				mid_nr_fa_prior_info_func.appendChild(mid_nr_band);
				Element mid_offset_freq = doc.createElement("mid:offset-freq");
				mid_offset_freq.setTextContent("0");
				mid_nr_fa_prior_info_func.appendChild(mid_offset_freq);

				k--;
				a++;

			}
			Element mid_new_radio_network1 = doc.createElement("mid:new-radio-network");
			mid_enb_function4.appendChild(mid_new_radio_network1);

			for (Integer i = 0; i < 5; i++) {

				Element mid_dc_control_param = doc.createElement("mid:dc-control-param");
				mid_dc_control_param.setAttribute("nc:operation", "merge");
				mid_new_radio_network1.appendChild(mid_dc_control_param);

				Element mid_dc_index = doc.createElement("mid:dc-index");
				mid_dc_index.setTextContent(i.toString());
				mid_dc_control_param.appendChild(mid_dc_index);

				Element mid_bearer_type_usage = doc.createElement("mid:bearer-type-usage");
				mid_bearer_type_usage.setTextContent("use");
				mid_dc_control_param.appendChild(mid_bearer_type_usage);

				Element mid_qci = doc.createElement("mid:qci");
				if (i == 0 || i == 2)
					mid_qci.setTextContent("8");
				if (i == 1 || i == 3)
					mid_qci.setTextContent("9");
				if (i ==4)
					mid_qci.setTextContent("7");
				mid_dc_control_param.appendChild(mid_qci);

				Element mid_arp = doc.createElement("mid:arp");
				if (i == 0 || i == 1 || i ==4)
					mid_arp.setTextContent("15");
				if (i == 2 || i == 3)
					mid_arp.setTextContent("12");
				mid_dc_control_param.appendChild(mid_arp);

				Element mid_bearer_type = doc.createElement("mid:bearer-type");
				mid_bearer_type.setTextContent("sn-term-split");
				mid_dc_control_param.appendChild(mid_bearer_type);

				Element mid_nr_band_freq = doc.createElement("mid:nr-band-freq");
				if(i == 0 || i == 1)
				mid_nr_band_freq.setTextContent("qciarp-not-use");
				if(i == 2 || i == 3)
				mid_nr_band_freq.setTextContent("qciarp-fr2");
				if(i == 4)
				mid_nr_band_freq.setTextContent("qciarp-fr1");
				mid_dc_control_param.appendChild(mid_nr_band_freq);

				Element mid_priority = doc.createElement("mid:priority");
				mid_priority.setTextContent("7");
				mid_dc_control_param.appendChild(mid_priority);
			}

		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);

		File xmlFile = new File(filepath + folderName);

		xmlFile.mkdir();
		xmlFile = new File(filepath + folderName + "/" + fileName);
		StreamResult result = new StreamResult(xmlFile);
		try {
			transformer.transform(source, result);
			writeXMLattribute(xmlFile);
			status = true;
		} catch (Exception ex) {
			logger.error("endc() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
		}

		if (validation.equals("RanConfig")) {
			File xmlFile1 = new File(filepath + "Test");
			xmlFile1.mkdir();
			xmlFile1 = new File(filepath + "Test" + "/" + endcUseCaseFileName);
			StreamResult result1 = new StreamResult(xmlFile1);
			try {
				transformer.transform(source, result1);
				status = true;
				writeXMLattribute(xmlFile1);
			} catch (Exception ex) {
				logger.error("endc() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}
		}

		return status;

	}

	@Override
	public JSONObject additionalTemplete(String version, String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks, String tempFolder, String date, String validation) {

		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId, enbName,
				dbcollectionFileName);
		String auid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEB_AU_ID").getHeaderValue();
		String nr_freq_band = listCIQDetailsModel.get(0).getCiqMap().get("NR FREQ BAND").getHeaderValue();
		// String f=Constants.SPECIFICTEMPLATE_PATH;
		String f = Constants.SPECIFICTEMPLATE_PATH_P2;
		String ip = null;
		String port = null;
		String value = null;
		if(version.contains("20C"))
		{

			if (auid.startsWith("0")) {
				value = auid.substring(1, 3);
			} else
				value = auid.substring(0, 2);
			String fileName = null;
			if (value.equals("56") || value.equals("59") || value.equals("60")) {
				Ip obj = rep.getip(value);
				if(obj== null) {
					obj=rep.getip("0");
				}
				ip = obj.getIp();
				port = obj.getPort();

				if (nr_freq_band.equals("n261")) {
					fileName = "20C-AU-param-config-NE-vDCM1-28ghz.xml";
				} else
					fileName = "20C-AU-param-config-NE-vDCM1-37-39ghz.xml";
			}
			if (value.equals("61") || value.equals("64") || value.equals("68")) {

				Ip obj = rep.getip(value);
				if(obj== null) {
					obj=rep.getip("0");
				}
				ip = obj.getIp();
				port = obj.getPort();

				if (nr_freq_band.equals("n261")) {
					fileName = "20C-AU-param-config-NE-vDCM2-28ghz.xml";
				} else
					fileName = "20C-AU-param-config-NE-vDCM2-37-39ghz.xml";

			}
			if (value.equals("70") || value.equals("71") || value.equals("72") || value.equals("73")) {

				Ip obj = rep.getip(value);
				if(obj== null) {
					obj=rep.getip("0");
				}
				ip = obj.getIp();
				port = obj.getPort();

				if (nr_freq_band.equals("n261")) {
					f = Constants.SPECIFICTEMPLATE_PATH_P2_DCM3;
					fileName = "20C-AU-param-config-UNY-vDCM3-28ghz.xml";
				} else
					fileName = "20C-AU-param-config-UNY-vDCM3-37-39ghz.xml";

			}
			if (value.equals("12")) {
				
				if (auid.startsWith("0")) {
					value = auid.substring(1, 4);
				} else
					value = auid.substring(0, 3);
				
				Ip obj = rep.getip(value);
				if(obj== null) {
					obj=rep.getip("0");
				}
				
				ip = obj.getIp();
				port = obj.getPort();
				// f=Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
				f = Constants.SPECIFICTEMPLATE_PATH_HOUSTON_P2;
				if (nr_freq_band.equals("n261")) {
					if (ciqFileName.contains("Houston"))
						fileName = "20C-AU-param-config-Houston-with-drb-rlc-added.xml";
					if (ciqFileName.contains("NOLA"))
						fileName = "20C-AU-param-config-Nola-with-drb-rlc-added.xml";
					if (ciqFileName.contains("Pensacola"))
						fileName = "20C-AU-param-config-Pensacola-with-drb-rlc-added.xml";

				} else
					fileName = "";
				
			}
			if (value.equals("36")) {

				Ip obj = rep.getip(value);
				if(obj== null) {
					obj=rep.getip("0");
				}
				ip = obj.getIp();
				port = obj.getPort();
				// f=Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
				f = Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
				if (nr_freq_band.equals("n261")) {
					fileName = "20C-AU-param-config-Sacramento-with-drb-rlc-added.xml";
				} else
					fileName = "";

			}
			String sheetName = "";
			if (f != null && fileName != null) {
				try {
					fileBuilder.setLength(0);
					fileBuilder.append(filePath);
					if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("XML")) {
						if (value.startsWith("36")) {
							status = addTempGeneration20AP1(fileName, f, port, ip, ciqFileName, enbId, enbName,
									dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString(), fileName, "", "",
									validation);
						} else
							status = addTempGeneration20AP2(fileName, f, port, ip, ciqFileName, enbId, enbName,
									dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString(), fileName, "", "",
									validation);

					}
				

				} catch (Exception e) {
					logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
				}

				finally {
					fileGenerateResult.put("status", status);
					fileGenerateResult.put("fileName", fileName);

				}
			}
			
		}else {
		if (auid.startsWith("0")) {
			value = auid.substring(1, 3);
		} else
			value = auid.substring(0, 2);
		String fileName = null;
		if (value.equals("56") || value.equals("59") || value.equals("60")) {
			Ip obj = rep.getip(value);
			if(obj== null) {
				obj=rep.getip("0");
			}
			ip = obj.getIp();
			port = obj.getPort();

			if (nr_freq_band.equals("n261")) {
				fileName = "20A-AU-param-config-NE-vDCM1-28ghz.xml";
			} else
				fileName = "20A-AU-param-config-NE-vDCM1-37-39ghz.xml";
		}
		if (value.equals("61") || value.equals("64") || value.equals("68")) {

			Ip obj = rep.getip(value);
			if(obj== null) {
				obj=rep.getip("0");
			}
			ip = obj.getIp();
			port = obj.getPort();

			if (nr_freq_band.equals("n261")) {
				fileName = "20A-AU-param-config-NE-vDCM2-28ghz.xml";
			} else
				fileName = "20A-AU-param-config-NE-vDCM2-37-39ghz.xml";

		}
		if (value.equals("70") || value.equals("71") || value.equals("72") || value.equals("73")) {

			Ip obj = rep.getip(value);
			if(obj== null) {
				obj=rep.getip("0");
			}
			ip = obj.getIp();
			port = obj.getPort();

			if (nr_freq_band.equals("n261")) {
				f = Constants.SPECIFICTEMPLATE_PATH_P2_DCM3;
				fileName = "20A-AU-param-config-UNY-vDCM3-28ghz.xml";
			} else
				fileName = "20A-AU-param-config-UNY-vDCM3-37-39ghz.xml";

		}
		if (value.equals("12")) {
			
			if (auid.startsWith("0")) {
				value = auid.substring(1, 4);
			} else
				value = auid.substring(0, 3);
			
			Ip obj = rep.getip(value);
			if(obj== null) {
				obj=rep.getip("0");
			}
			
			ip = obj.getIp();
			port = obj.getPort();
			// f=Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
			f = Constants.SPECIFICTEMPLATE_PATH_HOUSTON_P2;
			if (nr_freq_band.equals("n261")) {
				if (ciqFileName.contains("Houston"))
					fileName = "20A-AU-param-config-Houston-with-drb-rlc-added.xml";
				if (ciqFileName.contains("NOLA"))
					fileName = "20A-AU-param-config-Nola-with-drb-rlc-added.xml";
				if (ciqFileName.contains("Pensacola"))
					fileName = "20A-AU-param-config-Pensacola-with-drb-rlc-added.xml";

			} else
				fileName = "";
			
		}
		if (value.equals("36")) {

			Ip obj = rep.getip(value);
			if(obj== null) {
				obj=rep.getip("0");
			}
			ip = obj.getIp();
			port = obj.getPort();
			// f=Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
			f = Constants.SPECIFICTEMPLATE_PATH_HOUSTON;
			if (nr_freq_band.equals("n261")) {
				fileName = "20A-AU-param-config-Sacramento-with-drb-rlc-added.xml";
			} else
				fileName = "";

		}
		String sheetName = "";
		if (f != null && fileName != null) {
			try {
				fileBuilder.setLength(0);
				fileBuilder.append(filePath);
				if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("XML")) {
					if (value.startsWith("36")) {
						status = addTempGeneration20AP1(fileName, f, port, ip, ciqFileName, enbId, enbName,
								dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString(), fileName, "", "",
								validation);
					} else
						status = addTempGeneration20AP2(fileName, f, port, ip, ciqFileName, enbId, enbName,
								dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString(), fileName, "", "",
								validation);

				}
			

			} catch (Exception e) {
				logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
			}

			finally {
				fileGenerateResult.put("status", status);
				fileGenerateResult.put("fileName", fileName);

			}
		}
		}
		return fileGenerateResult;

	}

	public boolean addTempGeneration20AP1(String fileName, String f, String port, String ip, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			String filepath, String cslFileNames, String a, String b, String validation) {

		boolean status = false;
		String NR_ARFCN;
		int c = 0;
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC0 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC1 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC2 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC3 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC4 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC5 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC6 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC7 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbau_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", "gnbau:managed-element");
			nc_config.appendChild(gnbau_managed_element);

			Element gnbau_gnb_du_function = doc.createElement("gnbau:gnb-du-function");
			gnbau_managed_element.appendChild(gnbau_gnb_du_function);

			Element gnbau_gutran_du_cell = doc.createElement("gnbau:gutran-du-cell");
			gnbau_gnb_du_function.appendChild(gnbau_gutran_du_cell);

			for (int k = 0; k < c; k++) {
				String id = "${cell-num=" + k + "}";
				Element gnbau_gutran_du_cell_entries = doc.createElement("gnbau:gutran-du-cell-entries");
				gnbau_gutran_du_cell.appendChild(gnbau_gutran_du_cell_entries);

				Element gnbau_cell_identity = doc.createElement("gnbau:cell-identity");
				gnbau_cell_identity.setTextContent(id);
				gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_identity);

				if (fileName.contains("Sacramento")) {
					Element gnbau_nr_carrier_aggregation = doc.createElement("gnbau:nr-carrier-aggregation");
					gnbau_nr_carrier_aggregation.setAttribute("nc:operation", "merge");
					gnbau_gutran_du_cell_entries.appendChild(gnbau_nr_carrier_aggregation);

					Element gnbau_ca_available_type = doc.createElement("gnbau:ca-available-type");
					gnbau_ca_available_type.setTextContent("ca-on");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_ca_available_type);

					Element gnbau_p_cell_only_flag = doc.createElement("gnbau:p-cell-only-flag");
					gnbau_p_cell_only_flag.setTextContent("false");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_p_cell_only_flag);

					Element gnbau_max_dl_ca_cc_num = doc.createElement("gnbau:max-dl-ca-cc-num");
					gnbau_max_dl_ca_cc_num.setTextContent("8");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_max_dl_ca_cc_num);
				}

				Element gnbau_ul_power_control_config = doc.createElement("gnbau:ul-power-control-config");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_power_control_config);

				Element gnbau_ul_power_control_common_config = doc
						.createElement("gnbau:ul-power-control-common-config");
				gnbau_ul_power_control_common_config.setAttribute("nc:operation", "merge");
				gnbau_ul_power_control_config.appendChild(gnbau_ul_power_control_common_config);

				Element gnbau_p0_nominal_with_grant = doc.createElement("gnbau:p0-nominal-with-grant");
				gnbau_p0_nominal_with_grant.setTextContent("-76");
				gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_with_grant);

				Element gnbau_p0_nominal_pucch = doc.createElement("gnbau:p0-nominal-pucch");
				gnbau_p0_nominal_pucch.setTextContent("-80");
				gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_pucch);

				Element gnbau_ul_mimo_configuration = doc.createElement("gnbau:ul-mimo-configuration");
				gnbau_ul_mimo_configuration.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_mimo_configuration);

				Element gnbau_ul_su_mimo_switch = doc.createElement("gnbau:ul-su-mimo-switch");
				gnbau_ul_su_mimo_switch.setTextContent("on");
				gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_switch);

				if (fileName.contains("DCM1") || fileName.contains("DCM2") || fileName.contains("DCM3")) {
					Element gnbau_dynamic_srs_port_adaptation = doc.createElement("gnbau:dynamic-srs-port-adaptation");
					gnbau_dynamic_srs_port_adaptation.setTextContent("off");
					gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);

				} else if (fileName.contains("Sacramento")) {
					Element gnbau_dynamic_srs_port_adaptation = doc.createElement("gnbau:dynamic-srs-port-adaptation");
					gnbau_dynamic_srs_port_adaptation.setTextContent("off");
					gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);

					Element gnbau_ul_su_mimo_config = doc.createElement("gnbau:ul-su-mimo-config");
					gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
					gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);

					Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in = doc
							.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
					gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
					gnbau_ul_su_mimo_config.appendChild(gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);

				} else {
					Element gnbau_ul_su_mimo_config = doc.createElement("gnbau:ul-su-mimo-config");
					gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
					gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);

					Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in = doc
							.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
					gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
					gnbau_ul_su_mimo_config.appendChild(gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);
				}

				Element gnbau_beam_management = doc.createElement("gnbau:beam-management");
				gnbau_beam_management.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_beam_management);
				Element gnbau_dl_mu_mimo_switch = doc.createElement("gnbau:dl-mu-mimo-switch");
				gnbau_dl_mu_mimo_switch.setTextContent("on");
				gnbau_beam_management.appendChild(gnbau_dl_mu_mimo_switch);

				Element gnbau_ul_physical_resource_config = doc.createElement("gnbau:ul-physical-resource-config");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_physical_resource_config);

				Element gnbau_prach_config = doc.createElement("gnbau:prach-config");
				gnbau_prach_config.setAttribute("nc:operation", "merge");
				gnbau_ul_physical_resource_config.appendChild(gnbau_prach_config);

				Element gnbau_preamble_receiver_target_power = doc
						.createElement("gnbau:preamble-receiver-target-power");
				gnbau_preamble_receiver_target_power.setTextContent("-69");
				gnbau_prach_config.appendChild(gnbau_preamble_receiver_target_power);

				Element gnbau_cell_cac_info = doc.createElement("gnbau:cell-cac-info");
				gnbau_cell_cac_info.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_cac_info);
				Element gnbau_nsa_call_threshold = doc.createElement("gnbau:nsa-call-threshold");
				gnbau_nsa_call_threshold.setTextContent("25");
				gnbau_cell_cac_info.appendChild(gnbau_nsa_call_threshold);

				Element gnbau_drx_config_du_cell = doc.createElement("gnbau:drx-config-du-cell");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_drx_config_du_cell);
				Element gnbau_drx_profile_du = doc.createElement("gnbau:drx-profile-du");
				gnbau_drx_profile_du.setAttribute("nc:operation", "merge");
				gnbau_drx_config_du_cell.appendChild(gnbau_drx_profile_du);

				Element gnbau_drx_cycle = doc.createElement("gnbau:drx-cycle");
				gnbau_drx_cycle.setTextContent("drx-long-cycle-normal-ms160");
				gnbau_drx_profile_du.appendChild(gnbau_drx_cycle);

				Element gnbau_drx_on_duration_timer_msec_normal = doc
						.createElement("gnbau:drx-on-duration-timer-msec-normal");
				gnbau_drx_on_duration_timer_msec_normal.setTextContent("drx-on-duration-ms10");
				gnbau_drx_profile_du.appendChild(gnbau_drx_on_duration_timer_msec_normal);

				Element gnbau_drx_inactivity_timer_normal = doc.createElement("gnbau:drx-inactivity-timer-normal");
				gnbau_drx_inactivity_timer_normal.setTextContent("drx-inactivity-ms100");
				gnbau_drx_profile_du.appendChild(gnbau_drx_inactivity_timer_normal);

			}

			Element gnbau_gutran_du_qci = doc.createElement("gnbau:gutran-du-qci");
			gnbau_gnb_du_function.appendChild(gnbau_gutran_du_qci);
			Element gnbau_qci = doc.createElement("gnbau:qci");
			gnbau_qci.setTextContent("8");
			gnbau_gutran_du_qci.appendChild(gnbau_qci);
			Element gnbau_logical_channel_config = doc.createElement("gnbau:logical-channel-config");
			gnbau_logical_channel_config.setAttribute("nc:operation", "merge");
			gnbau_gutran_du_qci.appendChild(gnbau_logical_channel_config);
			Element gnbau_prioritised_bitrate = doc.createElement("gnbau:prioritised-bitrate");
			gnbau_prioritised_bitrate.setTextContent("prioritised-bit-rate-infinity");
			gnbau_logical_channel_config.appendChild(gnbau_prioritised_bitrate);
			if (fileName.contains("Sacramento")) {

				Element gnbau_rlc_functions = doc.createElement("gnbau:rlc-functions");
				gnbau_gnb_du_function.appendChild(gnbau_rlc_functions);
				Element gnbau_drb_rlc_info_func = doc.createElement("gnbau:drb-rlc-info-func");
				gnbau_drb_rlc_info_func.setAttribute("nc:operation", "merge");
				gnbau_rlc_functions.appendChild(gnbau_drb_rlc_info_func);
				Element gnbau_config_type = doc.createElement("gnbau:config-type");
				gnbau_config_type.setTextContent("A6G");
				gnbau_drb_rlc_info_func.appendChild(gnbau_config_type);
				Element gnbau_qci1 = doc.createElement("gnbau:qci");
				gnbau_qci1.setTextContent("132");
				gnbau_drb_rlc_info_func.appendChild(gnbau_qci1);

				Element gnbau_gnb_timer_poll_retransmit = doc.createElement("gnbau:gnb-timer-poll-retransmit");
				gnbau_gnb_timer_poll_retransmit.setTextContent("t-poll-retransmit-ms20");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_timer_poll_retransmit);

				Element gnbau_gnb_poll_pdu = doc.createElement("gnbau:gnb-poll-pdu");
				gnbau_gnb_poll_pdu.setTextContent("p128");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_poll_pdu);

				Element gnbau_gnb_poll_byte = doc.createElement("gnbau:gnb-poll-byte");
				gnbau_gnb_poll_byte.setTextContent("kB125");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_poll_byte);

				Element gnbau_gnb_t_reassembly = doc.createElement("gnbau:gnb-t-reassembly");
				gnbau_gnb_t_reassembly.setTextContent("ms15");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_t_reassembly);

				Element gnbau_gnb_max_retransmission_threshold = doc
						.createElement("gnbau:gnb-max-retransmission-threshold");
				gnbau_gnb_max_retransmission_threshold.setTextContent("t32");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_max_retransmission_threshold);

				Element gnbau_gnb_timer_status_prohibit = doc.createElement("gnbau:gnb-timer-status-prohibit");
				gnbau_gnb_timer_status_prohibit.setTextContent("ms10");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_timer_status_prohibit);

				Element gnbau_ue_timer_poll_retransmit = doc.createElement("gnbau:ue-timer-poll-retransmit");
				gnbau_ue_timer_poll_retransmit.setTextContent("t-poll-retransmit-ms20");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_timer_poll_retransmit);

				Element gnbau_ue_poll_pdu = doc.createElement("gnbau:ue-poll-pdu");
				gnbau_ue_poll_pdu.setTextContent("p128");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_poll_pdu);

				Element gnbau_ue_poll_byte = doc.createElement("gnbau:ue-poll-byte");
				gnbau_ue_poll_byte.setTextContent("kB125");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_poll_byte);

				Element gnbau_ue_t_reassembly = doc.createElement("gnbau:ue-t-reassembly");
				gnbau_ue_t_reassembly.setTextContent("ms10");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_t_reassembly);

				Element gnbau_ue_max_retransmission_threshold = doc
						.createElement("gnbau:ue-max-retransmission-threshold");
				gnbau_ue_max_retransmission_threshold.setTextContent("t32");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_max_retransmission_threshold);
				Element gnbau_ue_timer_status_prohibit = doc.createElement("gnbau:ue-timer-status-prohibit");
				gnbau_ue_timer_status_prohibit.setTextContent("ms10");
				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_timer_status_prohibit);

				Element gnbau_sn_field_length_ul_um = doc.createElement("gnbau:sn-field-length-ul-um");
				gnbau_sn_field_length_ul_um.setTextContent("sizeum12");
				gnbau_drb_rlc_info_func.appendChild(gnbau_sn_field_length_ul_um);
				Element sn_ield_length_ul_am = doc.createElement("gnbau:sn-field-length-ul-am");
				sn_ield_length_ul_am.setTextContent("sizeam18");
				gnbau_drb_rlc_info_func.appendChild(sn_ield_length_ul_am);
				Element gnbau_sn_field_length_dl_um = doc.createElement("gnbau:sn-field-length-dl-um");
				gnbau_sn_field_length_dl_um.setTextContent("sizeum12");
				gnbau_drb_rlc_info_func.appendChild(gnbau_sn_field_length_dl_um);
				Element sn_ield_length_dl_am = doc.createElement("gnbau:sn-field-length-dl-am");
				sn_ield_length_dl_am.setTextContent("sizeam18");
				gnbau_drb_rlc_info_func.appendChild(sn_ield_length_dl_am);

			}

			Element gnbau_common_management = doc.createElement("gnbau:common-management");
			gnbau_managed_element.appendChild(gnbau_common_management);
			Element gnbau_csl_configuration = doc.createElement("gnbau:csl-configuration");
			gnbau_common_management.appendChild(gnbau_csl_configuration);

			if (fileName.contains("DCM1") || fileName.contains("DCM2") || fileName.contains("DCM3")) {
				Element gnbau_csl_tce_ems_server = doc.createElement("gnbau:csl-tce-ems-server");
				gnbau_csl_tce_ems_server.setAttribute("nc:operation", "merge");
				gnbau_csl_configuration.appendChild(gnbau_csl_tce_ems_server);
				Element gnbau_csl_tce_ems_server_port = doc.createElement("gnbau:csl-tce-ems-server-port");
				gnbau_csl_tce_ems_server_port.setTextContent("50002");
				gnbau_csl_tce_ems_server.appendChild(gnbau_csl_tce_ems_server_port);
				Element gnbau_csl_tce_ems_option = doc.createElement("gnbau:csl-tce-ems-option");
				gnbau_csl_tce_ems_option.setTextContent("abnormal-call-only");
				gnbau_csl_tce_ems_server.appendChild(gnbau_csl_tce_ems_option);
			}

			Element gnbau_csl_tce_server = doc.createElement("gnbau:csl-tce-server");
			gnbau_csl_tce_server.setAttribute("nc:operation", "merge");
			gnbau_csl_configuration.appendChild(gnbau_csl_tce_server);
			Element gnbau_csl_tce_server_ip_address = doc.createElement("gnbau:csl-tce-server-ip-address");
			gnbau_csl_tce_server_ip_address.setTextContent(ip);
			gnbau_csl_tce_server.appendChild(gnbau_csl_tce_server_ip_address);
			Element gnbau_csl_tce_server_port = doc.createElement("gnbau:csl-tce-server-port");
			gnbau_csl_tce_server_port.setTextContent(port);
			gnbau_csl_tce_server.appendChild(gnbau_csl_tce_server_port);
			Element gnbau_csl_ce_option = doc.createElement("gnbau:csl-tce-option");
			gnbau_csl_ce_option.setTextContent("normal-and-abnormal-and-intra-ho-call");
			gnbau_csl_tce_server.appendChild(gnbau_csl_ce_option);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			// DOMSource source = new DOMSource(doc);
			StringBuilder sb = new StringBuilder();

			File xmlFile = new File(filepath + fileName);
			// File xmlFile = new
			// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			writeXMLattribute(xmlFile);
			status = true;
			if (validation.equals("RanConfig")) {
				File xmlFile1 = new File(filepath + "Test");
				xmlFile1.mkdir();
				xmlFile1 = new File(filepath + "Test" + "/" + fileName);
				// File xmlFile = new
				// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
				StreamResult result1 = new StreamResult(xmlFile1);
				transformer.transform(source, result1);
				writeXMLattribute(xmlFile1);
				status = true;

			}

		} catch (Exception e) {
			logger.error("additionalTempGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	public boolean addTempGeneration20AP2(String fileName, String f, String port, String ip, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			String filepath, String cslFileNames, String a, String b, String validation) {

		boolean status = false;
		String NR_ARFCN;
		int c = 0;
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC0 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC1 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC2 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC3 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC4 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC5 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC6 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}
		if (listCIQDetailsModel.get(0).getCiqMap().containsKey("CC7 NR ARFCN")) {
			if ((listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue()) == null
					|| listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue().isEmpty())
				NR_ARFCN = "TBD";
			else
				NR_ARFCN = (listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue());
			if (!NR_ARFCN.equals("TBD"))
				c++;
		}

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbau_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", "gnbau:managed-element");
			nc_config.appendChild(gnbau_managed_element);

			Element gnbau_gnb_du_function = doc.createElement("gnbau:gnb-du-function");
			gnbau_managed_element.appendChild(gnbau_gnb_du_function);

			Element gnbau_gutran_du_cell = doc.createElement("gnbau:gutran-du-cell");
			gnbau_gnb_du_function.appendChild(gnbau_gutran_du_cell);

			for (int k = 0; k < c; k++) {
				String id = "${cell-num=" + k + "}";
				Element gnbau_gutran_du_cell_entries = doc.createElement("gnbau:gutran-du-cell-entries");
				gnbau_gutran_du_cell.appendChild(gnbau_gutran_du_cell_entries);

				Element gnbau_cell_identity = doc.createElement("gnbau:cell-identity");
				gnbau_cell_identity.setTextContent(id);
				gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_identity);

				if (fileName.contains("Sacramento")) {
					Element gnbau_nr_carrier_aggregation = doc.createElement("gnbau:nr-carrier-aggregation");
					gnbau_nr_carrier_aggregation.setAttribute("nc:operation", "merge");
					gnbau_gutran_du_cell_entries.appendChild(gnbau_nr_carrier_aggregation);

					Element gnbau_ca_available_type = doc.createElement("gnbau:ca-available-type");
					gnbau_ca_available_type.setTextContent("ca-on");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_ca_available_type);

					Element gnbau_p_cell_only_flag = doc.createElement("gnbau:p-cell-only-flag");
					gnbau_p_cell_only_flag.setTextContent("false");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_p_cell_only_flag);

					Element gnbau_max_dl_ca_cc_num = doc.createElement("gnbau:max-dl-ca-cc-num");
					gnbau_max_dl_ca_cc_num.setTextContent("8");
					gnbau_nr_carrier_aggregation.appendChild(gnbau_max_dl_ca_cc_num);
				}

				Element gnbau_ul_power_control_config = doc.createElement("gnbau:ul-power-control-config");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_power_control_config);

				Element gnbau_ul_power_control_common_config = doc
						.createElement("gnbau:ul-power-control-common-config");
				gnbau_ul_power_control_common_config.setAttribute("nc:operation", "merge");
				gnbau_ul_power_control_config.appendChild(gnbau_ul_power_control_common_config);

				Element gnbau_p0_nominal_with_grant = doc.createElement("gnbau:p0-nominal-with-grant");
				gnbau_p0_nominal_with_grant.setTextContent("-76");
				gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_with_grant);

				Element gnbau_p0_nominal_pucch = doc.createElement("gnbau:p0-nominal-pucch");
				gnbau_p0_nominal_pucch.setTextContent("-80");
				gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_pucch);

				Element gnbau_ul_mimo_configuration = doc.createElement("gnbau:ul-mimo-configuration");
				gnbau_ul_mimo_configuration.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_mimo_configuration);

				Element gnbau_ul_su_mimo_switch = doc.createElement("gnbau:ul-su-mimo-switch");
				gnbau_ul_su_mimo_switch.setTextContent("on");
				gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_switch);

				if (fileName.contains("DCM1") || fileName.contains("DCM2") || fileName.contains("DCM3")) {
					Element gnbau_dynamic_srs_port_adaptation = doc.createElement("gnbau:dynamic-srs-port-adaptation");
					gnbau_dynamic_srs_port_adaptation.setTextContent("off");
					gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);

				} else {
					Element gnbau_dynamic_srs_port_adaptation = doc.createElement("gnbau:dynamic-srs-port-adaptation");
					gnbau_dynamic_srs_port_adaptation.setTextContent("off");
					gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);

					Element gnbau_ul_su_mimo_config = doc.createElement("gnbau:ul-su-mimo-config");
					gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
					gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);

					Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in = doc
							.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
					gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
					gnbau_ul_su_mimo_config.appendChild(gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);

				}

				Element gnbau_beam_management = doc.createElement("gnbau:beam-management");
				gnbau_beam_management.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_beam_management);
				Element gnbau_dl_mu_mimo_switch = doc.createElement("gnbau:dl-mu-mimo-switch");
				gnbau_dl_mu_mimo_switch.setTextContent("on");
				gnbau_beam_management.appendChild(gnbau_dl_mu_mimo_switch);

				Element gnbau_ul_physical_resource_config = doc.createElement("gnbau:ul-physical-resource-config");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_physical_resource_config);

				Element gnbau_prach_config = doc.createElement("gnbau:prach-config");
				gnbau_prach_config.setAttribute("nc:operation", "merge");
				gnbau_ul_physical_resource_config.appendChild(gnbau_prach_config);

				Element gnbau_preamble_receiver_target_power = doc
						.createElement("gnbau:preamble-receiver-target-power");
				gnbau_preamble_receiver_target_power.setTextContent("-69");
				gnbau_prach_config.appendChild(gnbau_preamble_receiver_target_power);

				Element gnbau_cell_cac_info = doc.createElement("gnbau:cell-cac-info");
				gnbau_cell_cac_info.setAttribute("nc:operation", "merge");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_cac_info);
				Element gnbau_nsa_call_threshold = doc.createElement("gnbau:nsa-call-threshold");
				gnbau_nsa_call_threshold.setTextContent("25");
				gnbau_cell_cac_info.appendChild(gnbau_nsa_call_threshold);

				Element gnbau_drx_config_du_cell = doc.createElement("gnbau:drx-config-du-cell");
				gnbau_gutran_du_cell_entries.appendChild(gnbau_drx_config_du_cell);
				Element gnbau_drx_profile_du = doc.createElement("gnbau:drx-profile-du");
				gnbau_drx_profile_du.setAttribute("nc:operation", "merge");
				gnbau_drx_config_du_cell.appendChild(gnbau_drx_profile_du);

				Element gnbau_drx_cycle = doc.createElement("gnbau:drx-cycle");
				gnbau_drx_cycle.setTextContent("drx-long-cycle-normal-ms160");
				gnbau_drx_profile_du.appendChild(gnbau_drx_cycle);

				Element gnbau_drx_on_duration_timer_msec_normal = doc
						.createElement("gnbau:drx-on-duration-timer-msec-normal");
				gnbau_drx_on_duration_timer_msec_normal.setTextContent("drx-on-duration-ms10");
				gnbau_drx_profile_du.appendChild(gnbau_drx_on_duration_timer_msec_normal);

				Element gnbau_drx_inactivity_timer_normal = doc.createElement("gnbau:drx-inactivity-timer-normal");
				gnbau_drx_inactivity_timer_normal.setTextContent("drx-inactivity-ms100");
				gnbau_drx_profile_du.appendChild(gnbau_drx_inactivity_timer_normal);

			}

			Element gnbau_gutran_du_qci = doc.createElement("gnbau:gutran-du-qci");
			gnbau_gnb_du_function.appendChild(gnbau_gutran_du_qci);
			Element gnbau_qci = doc.createElement("gnbau:qci");
			gnbau_qci.setTextContent("8");
			gnbau_gutran_du_qci.appendChild(gnbau_qci);
			Element gnbau_logical_channel_config = doc.createElement("gnbau:logical-channel-config");
			gnbau_logical_channel_config.setAttribute("nc:operation", "merge");
			gnbau_gutran_du_qci.appendChild(gnbau_logical_channel_config);
			Element gnbau_prioritised_bitrate = doc.createElement("gnbau:prioritised-bitrate");
			gnbau_prioritised_bitrate.setTextContent("prioritised-bit-rate-infinity");
			gnbau_logical_channel_config.appendChild(gnbau_prioritised_bitrate);
			for (int i = 0; i < 3; i++) {
				Element gnbau_rlc_functions = doc.createElement("gnbau:rlc-functions");
				gnbau_gnb_du_function.appendChild(gnbau_rlc_functions);
				Element gnbau_drb_rlc_info_func = doc.createElement("gnbau:drb-rlc-info-func");
				gnbau_drb_rlc_info_func.setAttribute("nc:operation", "merge");
				gnbau_rlc_functions.appendChild(gnbau_drb_rlc_info_func);
				Element gnbau_config_type = doc.createElement("gnbau:config-type");
				gnbau_config_type.setTextContent("A6G");
				gnbau_drb_rlc_info_func.appendChild(gnbau_config_type);
				Element gnbau_qci1 = doc.createElement("gnbau:qci");
				if (i == 0)
					gnbau_qci1.setTextContent("8");
				if (i == 1)
					gnbau_qci1.setTextContent("9");
				if (i == 2)
					gnbau_qci1.setTextContent("132");
				gnbau_drb_rlc_info_func.appendChild(gnbau_qci1);
				Element gnbau_gnb_timer_poll_retransmit = doc.createElement("gnbau:gnb-timer-poll-retransmit");
				if (fileName.contains("DCM3-28ghz") && i == 0)
					gnbau_gnb_timer_poll_retransmit.setTextContent("t-poll-retransmit-ms20");
				else
					gnbau_gnb_timer_poll_retransmit.setTextContent("t-poll-retransmit-ms30");
				gnbau_drb_rlc_info_func.appendChild(gnbau_gnb_timer_poll_retransmit);
				Element gnbau_ue_t_reassembly = doc.createElement("gnbau:ue-t-reassembly");
				if (fileName.contains("DCM3-28ghz") && i == 0)
					gnbau_ue_t_reassembly.setTextContent("ms10");
				else
					gnbau_ue_t_reassembly.setTextContent("ms15");

				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_t_reassembly);
				Element gnbau_ue_timer_status_prohibit = doc.createElement("gnbau:ue-timer-status-prohibit");
				if (fileName.contains("DCM3-28ghz") && i == 0)
					gnbau_ue_timer_status_prohibit.setTextContent("ms10");
				else
					gnbau_ue_timer_status_prohibit.setTextContent("ms15");

				gnbau_drb_rlc_info_func.appendChild(gnbau_ue_timer_status_prohibit);

			}
			Element gnbau_common_management = doc.createElement("gnbau:common-management");
			gnbau_managed_element.appendChild(gnbau_common_management);
			Element gnbau_csl_configuration = doc.createElement("gnbau:csl-configuration");
			gnbau_common_management.appendChild(gnbau_csl_configuration);

			if (fileName.contains("DCM1") || fileName.contains("DCM2") || fileName.contains("DCM3")) {

				Element gnbau_csl_tce_ems_server = doc.createElement("gnbau:csl-tce-ems-server");
				gnbau_csl_tce_ems_server.setAttribute("nc:operation", "merge");
				gnbau_csl_configuration.appendChild(gnbau_csl_tce_ems_server);

				Element gnbau_csl_tce_ems_server_port = doc.createElement("gnbau:csl-tce-ems-server-port");
				gnbau_csl_tce_ems_server_port.setTextContent("50002");
				gnbau_csl_tce_ems_server.appendChild(gnbau_csl_tce_ems_server_port);

				Element gnbau_csl_tce_ems_option = doc.createElement("gnbau:csl-tce-ems-option");
				gnbau_csl_tce_ems_option.setTextContent("abnormal-call-only");
				gnbau_csl_tce_ems_server.appendChild(gnbau_csl_tce_ems_option);
			}

			Element gnbau_csl_tce_server = doc.createElement("gnbau:csl-tce-server");
			gnbau_csl_tce_server.setAttribute("nc:operation", "merge");
			gnbau_csl_configuration.appendChild(gnbau_csl_tce_server);
			Element gnbau_csl_tce_server_ip_address = doc.createElement("gnbau:csl-tce-server-ip-address");
			gnbau_csl_tce_server_ip_address.setTextContent(ip);
			gnbau_csl_tce_server.appendChild(gnbau_csl_tce_server_ip_address);
			Element gnbau_csl_tce_server_port = doc.createElement("gnbau:csl-tce-server-port");
			gnbau_csl_tce_server_port.setTextContent(port);
			gnbau_csl_tce_server.appendChild(gnbau_csl_tce_server_port);
			Element gnbau_csl_ce_option = doc.createElement("gnbau:csl-tce-option");
			gnbau_csl_ce_option.setTextContent("normal-and-abnormal-and-intra-ho-call");
			gnbau_csl_tce_server.appendChild(gnbau_csl_ce_option);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			// DOMSource source = new DOMSource(doc);
			StringBuilder sb = new StringBuilder();

			File xmlFile = new File(filepath + fileName);
			// File xmlFile = new
			// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			writeXMLattribute(xmlFile);
			status = true;
			if (validation.equals("RanConfig")) {
				File xmlFile1 = new File(filepath + "Test");
				xmlFile1.mkdir();
				xmlFile1 = new File(filepath + "Test" + "/" + fileName);
				// File xmlFile = new
				// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
				StreamResult result1 = new StreamResult(xmlFile1);
				transformer.transform(source, result1);
				writeXMLattribute(xmlFile1);
				status = true;

			}

		} catch (Exception e) {
			logger.error("additionalTempGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}
	// public boolean addTempGeneration20AP1(String fileName,String f,String
	// port,String ip,String ciqFileName,String enbId,String enbName,String
	// dbcollectionFileName,List<CIQDetailsModel> listCIQDetailsModel,String
	// filepath,String cslFileNames,String a,String b,String validation) {
	//
	// boolean status = false;
	// String NR_ARFCN;
	// int c=0;
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC0 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC0
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC1 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC1
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC2 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC2
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC3 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC3
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC4 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC4
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC5 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR
	// ARFCN").getHeaderValue())==null||listCIQDetailsModel.get(0).getCiqMap().get("CC5
	// NR ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC6 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR
	// ARFCN").getHeaderValue())==null ||
	// listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR
	// ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC7 NR ARFCN")) {
	// if(( listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR
	// ARFCN").getHeaderValue())==null
	// ||listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR
	// ARFCN").getHeaderValue().isEmpty())
	// NR_ARFCN ="TBD";
	// else
	// NR_ARFCN =( listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR
	// ARFCN").getHeaderValue());
	// if(!NR_ARFCN.equals("TBD"))
	// c++;
	// }
	// try {
	// File file = new File(getClass().getClassLoader().getResource(f).getFile());
	// DocumentBuilder db =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder();
	// Document doc = db.parse(file);
	// doc.getDocumentElement().normalize();
	//
	// NodeList element=doc.getElementsByTagName("*");
	//
	//
	// for (int j = 0; j < element.getLength(); j++) {
	// Node node = element.item(j);
	//
	//
	// if (node.getNodeName().equals("gnbau:gutran-du-cell")) {
	// for(int k=0;k<c;k++) {
	// String id= "${cell-num="+k+"}";
	// Element gnbau_gutran_du_cell_entries =
	// doc.createElement("gnbau:gutran-du-cell-entries");
	// node.appendChild(gnbau_gutran_du_cell_entries);
	//
	// Element gnbau_cell_identity = doc.createElement("gnbau:cell-identity");
	// gnbau_cell_identity.setTextContent(id);
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_identity);
	//
	// if(fileName.contains("Sacramento")) {
	// Element gnbau_nr_carrier_aggregation=
	// doc.createElement("gnbau:nr-carrier-aggregation");
	// gnbau_nr_carrier_aggregation.setAttribute("nc:operation", "merge");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_nr_carrier_aggregation);
	//
	//
	// Element gnbau_ca_available_type=
	// doc.createElement("gnbau:ca-available-type");
	// gnbau_ca_available_type.setTextContent("ca-on");
	// gnbau_nr_carrier_aggregation.appendChild(gnbau_ca_available_type);
	//
	// Element gnbau_p_cell_only_flag= doc.createElement("gnbau:p-cell-only-flag");
	// gnbau_p_cell_only_flag.setTextContent("false");
	// gnbau_nr_carrier_aggregation.appendChild(gnbau_p_cell_only_flag);
	//
	// Element gnbau_max_dl_ca_cc_num= doc.createElement("gnbau:max-dl-ca-cc-num");
	// gnbau_max_dl_ca_cc_num.setTextContent("8");
	// gnbau_nr_carrier_aggregation.appendChild(gnbau_max_dl_ca_cc_num);
	// }
	//
	// Element gnbau_ul_power_control_config =
	// doc.createElement("gnbau:ul-power-control-config");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_power_control_config);
	//
	// Element gnbau_ul_power_control_common_config =
	// doc.createElement("gnbau:ul-power-control-common-config");
	// gnbau_ul_power_control_common_config.setAttribute("nc:operation", "merge");
	// gnbau_ul_power_control_config.appendChild(gnbau_ul_power_control_common_config);
	//
	// Element gnbau_p0_nominal_with_grant =
	// doc.createElement("gnbau:p0-nominal-with-grant");
	// gnbau_p0_nominal_with_grant.setTextContent("-76");
	// gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_with_grant);
	//
	// Element gnbau_p0_nominal_pucch = doc.createElement("gnbau:p0-nominal-pucch");
	// gnbau_p0_nominal_pucch.setTextContent("-80");
	// gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_pucch);
	//
	// Element gnbau_ul_mimo_configuration =
	// doc.createElement("gnbau:ul-mimo-configuration");
	// gnbau_ul_mimo_configuration.setAttribute("nc:operation", "merge");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_mimo_configuration);
	//
	// Element gnbau_ul_su_mimo_switch =
	// doc.createElement("gnbau:ul-su-mimo-switch");
	// gnbau_ul_su_mimo_switch.setTextContent("on");
	// gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_switch);
	//
	// if(fileName.contains("DCM1")||
	// fileName.contains("DCM2")||fileName.contains("DCM3")) {
	// Element gnbau_dynamic_srs_port_adaptation =
	// doc.createElement("gnbau:dynamic-srs-port-adaptation");
	// gnbau_dynamic_srs_port_adaptation.setTextContent("off");
	// gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);
	//
	// }
	// else if(fileName.contains("Sacramento")) {
	// Element gnbau_dynamic_srs_port_adaptation =
	// doc.createElement("gnbau:dynamic-srs-port-adaptation");
	// gnbau_dynamic_srs_port_adaptation.setTextContent("off");
	// gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);
	//
	// Element gnbau_ul_su_mimo_config =
	// doc.createElement("gnbau:ul-su-mimo-config");
	// gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
	// gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);
	//
	// Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in =
	// doc.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
	// gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
	// gnbau_ul_su_mimo_config.appendChild(gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);
	//
	// }
	// else {
	// Element gnbau_ul_su_mimo_config =
	// doc.createElement("gnbau:ul-su-mimo-config");
	// gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
	// gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);
	//
	// Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in =
	// doc.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
	// gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
	// gnbau_ul_su_mimo_config.appendChild(gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);
	// }
	//
	// Element gnbau_beam_management = doc.createElement("gnbau:beam-management");
	// gnbau_beam_management.setAttribute("nc:operation", "merge");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_beam_management);
	// Element gnbau_dl_mu_mimo_switch =
	// doc.createElement("gnbau:dl-mu-mimo-switch");
	// gnbau_dl_mu_mimo_switch.setTextContent("on");
	// gnbau_beam_management.appendChild(gnbau_dl_mu_mimo_switch);
	//
	// Element gnbau_ul_physical_resource_config =
	// doc.createElement("gnbau:ul-physical-resource-config");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_physical_resource_config);
	//
	// Element gnbau_prach_config = doc.createElement("gnbau:prach-config");
	// gnbau_prach_config.setAttribute("nc:operation", "merge");
	// gnbau_ul_physical_resource_config.appendChild(gnbau_prach_config);
	//
	// Element gnbau_preamble_receiver_target_power =
	// doc.createElement("gnbau:preamble-receiver-target-power");
	// gnbau_preamble_receiver_target_power.setTextContent("-69");
	// gnbau_prach_config.appendChild(gnbau_preamble_receiver_target_power);
	//
	//
	// Element gnbau_cell_cac_info = doc.createElement("gnbau:cell-cac-info");
	// gnbau_cell_cac_info.setAttribute("nc:operation", "merge");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_cac_info);
	// Element gnbau_nsa_call_threshold =
	// doc.createElement("gnbau:nsa-call-threshold");
	// gnbau_nsa_call_threshold.setTextContent("25");
	// gnbau_cell_cac_info.appendChild(gnbau_nsa_call_threshold);
	//
	//
	// Element gnbau_drx_config_du_cell =
	// doc.createElement("gnbau:drx-config-du-cell");
	// gnbau_gutran_du_cell_entries.appendChild(gnbau_drx_config_du_cell);
	// Element gnbau_drx_profile_du = doc.createElement("gnbau:drx-profile-du");
	// gnbau_drx_profile_du.setAttribute("nc:operation", "merge");
	// gnbau_drx_config_du_cell.appendChild(gnbau_drx_profile_du);
	//
	// Element gnbau_drx_cycle = doc.createElement("gnbau:drx-cycle");
	// gnbau_drx_cycle.setTextContent("drx-long-cycle-normal-ms160");
	// gnbau_drx_profile_du.appendChild(gnbau_drx_cycle);
	//
	// Element gnbau_drx_on_duration_timer_msec_normal =
	// doc.createElement("gnbau:drx-on-duration-timer-msec-normal");
	// gnbau_drx_on_duration_timer_msec_normal.setTextContent("drx-on-duration-ms10");
	// gnbau_drx_profile_du.appendChild(gnbau_drx_on_duration_timer_msec_normal);
	//
	// Element gnbau_drx_inactivity_timer_normal =
	// doc.createElement("gnbau:drx-inactivity-timer-normal");
	// gnbau_drx_inactivity_timer_normal.setTextContent("drx-inactivity-ms100");
	// gnbau_drx_profile_du.appendChild(gnbau_drx_inactivity_timer_normal);
	//
	// }
	//
	// }
	// if (node.getNodeName().equals("gnbau:csl-tce-server-ip-address")) {
	//
	// node.getFirstChild().setNodeValue(ip) ;
	// }
	// if (node.getNodeName().equals("gnbau:csl-tce-server-port")) {
	// node.getFirstChild().setNodeValue(port) ;
	// }
	//
	//
	// }
	// TransformerFactory transformerFactory = TransformerFactory.newInstance();
	// Transformer transformer = transformerFactory.newTransformer();
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
	// "2");
	// DOMSource source = new DOMSource(doc);
	// // DOMSource source = new DOMSource(doc);
	// StringBuilder sb = new StringBuilder();
	//
	// File xmlFile = new File(filepath+fileName);
	// //File xmlFile = new
	// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
	// StreamResult result = new StreamResult(xmlFile);
	// transformer.transform(source, result);
	// writeXMLattribute(xmlFile);
	// status = true;
	// if(validation.equals("RanConfig"))
	// {
	// File xmlFile1 = new File(filepath+"Test");
	// xmlFile1.mkdir();
	// xmlFile1 = new File(filepath+"Test"+"/"+fileName);
	// //File xmlFile = new
	// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
	// StreamResult result1 = new StreamResult(xmlFile1);
	// transformer.transform(source, result1);
	// writeXMLattribute(xmlFile1);
	// status = true;
	//
	// }
	//
	//
	// }
	// catch (Exception e) {
	// logger.error("routeFileGeneration() GenerateCsvServiceImpl" +
	// ExceptionUtils.getFullStackTrace(e));
	// }
	// return status;
	//
	//
	// }

	/*
	 * public boolean addTempGeneration20AP2(String fileName,String f,String
	 * port,String ip,String ciqFileName,String enbId,String enbName,String
	 * dbcollectionFileName,List<CIQDetailsModel> listCIQDetailsModel,String
	 * filepath,String cslFileNames,String a,String b,String validation) {
	 * 
	 * boolean status = false; String NR_ARFCN; int c=0;
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC0 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue().
	 * isEmpty() ) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC1 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC2 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC3 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC4 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC5 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC6 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; }
	 * if(listCIQDetailsModel.get(0).getCiqMap().containsKey("CC7 NR ARFCN")) { if((
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue())=
	 * =null ||
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue().
	 * isEmpty()) NR_ARFCN ="TBD"; else NR_ARFCN =(
	 * listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR ARFCN").getHeaderValue());
	 * if(!NR_ARFCN.equals("TBD")) c++; } try { File file = new
	 * File(getClass().getClassLoader().getResource(f).getFile()); DocumentBuilder
	 * db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); Document doc
	 * = db.parse(file); doc.getDocumentElement().normalize();
	 * 
	 * NodeList element=doc.getElementsByTagName("*");
	 * 
	 * 
	 * for (int j = 0; j < element.getLength(); j++) { Node node = element.item(j);
	 * 
	 * 
	 * if (node.getNodeName().equals("gnbau:gutran-du-cell")) { for(int k=0;k<c;k++)
	 * { String id= "${cell-num="+k+"}"; Element gnbau_gutran_du_cell_entries =
	 * doc.createElement("gnbau:gutran-du-cell-entries");
	 * node.appendChild(gnbau_gutran_du_cell_entries);
	 * 
	 * Element gnbau_cell_identity = doc.createElement("gnbau:cell-identity");
	 * gnbau_cell_identity.setTextContent(id);
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_identity);
	 * 
	 * if(fileName.contains("Sacramento")) { Element gnbau_nr_carrier_aggregation=
	 * doc.createElement("gnbau:nr-carrier-aggregation");
	 * gnbau_nr_carrier_aggregation.setAttribute("nc:operation", "merge");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_nr_carrier_aggregation);
	 * 
	 * 
	 * Element gnbau_ca_available_type=
	 * doc.createElement("gnbau:ca-available-type");
	 * gnbau_ca_available_type.setTextContent("ca-on");
	 * gnbau_nr_carrier_aggregation.appendChild(gnbau_ca_available_type);
	 * 
	 * Element gnbau_p_cell_only_flag= doc.createElement("gnbau:p-cell-only-flag");
	 * gnbau_p_cell_only_flag.setTextContent("false");
	 * gnbau_nr_carrier_aggregation.appendChild(gnbau_p_cell_only_flag);
	 * 
	 * Element gnbau_max_dl_ca_cc_num= doc.createElement("gnbau:max-dl-ca-cc-num");
	 * gnbau_max_dl_ca_cc_num.setTextContent("8");
	 * gnbau_nr_carrier_aggregation.appendChild(gnbau_max_dl_ca_cc_num); } Element
	 * gnbau_ul_power_control_config =
	 * doc.createElement("gnbau:ul-power-control-config");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_power_control_config);
	 * 
	 * Element gnbau_ul_power_control_common_config =
	 * doc.createElement("gnbau:ul-power-control-common-config");
	 * gnbau_ul_power_control_common_config.setAttribute("nc:operation", "merge");
	 * gnbau_ul_power_control_config.appendChild(
	 * gnbau_ul_power_control_common_config);
	 * 
	 * Element gnbau_p0_nominal_with_grant =
	 * doc.createElement("gnbau:p0-nominal-with-grant");
	 * gnbau_p0_nominal_with_grant.setTextContent("-76");
	 * gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_with_grant)
	 * ;
	 * 
	 * Element gnbau_p0_nominal_pucch = doc.createElement("gnbau:p0-nominal-pucch");
	 * gnbau_p0_nominal_pucch.setTextContent("-80");
	 * gnbau_ul_power_control_common_config.appendChild(gnbau_p0_nominal_pucch);
	 * 
	 * Element gnbau_ul_mimo_configuration =
	 * doc.createElement("gnbau:ul-mimo-configuration");
	 * gnbau_ul_mimo_configuration.setAttribute("nc:operation", "merge");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_mimo_configuration);
	 * 
	 * Element gnbau_ul_su_mimo_switch =
	 * doc.createElement("gnbau:ul-su-mimo-switch");
	 * gnbau_ul_su_mimo_switch.setTextContent("on");
	 * gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_switch);
	 * if(fileName.contains("DCM1")||
	 * fileName.contains("DCM2")||fileName.contains("DCM3")) { Element
	 * gnbau_dynamic_srs_port_adaptation =
	 * doc.createElement("gnbau:dynamic-srs-port-adaptation");
	 * gnbau_dynamic_srs_port_adaptation.setTextContent("off");
	 * gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);
	 * 
	 * } else { Element gnbau_dynamic_srs_port_adaptation =
	 * doc.createElement("gnbau:dynamic-srs-port-adaptation");
	 * gnbau_dynamic_srs_port_adaptation.setTextContent("off");
	 * gnbau_ul_mimo_configuration.appendChild(gnbau_dynamic_srs_port_adaptation);
	 * 
	 * Element gnbau_ul_su_mimo_config =
	 * doc.createElement("gnbau:ul-su-mimo-config");
	 * gnbau_ul_su_mimo_config.setAttribute("nc:operation", "merge");
	 * gnbau_ul_mimo_configuration.appendChild(gnbau_ul_su_mimo_config);
	 * 
	 * Element gnbau_ul_su_mimo_phr_rb_threshold_rank2_in =
	 * doc.createElement("gnbau:ul-su-mimo-phr-rb-threshold-rank2-in");
	 * gnbau_ul_su_mimo_phr_rb_threshold_rank2_in.setTextContent("1");
	 * gnbau_ul_su_mimo_config.appendChild(
	 * gnbau_ul_su_mimo_phr_rb_threshold_rank2_in);
	 * 
	 * }
	 * 
	 * 
	 * Element gnbau_beam_management = doc.createElement("gnbau:beam-management");
	 * gnbau_beam_management.setAttribute("nc:operation", "merge");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_beam_management); Element
	 * gnbau_dl_mu_mimo_switch = doc.createElement("gnbau:dl-mu-mimo-switch");
	 * gnbau_dl_mu_mimo_switch.setTextContent("on");
	 * gnbau_beam_management.appendChild(gnbau_dl_mu_mimo_switch);
	 * 
	 * Element gnbau_ul_physical_resource_config =
	 * doc.createElement("gnbau:ul-physical-resource-config");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_ul_physical_resource_config);
	 * 
	 * Element gnbau_prach_config = doc.createElement("gnbau:prach-config");
	 * gnbau_prach_config.setAttribute("nc:operation", "merge");
	 * gnbau_ul_physical_resource_config.appendChild(gnbau_prach_config);
	 * 
	 * Element gnbau_preamble_receiver_target_power =
	 * doc.createElement("gnbau:preamble-receiver-target-power");
	 * gnbau_preamble_receiver_target_power.setTextContent("-69");
	 * gnbau_prach_config.appendChild(gnbau_preamble_receiver_target_power);
	 * 
	 * 
	 * Element gnbau_cell_cac_info = doc.createElement("gnbau:cell-cac-info");
	 * gnbau_cell_cac_info.setAttribute("nc:operation", "merge");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_cell_cac_info); Element
	 * gnbau_nsa_call_threshold = doc.createElement("gnbau:nsa-call-threshold");
	 * gnbau_nsa_call_threshold.setTextContent("25");
	 * gnbau_cell_cac_info.appendChild(gnbau_nsa_call_threshold);
	 * 
	 * 
	 * Element gnbau_drx_config_du_cell =
	 * doc.createElement("gnbau:drx-config-du-cell");
	 * gnbau_gutran_du_cell_entries.appendChild(gnbau_drx_config_du_cell); Element
	 * gnbau_drx_profile_du = doc.createElement("gnbau:drx-profile-du");
	 * gnbau_drx_profile_du.setAttribute("nc:operation", "merge");
	 * gnbau_drx_config_du_cell.appendChild(gnbau_drx_profile_du);
	 * 
	 * Element gnbau_drx_cycle = doc.createElement("gnbau:drx-cycle");
	 * gnbau_drx_cycle.setTextContent("drx-long-cycle-normal-ms160");
	 * gnbau_drx_profile_du.appendChild(gnbau_drx_cycle);
	 * 
	 * Element gnbau_drx_on_duration_timer_msec_normal =
	 * doc.createElement("gnbau:drx-on-duration-timer-msec-normal");
	 * gnbau_drx_on_duration_timer_msec_normal.setTextContent("drx-on-duration-ms10"
	 * ); gnbau_drx_profile_du.appendChild(gnbau_drx_on_duration_timer_msec_normal);
	 * 
	 * Element gnbau_drx_inactivity_timer_normal =
	 * doc.createElement("gnbau:drx-inactivity-timer-normal");
	 * gnbau_drx_inactivity_timer_normal.setTextContent("drx-inactivity-ms100");
	 * gnbau_drx_profile_du.appendChild(gnbau_drx_inactivity_timer_normal);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * if (node.getNodeName().equals("gnbau:csl-tce-server-ip-address")) {
	 * 
	 * node.getFirstChild().setNodeValue(ip) ; } if
	 * (node.getNodeName().equals("gnbau:csl-tce-server-port")) {
	 * node.getFirstChild().setNodeValue(port) ; }
	 * 
	 * 
	 * } TransformerFactory transformerFactory = TransformerFactory.newInstance();
	 * Transformer transformer = transformerFactory.newTransformer();
	 * transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	 * transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
	 * "2"); DOMSource source = new DOMSource(doc); // DOMSource source = new
	 * DOMSource(doc); StringBuilder sb = new StringBuilder();
	 * 
	 * File xmlFile = new File(filepath+fileName); //File xmlFile = new File(
	 * "/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001"
	 * ); StreamResult result = new StreamResult(xmlFile);
	 * transformer.transform(source, result); writeXMLattribute(xmlFile); status =
	 * true; if(validation.equals("RanConfig")) { File xmlFile1 = new
	 * File(filepath+"Test"); xmlFile1.mkdir(); xmlFile1 = new
	 * File(filepath+"Test"+"/"+fileName); //File xmlFile = new File(
	 * "/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001"
	 * ); StreamResult result1 = new StreamResult(xmlFile1);
	 * transformer.transform(source, result1); writeXMLattribute(xmlFile1); status =
	 * true;
	 * 
	 * }
	 * 
	 * 
	 * } catch (Exception e) {
	 * logger.error("routeFileGeneration() GenerateCsvServiceImpl" +
	 * ExceptionUtils.getFullStackTrace(e)); } return status;
	 * 
	 * 
	 * }
	 * 
	 */

	// for 5g route generation
	@Override
	public JSONObject routeFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation) {
		boolean status = false;
		String timestamp = null;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		StringBuilder fileBuilders = new StringBuilder();

		String routeFileName = "";
		String routeFileNames = "";
		String sheetName = "";
		String enb = enbId;
		StringBuilder temp = new StringBuilder();
		temp.append(tempFolder);

		try {
			
			String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
					.toString();
			if (version.contains("19")) {
				version = "19A";
			}
			if (version.contains("20.A")) {
				version = "20A";
			}
			if (version.contains("20.B")) {
				version = "20B";
			}
			
			if (version.contains("20.C")) {
				version = "20C";
			}
			if (version.contains("21.A")) {
				version = "21A";
			} else if(version.contains("21.B")) {
				version = "21B";
			} else if(version.contains("21.C")) {
				version = "21C";
			}else if(version.contains("21.D")) {
				version = "21D";
			} else if(version.contains("22.A")) {
				version = "22A";
			}else if(version.contains("22.C")) {
				version = "22C";
			} 
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String auid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEB_AU_ID").getHeaderValue();
			// List<GrowConstantsEntity> objListProgDetails =
			// growConstantsRepository.getGrowConstantsDetails();
			String auType="";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
			{
				auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue().trim();
			}
			String siteType = "";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("Site_Type")) {
				siteType = listCIQDetailsModel.get(0).getCiqMap().get("Site_Type").getHeaderValue();
			}
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("XML")) {
				String DAS = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId, "DAS");
				routeFileName = "AU_ROUTE_"+auType+"_" + enb + "_" + timeStamp + ".xml";
				String routeFileName1 = "AU_ROUTE_"+auType+"_" + enb + "_" + dateString + ".xml";
				//routeFileNames = "AU_ROUTE_OFFSET_" + enb + "_" + timeStamp + ".xml";

				fileBuilder.append(routeFileName);
				temp.setLength(0);
				temp.append(filePath);
				// temp.append(routeFileName1);
				if(siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))) {
					status = getIAURouteFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), routeFileName, temp.toString(), date, validation,version);
				} else {
					status = getRouteFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), routeFileName, temp.toString(), date, validation,version);
				}
				
				
			}

		} catch (Exception e) {
			logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", routeFileName);

		}
		return fileGenerateResult;
	}
	
	@Override
	public JSONObject gpScriptFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation) {
		ArrayList<String> fileNameList = new ArrayList<>();
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		StringBuilder testFolder = new StringBuilder();
		StringBuilder actualFile = new StringBuilder();
		String market = "";
		String nrfreq = "";
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		try {
			String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
					.toString();
			if (version.contains("20.A")) {
				version = "20A";
			}else if (version.contains("20.C")) {
				version = "20C";
			}
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String[] cellKeys = {"CC0 Cell Identity", "CC1 Cell Identity", "CC2 Cell Identity", "CC3 Cell Identity", "CC4 Cell Identity",
					"CC5 Cell Identity", "CC6 Cell Identity", "CC7 Cell Identity"};
			Set<Integer> cellIdSet = new HashSet<>();
			if (!ObjectUtils.isEmpty(listCIQDetailsModel)) {
				CIQDetailsModel ciqDetailsModel = listCIQDetailsModel.get(0);
				LinkedHashMap<String, CiqMapValuesModel> objMapDetails = ciqDetailsModel.getCiqMap();
				for(String cellKey : cellKeys) {
					if(objMapDetails.containsKey(cellKey)) {
						String cellId = objMapDetails.get(cellKey).getHeaderValue();
						if(NumberUtils.isNumber(cellId)) {
							cellIdSet.add(NumberUtils.toInt(cellId));
						}
					}
				}	
				if(objMapDetails.containsKey("NR FREQ BAND")) {
					nrfreq = objMapDetails.get("NR FREQ BAND").getHeaderValue();
				}
				if(objMapDetails.containsKey("Market")) {
					market = objMapDetails.get("Market").getHeaderValue();
				}
			}
			StringBuilder gpscriptFilePath = new StringBuilder();
			gpscriptFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")).append("GP_Scripts").append(Constants.SEPARATOR);
			String subFoldername = "";
			if(cellIdSet.size() == 4) {
				subFoldername = "4CC";
			} else if(cellIdSet.size() == 6) {
				subFoldername = "6CC";
			} else if(cellIdSet.size() == 7) {
				subFoldername = "7CC";
			} else if(cellIdSet.size() == 8) {
				subFoldername = "8CC";
			}
			if(version.equals("20C")) {
				gpscriptFilePath.append("20C_" + subFoldername);
			} else {
				gpscriptFilePath.append(subFoldername);
			}
			subFoldername = version + "_" + subFoldername;
			testFolder.append(filePath).append("Test");
			actualFile.append(filePath);
			File gpscriptFolder = new File(gpscriptFilePath.toString());
			if(gpscriptFolder.exists()) {
				File[] gpscriptFileList = gpscriptFolder.listFiles();
				for(File gpScriptFile : gpscriptFileList) {
					String scriptFilePath = gpScriptFile.getAbsolutePath();
					String scriptFileName = gpScriptFile.getName();
					String actualFilename = "";
					String testFilename = "";
					String actualFilePath = "";
					String testFilePath = "";
					if(scriptFileName.contains("MAIN_GP")) {
						actualFilename = "AU_GPScript_" + "MAIN_GP" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
						testFilename = "AU_GPScript_" + "MAIN_GP" + "_" + subFoldername + ".xml";
						actualFilePath = actualFile.toString() + actualFilename;
						testFilePath = testFolder.toString() + Constants.SEPARATOR + testFilename;
						FileUtils.copyFile(new File(scriptFilePath), new File(actualFilePath));
						FileUtils.copyFile(new File(scriptFilePath), new File(testFilePath));
						fileNameList.add(actualFilename);
					} else if(scriptFileName.contains("CA")) {
						actualFilename = "AU_GPScript_" + "CA" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
						testFilename = "AU_GPScript_" + "CA" + "_" + subFoldername + ".xml";
						actualFilePath = actualFile.toString() + actualFilename;
						testFilePath = testFolder.toString() + Constants.SEPARATOR + testFilename;
						FileUtils.copyFile(new File(scriptFilePath), new File(actualFilePath));
						FileUtils.copyFile(new File(scriptFilePath), new File(testFilePath));
						fileNameList.add(actualFilename);
					} else if(scriptFileName.contains("BAND_COMBINATION")) {
						boolean copyStatus = false;
						if(nrfreq.equalsIgnoreCase("n261") && scriptFileName.contains("28")) {
							if(subFoldername.contains("6CC")){
								if(market.equalsIgnoreCase("Providence") || market.equalsIgnoreCase("HartFord")) {
									if(scriptFileName.contains("3b")) {
										actualFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_28Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
										testFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_28Ghz" + "_" + subFoldername + ".xml";
										copyStatus = true;
									}										
								} else if(scriptFileName.contains("3a")){
									actualFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_28Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
									testFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_28Ghz" + "_" + subFoldername + ".xml";
									copyStatus = true;	
								}
							} else if(subFoldername.contains("8CC") && scriptFileName.contains("3a")) {
								actualFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_28Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
								testFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_28Ghz" + "_" + subFoldername + ".xml";
								copyStatus = true;
							} else if(subFoldername.contains("4CC") || subFoldername.contains("7CC")){
								actualFilename = "AU_GPScript_" + "3_BAND_COMBINATION_28Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
								testFilename = "AU_GPScript_" + "3_BAND_COMBINATION_28Ghz" + "_" + subFoldername + ".xml";
								copyStatus = true;
							}
						} else if(nrfreq.equalsIgnoreCase("n260") && scriptFileName.contains("37")) {
							if(subFoldername.contains("6CC")){
								if(market.equalsIgnoreCase("Providence") || market.equalsIgnoreCase("HartFord")) {
									if(scriptFileName.contains("3b")) {
										actualFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_37Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
										testFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_37Ghz" + "_" + subFoldername + ".xml";
										copyStatus = true;
									}									
								} else if(scriptFileName.contains("3a")){
									actualFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_37Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
									testFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_37Ghz" + "_" + subFoldername + ".xml";
									copyStatus = true;	
								}
							} else if(subFoldername.contains("8CC")) {
								if(market.toUpperCase().startsWith("OPW")) {
									if(scriptFileName.contains("3b")) {
										actualFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_37Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
										testFilename = "AU_GPScript_" + "3b_BAND_COMBINATION_37Ghz" + "_" + subFoldername + ".xml";
										copyStatus = true;
									}									
								} else if(scriptFileName.contains("3a")){
									actualFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_37Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
									testFilename = "AU_GPScript_" + "3a_BAND_COMBINATION_37Ghz" + "_" + subFoldername + ".xml";
									copyStatus = true;	
								}
							} else if(subFoldername.contains("4CC") || subFoldername.contains("7CC")){
								actualFilename = "AU_GPScript_" + "3_BAND_COMBINATION_37Ghz" + "_" + subFoldername + "_" + enbId + "_" +timeStamp + ".xml";
								testFilename = "AU_GPScript_" + "3_BAND_COMBINATION_37Ghz" + "_" + subFoldername + ".xml";
								copyStatus = true;
							}
						}
						
						if(copyStatus) {
							System.out.println(scriptFileName);
							actualFilePath = actualFile.toString() + actualFilename;
							testFilePath = testFolder.toString() + Constants.SEPARATOR + testFilename;
							FileUtils.copyFile(new File(scriptFilePath), new File(actualFilePath));
							FileUtils.copyFile(new File(scriptFilePath), new File(testFilePath));
							fileNameList.add(actualFilename);
						}
					}
				}
				status = true;
			} else {
				logger.error("gpScriptFileGeneration() GenerateCsvServiceImpl : No GpScripts found");
			}
		} catch (Exception e) {
			logger.error("gpScriptFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", fileNameList);

		}
		return fileGenerateResult;
	}

	@Override
	public JSONObject cslTemplete(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, Set<String> neList, String tempFolder, String date, String validation) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		boolean anchor = false;
		StringBuilder anchorTemp = new StringBuilder();
		String folderName = "Anchor_" + enbId + "_" + timeStamp;
	
		List<String> fileNames = new ArrayList<>();
		String sheetName = "";
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			
			String auType="";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
			{
				auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
			}
			List<String> ls = new ArrayList<String>(Arrays.asList("AU_CSL_"+auType+"_" + enbId + "_" + timeStamp + ".xml",
					"ACPF_CSL_" + enbId + "_" + timeStamp + ".xml", "AUPF_CSL_" + enbId + "_" + timeStamp + ".xml",
					"ANCHOR_CSL_" + enbId + "_" + timeStamp + ".xml"));
			List<String> ls1 = new ArrayList<String>(Arrays.asList("AU_CSL_" + enbId + ".xml", "ACPF_CSL_" + enbId + ".xml",
					"AUPF_CSL_" + enbId + ".xml", "ANCHOR_CSL_" + enbId + ".xml"));
			
			
			String auid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEB_AU_ID").getHeaderValue();
			List<GrowConstantsEntity> objListProgDetails = growConstantsRepository.getGrowConstantsDetails();
			fileBuilder.setLength(0);
			fileBuilder.append(filePath.toString());
			if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("XML")) {
				temp.append(fileBuilder);
				anchorTemp.append(fileBuilder);
				for (int i = 0; i < 4; i++) {
					String cslFileName1 = ls.get(i);
					String ancFile = ls1.get(i);
					/*
					 * if (i > 0) { fileBuilder.setLength(0); fileBuilder.append(temp); }
					 */
					if (i == 3) {
						for (String neid : neList) {
							if (neid.startsWith("0"))
								neid = neid.substring(1, neid.length());
							anchor = true;
							String aName = "ANCHOR_CSL_" + enbId + "_" + neid + "_" + timeStamp + ".xml";
							String tempaName = "ANCHOR_CSL_" + enbId + "_" + neid + ".xml";

							status = cslGeneration(ciqFileName, enbId, enbName, dbcollectionFileName,
									listCIQDetailsModel, fileBuilder.toString(), i, cslFileName1, neList, tempaName,
									aName, folderName, validation);
						}
						if (anchor == true)
							fileNames.add(folderName);
					} else {
						status = cslGeneration(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
								fileBuilder.toString(), i, cslFileName1, neList, ancFile, "", " ", validation);
						fileNames.add(cslFileName1);
					}
				}
			}
		} catch (Exception e) {
			logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", fileNames);
		}
		return fileGenerateResult;
	}

	// for 5g A1_A2 generation
	@Override
	public JSONObject a1a2ConfigFileGeneration(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String remarks,
			String tempFolder, String date, String validation) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		String a1a2FileName = "";
		String sheetName = "";
		String gnodebid;
		String enb = enbId;
		int set;
		StringBuilder temp = new StringBuilder();
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String auid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEB_AU_ID").getHeaderValue();
			gnodebid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();

			// List<GrowConstantsEntity> objListProgDetails =
			// growConstantsRepository.getGrowConstantsDetails();
			
			String auType="";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
			{
				auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
			}
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			temp.append(tempFolder);
			if (CommonUtil.isValidObject(ciqFileName)) {
				a1a2FileName = "AU_A1A2_Config_" + version +"_"+auType+ "_" + enb + "_" + timeStamp + ".xml";
				String a1a2FileName1 = "AU_A1A2_Config_" + version  +"_"+auType+ "_" + enb + "_" + dateString + ".xml";
				fileBuilder.append(a1a2FileName);
				// temp.append(a1a2FileName1);
				temp.setLength(0);
				temp.append(filePath);

				if (version.equalsIgnoreCase("20A")) {
					status = getA1A2ConfigFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), a1a2FileName, temp.toString(), date, validation);
				}else if (version.equalsIgnoreCase("20C")) {
					status = getA1A2ConfigFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), a1a2FileName, temp.toString(), date, validation);
				}
			}

		} catch (Exception e) {
			logger.error("a1a2ConfigFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", a1a2FileName);

		}
		return fileGenerateResult;
	}

	@Override
	public JSONObject a1a2CreateFileGeneration(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String remarks,
			String tempFolder, String date, String validation) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		String a1a2FileName = "";
		String sheetName = "";
		String gnodebid;
		String enb = enbId;
		StringBuilder temp = new StringBuilder();
		temp.append(tempFolder);

		int set = 0;
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			gnodebid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();
			int gid = Integer.valueOf(gnodebid);
			// if(gid==72)
			// {
			// set = 8;
			// }
			// else if((gid==56) || (gid==129))
			// {
			// set = 6;
			// }
			String auid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEB_AU_ID").getHeaderValue();
			List<GrowConstantsEntity> objListProgDetails = growConstantsRepository.getGrowConstantsDetails();
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			
			String auType="";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
			{
				auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
			}
			if (CommonUtil.isValidObject(ciqFileName)) {
				String DAS = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId, "DAS");
				a1a2FileName = "AU_A1A2_Create_" + version +"_"+auType+ "_" +enb + "_" + timeStamp + ".xml";
				String a1a2FileName1 = "AU_A1A2_Create_" + version+"_"+auType+ "_" + enb + "_" + dateString + ".xml";
				fileBuilder.append(a1a2FileName);
				temp.setLength(0);
				temp.append(filePath);
				// temp.append(a1a2FileName1);
				if (version.equalsIgnoreCase("20A")) {
					status = getA1A2CreateFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), a1a2FileName, temp.toString(), date, validation);
				}else if (version.equalsIgnoreCase("20C")) {
					status = getA1A2CreateFile(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
							fileBuilder.toString(), a1a2FileName, temp.toString(), date, validation);
				}
			}

		} catch (Exception e) {
			logger.error("a1a2CreateFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", a1a2FileName);

		}
		return fileGenerateResult;
	}

	
	// method for offset generation file
		public boolean getRouteFiles(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
				List<CIQDetailsModel> listCIQDetailsModel, String filepath, String routeFileName, String temp, String date,
				String validation,String version) {
			boolean status = false;
			try {
				String auType="";
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
				{
					auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
				}
				String CC0_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC0_Offset").getHeaderValue();
				String CC1_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC1_Offset").getHeaderValue();
				String CC2_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC2_Offset").getHeaderValue();
				String CC3_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC3_Offset").getHeaderValue();
				String CC4_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC4_Offset").getHeaderValue();
				String CC5_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC5_Offset").getHeaderValue();
				String CC6_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC6_Offset").getHeaderValue();
				String CC7_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC7_Offset").getHeaderValue();
				Integer value=(CC0_Offset.isEmpty()||CC0_Offset.contains("TBD"))?0:((CC1_Offset.isEmpty()||CC1_Offset.contains("TBD"))?1:(CC2_Offset.isEmpty()||CC2_Offset.contains("TBD"))?2:(CC3_Offset.isEmpty()||CC3_Offset.contains("TBD"))?3:(CC4_Offset.isEmpty()||CC4_Offset.contains("TBD"))?4:(CC5_Offset.isEmpty()||CC5_Offset.contains("TBD"))?5:(CC6_Offset.isEmpty()||CC6_Offset.contains("TBD"))?6:(CC7_Offset.isEmpty()||CC7_Offset.contains("TBD"))?7:8);
				String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
				String msg1_frequency_start=listCIQDetailsModel.get(0).getCiqMap().get("msg1_frequency_start").getHeaderValue();
					
				
				ArrayList<String> as= new ArrayList<>();
				as.add(CC0_Offset);
				as.add(CC1_Offset);
				as.add(CC2_Offset);
				as.add(CC3_Offset);
				as.add(CC4_Offset);
				as.add(CC5_Offset);
				as.add(CC6_Offset);
				as.add(CC7_Offset);
				// gnodebid = gnodebid.substring(1);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();

				Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
				// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
				doc.appendChild(nc_rpc);

				Element nc_edit_config = doc.createElement("nc:edit-config");
				nc_rpc.appendChild(nc_edit_config);

				Element nc_target = doc.createElement("nc:target");
				nc_edit_config.appendChild(nc_target);

				Element running = doc.createElement("nc:running");
				nc_target.appendChild(running);
				//running.setTextContent(" ");

				Element nc_default_operation = doc.createElement("nc:default-operation");
				nc_edit_config.appendChild(nc_default_operation);
				nc_default_operation.appendChild(doc.createTextNode("none"));

				Element nc_config = doc.createElement("nc:config");
				nc_edit_config.appendChild(nc_config);

				Element gnbcp_managed_element = doc.createElementNS(
						"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", "gnbau:managed-element");
				nc_config.appendChild(gnbcp_managed_element);

				Element gnb_cu_cp_function = doc.createElement("gnbau:gnb-du-function");
				gnbcp_managed_element.appendChild(gnb_cu_cp_function);

				Element gnb_cu_cp_function_cell = doc.createElement("gnbau:gutran-du-cell");
				gnb_cu_cp_function.appendChild(gnb_cu_cp_function_cell);
				for(int i=0;i<value;i++) {
				
				Element gnb_cu_cp_function_entries = doc.createElement("gnbau:gutran-du-cell-entries");
				gnb_cu_cp_function_cell.appendChild(gnb_cu_cp_function_entries);

				Element gnodeb_id = doc.createElement("gnbau:cell-identity");
				gnb_cu_cp_function_entries.appendChild(gnodeb_id);
				String s= "${cell-num="+Integer.toString(i)+"}";
				gnodeb_id.setTextContent(s);

				Element gutran_cu_cell = doc.createElement("gnbau:cell-num");
				gnb_cu_cp_function_entries.appendChild(gutran_cu_cell);
				gutran_cu_cell.setTextContent(Integer.toString(i));
				if ("20C".equalsIgnoreCase(version)) {
				Element physicalResource = doc.createElement("gnbau:ul-physical-resource-config");
				gnb_cu_cp_function_entries.appendChild(physicalResource);
				Element prach_config = doc.createElement("gnbau:prach-config");
				physicalResource.appendChild(prach_config);
				prach_config.setAttribute("nc:operation", "merge");
				Element msg1_frequency_starts = doc.createElement("gnbau:msg1-frequency-start");
				prach_config.appendChild(msg1_frequency_starts);
				msg1_frequency_starts.setTextContent(msg1_frequency_start);
				physicalResource.appendChild(prach_config);
				}
				Element ssb_configuration = doc.createElement("gnbau:ssb-configuration");
				ssb_configuration.setAttribute("nc:operation", "merge");
				gnb_cu_cp_function_entries.appendChild(ssb_configuration);
				Element freq_offset = doc.createElement("gnbau:ssb-freq-offset");
				ssb_configuration.appendChild(freq_offset);
				freq_offset.setTextContent(as.get(i));
				gnb_cu_cp_function_entries.appendChild(ssb_configuration);
				Element config_mode = doc.createElement("gnbau:ssb-freq-config-mode");
				ssb_configuration.appendChild(config_mode);
				config_mode.setTextContent("gscn-based");
				
				}

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource source = new DOMSource(doc);
				String name = "/AU_SSB_offset_"+auType+"_" + Integer.toString(value)+"CC.xml";
				if (validation.equals("RanConfig")) {
					File xmlFile1 = new File(temp + "Test");
					xmlFile1.mkdir();
					StreamResult result1 = new StreamResult(xmlFile1 + name);

					try {
						transformer.transform(source, result1);
						String xmlFile3 = xmlFile1.toString() + name;
						File xmlFile2 = new File(xmlFile3);
						writeXMLattribute(xmlFile2);
						status = true;

					} catch (Exception ex) {
						logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
					}

				}
				File xmlFile = new File(filepath);
				StreamResult result = new StreamResult(xmlFile);
				// StreamResult result = new StreamResult(new
				// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
				try {
					transformer.transform(source, result);
				} catch (Exception ex) {
					logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}
				writeXMLattribute(xmlFile);
				status = true;
			} catch (Exception e) {
				logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
			}
			return status;
		}		
	// method for route generation file
	public boolean getRouteFile(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String routeFileName, String temp, String date,
			String validation,String version) {
		boolean status = false;
		String value = null;
		
		if (enbId.startsWith("0")) {
			value = enbId.substring(1, 3);
		} else
			value = enbId.substring(0, 2);
		
		Ip obj = rep.getip(value);
		if(obj== null) {
			if (enbId.startsWith("0")) {
				value = enbId.substring(1, 4);
			} else
				value = enbId.substring(0, 3);	
			obj=rep.getip(value);
			if(obj == null)
				obj=rep.getip("0");
		}
		
		try {
			String oam_gateway = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Gateway").getHeaderValue();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbau_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", "gnbau:managed-element");
			nc_config.appendChild(gnbau_managed_element);

			Element ip_system = doc.createElement("gnbau:ip-system");
			gnbau_managed_element.appendChild(ip_system);

			Element cpu = doc.createElement("gnbau:cpu");
			ip_system.appendChild(cpu);

			Element cpu_id = doc.createElement("gnbau:cpu-id");
			cpu.appendChild(cpu_id);
			cpu_id.setTextContent("0");

			Element ip_route = doc.createElement("gnbau:ip-route");
			cpu.appendChild(ip_route);

			Element ipv6_route = doc.createElement("gnbau:ipv6-route");
			ip_route.appendChild(ipv6_route);

			Element ipv6_static_route = doc.createElement("gnbau:ipv6-static-route");
			ipv6_static_route.setAttribute("nc:operation", "merge");
			ipv6_route.appendChild(ipv6_static_route);
			
			if ("20C".equalsIgnoreCase(version) || "21A".equalsIgnoreCase(version)
					|| "21B".equalsIgnoreCase(version) || "21C".equalsIgnoreCase(version)) {
				Element gnbau_vr_id = doc.createElement("gnbau:vr-id");
				gnbau_vr_id.setTextContent("0");
				ipv6_static_route.appendChild(gnbau_vr_id);
			}

			Element prefix = doc.createElement("gnbau:prefix");
			prefix.setTextContent(obj.getPrefix());
			ipv6_static_route.appendChild(prefix);

			Element gateway = doc.createElement("gnbau:gateway");
			gateway.setTextContent(oam_gateway);
			ipv6_static_route.appendChild(gateway);

			Element interface_name = doc.createElement("gnbau:interface-name");
			interface_name.setTextContent("-");
			ipv6_static_route.appendChild(interface_name);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			if (validation.equals("RanConfig")) {

				File xmlFile1 = new File(temp + "Test");
				xmlFile1.mkdir();
				StreamResult result1 = new StreamResult(xmlFile1 + "/AU_ROUTE.xml");
				try {
					transformer.transform(source, result1);
					// String xmlFile3 = xmlFile1.toString()+"/A1A2Config.xml";
					// File xmlFile2 = new File(xmlFile3);
					String xmlFile3 = xmlFile1.toString() + "/AU_ROUTE.xml";
					File xmlFile2 = new File(xmlFile3);
					writeXMLattribute(xmlFile2);
					status = true;
				} catch (Exception ex) {
					logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}

			}
			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);

			// StreamResult result = new StreamResult(new
			// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
			try {
				transformer.transform(source, result);
				writeXMLattribute(xmlFile);
			} catch (Exception ex) {
				logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			status = true;

		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	
	public boolean getIAURouteFile(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String routeFileName, String temp, String date,
			String validation,String version) {
		boolean status = false;
		String value = null;
		
		if (enbId.startsWith("0")) {
			value = enbId.substring(1, 3);
		} else
			value = enbId.substring(0, 2);
		
		Ip obj = rep.getip(value);
		if(obj== null) {
			if (enbId.startsWith("0")) {
				value = enbId.substring(1, 4);
			} else
				value = enbId.substring(0, 3);	
			obj=rep.getip(value);
			if(obj == null)
				obj=rep.getip("0");
		}
		
		try {
			String oam_gateway = listCIQDetailsModel.get(0).getCiqMap().get("OAM_Gateway").getHeaderValue();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			//running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbausc_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au-sc", "gnbausc:managed-element");
			nc_config.appendChild(gnbausc_managed_element);

			Element ip_system = doc.createElement("gnbausc:ip-system");
			gnbausc_managed_element.appendChild(ip_system);

			Element cpu = doc.createElement("gnbausc:cpu");
			ip_system.appendChild(cpu);

			Element cpu_id = doc.createElement("gnbausc:cpu-id");
			cpu.appendChild(cpu_id);
			cpu_id.setTextContent("0");

			Element ip_route = doc.createElement("gnbausc:ip-route");
			cpu.appendChild(ip_route);

			Element ipv6_route = doc.createElement("gnbausc:ipv6-route");
			ip_route.appendChild(ipv6_route);

			Element ipv6_static_route = doc.createElement("gnbausc:ipv6-static-route");
			ipv6_static_route.setAttribute("nc:operation", "merge");
			ipv6_route.appendChild(ipv6_static_route);
			
//			if ("20C".equalsIgnoreCase(version) || "21A".equalsIgnoreCase(version)) {
//				Element gnbausc_vr_id = doc.createElement("gnbausc:vr-id");
//				gnbausc_vr_id.setTextContent("0");
//				ipv6_static_route.appendChild(gnbausc_vr_id);
//			}

			Element prefix = doc.createElement("gnbausc:prefix");
			prefix.setTextContent(obj.getPrefix());
			ipv6_static_route.appendChild(prefix);

			Element gateway = doc.createElement("gnbausc:gateway");
			gateway.setTextContent(oam_gateway);
			ipv6_static_route.appendChild(gateway);

			Element interface_name = doc.createElement("gnbausc:interface-name");
			interface_name.setTextContent("-");
			ipv6_static_route.appendChild(interface_name);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			if (validation.equals("RanConfig")) {

				File xmlFile1 = new File(temp + "Test");
				xmlFile1.mkdir();
				StreamResult result1 = new StreamResult(xmlFile1 + "/AU_ROUTE.xml");
				try {
					transformer.transform(source, result1);
					// String xmlFile3 = xmlFile1.toString()+"/A1A2Config.xml";
					// File xmlFile2 = new File(xmlFile3);
					String xmlFile3 = xmlFile1.toString() + "/AU_ROUTE.xml";
					File xmlFile2 = new File(xmlFile3);
					writeXMLattribute(xmlFile2);
					status = true;
				} catch (Exception ex) {
					logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}

			}
			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);

			// StreamResult result = new StreamResult(new
			// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
			try {
				transformer.transform(source, result);
				writeXMLattribute(xmlFile);
			} catch (Exception ex) {
				logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			status = true;

		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	// method for csl generation
	public boolean cslGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, int i, String cslFileNames, Set<String> neList,
			String tempaName, String name, String folderName, String validation) {
		boolean status = false;
		String rfPath = filepath;
		String gnodebid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();
		String ip = null;
		String port = null;
		String cslServerIpv6 =null;
		String cslPortNum = null;
		String secondCslServerIpv6 = null;
		String secondCslPortNum = null;
		String siteType = "";
		if(listCIQDetailsModel.get(0).getCiqMap().containsKey("Site_Type")) {
			siteType = listCIQDetailsModel.get(0).getCiqMap().get("Site_Type").getHeaderValue();
		}
		String auType = "";
		if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
		{
			auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue().trim();
		}
		// String mId = gnodebid.substring(1, 3);
		int a = Integer.valueOf(gnodebid);
		String gnodevalue = String.valueOf(a).toString();
		String mId = gnodevalue.substring(0, 2);
		/*
		 * if (mId.equals("56") || mId.equals("59") || mId.equals("60")) { Ip obj =
		 * rep.getip(mId); ip = obj.getIp(); port = obj.getPort(); } else if
		 * (mId.equals("61") || mId.equals("64") || mId.equals("68")) { Ip obj =
		 * rep.getip(mId); ip = obj.getIp(); port = obj.getPort();
		 * 
		 * } else if (mId.equals("70") || mId.equals("72") || mId.equals("73") ||
		 * mId.equals("12") || mId.equals("36")) { Ip obj = rep.getip(mId); ip =
		 * obj.getIp(); port = obj.getPort();
		 * 
		 * }
		 */
		/*
		 * if(mId.equals("12")) {
		 * 
		 * String marketid = gnodevalue.substring(0, 3); if(marketid.equals("120" ) ||
		 * marketid.equals("127")) mId =marketid;
		 * 
		 * }
		 */
		Ip obj = rep.getip(mId); 
		if(obj==null) {
			 mId = gnodebid.substring(0, 3);
			 obj = rep.getip(mId);
			 if(obj==null) {
				 obj=rep.getip("0");
			 }
		}
		
		if(obj.getIp()!= null)
			ip=obj.getIp(); 
		
		if(obj.getPort()!= null)
			port = obj.getPort();
		
		if(obj.getCslServerIpv6()!=null)
			cslServerIpv6= obj.getCslServerIpv6();
		
		if(obj.getCslPortNum()!=null)
			cslPortNum=obj.getCslPortNum();
		
		if(obj.getSecondCslServerIpv6()!=null)
			secondCslServerIpv6 =obj.getSecondCslServerIpv6();
		
		if(obj.getSecondCslPortNum()!=null)
			secondCslPortNum = obj.getSecondCslPortNum();
		
		if (i == 3) {
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
				// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
				doc.appendChild(nc_rpc);
				Element nc_edit_config = doc.createElement("nc:edit-config");
				nc_rpc.appendChild(nc_edit_config);

				Element nc_target = doc.createElement("nc:target");
				nc_edit_config.appendChild(nc_target);
				Element running = doc.createElement("nc:running");
				nc_target.appendChild(running);
				running.setTextContent(" ");
				Element nc_default_operation = doc.createElement("nc:default-operation");
				nc_edit_config.appendChild(nc_default_operation);
				nc_default_operation.appendChild(doc.createTextNode("none"));
				Element nc_config = doc.createElement("nc:config");
				nc_edit_config.appendChild(nc_config);
				Element mid_managed_element = doc.createElementNS(
						"http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist", "mid:managed-element");
				nc_config.appendChild(mid_managed_element);
				Element mid_enb_function = doc.createElement("mid:enb-function");
				mid_managed_element.appendChild(mid_enb_function);
				Element mid_call_trace = doc.createElement("mid:call-trace");
				mid_enb_function.appendChild(mid_call_trace);
				Element mid_csl_control_func = doc.createElement("mid:csl-control-func");
				mid_csl_control_func.setAttribute("nc:operation", "merge");
				mid_call_trace.appendChild(mid_csl_control_func);
				Element mid_delta_oos_threshold = doc.createElement("mid:delta-oos-threshold");
				mid_delta_oos_threshold.setTextContent("1");
				mid_csl_control_func.appendChild(mid_delta_oos_threshold);
				Element mid_mr_overwrite_enable = doc.createElement("mid:mr-overwrite-enable");
				mid_mr_overwrite_enable.setTextContent("first-and-last-mr");
				mid_csl_control_func.appendChild(mid_mr_overwrite_enable);
				Element mid_single_measure_report_control = doc.createElement("mid:single-measure-report-control");
				mid_single_measure_report_control.setTextContent("off");
				mid_csl_control_func.appendChild(mid_single_measure_report_control);
				Element mid_endc_csl_enable = doc.createElement("mid:endc-csl-enable");
				mid_endc_csl_enable.setTextContent("on");
				mid_csl_control_func.appendChild(mid_endc_csl_enable);
				Element mid_endc_csl_create_condition = doc.createElement("mid:endc-csl-create-condition");
				mid_endc_csl_create_condition.setTextContent("all");
				mid_csl_control_func.appendChild(mid_endc_csl_create_condition);
				Element mid_csl_info = doc.createElement("mid:csl-info");
				mid_csl_info.setAttribute("nc:operation", "merge");
				mid_call_trace.appendChild(mid_csl_info);
				Element mid_csl_ip_ver = doc.createElement("mid:csl-ip-ver");
				mid_csl_ip_ver.setTextContent("ipv6");
				mid_csl_info.appendChild(mid_csl_ip_ver);
				Element mid_csl_server_ipv6 = doc.createElement("mid:csl-server-ipv6");
				mid_csl_server_ipv6.setTextContent(cslServerIpv6);
				mid_csl_info.appendChild(mid_csl_server_ipv6);
				Element mid_csl_port_num = doc.createElement("mid:csl-port-num");
				mid_csl_port_num.setTextContent(cslPortNum);
				mid_csl_info.appendChild(mid_csl_port_num);
				Element mid_buffering_time = doc.createElement("mid:buffering-time");
				mid_buffering_time.setTextContent("2");
				mid_csl_info.appendChild(mid_buffering_time);
				Element mid_udp_ack_control = doc.createElement("mid:udp-ack-control");
				mid_udp_ack_control.setTextContent("no-retransmission");
				mid_csl_info.appendChild(mid_udp_ack_control);
				Element mid_protocol_selection = doc.createElement("mid:protocol-selection");
				mid_protocol_selection.setTextContent("1");
				mid_csl_info.appendChild(mid_protocol_selection);
				Element mid_csl_report_control = doc.createElement("mid:csl-report-control");
				mid_csl_report_control.setTextContent("on");
				mid_csl_info.appendChild(mid_csl_report_control);
				Element mid_csl_encryption_mask_mode = doc.createElement("mid:csl-encryption-mask-mode");
				mid_csl_encryption_mask_mode.setTextContent("0");
				mid_csl_info.appendChild(mid_csl_encryption_mask_mode);
				Element mid_second_csl_ip_ver = doc.createElement("mid:second-csl-ip-ver");
				mid_second_csl_ip_ver.setTextContent("ipv6");
				mid_csl_info.appendChild(mid_second_csl_ip_ver);
				Element mid_second_csl_server_ipv6 = doc.createElement("mid:second-csl-server-ipv6");
				mid_second_csl_server_ipv6.setTextContent(secondCslServerIpv6);
				mid_csl_info.appendChild(mid_second_csl_server_ipv6);
				Element mid_second_csl_port_num = doc.createElement("mid:second-csl-port-num");
				mid_second_csl_port_num.setTextContent(secondCslPortNum);
				mid_csl_info.appendChild(mid_second_csl_port_num);
				Element mid_second_buffering_time = doc.createElement("mid:second-buffering-time");
				mid_second_buffering_time.setTextContent("2");
				mid_csl_info.appendChild(mid_second_buffering_time);
				Element mid_second_udp_ack_control = doc.createElement("mid:second-udp-ack-control");
				mid_second_udp_ack_control.setTextContent("no-retransmission");
				mid_csl_info.appendChild(mid_second_udp_ack_control);
				Element mid_second_protocol_selection = doc.createElement("mid:second-protocol-selection");
				mid_second_protocol_selection.setTextContent("1");
				mid_csl_info.appendChild(mid_second_protocol_selection);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				// String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new
				// Timestamp(System.currentTimeMillis()));
				DOMSource source = new DOMSource(doc);
				// anchorPath+"Anchor_CSL_"+enbId+"_"+neList.get(p)+"_"+timeStamp
				File xmlFile = new File(filepath + folderName);
				xmlFile.mkdir();
				xmlFile = new File(filepath + folderName + "/" + name);
				// File xmlFile = new
				// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
				StreamResult result = new StreamResult(xmlFile);
				transformer.transform(source, result);
				status = true;
				writeXMLattribute(xmlFile);
				if (validation.equals("RanConfig")) {
					File xmlFile1 = new File(rfPath + "Test");

					xmlFile1.mkdirs();
					xmlFile1 = new File(rfPath + "Test" + "/" + tempaName);
					// File xmlFile = new
					// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");

					StreamResult result1 = new StreamResult(xmlFile1);
					transformer.transform(source, result1);
					status = true;
					writeXMLattribute(xmlFile1);
				}
			} catch (Exception e) {
				logger.error("cslGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
			}
			return status;
		} else {
			List<String> ls = new ArrayList<String>(Arrays.asList("gnbau", "gnbcp", "gnbup"));
			if(i==0 && siteType.equalsIgnoreCase("IB") && (auType.isEmpty() || auType.equalsIgnoreCase("TBD"))) {
				ls.remove(0);
				ls.add(0, "gnbausc");
			}

			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();
				// add elements to Document
				Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
				// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
				doc.appendChild(nc_rpc);
				Element nc_edit_config = doc.createElement("nc:edit-config");
				nc_rpc.appendChild(nc_edit_config);

				Element nc_target = doc.createElement("nc:target");
				nc_edit_config.appendChild(nc_target);
				Element running = doc.createElement("nc:running");
				nc_target.appendChild(running);
				running.setTextContent(" ");
				Element nc_default_operation = doc.createElement("nc:default-operation");
				nc_edit_config.appendChild(nc_default_operation);
				nc_default_operation.appendChild(doc.createTextNode("none"));
				Element nc_config = doc.createElement("nc:config");
				nc_edit_config.appendChild(nc_config);
				Element gnbc_managed_element = null;
				if(ls.get(i).equals("gnbau")) {
					gnbc_managed_element = doc.createElementNS(
							"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", ls.get(i) + ":managed-element");
				} else if(ls.get(i).equals("gnbausc")) {
					gnbc_managed_element = doc.createElementNS(
							"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au-sc", ls.get(i) + ":managed-element");
				}
				else if(ls.get(i).equals("gnbup")) {
					gnbc_managed_element = doc.createElementNS(
							"http://www.samsung.com/global/business/5GvRAN/ns/gnb-cu-up", ls.get(i) + ":managed-element");
				}
				else {
					gnbc_managed_element = doc.createElementNS(
							"http://www.samsung.com/global/business/5GvRAN/ns/gnb-cu-cp", ls.get(i) + ":managed-element");
				}
				nc_config.appendChild(gnbc_managed_element);
				Element gnbcp_common_management = doc.createElement(ls.get(i) + ":common-management");
				gnbc_managed_element.appendChild(gnbcp_common_management);
				Element gnbcp_csl_configuration = doc.createElement(ls.get(i) + ":csl-configuration");
				gnbcp_common_management.appendChild(gnbcp_csl_configuration);
				if (i == 0) {
					Element gnbcp_csl_tce_ems_server = doc.createElement(ls.get(i) + ":csl-tce-ems-server");
					gnbcp_csl_tce_ems_server.setAttribute("nc:operation", "merge");
					gnbcp_csl_configuration.appendChild(gnbcp_csl_tce_ems_server);
					Element gnbcp_csl_tce_ems_server_port = doc.createElement(ls.get(i) + ":csl-tce-ems-server-port");
					gnbcp_csl_tce_ems_server_port.setTextContent("50002");
					gnbcp_csl_tce_ems_server.appendChild(gnbcp_csl_tce_ems_server_port);
					Element gnbcp_csl_tce_ems_option = doc.createElement(ls.get(i) + ":csl-tce-ems-option");
					gnbcp_csl_tce_ems_option.setTextContent("abnormal-call-only");
					gnbcp_csl_tce_ems_server.appendChild(gnbcp_csl_tce_ems_option);
				}
				Element gnbcp_csl_tce_server = doc.createElement(ls.get(i) + ":csl-tce-server");
				gnbcp_csl_tce_server.setAttribute("nc:operation", "merge");
				gnbcp_csl_configuration.appendChild(gnbcp_csl_tce_server);
				Element gnbcp_csl_tce_server_ip_address = doc.createElement(ls.get(i) + ":csl-tce-server-ip-address");
				gnbcp_csl_tce_server_ip_address.setTextContent(ip);
				gnbcp_csl_tce_server.appendChild(gnbcp_csl_tce_server_ip_address);
				Element gnbcp_csl_tce_server_port = doc.createElement(ls.get(i) + ":csl-tce-server-port");
				gnbcp_csl_tce_server_port.setTextContent(port);
				gnbcp_csl_tce_server.appendChild(gnbcp_csl_tce_server_port);
				Element gnbcp_csl_tce_option = doc.createElement(ls.get(i) + ":csl-tce-option");
				if(i == 2) {
					gnbcp_csl_tce_option.setTextContent("normal-and-abnormal-call");
				}else {
				gnbcp_csl_tce_option.setTextContent("normal-and-abnormal-and-intra-ho-call");
				}
				gnbcp_csl_tce_server.appendChild(gnbcp_csl_tce_option);
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				DOMSource source = new DOMSource(doc);
				File xmlFile = new File(filepath + cslFileNames);
				// File xmlFile = new
				// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
				StreamResult result = new StreamResult(xmlFile);
				transformer.transform(source, result);
				status = true;
				writeXMLattribute(xmlFile);
				if (validation.equals("RanConfig")) {
					File xmlFile1 = new File(rfPath + "Test");
					xmlFile1.mkdir();
					xmlFile1 = new File(rfPath + "Test" + "/" + tempaName);
					// File xmlFile = new
					// File("/home/user/RCT/rctsoftware/Customer/40/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/CommissionScripts/20A/07000110001");
					StreamResult result1 = new StreamResult(xmlFile1);
					transformer.transform(source, result1);
					status = true;
					writeXMLattribute(xmlFile1);
				}

			} catch (Exception e) {
				logger.error("cslGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;

	}

	// method for a1a2 Config generation file
	public boolean getA1A2ConfigFile(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String a1a2FileName, String temp, String date,
			String validation) {
		boolean status = false;
		try {
			String gnodebid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();
			// gnodebid = gnodebid.substring(1);
			int gn = Integer.valueOf(gnodebid);
			String gnid = String.valueOf(gn);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbcp_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-cu-cp", "gnbcp:managed-element");
			nc_config.appendChild(gnbcp_managed_element);

			Element gnb_cu_cp_function = doc.createElement("gnbcp:gnb-cu-cp-function");
			gnbcp_managed_element.appendChild(gnb_cu_cp_function);

			Element gnb_cu_cp_function_entries = doc.createElement("gnbcp:gnb-cu-cp-function-entries");
			gnb_cu_cp_function.appendChild(gnb_cu_cp_function_entries);

			Element gnodeb_id = doc.createElement("gnbcp:gnodeb-id");
			gnb_cu_cp_function_entries.appendChild(gnodeb_id);
			gnodeb_id.setTextContent(gnid);

			Element gutran_cu_cell = doc.createElement("gnbcp:gutran-cu-cell");
			gnb_cu_cp_function_entries.appendChild(gutran_cu_cell);

			Map<String, String> mp = new HashMap<String, String>();
			List<Map<String, String>> cList = new ArrayList<Map<String, String>>();
			int inc = 0;
			int k = 0;
			Set<String> keys = listCIQDetailsModel.get(0).getCiqMap().keySet();
			List<String> list = new ArrayList<String>(keys);

			for (int i = 0; i < keys.size(); i++) {
				if (list.get(i).equals("CC" + String.valueOf(k) + " " + "NR ARFCN"))
				// if(listCIQDetailsModel.get(0).getCiqMap().containsKey(("CC"+String.valueOf(k)+"
				// "+"NR ARFCN")))
				{
					String va = String.valueOf(k);
					String obj = "CC" + va + " " + "NR ARFCN";
					if (listCIQDetailsModel.get(0).getCiqMap().get(obj).getHeaderValue() != null
							&& !listCIQDetailsModel.get(0).getCiqMap().get(obj).getHeaderValue().equals("TBD")) {
						if (!listCIQDetailsModel.get(0).getCiqMap()
								.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue().equals("TBD")
								&& !listCIQDetailsModel.get(0).getCiqMap()
										.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue()
										.isEmpty()) {
							Integer value = Integer.parseInt(listCIQDetailsModel.get(0).getCiqMap()
									.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue());
							mp.put(String.valueOf(k), value.toString());
							k++;
						}
					} else {
						mp.put(String.valueOf(k), "TBD");
						k++;
					}
				}

				// String cell_identity0 = listCIQDetailsModel.get(0).getCiqMap().get("CC0 Cell
				// Identity").getHeaderValue();
			}

			for (int j = 0; j < mp.size(); j++) {
				// String temp = mp.get(String.valueOf(j));
				// int len = temp.length()-1;
				if (mp.get(String.valueOf(j)).equals("TBD"))
					continue;

				Element gutran_cu_cell_entries = doc.createElement("gnbcp:gutran-cu-cell-entries");
				gutran_cu_cell_entries.setAttribute("nc:operation", "merge");
				gutran_cu_cell.appendChild(gutran_cu_cell_entries);

				Element cell_identity = doc.createElement("gnbcp:cell-identity");
				cell_identity.setTextContent(mp.get(String.valueOf(j)));
				gutran_cu_cell_entries.appendChild(cell_identity);

				Element nr_ul_coverage_method = doc.createElement("gnbcp:nr-ul-coverage-method");
				nr_ul_coverage_method.setTextContent("A1-A2");
				gutran_cu_cell_entries.appendChild(nr_ul_coverage_method);

				Element report_config_entries = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries);

				Element index = doc.createElement("gnbcp:index");
				index.setTextContent(String.valueOf("1"));
				report_config_entries.appendChild(index);

				Element a2_report_config = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a2-report-config");
				a2_report_config.setAttribute("nc:operation", "merge");
				report_config_entries.appendChild(a2_report_config);

				Element a2_threshold_rsrp = doc.createElement("gnbcp:a2-threshold-rsrp");
				a2_threshold_rsrp.setTextContent("48");
				a2_report_config.appendChild(a2_threshold_rsrp);

				Element a2_time_to_trigger = doc.createElement("gnbcp:a2-time-to-trigger");
				a2_time_to_trigger.setTextContent("ms40");
				a2_report_config.appendChild(a2_time_to_trigger);

				Element report_config_entries1 = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries1);

				Element index1 = doc.createElement("gnbcp:index");
				index1.setTextContent(String.valueOf("2"));
				report_config_entries1.appendChild(index1);

				Element a3_report_config = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a3-report-config");
				a3_report_config.setAttribute("nc:operation", "merge");
				report_config_entries1.appendChild(a3_report_config);

				Element a3_time_to_trigger = doc.createElement("gnbcp:a3-time-to-trigger");
				a3_time_to_trigger.setTextContent("ms128");
				a3_report_config.appendChild(a3_time_to_trigger);

				Element report_config_entries3 = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries3);

				Element index2 = doc.createElement("gnbcp:index");
				index2.setTextContent(String.valueOf("3"));
				report_config_entries3.appendChild(index2);

				Element a1_report_config = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a1-report-config");
				a1_report_config.setAttribute("nc:operation", "merge");
				report_config_entries3.appendChild(a1_report_config);

				Element a1_threshold_rsrp = doc.createElement("gnbcp:a1-threshold-rsrp");
				a1_threshold_rsrp.setTextContent("63");
				a1_report_config.appendChild(a1_threshold_rsrp);

				Element a1_time_to_trigger = doc.createElement("gnbcp:a1-time-to-trigger");
				a1_time_to_trigger.setTextContent("ms128");
				a1_report_config.appendChild(a1_time_to_trigger);

				Element report_config_entries4 = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries4);

				Element index3 = doc.createElement("gnbcp:index");
				index3.setTextContent(String.valueOf("4"));
				report_config_entries4.appendChild(index3);

				Element a2_report_config1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a2-report-config");
				a2_report_config1.setAttribute("nc:operation", "merge");
				report_config_entries4.appendChild(a2_report_config1);

				Element a2_threshold_rsrp1 = doc.createElement("gnbcp:a2-threshold-rsrp");
				a2_threshold_rsrp1.setTextContent("60");
				a2_report_config1.appendChild(a2_threshold_rsrp1);

				Element a2_time_to_trigger1 = doc.createElement("gnbcp:a2-time-to-trigger");
				a2_time_to_trigger1.setTextContent("ms128");
				a2_report_config1.appendChild(a2_time_to_trigger1);

				Element report_config_entries5 = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries5);

				Element index4 = doc.createElement("gnbcp:index");
				index4.setTextContent(String.valueOf("5"));
				report_config_entries5.appendChild(index4);

				Element a1_report_config1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a1-report-config");
				a1_report_config1.setAttribute("nc:operation", "merge");
				report_config_entries5.appendChild(a1_report_config1);

				Element a1_threshold_rsrp1 = doc.createElement("gnbcp:a1-threshold-rsrp");
				a1_threshold_rsrp1.setTextContent("63");
				a1_report_config1.appendChild(a1_threshold_rsrp1);

				Element a1_time_to_trigger1 = doc.createElement("gnbcp:a1-time-to-trigger");
				a1_time_to_trigger1.setTextContent("ms128");
				a1_report_config1.appendChild(a1_time_to_trigger1);

				Element report_config_entries6 = doc.createElement("gnbcp:report-config-entries");
				gutran_cu_cell_entries.appendChild(report_config_entries6);

				Element index5 = doc.createElement("gnbcp:index");
				index5.setTextContent(String.valueOf("6"));
				report_config_entries6.appendChild(index5);

				Element a2_report_config11 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
						"gnbcp:a2-report-config");
				a2_report_config11.setAttribute("nc:operation", "merge");
				report_config_entries6.appendChild(a2_report_config11);

				Element a2_threshold_rsrp11 = doc.createElement("gnbcp:a2-threshold-rsrp");
				a2_threshold_rsrp11.setTextContent("60");
				a2_report_config11.appendChild(a2_threshold_rsrp11);

				Element a2_time_to_trigger11 = doc.createElement("gnbcp:a2-time-to-trigger");
				a2_time_to_trigger11.setTextContent("ms128");
				a2_report_config11.appendChild(a2_time_to_trigger11);
			}

			// int set =0;
			// String cco_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR
			// ARFCN").getHeaderValue();
			// String cc1_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR
			// ARFCN").getHeaderValue();
			// String cc2_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR
			// ARFCN").getHeaderValue();
			// String cc3_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR
			// ARFCN").getHeaderValue();
			// String cc4_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR
			// ARFCN").getHeaderValue();
			// String cc5_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR
			// ARFCN").getHeaderValue();
			// String cc6_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR
			// ARFCN").getHeaderValue();
			// String cc7_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR
			// ARFCN").getHeaderValue();
			//
			// if(!(cco_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc1_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc2_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc3_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc4_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc5_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc6_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc7_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			//
			// String cell_identity0 = listCIQDetailsModel.get(0).getCiqMap().get("CC0 Cell
			// Identity").getHeaderValue();
			// int cell = Integer.valueOf(cell_identity0);
			//
			// String abc;
			// int i = 0;
			//
			// for(i=0;i<set;i++)
			// {
			// if((i==0) || (i==1) || (i==2) || (i==3) || (i==4) || (i==5) || (i==6) ||
			// (i==7))
			// {
			//
			//
			// Element gutran_cu_cell_entries =
			// doc.createElement("gnbcp:gutran-cu-cell-entries");
			// gutran_cu_cell_entries.setAttribute("nc:operation", "merge");
			// gutran_cu_cell.appendChild(gutran_cu_cell_entries);
			//
			// Element cell_identity = doc.createElement("gnbcp:cell-identity");
			// cell_identity.setTextContent(String.valueOf(cell));
			// gutran_cu_cell_entries.appendChild(cell_identity);
			//
			//
			//
			// Element nr_ul_coverage_method = doc
			// .createElement("gnbcp:nr-ul-coverage-method");
			// nr_ul_coverage_method.setTextContent("A1-A2");
			// gutran_cu_cell_entries.appendChild(nr_ul_coverage_method);
			//
			// Element report_config_entries =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries);
			//
			// Element index = doc.createElement("gnbcp:index");
			// index.setTextContent(String.valueOf("1"));
			// report_config_entries.appendChild(index);
			//
			// Element a2_report_config =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a2-report-config");
			// a2_report_config.setAttribute("nc:operation","merge");
			// report_config_entries.appendChild(a2_report_config);
			//
			// Element a2_threshold_rsrp = doc.createElement("gnbcp:a2-threshold-rsrp");
			// a2_threshold_rsrp.setTextContent("48");
			// a2_report_config.appendChild(a2_threshold_rsrp);
			//
			// Element a2_time_to_trigger = doc.createElement("gnbcp:a2-time-to-trigger");
			// a2_time_to_trigger.setTextContent("ms40");
			// a2_report_config.appendChild(a2_time_to_trigger);
			//
			// Element report_config_entries1 =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries1);
			//
			// Element index1 = doc.createElement("gnbcp:index");
			// index1.setTextContent(String.valueOf("2"));
			// report_config_entries1.appendChild(index1);
			//
			//
			// Element a3_report_config =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a3-report-config");
			// a3_report_config.setAttribute("nc:operation","merge");
			// report_config_entries1.appendChild(a3_report_config);
			//
			// Element a3_time_to_trigger = doc.createElement("gnbcp:a3-time-to-trigger");
			// a3_time_to_trigger.setTextContent("ms128");
			// a3_report_config.appendChild(a3_time_to_trigger);
			//
			// Element report_config_entries3 =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries3);
			//
			// Element index2 = doc.createElement("gnbcp:index");
			// index2.setTextContent(String.valueOf("3"));
			// report_config_entries3.appendChild(index2);
			//
			//
			// Element a1_report_config =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a1-report-config");
			// a1_report_config.setAttribute("nc:operation","merge");
			// report_config_entries3.appendChild(a1_report_config);
			//
			// Element a1_threshold_rsrp = doc.createElement("gnbcp:a1-threshold-rsrp");
			// a1_threshold_rsrp.setTextContent("63");
			// a1_report_config.appendChild(a1_threshold_rsrp);
			//
			// Element a1_time_to_trigger = doc.createElement("gnbcp:a1-time-to-trigger");
			// a1_time_to_trigger.setTextContent("ms128");
			// a1_report_config.appendChild(a1_time_to_trigger);
			//
			// Element report_config_entries4 =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries4);
			//
			// Element index3 = doc.createElement("gnbcp:index");
			// index3.setTextContent(String.valueOf("4"));
			// report_config_entries4.appendChild(index3);
			//
			//
			// Element a2_report_config1 =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a2-report-config");
			// a2_report_config1.setAttribute("nc:operation","merge");
			// report_config_entries4.appendChild(a2_report_config1);
			//
			// Element a2_threshold_rsrp1 = doc.createElement("gnbcp:a2-threshold-rsrp");
			// a2_threshold_rsrp1.setTextContent("60");
			// a2_report_config1.appendChild(a2_threshold_rsrp1);
			//
			// Element a2_time_to_trigger1 = doc.createElement("gnbcp:a2-time-to-trigger");
			// a2_time_to_trigger1.setTextContent("ms128");
			// a2_report_config1.appendChild(a2_time_to_trigger1);
			//
			// Element report_config_entries5 =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries5);
			//
			// Element index4 = doc.createElement("gnbcp:index");
			// index4.setTextContent(String.valueOf("5"));
			// report_config_entries5.appendChild(index4);
			//
			//
			// Element a1_report_config1 =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a1-report-config");
			// a1_report_config1.setAttribute("nc:operation","merge");
			// report_config_entries5.appendChild(a1_report_config1);
			//
			// Element a1_threshold_rsrp1 = doc.createElement("gnbcp:a1-threshold-rsrp");
			// a1_threshold_rsrp1.setTextContent("63");
			// a1_report_config1.appendChild(a1_threshold_rsrp1);
			//
			// Element a1_time_to_trigger1 = doc.createElement("gnbcp:a1-time-to-trigger");
			// a1_time_to_trigger1.setTextContent("ms128");
			// a1_report_config1.appendChild(a1_time_to_trigger1);
			//
			// Element report_config_entries6 =
			// doc.createElement("gnbcp:report-config-entries");
			// gutran_cu_cell_entries.appendChild(report_config_entries6);
			//
			// Element index5 = doc.createElement("gnbcp:index");
			// index5.setTextContent(String.valueOf("6"));
			// report_config_entries6.appendChild(index5);
			//
			//
			// Element a2_report_config11 =
			// doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
			// "gnbcp:a2-report-config");
			// a2_report_config11.setAttribute("nc:operation","merge");
			// report_config_entries6.appendChild(a2_report_config11);
			//
			// Element a2_threshold_rsrp11 = doc.createElement("gnbcp:a2-threshold-rsrp");
			// a2_threshold_rsrp11.setTextContent("60");
			// a2_report_config11.appendChild(a2_threshold_rsrp11);
			//
			// Element a2_time_to_trigger11 = doc.createElement("gnbcp:a2-time-to-trigger");
			// a2_time_to_trigger11.setTextContent("ms128");
			// a2_report_config11.appendChild(a2_time_to_trigger11);
			// }
			// cell++;
			//
			// }
			//

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			if (validation.equals("RanConfig")) {

				File xmlFile1 = new File(temp + "Test");
				if (xmlFile1.exists()) {
					FileUtils.cleanDirectory(xmlFile1);
				}
				xmlFile1.mkdir();
				StreamResult result1 = new StreamResult(xmlFile1 + "/A1A2Config.xml");
				try {
					transformer.transform(source, result1);
					String xmlFile3 = xmlFile1.toString() + "/A1A2Config.xml";
					File xmlFile2 = new File(xmlFile3);
					writeXMLattribute(xmlFile2);
					status = true;

				} catch (Exception ex) {
					logger.error("A1A2Config() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}

			}

			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			// StreamResult result = new StreamResult(new
			// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			writeXMLattribute(xmlFile);

			status = true;

		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));

		}
		return status;

	}

	// method for a1a2 Create generation file
	public boolean getA1A2CreateFile(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String a1a2FileName, String temp, String date,
			String validation) {
		boolean status = false;
		try {
			String gnodebid = listCIQDetailsModel.get(0).getCiqMap().get("GNODEBID").getHeaderValue();
			int gn = Integer.valueOf(gnodebid);
			String gnid = String.valueOf(gn);
			// gnodebid = gnodebid.substring(1);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbcp_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-cu-cp", "gnbcp:managed-element");
			nc_config.appendChild(gnbcp_managed_element);

			Element gnb_cu_cp_function = doc.createElement("gnbcp:gnb-cu-cp-function");
			gnbcp_managed_element.appendChild(gnb_cu_cp_function);

			Element gnb_cu_cp_function_entries = doc.createElement("gnbcp:gnb-cu-cp-function-entries");
			gnb_cu_cp_function.appendChild(gnb_cu_cp_function_entries);

			Element gnodeb_id = doc.createElement("gnbcp:gnodeb-id");
			gnb_cu_cp_function_entries.appendChild(gnodeb_id);
			gnodeb_id.setTextContent(gnid);

			Element gutran_cu_cell = doc.createElement("gnbcp:gutran-cu-cell");
			gnb_cu_cp_function_entries.appendChild(gutran_cu_cell);

			Map<String, String> mp = new HashMap<String, String>();
			List<Map<String, String>> cList = new ArrayList<Map<String, String>>();
			int inc = 0;
			int k = 0;
			Set<String> keys = listCIQDetailsModel.get(0).getCiqMap().keySet();
			List<String> list = new ArrayList<String>(keys);

			for (int i = 0; i < keys.size(); i++) {
				if (list.get(i).equals("CC" + String.valueOf(k) + " " + "NR ARFCN"))
				// if(listCIQDetailsModel.get(0).getCiqMap().containsKey(("CC"+String.valueOf(k)+"
				// "+"NR ARFCN")))
				{
					String va = String.valueOf(k);
					String obj = "CC" + va + " " + "NR ARFCN";
					if (listCIQDetailsModel.get(0).getCiqMap().get(obj).getHeaderValue() != null
							&& !listCIQDetailsModel.get(0).getCiqMap().get(obj).getHeaderValue().equals("TBD")) {
						if (!listCIQDetailsModel.get(0).getCiqMap()
								.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue().equals("TBD")
								&& !listCIQDetailsModel.get(0).getCiqMap()
										.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue()
										.isEmpty()) {
							Integer value = Integer.parseInt(listCIQDetailsModel.get(0).getCiqMap()
									.get("CC" + String.valueOf(k) + " " + "Cell Identity").getHeaderValue());
							mp.put(String.valueOf(k), value.toString());
							k++;
						}
					} else {
						mp.put(String.valueOf(k), "TBD");
						k++;
					}
				}

				// String cell_identity0 = listCIQDetailsModel.get(0).getCiqMap().get("CC0 Cell
				// Identity").getHeaderValue();
			}

			for (int j = 0; j < mp.size(); j++) {
				// String temp = mp.get(String.valueOf(j));
				// int len = temp.length()-1;
				if (mp.get(String.valueOf(j)).equals("TBD"))
					continue;
				Element gutran_cu_cell_entries = doc.createElement("gnbcp:gutran-cu-cell-entries");
				gutran_cu_cell.appendChild(gutran_cu_cell_entries);

				Element cell_identity = doc.createElement("gnbcp:cell-identity");
				cell_identity.setTextContent(String.valueOf(mp.get(String.valueOf(j))));
				gutran_cu_cell_entries.appendChild(cell_identity);

				Element report_config_entries = doc.createElement("gnbcp:report-config-entries");
				report_config_entries.setAttribute("nc:operation", "create");
				gutran_cu_cell_entries.appendChild(report_config_entries);

				Element index = doc.createElement("gnbcp:index");
				index.setTextContent("3");
				report_config_entries.appendChild(index);

				Element report_type = doc.createElement("gnbcp:report-type");
				report_type.setTextContent("event-based-A1");
				report_config_entries.appendChild(report_type);

				Element a1_report_config = doc.createElement("gnbcp:a1-report-config");
				a1_report_config.setAttribute("nc:operation", "create");
				report_config_entries.appendChild(a1_report_config);

				Element a1_time_to_trigger = doc.createElement("gnbcp:a1-time-to-trigger");
				a1_time_to_trigger.setTextContent("ms128");
				a1_report_config.appendChild(a1_time_to_trigger);

				Element a1_purpose = doc.createElement("gnbcp:a1-purpose");
				a1_purpose.setTextContent("en-dc-in-nr-ul-coverage-ueulsplit-sup-purpose");
				a1_report_config.appendChild(a1_purpose);

				Element report_config_entries1 = doc.createElement("gnbcp:report-config-entries");
				report_config_entries1.setAttribute("nc:operation", "create");
				gutran_cu_cell_entries.appendChild(report_config_entries1);

				Element index1 = doc.createElement("gnbcp:index");
				index1.setTextContent("4");
				report_config_entries1.appendChild(index1);

				Element report_type1 = doc.createElement("gnbcp:report-type");
				report_type1.setTextContent("event-based-A2");
				report_config_entries1.appendChild(report_type1);

				Element a1_report_config1 = doc.createElement("gnbcp:a2-report-config");
				a1_report_config1.setAttribute("nc:operation", "create");
				report_config_entries1.appendChild(a1_report_config1);

				Element a1_time_to_trigger1 = doc.createElement("gnbcp:a2-time-to-trigger");
				a1_time_to_trigger1.setTextContent("ms128");
				a1_report_config1.appendChild(a1_time_to_trigger1);

				Element a1_purpose1 = doc.createElement("gnbcp:a2-purpose");
				a1_purpose1.setTextContent("en-dc-out-of-nr-ul-coverage-ueulsplit-sup-purpose");
				a1_report_config1.appendChild(a1_purpose1);

				Element report_config_entries2 = doc.createElement("gnbcp:report-config-entries");
				report_config_entries2.setAttribute("nc:operation", "create");
				gutran_cu_cell_entries.appendChild(report_config_entries2);

				Element index2 = doc.createElement("gnbcp:index");
				index2.setTextContent("5");
				report_config_entries2.appendChild(index2);

				Element report_type2 = doc.createElement("gnbcp:report-type");
				report_type2.setTextContent("event-based-A1");
				report_config_entries2.appendChild(report_type2);

				Element a1_report_config2 = doc.createElement("gnbcp:a1-report-config");
				a1_report_config2.setAttribute("nc:operation", "create");
				report_config_entries2.appendChild(a1_report_config2);

				Element a1_time_to_trigger2 = doc.createElement("gnbcp:a1-time-to-trigger");
				a1_time_to_trigger2.setTextContent("ms128");
				a1_report_config2.appendChild(a1_time_to_trigger2);

				Element a1_purpose2 = doc.createElement("gnbcp:a1-purpose");
				a1_purpose2.setTextContent("en-dc-in-nr-ul-coverage-ueulsplit-notsup-purpose");
				a1_report_config2.appendChild(a1_purpose2);

				Element report_config_entries3 = doc.createElement("gnbcp:report-config-entries");
				report_config_entries3.setAttribute("nc:operation", "create");
				gutran_cu_cell_entries.appendChild(report_config_entries3);

				Element index3 = doc.createElement("gnbcp:index");
				index3.setTextContent("6");
				report_config_entries3.appendChild(index3);

				Element report_type3 = doc.createElement("gnbcp:report-type");
				report_type3.setTextContent("event-based-A2");
				report_config_entries3.appendChild(report_type3);

				Element a1_report_config3 = doc.createElement("gnbcp:a2-report-config");
				a1_report_config3.setAttribute("nc:operation", "create");
				report_config_entries3.appendChild(a1_report_config3);

				Element a1_time_to_trigger3 = doc.createElement("gnbcp:a2-time-to-trigger");
				a1_time_to_trigger3.setTextContent("ms128");
				a1_report_config3.appendChild(a1_time_to_trigger3);

				Element a1_purpose3 = doc.createElement("gnbcp:a2-purpose");
				a1_purpose3.setTextContent("en-dc-out-of-nr-ul-coverage-ueulsplit-notsup-purpose");
				a1_report_config3.appendChild(a1_purpose3);
			}

			// int set =0;
			// String cco_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC0 NR
			// ARFCN").getHeaderValue();
			// String cc1_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC1 NR
			// ARFCN").getHeaderValue();
			// String cc2_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC2 NR
			// ARFCN").getHeaderValue();
			// String cc3_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC3 NR
			// ARFCN").getHeaderValue();
			// String cc4_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC4 NR
			// ARFCN").getHeaderValue();
			// String cc5_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC5 NR
			// ARFCN").getHeaderValue();
			// String cc6_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC6 NR
			// ARFCN").getHeaderValue();
			// String cc7_nrarfcn = listCIQDetailsModel.get(0).getCiqMap().get("CC7 NR
			// ARFCN").getHeaderValue();
			//
			// if(!(cco_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc1_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc2_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc3_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc4_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc5_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc6_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			// if(!(cc7_nrarfcn.equalsIgnoreCase("TBD")))
			// {
			// set++;
			// }
			//
			//
			//
			// String cell_identity0 = listCIQDetailsModel.get(0).getCiqMap().get("CC0 Cell
			// Identity").getHeaderValue();
			// int cell = Integer.valueOf(cell_identity0);
			//
			//
			// String abc;
			// int i = 0;
			// // set = 8;
			// for(i=0;i<set;i++)
			// {
			// Element gutran_cu_cell_entries =
			// doc.createElement("gnbcp:gutran-cu-cell-entries");
			// gutran_cu_cell.appendChild(gutran_cu_cell_entries);
			//
			// Element cell_identity = doc.createElement("gnbcp:cell-identity");
			// cell_identity.setTextContent(String.valueOf(cell));
			// gutran_cu_cell_entries.appendChild(cell_identity);
			//
			// Element report_config_entries =
			// doc.createElement("gnbcp:report-config-entries");
			// report_config_entries.setAttribute("nc:operation", "create");
			// gutran_cu_cell_entries.appendChild(report_config_entries);
			//
			// Element index = doc.createElement("gnbcp:index");
			// index.setTextContent("3");
			// report_config_entries.appendChild(index);
			//
			// Element report_type = doc.createElement("gnbcp:report-type");
			// report_type.setTextContent("event-based-A1");
			// report_config_entries.appendChild(report_type);
			//
			// Element a1_report_config = doc.createElement("gnbcp:a1-report-config");
			// a1_report_config.setAttribute("nc:operation", "create");
			// report_config_entries.appendChild(a1_report_config);
			//
			// Element a1_time_to_trigger = doc.createElement("gnbcp:a1-time-to-trigger");
			// a1_time_to_trigger.setTextContent("ms128");
			// a1_report_config.appendChild(a1_time_to_trigger);
			//
			// Element a1_purpose = doc.createElement("gnbcp:a1-purpose");
			// a1_purpose.setTextContent("en-dc-in-nr-ul-coverage-ueulsplit-sup-purpose");
			// a1_report_config.appendChild(a1_purpose);
			//
			//
			// Element report_config_entries1 =
			// doc.createElement("gnbcp:report-config-entries");
			// report_config_entries1.setAttribute("nc:operation", "create");
			// gutran_cu_cell_entries.appendChild(report_config_entries1);
			//
			// Element index1 = doc.createElement("gnbcp:index");
			// index1.setTextContent("4");
			// report_config_entries1.appendChild(index1);
			//
			// Element report_type1 = doc.createElement("gnbcp:report-type");
			// report_type1.setTextContent("event-based-A2");
			// report_config_entries1.appendChild(report_type1);
			//
			// Element a1_report_config1 = doc.createElement("gnbcp:a2-report-config");
			// a1_report_config1.setAttribute("nc:operation", "create");
			// report_config_entries1.appendChild(a1_report_config1);
			//
			// Element a1_time_to_trigger1 = doc.createElement("gnbcp:a2-time-to-trigger");
			// a1_time_to_trigger1.setTextContent("ms128");
			// a1_report_config1.appendChild(a1_time_to_trigger1);
			//
			// Element a1_purpose1= doc.createElement("gnbcp:a2-purpose");
			// a1_purpose1.setTextContent("en-dc-out-of-nr-ul-coverage-ueulsplit-sup-purpose");
			// a1_report_config1.appendChild(a1_purpose1);
			//
			//
			// Element report_config_entries2 =
			// doc.createElement("gnbcp:report-config-entries");
			// report_config_entries2.setAttribute("nc:operation", "create");
			// gutran_cu_cell_entries.appendChild(report_config_entries2);
			//
			// Element index2 = doc.createElement("gnbcp:index");
			// index2.setTextContent("5");
			// report_config_entries2.appendChild(index2);
			//
			// Element report_type2 = doc.createElement("gnbcp:report-type");
			// report_type2.setTextContent("event-based-A1");
			// report_config_entries2.appendChild(report_type2);
			//
			// Element a1_report_config2 = doc.createElement("gnbcp:a1-report-config");
			// a1_report_config2.setAttribute("nc:operation", "create");
			// report_config_entries2.appendChild(a1_report_config2);
			//
			// Element a1_time_to_trigger2 = doc.createElement("gnbcp:a1-time-to-trigger");
			// a1_time_to_trigger2.setTextContent("ms128");
			// a1_report_config2.appendChild(a1_time_to_trigger2);
			//
			// Element a1_purpose2 = doc.createElement("gnbcp:a1-purpose");
			// a1_purpose2.setTextContent("en-dc-in-nr-ul-coverage-ueulsplit-notsup-purpose");
			// a1_report_config2.appendChild(a1_purpose2);
			//
			//
			// Element report_config_entries3 =
			// doc.createElement("gnbcp:report-config-entries");
			// report_config_entries3.setAttribute("nc:operation", "create");
			// gutran_cu_cell_entries.appendChild(report_config_entries3);
			//
			// Element index3 = doc.createElement("gnbcp:index");
			// index3.setTextContent("6");
			// report_config_entries3.appendChild(index3);
			//
			// Element report_type3 = doc.createElement("gnbcp:report-type");
			// report_type3.setTextContent("event-based-A2");
			// report_config_entries3.appendChild(report_type3);
			//
			// Element a1_report_config3 = doc.createElement("gnbcp:a2-report-config");
			// a1_report_config3.setAttribute("nc:operation", "create");
			// report_config_entries3.appendChild(a1_report_config3);
			//
			// Element a1_time_to_trigger3 = doc.createElement("gnbcp:a2-time-to-trigger");
			// a1_time_to_trigger3.setTextContent("ms128");
			// a1_report_config3.appendChild(a1_time_to_trigger3);
			//
			// Element a1_purpose3= doc.createElement("gnbcp:a2-purpose");
			// a1_purpose3.setTextContent("en-dc-out-of-nr-ul-coverage-ueulsplit-notsup-purpose");
			// a1_report_config3.appendChild(a1_purpose3);
			// cell++;
			// }

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			if (validation.equals("RanConfig")) {
				File xmlFile1 = new File(temp + "Test");
				xmlFile1.mkdir();
				StreamResult result1 = new StreamResult(xmlFile1 + "/A1A2Create.xml");

				try {
					transformer.transform(source, result1);
					String xmlFile3 = xmlFile1.toString() + "/A1A2Create.xml";
					File xmlFile2 = new File(xmlFile3);
					writeXMLattribute(xmlFile2);
					status = true;

				} catch (Exception ex) {
					logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}

			}
			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			// StreamResult result = new StreamResult(new
			// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}
			writeXMLattribute(xmlFile);
			status = true;
		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

		@Override
		public synchronized JSONObject csvFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
				Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
				String remarks, Boolean supportCA, String dummy_IP) {
			logger.error("Going for generate csv file generation");
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		String csvFilename = "";
		JSONObject fileGenerateResult = new JSONObject();
		boolean addQuotes = true;
		//carrier add
		//Boolean support_ca = (supportCA.toString().equals("true")) ? true : false;
		
		try {
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			// fileBuilder.append(File.separator);

			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			if (listCIQDetailsModel != null && listCIQDetailsModel.size() > 0) {

				List<GrowConstantsEntity> objListProgDetails = fileUploadRepository.getGrowConstantsDetails();
				Map<String, String> resultMapForConstants = objListProgDetails.stream().filter(
						X -> X.getLabel().startsWith("ORAN_") && X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));

				StringBuilder csvBuilder = new StringBuilder();
				if (CommonUtil.isValidObject(neMappingEntity)
						&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
						&& neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NA)) {
					csvFilename = "GROW_ENB_" + enbName + dateString + ".csv";
					logger.error(enbId+"**csvStringForLegacy::**** ");
					csvBuilder = csvStringForLegacy(resultMapForConstants, ciqFileName, enbId, enbName,
							dbcollectionFileName, listCIQDetailsModel);
				} else if (CommonUtil.isValidObject(neMappingEntity)
						&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
						&& (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)
								|| neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_ADD)
								|| neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_NB_IOT_NO))) {
					if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("enb")) {
						logger.error(enbId+":filetype-enb:"+fileType);
						if (!enbName.contains("FSU")) {
							csvFilename = "GROW_ENB_" + enbName.replaceAll(" ", "_") + dateString + ".csv";
						} else {
							csvFilename = "FSU_TEMPLATE_" + enbName + dateString + ".csv";
						}
						if ((neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
								.equals(Constants.VERSION900)) {

							csvBuilder = getENBStringForV9(resultMapForConstants, ciqFileName, enbId, enbName,
									dbcollectionFileName, neMappingEntity, listCIQDetailsModel);

						} else if ((neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
								.equals("20.A.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("20.B.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("20.C.0")|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("21.A.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("21.B.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
									.equals("21.C.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
									.equals("21.D.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("22.A.0")
								|| (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())
										.equals("22.C.0")) {
							if(dummy_IP.contains("Pseudo IP")) { //dummy_IP
								csvBuilder = getENBStringForDummyIP(resultMapForConstants, ciqFileName, enbId, enbName,
										dbcollectionFileName, neMappingEntity, listCIQDetailsModel,
										neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion(), dummy_IP);
								fileGenerateResult.put("integrationType", "Pseudo IP");
							}
							else {
								csvBuilder = getENBStringForV20A(resultMapForConstants, ciqFileName, enbId, enbName,
										dbcollectionFileName, neMappingEntity, listCIQDetailsModel,
										neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion());
								fileGenerateResult.put("integrationType", "Legacy IP");
								logger.error(enbId+":::csvBuilder::"+csvBuilder);
							}
						}
						// else
						// if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("19.A.0"))
						// else if (enbName.contains("FSU")){
						// csvBuilder = getENBStringForFsuV9(resultMapForConstants, ciqFileName, enbId,
						// enbName,
						// dbcollectionFileName, neMappingEntity, listCIQDetailsModel);
						// }
						else {
							csvBuilder = getENBStringForVlsm(resultMapForConstants, ciqFileName, enbId, enbName,
									dbcollectionFileName, neMappingEntity, listCIQDetailsModel);
						}
					} else if (CommonUtil.isValidObject(fileType) && fileType.equalsIgnoreCase("cell")) {
						logger.error(enbId+":fileType:"+fileType);
						csvFilename = "GROW_CELL_" + enbName.replaceAll(" ", "_") + dateString + ".csv";
						logger.error("csvFilename::"+csvFilename);
						csvBuilder = null;
						// csvBuilder = getCellStringForVlsm(resultMapForConstants, ciqFileName, enbId,
						// enbName, dbcollectionFileName, neMappingEntity, listCIQDetailsModel);
						logger.error("Going to call GT");
						StringBuilder consCsvBuilder = getConsCellStringForVlsm(resultMapForConstants, ciqFileName,
								enbId, enbName, dbcollectionFileName, neMappingEntity, listCIQDetailsModel);
						logger.error("consCsvBuilder::"+consCsvBuilder);
						StringBuilder commFilePath = new StringBuilder();
						commFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
								.append(Constants.CUSTOMER);

						// StringBuilder perlFilePath = new StringBuilder();
						// perlFilePath.append(commFilePath).append(Constants.SEPARATOR)
						// .append(Constants.CELL_GROW_PERL_FILE);

						/* Copying perl and renaming it */
						// File dir = new File(fileBuilder.toString() + Constants.CELL_GROW_PERL_DIR);
						// if (!dir.exists()) {
						// FileUtil.createDirectory(fileBuilder.toString() +
						// Constants.CELL_GROW_PERL_DIR);
						// }
						// FileUtils.copyFileToDirectory(new File(perlFilePath.toString()), dir);
						// File originalfile = new File(dir + Constants.SEPARATOR +
						// Constants.CELL_GROW_PERL_FILE);
						// String perlFilePathWithSession = dir + Constants.SEPARATOR + "GROW_CELL_" +
						// enbName + "_"
						// + FilenameUtils.removeExtension(Constants.CELL_GROW_PERL_FILE) + "_" +
						// sessionId
						// + ".pl";
						// originalfile.renameTo(new File(perlFilePathWithSession));

						/* Creating consolidated csv file */
						File dir = new File(fileBuilder.toString() + Constants.CELL_GROW_CONS_CSV_DIR);
						if (!dir.exists()) {
							FileUtil.createDirectory(fileBuilder.toString() + Constants.CELL_GROW_CONS_CSV_DIR);
						}

						String consCsvPathWithSession = dir + Constants.SEPARATOR + "GROW_CELL_"
								+ enbName.replace(" ", "_") + "_"
								+ FilenameUtils.removeExtension(Constants.CELL_GROW_CONS_CSV_FILE) + "_" + sessionId
								+ ".csv";
						logger.error(enbId+"***consCsvPathWithSession:********* " + consCsvPathWithSession);
						if (CommonUtil.isValidObject(consCsvBuilder)) {
							FileWriter fileWriter = new FileWriter(consCsvPathWithSession);
							BufferedWriter bw = null;
							bw = new BufferedWriter(fileWriter);
							try {
								bw.write(consCsvBuilder.toString());
								consCsvBuilder.delete(0, consCsvBuilder.length());
								status = true;
								logger.error("Con csv is written");
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								bw.close();
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
						logger.error(enbId+"***consCsvPathWithSession2222:********* " + consCsvPathWithSession);

						/* Building consolidated output file */
						// dir = new File(fileBuilder.toString() + Constants.CELL_GROW_PERL_OUTPUT_DIR);
						// if (!dir.exists()) {
						// FileUtil.createDirectory(fileBuilder.toString() +
						// Constants.CELL_GROW_PERL_OUTPUT_DIR);
						// }
						// StringBuilder outputFileName = new StringBuilder();
						// outputFileName.append(fileBuilder.toString()).append(Constants.CELL_GROW_PERL_OUTPUT_DIR)
						// .append(Constants.SEPARATOR)
						// .append("GROW_CELL_" + enbName + "_"
						// + FilenameUtils.removeExtension(Constants.CELL_GROW_PERL_OUTPUT_FILE) + "_"
						// + sessionId + ".txt");

						String finalArguments = enbId + " -cons " + consCsvPathWithSession.toString() + " -v "
								+ neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
								+ " -dir " + filePath.toString() + " -n " + enbName.replace(" ", "_") + dateString + " " + supportCA.toString();

						String[] arguments = finalArguments.split(" ");
						UsmCellGrower usmCellGrower = new UsmCellGrower();
						boolean check = usmCellGrower.cellTemplate(arguments);
						logger.error("Done calling cell grow template*****"+enbId);
						// carrierAdd check
						boolean check2 = false;
						if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().contains("22.A")) {
							check2 = UsmCellGrower22V.AdNew;
						}else if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().contains("22.C")) {
							check2 = UsmCellGrower22C.AdNew;
						} else {
							check2 = UsmCellGrower.AdNew;
						}
						// 22A check
						boolean check22 = UsmCellGrower22V.remarks;
						if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().contains("22.A")) {
							check2 = UsmCellGrower22V.remarks;
						}else if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().contains("22.C")) {
							check2 = UsmCellGrower22C.remarks;
						}
						if (!check22) {
							fileGenerateResult.put("cbrsPref",
									"Failed to generate cell grow : CBRS preference information is not populated in CIQ");
							if (!check2) {
								fileGenerateResult.put("AdNew", "Not New");
								return fileGenerateResult;
							} else {
								fileGenerateResult.put("AdNew", "New");
								return fileGenerateResult;
							}
						}
						if (!check) {
							fileGenerateResult.put("stat", "fail");
							//carrier add check
							
							return fileGenerateResult;
						}
						if(!check2) {
							fileGenerateResult.put("AdNew", "Not New");
							return fileGenerateResult;
						}else {
							fileGenerateResult.put("AdNew", "New");
							return fileGenerateResult;
						}

						// String output = CommonUtil.executeCommand(perlFilePathWithSession.toString(),
						// outputFileName.toString(), finalArguments);

						// logger.info("GenerateCsvServiceImpl.csvFileGeneration() output: " + output);

						// FileWriter fileWriter = new FileWriter(outputFileName.toString());
						// fileWriter.write(output);
						// fileWriter.close();

						// CommonUtil.removeCtrlChars(outputFileName.toString());
					}
				} else if (CommonUtil.isValidObject(neMappingEntity)
						&& CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())
						&& (neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Put)
								|| neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD_TDD_Script)
								|| neMappingEntity.getSiteConfigType().equalsIgnoreCase(Constants.NE_CONFIG_TYPE_FDD)
								|| neMappingEntity.getSiteConfigType()
										.equalsIgnoreCase(Constants.NE_CONFIG_TYPE_New_Site))) {
					String cascade = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
							listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
							Constants.ORAN_SPRINT_COMM_SCRIPT_CASCADE);
					csvFilename = "GROW_ENB_" + enbId + "_" + cascade + dateString + ".csv";
					csvBuilder = csvStringForSprint(resultMapForConstants, ciqFileName, enbId, enbName,
							dbcollectionFileName, listCIQDetailsModel, neMappingEntity);
				}

				if (CommonUtil.isValidObject(csvBuilder)) {
					FileWriter fileWriter = new FileWriter(fileBuilder.toString() + csvFilename);
					BufferedWriter bw = null;
					bw = new BufferedWriter(fileWriter);
					try {
						String csvString = csvBuilder.toString();
						logger.error(enbId+"**csvFileGeneration1-csvString::**** "+csvString);
						if (addQuotes&&StringUtils.isNotEmpty(csvString)) {
							
							csvString = "\"" + csvString.replaceAll(",", "\",\"").replaceAll("\n", "\"\n\"");
							csvString = csvString.substring(0, csvString.length() - 2);
						}
						bw.write(csvString);
						csvBuilder.delete(0, csvBuilder.length());
						status = true;
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						bw.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}

				status = true;
				logger.error(enbId+"**csvFileGeneration1::**** "+status);
				// }
			} else {
				status = false;
				logger.error(enbId+"**csvFileGeneration2::**** "+status);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error("csvFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", csvFilename);

			/*
			 * User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
			 * { String fileSavePath = fileBuilder.toString(); fileSavePath =
			 * fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			 * , ""); objInfo.setFilePath(fileSavePath);
			 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
			 * objInfo.setRemarks(remarks); objInfo.setFileName(csvFilename);
			 * objInfo.setGenerationDate(new Date());
			 * objInfo.setGeneratedBy(user.getUserName());
			 * objInfo.setFileType(Constants.FILE_TYPE_CSV); CustomerDetailsEntity
			 * programDetailsEntity = new CustomerDetailsEntity();
			 * programDetailsEntity.setId(programId);
			 * objInfo.setProgramDetailsEntity(programDetailsEntity);
			 * objGenerateCsvRepository.saveCsvAudit(objInfo); }
			 */
		}

		return fileGenerateResult;
	}

	/*
	 * @Override public boolean csvFileGeneration(String ciqFileName, String enbId,
	 * String enbName, String dbcollectionFileName, Integer programId, String
	 * filePath, String sessionId, String remarks) {
	 * 
	 * // TODO Auto-generated method stub boolean status = false; StringBuilder
	 * fileBuilder = new StringBuilder(); String timeStamp = new
	 * SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new
	 * Timestamp(System.currentTimeMillis())); String csvFilename = ""; try {
	 * fileBuilder.setLength(0); fileBuilder.append(filePath); csvFilename =
	 * "ORAN_GROW_" + enbId + "_" + timeStamp + ".csv"; List<CIQDetailsModel>
	 * listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName,
	 * enbId, enbName, dbcollectionFileName); if (listCIQDetailsModel != null &&
	 * listCIQDetailsModel.size() > 0) {
	 * 
	 * List<GrowConstantsEntity> objListProgDetails =
	 * growConstantsRepository.getGrowConstantsDetails(); Map<String, String>
	 * resultMapForConstants = objListProgDetails.stream() .filter(X ->
	 * X.getLabel().startsWith("ORAN_GROW"))
	 * .collect(Collectors.toMap(GrowConstantsEntity::getLabel,
	 * GrowConstantsEntity::getValue));
	 * 
	 * StringBuilder csvBuilder = csvString(resultMapForConstants, ciqFileName,
	 * enbId, enbName, dbcollectionFileName, listCIQDetailsModel);
	 * 
	 * if (CommonUtil.isValidObject(csvBuilder)) { FileWriter fileWriter = new
	 * FileWriter(fileBuilder.toString() + csvFilename); BufferedWriter bw = null;
	 * bw = new BufferedWriter(fileWriter); try { bw.write(csvBuilder.toString());
	 * csvBuilder.delete(0, csvBuilder.length()); status = true; } catch
	 * (IOException e) { e.printStackTrace(); } try { bw.close(); } catch
	 * (IOException e) {
	 * 
	 * e.printStackTrace(); } }
	 * 
	 * status = true; // } } else { status = false; }
	 * 
	 * } catch (Exception e) { // TODO: handle exception
	 * logger.error("csvFileGeneration() GenerateCsvServiceImpl" +
	 * ExceptionUtils.getFullStackTrace(e)); } finally { User user =
	 * UserSessionPool.getInstance().getSessionUser(sessionId);
	 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
	 * { objInfo.setFilePath(fileBuilder.toString());
	 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
	 * objInfo.setRemarks(remarks); objInfo.setFileName(csvFilename);
	 * objInfo.setGenerationDate(new Date());
	 * objInfo.setGeneratedBy(user.getUserName());
	 * objInfo.setFileType(Constants.FILE_TYPE_CSV); CustomerDetailsEntity
	 * programDetailsEntity = new CustomerDetailsEntity();
	 * programDetailsEntity.setId(programId);
	 * objInfo.setProgramDetailsEntity(programDetailsEntity);
	 * objGenerateCsvRepository.saveCsvAudit(objInfo); } }
	 * 
	 * return status; }
	 */

	/*
	 * get_csl_ip() for getComisionScriptFile
	 * 
	 * 
	 */
	public static String get_csl_ip(String eNodID) {
		String eNodeID = eNodID.trim();
		String substr = eNodeID.substring(0, 2);
		String cslip = "";
		if ((substr.equals("72")) || (substr.equals("73"))) {
			cslip = "2001:4888:a1f:c223:01b4:01a2:0:3";
		} else if (substr.equals("74")) {
			cslip = "2001:4888:a1f:c223:1b4:1a2:0:12";
		} else if ((substr.equals("70")) || (substr.equals("71"))) {
			cslip = "2001:4888:a1f:c223:1b4:1a2:0:22";
		} else if ((substr.equals("59")) || (substr.equals("60")) || (substr.equals("68"))) {
			cslip = "2001:4888:a1f:c223:01b4:01a2:0:6";
		} else if ((substr.equals("61")) || (substr.equals("64")) || (substr.equals("66"))) {
			cslip = "2001:4888:a1f:c223:01b4:01a2:0:9";
		} else if ((substr.equals("56")) || (substr.equals("57"))) {
			cslip = "2001:4888:a1f:c223:01b4:01a2:0:12";
		} else if ((substr.equals("58")) || (substr.equals("62"))) {
			cslip = "2001:4888:a1f:c223:01b4:01a2:0:15";
		} else if (substr.equals("65")) {
			cslip = "2001:4888:a1f:c223:1b4:1a2:0:22";
		} else {
			cslip = "0000:0000:0000:0000:0000:0000:0000:0000";
		}

		return cslip;
	}

	public boolean getComisionScriptFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filepath,
			String DAS) {
		boolean status = false;
		try {

			String oam_ip = Constants.ORAN_SPRINT_COMM_SCRIPT_eNB_OAM_IP_eNB_S_B_IP;
			String oam_vlan = "eNB_OAM_VLAN";
			String ENB_SNB_VLAN = "";
			String Enb_SNB_IP = "";
			String Tx_Diversity_val = "";
			String Rx_Diveristy_val = "";
			String leastCellId = "";
			String sheetName = "";

			String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);
			
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
			
			String[] cellIds = dataCiqIndex(listCIQDetailsModel, Constants.ORAN_COMM_SCRIPT_Cell_ID, sheetName);
			List<CIQDetailsModel> listCIQDetailsModelNew = listCIQDetailsModel.stream()
					.filter(x -> x.getCiqMap().containsKey("Cell_ID")
							&& StringUtils.isNotEmpty(x.getCiqMap().get("Cell_ID").getHeaderValue()))
					.collect(Collectors.toList());
			listCIQDetailsModelNew.sort((p1, p2) -> Integer.valueOf(p1.getCiqMap().get("Cell_ID").getHeaderValue())
					.compareTo(Integer.valueOf(p2.getCiqMap().get("Cell_ID").getHeaderValue())));

			List<CIQDetailsModel> listCIQDetailsModelPorts = listCIQDetailsModel.stream()
					.filter(x -> x.getCiqMap().containsKey("Cell_ID")
							&& StringUtils.isNotEmpty(x.getCiqMap().get("Cell_ID").getHeaderValue()))
					.collect(Collectors.toList());
			// listCIQDetailsModelPorts.sort((p1,p2) ->
			// p1.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue().compareTo(p2.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue()));
			String[] portIds = null;
			if(LoadPropertyFiles.getInstance().getProperty("ciqType").equals("NEW")) {
			String[] lccCard = dataCiqIndex(listCIQDetailsModel, "lCCCard", sheetName);
			String[] CRPIPortID = dataCiqIndex(listCIQDetailsModel, "CRPIPortID", sheetName);
			List<String> ls= new ArrayList<>();
			for(int i=0;i<CRPIPortID.length;i++) {
			String channel=String.valueOf(Integer.parseInt(lccCard[i])+1);
			String val= CRPIPortID[i]+"("+"LCC-"+channel+")";
			ls.add(val);
			portIds=(String[]) ls.toArray(new String[ls.size()]);
			}
			}else {
			portIds = dataCiqIndex(listCIQDetailsModelNew, Constants.VZ_GROW_CPRI_Port_Assignment, sheetName);
			}
			Map<String, Integer> cpriPortMap = new HashMap<String, Integer>();
			Map<String, Integer> carrierIndexMap = new HashMap<String, Integer>();

			for (String cpriPort : portIds) {
				cpriPortMap.put(cpriPort, 0);
			}

			for (int i = 0; i < listCIQDetailsModelNew.size(); i++) {
				String portid = portIds[i];
				carrierIndexMap.put(listCIQDetailsModelNew.get(i).getCiqMap().get("Cell_ID").getHeaderValue(),
						cpriPortMap.get(portid));
				cpriPortMap.put(portid, cpriPortMap.get(portid) + 1);
			}

			for (Entry<String, Integer> carrierIndex : carrierIndexMap.entrySet()) {

			}

			if (CommonUtil.isValidObject(cellIds) && cellIds.length > 0) {
				Arrays.sort(cellIds);
				leastCellId = cellIds[0];

			}

			// List<CIQDetailsModel> ciqRows =
			// fileUploadRepository.getCiqRowsByCellId(dbcollectionFileName,sheetName,
			// enbId, leastCellId);

			// if(CommonUtil.isValidObject(ciqRows) && ciqRows.size() > 0) {
			// Tx_Diversity_val=listCIQDetailsModel.get(i).getCiqMap().get("Tx_Diversity").getHeaderValue();
			// Rx_Diveristy_val=listCIQDetailsModel.get(i).getCiqMap().get("Rx_Diveristy").getHeaderValue();
			//
			// }

			// List<CIQDetailsModel> oamRows =
			// fileUploadRepository.getEnBData(dbcollectionFileName,
			// Constants.VZ_GROW_IPPLAN, enbId);

			// String[] oamIpValues = getColumnValuesBySheet(oamRows,
			// oamRows.get(0).getSheetAliasName(), oam_ip);
			// String[] oamVlanValues = getColumnValuesBySheet(oamRows,
			// oamRows.get(0).getSheetAliasName(), oam_vlan);

			// if(CommonUtil.isValidObject(oamIpValues) && oamIpValues.length > 0) {
			// oam_ip_val= oamIpValues[0];
			// }
			// if(CommonUtil.isValidObject(oamVlanValues) && oamVlanValues.length > 0) {
			// oam_vlan_val = oamVlanValues[0];
			// }

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			String defaultOpNone = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_NONE);
			nc_default_operation.appendChild(doc.createTextNode(defaultOpNone));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element mid_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist", "mid:managed-element");
			// mid_managed_element.setAttribute("xmlns:mid",
			// "http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist");

			nc_config.appendChild(mid_managed_element);

			Element mid_enb_function = doc.createElement("mid:enb-function");
			mid_managed_element.appendChild(mid_enb_function);

			Element mid_call_trace = doc.createElement("mid:call-trace");
			mid_enb_function.appendChild(mid_call_trace);

			Element mid_generic_cell = doc.createElement("mid:eutran-generic-cell");
			mid_enb_function.appendChild(mid_generic_cell);
			
			Element mid_csl_control_func = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:csl-control-func");
			mid_csl_control_func.setAttribute("nc:operation", "merge");
			mid_call_trace.appendChild(mid_csl_control_func);
			Element mid_endc_csl = doc.createElement("mid:endc-csl-enable");
			mid_csl_control_func.appendChild(mid_endc_csl);
			mid_endc_csl.appendChild(doc.createTextNode("on"));
			String market=fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);		
			Ip mark = rep.getip(market);
			if(mark==null)
			{
				mark = rep.getip("0");
			}
			

			Element mid_csl_info = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:csl-info");
			mid_csl_info.setAttribute("nc:operation", "merge");
			// mid_csl_info.setAttribute("xmlns:mid",
			// "urn:ietf:params:xml:ns:netconf:base:1.0");
			mid_call_trace.appendChild(mid_csl_info);

			Element mid_csl_ip_ver = doc.createElement("mid:csl-ip-ver");
			mid_csl_info.appendChild(mid_csl_ip_ver);
			String defaultOpipv6 = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_IPV6);
			mid_csl_ip_ver.appendChild(doc.createTextNode(defaultOpipv6));

			Element mid_csl_server_ipv6 = doc.createElement("mid:csl-server-ipv6");
			mid_csl_info.appendChild(mid_csl_server_ipv6);
			//String csl_ip = get_csl_ip(enbId);
			String value = null;
			if (enbId.startsWith("0")) {
				value = enbId.substring(1, 3);
			} else {
				value = enbId.substring(0, 2);
			}
				Ip obj = rep.getip(value);
				if(obj==null) {
					obj=rep.getip(enbId.substring(0, 3));
					if(obj==null) {
						obj=rep.getip("0");
					}
				}
			mid_csl_server_ipv6.appendChild(doc.createTextNode(obj.getCslServerIpv6()));

			Element mid_csl_port_num = doc.createElement("mid:csl-port-num");
			mid_csl_info.appendChild(mid_csl_port_num);
			//String defaultOpPortNum = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_PORT_NO);
			mid_csl_port_num.appendChild(doc.createTextNode(obj.getCslPortNum()));

			Element mid_buffering_time = doc.createElement("mid:buffering-time");
			mid_csl_info.appendChild(mid_buffering_time);
			String defaultOpBuffTime = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BUFF_TIME);
			mid_buffering_time.appendChild(doc.createTextNode(defaultOpBuffTime));

			Element mid_udp_ack_control = doc.createElement("mid:udp-ack-control");
			mid_csl_info.appendChild(mid_udp_ack_control);
			String defaultOpAckControl = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_ACK_CONTROL);
			mid_udp_ack_control.appendChild(doc.createTextNode(defaultOpAckControl));

			Element mid_protocol_selection = doc.createElement("mid:protocol-selection");
			mid_csl_info.appendChild(mid_protocol_selection);
			String defaultOpProtocolSel = resultMapForConstants
					.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_PROTOCOL_SELECTION);
			mid_protocol_selection.appendChild(doc.createTextNode(defaultOpProtocolSel));

			Element mid_csl_report_control = doc.createElement("mid:csl-report-control");
			mid_csl_info.appendChild(mid_csl_report_control);
			String defaultOpReportControl = resultMapForConstants
					.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_REPORT_CONTROL);
			mid_csl_report_control.appendChild(doc.createTextNode(defaultOpReportControl));
			
			
		/*	Element mid_csl_infos = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:csl-info");
			mid_csl_infos.setAttribute("nc:operation", "merge");
			mid_call_trace.appendChild(mid_csl_infos);*/
			Element third_csl_ip_ver = doc.createElement("mid:third-csl-ip-ver");
			mid_csl_info.appendChild(third_csl_ip_ver);
			third_csl_ip_ver.appendChild(doc.createTextNode("ipv6"));
			Element mid_third_csl_server_ipv6 = doc.createElement("mid:third-csl-server-ipv6");
			mid_csl_info.appendChild(mid_third_csl_server_ipv6);
			mid_third_csl_server_ipv6.appendChild(doc.createTextNode(mark.getThirdCslServerIpv6()));
			Element mid_third_csl_port_num = doc.createElement("mid:third-csl-port-num");
			mid_csl_info.appendChild(mid_third_csl_port_num);
			mid_third_csl_port_num.appendChild(doc.createTextNode("50001"));
			Element fourth_csl_ip_ver = doc.createElement("mid:fourth-csl-ip-ver");
			mid_csl_info.appendChild(fourth_csl_ip_ver);
			fourth_csl_ip_ver.appendChild(doc.createTextNode("ipv6"));
			Element mid_fourth_csl_server_ipv6 = doc.createElement("mid:fourth-csl-server-ipv6");
			mid_csl_info.appendChild(mid_fourth_csl_server_ipv6);
			mid_fourth_csl_server_ipv6.appendChild(doc.createTextNode(mark.getThirdCslServerIpv6()));
			Element mid_fourth_csl_port_num = doc.createElement("mid:fourth-csl-port-num");
			mid_csl_info.appendChild(mid_fourth_csl_port_num);
			mid_fourth_csl_port_num.appendChild(doc.createTextNode("50001"));

			Element mid_tce_list = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:tce-list");
			mid_tce_list.setAttribute("nc:operation", "merge");
			mid_call_trace.appendChild(mid_tce_list);
			Element mid_tce_list_index = doc.createElement("mid:tce-list-index");
			mid_tce_list.appendChild(mid_tce_list_index);
			mid_tce_list_index.appendChild(doc.createTextNode("3"));
			Element mid_tce_id = doc.createElement("mid:tce-id");
			mid_tce_list.appendChild(mid_tce_id);
			mid_tce_id.appendChild(doc.createTextNode("3"));
			Element mid_tce_ip_ver = doc.createElement("mid:tce-ip-ver");
			mid_tce_list.appendChild(mid_tce_ip_ver);
			mid_tce_ip_ver.appendChild(doc.createTextNode("ipv6"));
			Element tce_ipv6 = doc.createElement("mid:tce-ipv6");
			mid_tce_list.appendChild(tce_ipv6);
			tce_ipv6.appendChild(doc.createTextNode(mark.getThirdCslServerIpv6()));
			Element mid_tce_lists = doc.createElement("mid:tce-type");
			mid_tce_list.appendChild(mid_tce_lists);
			mid_tce_lists.appendChild(doc.createTextNode("stand-alone"));

			if (CommonUtil.isValidObject(DAS)
					&& (DAS.equalsIgnoreCase("yes") || DAS.equalsIgnoreCase("YES") || DAS.equalsIgnoreCase("Y"))) {
				// Collections.sort(listCIQDetailsModel,
				// Comparator.comparing(CIQDetailsModel::getCiqMap().);
				for (int i = 0; i < listCIQDetailsModelNew.size(); i++) {

					/*
					 * if (listCIQDetailsModel.get(i).getSheetAliasName()
					 * .equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)) {
					 */

					Element mid_eutran_cell_fdd_tdd1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:eutran-cell-fdd-tdd");
					mid_eutran_cell_fdd_tdd1.setAttribute("nc:operation", "merge");
					// mid_eutran_cell_fdd_tdd1.setAttribute("xmlns:mid",
					// "urn:ietf:params:xml:ns:netconf:base:1.0");
					mid_generic_cell.appendChild(mid_eutran_cell_fdd_tdd1);

					Element mid_cell_num1 = doc.createElement("mid:cell-num");
					mid_eutran_cell_fdd_tdd1.appendChild(mid_cell_num1);
					mid_cell_num1.appendChild(doc
							.createTextNode(listCIQDetailsModelNew.get(i).getCiqMap().get("Cell_ID").getHeaderValue()));

					Element mid_user_label1 = doc.createElement("mid:user-label");
					mid_eutran_cell_fdd_tdd1.appendChild(mid_user_label1);
					mid_user_label1.appendChild(doc.createTextNode(
							listCIQDetailsModelNew.get(i).getCiqMap().get("aliasName").getHeaderValue()));

					Element mid_eutran_cell_info = doc.createElement("mid:eutran-cell-info");
					Attr attr2 = doc.createAttribute("nc:operation");
					attr2.setValue("merge");
					mid_eutran_cell_info.setAttributeNode(attr2);
					mid_eutran_cell_fdd_tdd1.appendChild(mid_eutran_cell_info);

					Element mid_cell_dl_total_power = doc.createElement("mid:cell-dl-total-power");
					mid_eutran_cell_info.appendChild(mid_cell_dl_total_power);
					// String
					// roundOffValue=String.valueOf(Math.round(Float.parseFloat(listCIQDetailsModelNew.get(i).getCiqMap().get("DAS_OUTPUT_POWER").getHeaderValue())));
					String totalPower = listCIQDetailsModelNew.get(i).getCiqMap().get("DAS_OUTPUT_POWER")
							.getHeaderValue();
					mid_cell_dl_total_power.appendChild(doc.createTextNode(totalPower));

					// mid_cell_dl_total_power.appendChild(doc.createTextNode("45"));
					// }
				}

				Element mid_hardware_management = doc.createElement("mid:hardware-management");
				mid_managed_element.appendChild(mid_hardware_management);

				Element mid_radio_unit = doc.createElement("mid:radio-unit");
				mid_hardware_management.appendChild(mid_radio_unit);

				String boardId;
				String port_rrh;

				for (int i = 0; i < listCIQDetailsModelPorts.size(); i++) {
					int CARRIER_INDEX = carrierIndexMap
							.get(listCIQDetailsModelPorts.get(i).getCiqMap().get("Cell_ID").getHeaderValue());
					if (CARRIER_INDEX > 0) {
						continue;
					}
					/*
					 * if (listCIQDetailsModel.get(i).getSheetAliasName()
					 * .equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)) {
					 */

					String portid = listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment")
							.getHeaderValue();
					if (portid.contains("LCC-2")) {
						boardId = "1";
					} else {
						boardId = "0";
					}

					if ((portid.contains("LCC") != false) || (portid.contains("-") != false)
							|| (portid.contains("(") != false)) {
						port_rrh = listCIQDetailsModelPorts.get(0).getCiqMap().get("CPRI_Port_Assignment")
								.getHeaderValue();
					} else {
						port_rrh = boardId;
					}

					Element mid_radio_unit_info1 = doc.createElement("mid:radio-unit-info");
					mid_radio_unit.appendChild(mid_radio_unit_info1);

					Element mid_connected_digital_unit_board_type1 = doc
							.createElement("mid:connected-digital-unit-board-type");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_type1);
					String defaultOpBoardType = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BOARD_TYPE);
					mid_connected_digital_unit_board_type1.appendChild(doc.createTextNode(defaultOpBoardType));

					Element mid_connected_digital_unit_board_id1 = doc
							.createElement("mid:connected-digital-unit-board-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_id1);
					mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode(boardId));

					Element mid_connected_digital_unit_port_id1 = doc
							.createElement("mid:connected-digital-unit-port-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_port_id1);
					String channelId = listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment")
							.getHeaderValue();
					;
					if (channelId.contains("(")) {
						channelId = StringUtils.substringBefore(channelId, "(");
					}
					mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(channelId));

					Element mid_cascade_radio_unit_id1 = doc.createElement("mid:cascade-radio-unit-id");
					mid_radio_unit_info1.appendChild(mid_cascade_radio_unit_id1);
					String defaultOpRadioUnitId = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RADIO_UNIT_ID);
					mid_cascade_radio_unit_id1.appendChild(doc.createTextNode(defaultOpRadioUnitId));

					Element mid_external_port = doc.createElement("mid:external-port");
					mid_radio_unit_info1.appendChild(mid_external_port);

					Element mid_cpri_port = doc.createElement("mid:cpri-port");
					mid_external_port.appendChild(mid_cpri_port);

					Element mid_cpri_port_entries = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:cpri-port-entries");
					mid_cpri_port_entries.setAttribute("nc:operation", "merge");
					// mid_cpri_port_entries.setAttribute("xmlns:mid",
					// "urn:ietf:params:xml:ns:netconf:base:1.0");
					mid_cpri_port.appendChild(mid_cpri_port_entries);

					Element mid_port_id = doc.createElement("mid:port-id");
					mid_cpri_port_entries.appendChild(mid_port_id);
					// mid_port_id.appendChild(doc.createTextNode(port_rrh));
					mid_port_id.appendChild(doc.createTextNode("0")); // In perl it is hardcoded

					Element mid_tx_repeater_delay = doc.createElement("mid:tx-repeater-delay");
					mid_cpri_port_entries.appendChild(mid_tx_repeater_delay);
					/*
					 * String defaultOpTxRepDelay = resultMapForConstants
					 * .get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_TX_REP_DELAY);
					 */
					String defaultOpTxRepDelay = listCIQDetailsModelPorts.get(i).getCiqMap()
							.get(Constants.VZ_GROW_antennaPathDelayUL_only).getHeaderValue();
					if (StringUtils.isEmpty(defaultOpTxRepDelay)) {
						defaultOpTxRepDelay = "0";
					}
					mid_tx_repeater_delay.appendChild(
							doc.createTextNode(String.valueOf(Math.round(Float.parseFloat(defaultOpTxRepDelay)))));

					Element mid_rx_repeater_delay = doc.createElement("mid:rx-repeater-delay");
					mid_cpri_port_entries.appendChild(mid_rx_repeater_delay);
					/*
					 * String defaultOpRxRepDelay = resultMapForConstants
					 * .get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RX_REP_DELAY);
					 */
					String defaultOpRxRepDelay = listCIQDetailsModelPorts.get(i).getCiqMap()
							.get(Constants.VZ_GROW_antennaPathDelayDL_only).getHeaderValue();
					if (StringUtils.isEmpty(defaultOpRxRepDelay)) {
						defaultOpRxRepDelay = "0";
					}
					mid_rx_repeater_delay.appendChild(
							doc.createTextNode(String.valueOf(Math.round(Float.parseFloat(defaultOpRxRepDelay)))));

					Element mid_carrier_control_info1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:carrier-control-info");
					mid_carrier_control_info1.setAttribute("nc:operation", "merge");
					// mid_carrier_control_info1.setAttribute("xmlns:mid",
					// "urn:ietf:params:xml:ns:netconf:base:1.0");
					mid_radio_unit_info1.appendChild(mid_carrier_control_info1);

					Element mid_carrier_index1 = doc.createElement("mid:carrier-index");
					mid_carrier_control_info1.appendChild(mid_carrier_index1);
					// String defaultOpCarrierIndex =
					// resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_CARRIER_INDEX);
					mid_carrier_index1.appendChild(doc.createTextNode(String.valueOf(CARRIER_INDEX)));

					Element mid_rssi_high_alarm_threshold1 = doc.createElement("mid:rssi-high-alarm-threshold");
					mid_carrier_control_info1.appendChild(mid_rssi_high_alarm_threshold1);
					String defaultOpAlrmThreshold = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_ALARM_THRESHOLD);
					mid_rssi_high_alarm_threshold1.appendChild(doc.createTextNode(defaultOpAlrmThreshold));

					for (CIQDetailsModel model : listCIQDetailsModelPorts) {
						String currentCellId = model.getCiqMap().get("Cell_ID").getHeaderValue();
						String matchingCellId = listCIQDetailsModelPorts.get(i).getCiqMap().get("Cell_ID")
								.getHeaderValue();

						String currentPort = model.getCiqMap().get("CPRI_Port_Assignment").getHeaderValue();
						String matchingPort = listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment")
								.getHeaderValue();

						int index = carrierIndexMap.get(model.getCiqMap().get("Cell_ID").getHeaderValue());
						if (currentPort.equalsIgnoreCase(matchingPort) && currentCellId != matchingCellId) {
							Element mid_carrier_control_info_temp = doc.createElementNS(
									"urn:ietf:params:xml:ns:netconf:base:1.0", "mid:carrier-control-info");
							mid_carrier_control_info_temp.setAttribute("nc:operation", "merge");
							mid_radio_unit_info1.appendChild(mid_carrier_control_info_temp);

							Element mid_carrier_index_temp = doc.createElement("mid:carrier-index");
							mid_carrier_control_info_temp.appendChild(mid_carrier_index_temp);
							mid_carrier_index_temp.appendChild(doc.createTextNode(String.valueOf(index)));

							Element mid_rssi_high_alarm_threshold_temp = doc
									.createElement("mid:rssi-high-alarm-threshold");
							mid_carrier_control_info_temp.appendChild(mid_rssi_high_alarm_threshold_temp);
							mid_rssi_high_alarm_threshold_temp.appendChild(doc.createTextNode(defaultOpAlrmThreshold));
						}
					}

					// }
				}

				Element mid_common_management = doc.createElement("mid:common-management");
				mid_managed_element.appendChild(mid_common_management);

				Element mid_time_sync_service = doc.createElement("mid:time-sync-service");
				mid_common_management.appendChild(mid_time_sync_service);

				Element mid_ntp_info1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:ntp-info");
				mid_ntp_info1.setAttribute("nc:operation", "merge");
				// mid_ntp_info1.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				mid_time_sync_service.appendChild(mid_ntp_info1);

				Element mid_server_type1 = doc.createElement("mid:server-type");
				mid_ntp_info1.appendChild(mid_server_type1);
				String defaultOpServerType1 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_TYPE);
				mid_server_type1.appendChild(doc.createTextNode(defaultOpServerType1));

				Element mid_server_ip_address1 = doc.createElement("mid:server-ip-address");
				mid_ntp_info1.appendChild(mid_server_ip_address1);
				String defaultOpServerIp1 = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_IP);
				mid_server_ip_address1.appendChild(doc.createTextNode(defaultOpServerIp1));

				Element mid_ntp_info2 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:ntp-info");
				mid_ntp_info2.setAttribute("nc:operation", "merge");
				// mid_ntp_info2.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				mid_time_sync_service.appendChild(mid_ntp_info2);

				Element mid_server_type2 = doc.createElement("mid:server-type");
				mid_ntp_info2.appendChild(mid_server_type2);
				String defaultOpServerType2 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_TYPE2);
				mid_server_type2.appendChild(doc.createTextNode(defaultOpServerType2));

				Element mid_server_ip_address2 = doc.createElement("mid:server-ip-address");
				mid_ntp_info2.appendChild(mid_server_ip_address2);
				String defaultOpServerIp2 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_IP2);
				mid_server_ip_address2.appendChild(doc.createTextNode(defaultOpServerIp2));
			} else {

				// List<String>
				// data=listCIQDetailsModel.stream().map(x->x.getCiqMap().get("Cell_ID").getHeaderValue()).collect(Collectors.toList());
				// listCIQDetailsModel.sort((p1,p2) ->
				// p1.getCiqMap().get("Cell_ID").getHeaderValue().compareTo(p2.getCiqMap().get("Cell_ID").getHeaderValue()));
				for (int i = 0; i < listCIQDetailsModelNew.size(); i++) {

					/*
					 * if (listCIQDetailsModelNew.get(i).getSheetAliasName()
					 * .equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)) {
					 */

					Element mid_eutran_cell_fdd_tdd1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
							"mid:eutran-cell-fdd-tdd");
					mid_eutran_cell_fdd_tdd1.setAttribute("nc:operation", "merge");
					// mid_eutran_cell_fdd_tdd1.setAttribute("xmlns:mid",
					// "urn:ietf:params:xml:ns:netconf:base:1.0");
					mid_generic_cell.appendChild(mid_eutran_cell_fdd_tdd1);

					Element mid_cell_num1 = doc.createElement("mid:cell-num");
					mid_eutran_cell_fdd_tdd1.appendChild(mid_cell_num1);
					mid_cell_num1.appendChild(doc
							.createTextNode(listCIQDetailsModelNew.get(i).getCiqMap().get("Cell_ID").getHeaderValue()));

					Element mid_user_label1 = doc.createElement("mid:user-label");
					mid_eutran_cell_fdd_tdd1.appendChild(mid_user_label1);
					mid_user_label1.appendChild(doc.createTextNode(
							listCIQDetailsModelNew.get(i).getCiqMap().get("aliasName").getHeaderValue()));

					Element mid_eutran_cell_info = doc.createElement("mid:eutran-cell-conf-idle");
					mid_eutran_cell_info.setAttribute("nc:operation", "merge");
					mid_eutran_cell_fdd_tdd1.appendChild(mid_eutran_cell_info);
					
					//Commented the code  according to SRCT-423
					/*
					Tx_Diversity_val = listCIQDetailsModelNew.get(i).getCiqMap().get("Tx_Diversity").getHeaderValue();
					Rx_Diveristy_val = listCIQDetailsModelNew.get(i).getCiqMap().get("Rx_Diveristy").getHeaderValue();
					if (Tx_Diversity_val.contains("T")) {
						Tx_Diversity_val = StringUtils.substringBefore(Tx_Diversity_val, "T");
					}
					if (Rx_Diveristy_val.contains("T")) {
						Rx_Diveristy_val = StringUtils.substringBefore(Rx_Diveristy_val, "T");
					}

					String Antenna_Count1 = "n" + Tx_Diversity_val + "-tx-antenna-count";
					String Antenna_Count2 = "n" + Rx_Diveristy_val + "-rx-antenna-count";
					String CRS_Port_Count = "n" + Tx_Diversity_val;

					Element mid_dl_antenna_count = doc.createElement("mid:dl-antenna-count");
					mid_eutran_cell_info.appendChild(mid_dl_antenna_count);
					mid_dl_antenna_count.appendChild(doc.createTextNode(Antenna_Count1));

					Element mid_ul_antenna_count = doc.createElement("mid:ul-antenna-count");
					mid_eutran_cell_info.appendChild(mid_ul_antenna_count);
					mid_ul_antenna_count.appendChild(doc.createTextNode(Antenna_Count2));

					Element mid_dl_crs_port_count = doc.createElement("mid:dl-crs-port-count");
					mid_eutran_cell_info.appendChild(mid_dl_crs_port_count);
					mid_dl_crs_port_count.appendChild(doc.createTextNode(CRS_Port_Count));
					*/
					// }

				}

				Element mid_hardware_management = doc.createElement("mid:hardware-management");
				mid_managed_element.appendChild(mid_hardware_management);

				Element mid_radio_unit = doc.createElement("mid:radio-unit");
				mid_hardware_management.appendChild(mid_radio_unit);

				// supriya:new implementation

				String cpriPort = null;
				// String portIdValue = null;
				ArrayList<String> arr1 = new ArrayList<>();
				ArrayList<String> arr2 = new ArrayList<>();
				ArrayList<String> arr3 = new ArrayList<>();
				LinkedHashMap<String, Integer> hs1 = new LinkedHashMap<>();
				LinkedHashMap<String, Integer> hs2 = new LinkedHashMap<>();
				LinkedHashMap<String, Integer> hs3 = new LinkedHashMap<>();
				for (int ii = 0; ii < listCIQDetailsModelPorts.size(); ii++) {
					cpriPort = portIds[ii];
					String valCpri = cpriPort.replaceAll(" ", "");
					if (cpriPort.contains("LCC-1")) {
						arr1.add(valCpri);
					} else if (cpriPort.contains("LCC-2")) {
						arr2.add(valCpri);
					} else if (cpriPort.contains("LCC-3")) {
						arr3.add(valCpri);
					} else {
						arr1.add(valCpri);
					}
				}
				for (int i = 0; i < arr1.size(); i++) {
					if (hs1.containsKey(arr1.get(i))) {
						hs1.put(arr1.get(i), hs1.get(arr1.get(i)) + 1);
					} else {
						hs1.put(arr1.get(i), 1);
					}

				}
				for (int i = 0; i < arr2.size(); i++) {
					if (hs2.containsKey(arr2.get(i))) {
						hs2.put(arr2.get(i), hs2.get(arr2.get(i)) + 1);
					} else {
						hs2.put(arr2.get(i), 1);
					}
				}
				for (int i = 0; i < arr3.size(); i++) {
					if (hs3.containsKey(arr3.get(i))) {
						hs3.put(arr3.get(i), hs3.get(arr3.get(i)) + 1);
					} else {
						hs3.put(arr3.get(i), 1);
					}
				}
				for (int is = 0; is < hs1.size(); is++) {
					Set<String> keySet = hs1.keySet();

					List<String> listKeys = new ArrayList<String>(keySet);
					String key = listKeys.get(is);
					String channelId = StringUtils.substringBefore(key, "(");
					Element mid_radio_unit_info1 = doc.createElement("mid:radio-unit-info");
					mid_radio_unit.appendChild(mid_radio_unit_info1);

					Element mid_connected_digital_unit_board_type1 = doc
							.createElement("mid:connected-digital-unit-board-type");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_type1);
					String defaultOpBoardType = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BOARD_TYPE);
					mid_connected_digital_unit_board_type1.appendChild(doc.createTextNode(defaultOpBoardType));

					Element mid_connected_digital_unit_board_id1 = doc
							.createElement("mid:connected-digital-unit-board-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_id1);
					mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("0"));
					Element mid_connected_digital_unit_port_id1 = doc
							.createElement("mid:connected-digital-unit-port-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_port_id1);
					mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(channelId));
					Element mid_cascade_radio_unit_id1 = doc.createElement("mid:cascade-radio-unit-id");
					mid_radio_unit_info1.appendChild(mid_cascade_radio_unit_id1);
					String defaultOpRadioUnitId = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RADIO_UNIT_ID);
					mid_cascade_radio_unit_id1.appendChild(doc.createTextNode(defaultOpRadioUnitId));
					for (int c = 0; c < hs1.get(key); c++) {
						Element mid_carrier_control_info1 = doc
								.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:carrier-control-info");
						mid_carrier_control_info1.setAttribute("nc:operation", "merge");
						// mid_carrier_control_info1.setAttribute("xmlns:mid",
						// "urn:ietf:params:xml:ns:netconf:base:1.0");
						mid_radio_unit_info1.appendChild(mid_carrier_control_info1);

						Element mid_carrier_index1 = doc.createElement("mid:carrier-index");
						mid_carrier_control_info1.appendChild(mid_carrier_index1);
						// String defaultOpCarrierIndex =
						// resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_CARRIER_INDEX);
						mid_carrier_index1.appendChild(doc.createTextNode(String.valueOf(c)));

						Element mid_rssi_high_alarm_threshold1 = doc.createElement("mid:rssi-high-alarm-threshold");
						mid_carrier_control_info1.appendChild(mid_rssi_high_alarm_threshold1);
						String defaultOpAlrmThreshold = resultMapForConstants
								.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_ALARM_THRESHOLD);
						mid_rssi_high_alarm_threshold1.appendChild(doc.createTextNode(defaultOpAlrmThreshold));
					}

				}

				for (int is = 0; is < hs2.size(); is++) {
					Set<String> keySet = hs2.keySet();
					List<String> listKeys = new ArrayList<String>(keySet);
					String key = listKeys.get(is);
					String channelId = StringUtils.substringBefore(key, "(");

					Element mid_radio_unit_info1 = doc.createElement("mid:radio-unit-info");
					mid_radio_unit.appendChild(mid_radio_unit_info1);

					Element mid_connected_digital_unit_board_type1 = doc
							.createElement("mid:connected-digital-unit-board-type");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_type1);
					String defaultOpBoardType = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BOARD_TYPE);
					mid_connected_digital_unit_board_type1.appendChild(doc.createTextNode(defaultOpBoardType));

					Element mid_connected_digital_unit_board_id1 = doc
							.createElement("mid:connected-digital-unit-board-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_id1);
					mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("1"));
					Element mid_connected_digital_unit_port_id1 = doc
							.createElement("mid:connected-digital-unit-port-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_port_id1);
					mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(channelId));
					Element mid_cascade_radio_unit_id1 = doc.createElement("mid:cascade-radio-unit-id");
					mid_radio_unit_info1.appendChild(mid_cascade_radio_unit_id1);
					String defaultOpRadioUnitId = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RADIO_UNIT_ID);
					mid_cascade_radio_unit_id1.appendChild(doc.createTextNode(defaultOpRadioUnitId));

					for (int c2 = 0; c2 < hs2.get(key); c2++) {
						Element mid_carrier_control_info1 = doc
								.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:carrier-control-info");
						mid_carrier_control_info1.setAttribute("nc:operation", "merge");
						// mid_carrier_control_info1.setAttribute("xmlns:mid",
						// "urn:ietf:params:xml:ns:netconf:base:1.0");
						mid_radio_unit_info1.appendChild(mid_carrier_control_info1);

						Element mid_carrier_index1 = doc.createElement("mid:carrier-index");
						mid_carrier_control_info1.appendChild(mid_carrier_index1);
						// String defaultOpCarrierIndex =
						// resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_CARRIER_INDEX);
						mid_carrier_index1.appendChild(doc.createTextNode(String.valueOf(c2)));

						Element mid_rssi_high_alarm_threshold1 = doc.createElement("mid:rssi-high-alarm-threshold");
						mid_carrier_control_info1.appendChild(mid_rssi_high_alarm_threshold1);
						String defaultOpAlrmThreshold = resultMapForConstants
								.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_ALARM_THRESHOLD);
						mid_rssi_high_alarm_threshold1.appendChild(doc.createTextNode(defaultOpAlrmThreshold));
					}
				}
				for (int is = 0; is < hs3.size(); is++) {
					Set<String> keySet = hs3.keySet();

					List<String> listKeys = new ArrayList<String>(keySet);
					String key = listKeys.get(is);
					String channelId = StringUtils.substringBefore(key, "(");
					Element mid_radio_unit_info1 = doc.createElement("mid:radio-unit-info");
					mid_radio_unit.appendChild(mid_radio_unit_info1);

					Element mid_connected_digital_unit_board_type1 = doc
							.createElement("mid:connected-digital-unit-board-type");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_type1);
					String defaultOpBoardType = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BOARD_TYPE);
					mid_connected_digital_unit_board_type1.appendChild(doc.createTextNode(defaultOpBoardType));

					Element mid_connected_digital_unit_board_id1 = doc
							.createElement("mid:connected-digital-unit-board-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_id1);
					mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("2"));
					Element mid_connected_digital_unit_port_id1 = doc
							.createElement("mid:connected-digital-unit-port-id");
					mid_radio_unit_info1.appendChild(mid_connected_digital_unit_port_id1);
					mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(channelId));
					Element mid_cascade_radio_unit_id1 = doc.createElement("mid:cascade-radio-unit-id");
					mid_radio_unit_info1.appendChild(mid_cascade_radio_unit_id1);
					String defaultOpRadioUnitId = resultMapForConstants
							.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RADIO_UNIT_ID);
					mid_cascade_radio_unit_id1.appendChild(doc.createTextNode(defaultOpRadioUnitId));
					for (int c3 = 0; c3 < hs3.get(key); c3++) {
						Element mid_carrier_control_info1 = doc
								.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:carrier-control-info");
						mid_carrier_control_info1.setAttribute("nc:operation", "merge");
						// mid_carrier_control_info1.setAttribute("xmlns:mid",
						// "urn:ietf:params:xml:ns:netconf:base:1.0");
						mid_radio_unit_info1.appendChild(mid_carrier_control_info1);

						Element mid_carrier_index1 = doc.createElement("mid:carrier-index");
						mid_carrier_control_info1.appendChild(mid_carrier_index1);
						// String defaultOpCarrierIndex =
						// resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_CARRIER_INDEX);
						mid_carrier_index1.appendChild(doc.createTextNode(String.valueOf(c3)));

						Element mid_rssi_high_alarm_threshold1 = doc.createElement("mid:rssi-high-alarm-threshold");
						mid_carrier_control_info1.appendChild(mid_rssi_high_alarm_threshold1);
						String defaultOpAlrmThreshold = resultMapForConstants
								.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_ALARM_THRESHOLD);
						mid_rssi_high_alarm_threshold1.appendChild(doc.createTextNode(defaultOpAlrmThreshold));
					}
				}

				/*
				 * String boardId; String port_rrh; int count =0; for(CIQDetailsModel model:
				 * listCIQDetailsModelPorts){ String ruport_id =
				 * model.getCiqMap().get("RU_port").getHeaderValue();
				 * if(ruport_id.contains("a-0") || ruport_id.contains("b-0") ||
				 * ruport_id.contains("g-0")) { count++; } } String[] portId =
				 * dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_CPRI_Port_Assignment,
				 * sheetName); int val=0; for(int k=0;k<portId.length;k++) {
				 * if(portId[k].contains("LCC-3")) { val++; } } for (int i = 0; i <
				 * listCIQDetailsModelPorts.size()-count; i++) {
				 * 
				 * 
				 * int CARRIER_INDEX =
				 * carrierIndexMap.get(listCIQDetailsModelPorts.get(i).getCiqMap().get("Cell_ID"
				 * ).getHeaderValue()); if(CARRIER_INDEX>0){ continue; } if
				 * (listCIQDetailsModel.get(i).getSheetAliasName()
				 * .equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)) {
				 * 
				 * 
				 * String portid =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment")
				 * .getHeaderValue(); if (portid.contains("LCC-2")) { boardId = "1"; } else {
				 * boardId = "0"; }
				 * 
				 * if ((portid.contains("LCC") != false) || (portid.contains("-") != false) ||
				 * (portid.contains("(") != false)) { port_rrh =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment")
				 * .getHeaderValue(); } else { port_rrh = portid; }
				 * 
				 * Element mid_radio_unit_info1 = doc.createElement("mid:radio-unit-info");
				 * mid_radio_unit.appendChild(mid_radio_unit_info1);
				 * 
				 * Element mid_connected_digital_unit_board_type1 = doc
				 * .createElement("mid:connected-digital-unit-board-type");
				 * mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_type1);
				 * String defaultOpBoardType = resultMapForConstants
				 * .get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_BOARD_TYPE);
				 * mid_connected_digital_unit_board_type1.appendChild(doc.createTextNode(
				 * defaultOpBoardType));
				 * 
				 * Element mid_connected_digital_unit_board_id1 = doc
				 * .createElement("mid:connected-digital-unit-board-id");
				 * mid_radio_unit_info1.appendChild(mid_connected_digital_unit_board_id1);
				 * 
				 * String channelId =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment").
				 * getHeaderValue(); if(channelId.contains( "(")){
				 * channelId=StringUtils.substringBefore(channelId, "("); }
				 * if(Integer.parseInt(channelId) == 6 && val>0) {
				 * mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("2"));
				 * 
				 * } else if(Integer.parseInt(channelId) == 7 && val>0) {
				 * mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("2"));
				 * 
				 * } else if(Integer.parseInt(channelId) == 8 && val>0) {
				 * mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode("2")); }
				 * else {
				 * mid_connected_digital_unit_board_id1.appendChild(doc.createTextNode(boardId))
				 * ; }
				 * 
				 * 
				 * Element mid_connected_digital_unit_port_id1 = doc
				 * .createElement("mid:connected-digital-unit-port-id");
				 * mid_radio_unit_info1.appendChild(mid_connected_digital_unit_port_id1);
				 * 
				 * 
				 * if(Integer.parseInt(channelId) == 7) { channelId = Integer.toString(8); }
				 * if(Integer.parseInt(channelId) == 8) { channelId = Integer.toString(10); }
				 * 
				 * if(Integer.parseInt(channelId) == 7) {
				 * mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(Integer.
				 * toString(8)));
				 * 
				 * } else if(Integer.parseInt(channelId) == 8) {
				 * mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(Integer.
				 * toString(10))); } else {
				 * mid_connected_digital_unit_port_id1.appendChild(doc.createTextNode(channelId)
				 * ); } Element mid_cascade_radio_unit_id1 =
				 * doc.createElement("mid:cascade-radio-unit-id");
				 * mid_radio_unit_info1.appendChild(mid_cascade_radio_unit_id1); String
				 * defaultOpRadioUnitId = resultMapForConstants
				 * .get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_RADIO_UNIT_ID);
				 * mid_cascade_radio_unit_id1.appendChild(doc.createTextNode(
				 * defaultOpRadioUnitId));
				 * 
				 * 
				 * Element mid_carrier_control_info1 =
				 * doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
				 * "mid:carrier-control-info");
				 * mid_carrier_control_info1.setAttribute("nc:operation", "merge"); //
				 * mid_carrier_control_info1.setAttribute("xmlns:mid",
				 * "urn:ietf:params:xml:ns:netconf:base:1.0");
				 * mid_radio_unit_info1.appendChild(mid_carrier_control_info1);
				 * 
				 * Element mid_carrier_index1 = doc.createElement("mid:carrier-index");
				 * mid_carrier_control_info1.appendChild(mid_carrier_index1); //String
				 * defaultOpCarrierIndex =
				 * resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_CARRIER_INDEX
				 * ); mid_carrier_index1.appendChild(doc.createTextNode(String.valueOf(
				 * CARRIER_INDEX)));
				 * 
				 * Element mid_rssi_high_alarm_threshold1 =
				 * doc.createElement("mid:rssi-high-alarm-threshold");
				 * mid_carrier_control_info1.appendChild(mid_rssi_high_alarm_threshold1); String
				 * defaultOpAlrmThreshold = resultMapForConstants.get(Constants.
				 * ORAN_COMM_SCRIPT_DEFAULT_OP_ALARM_THRESHOLD);
				 * mid_rssi_high_alarm_threshold1.appendChild(doc.createTextNode(
				 * defaultOpAlrmThreshold)); int ruindex =1;
				 * 
				 * for(CIQDetailsModel model: listCIQDetailsModelPorts){ String currentCellId =
				 * model.getCiqMap().get("Cell_ID").getHeaderValue(); String matchingCellId =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("Cell_ID").getHeaderValue();
				 * 
				 * String currentRuport = model.getCiqMap().get("RU_port").getHeaderValue();
				 * String matchingRuport =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("RU_port").getHeaderValue();
				 * 
				 * String currentPort =
				 * model.getCiqMap().get("CPRI_Port_Assignment").getHeaderValue(); String
				 * matchingPort =
				 * listCIQDetailsModelPorts.get(i).getCiqMap().get("CPRI_Port_Assignment").
				 * getHeaderValue(); int index =
				 * carrierIndexMap.get(model.getCiqMap().get("Cell_ID").getHeaderValue());
				 * if(currentPort.equalsIgnoreCase(matchingPort) && currentCellId !=
				 * matchingCellId && !matchingRuport.equals("a-0") &&
				 * !matchingRuport.equals("a-1") && !matchingRuport.equals("b-0") &&
				 * !matchingRuport.equals("b-1") && !matchingRuport.equals("g-0") &&
				 * !matchingRuport.equals("g-1")){ Element mid_carrier_control_info_temp =
				 * doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
				 * "mid:carrier-control-info");
				 * mid_carrier_control_info_temp.setAttribute("nc:operation", "merge");
				 * mid_radio_unit_info1.appendChild(mid_carrier_control_info_temp);
				 * 
				 * 
				 * Element mid_carrier_index_temp = doc.createElement("mid:carrier-index");
				 * mid_carrier_control_info_temp.appendChild(mid_carrier_index_temp);
				 * mid_carrier_index_temp.appendChild(doc.createTextNode(String.valueOf(index)))
				 * ;
				 * 
				 * Element mid_rssi_high_alarm_threshold_temp =
				 * doc.createElement("mid:rssi-high-alarm-threshold");
				 * mid_carrier_control_info_temp.appendChild(mid_rssi_high_alarm_threshold_temp)
				 * ; mid_rssi_high_alarm_threshold_temp.appendChild(doc.createTextNode(
				 * defaultOpAlrmThreshold)); } else if (currentRuport.equals("a-0") ||
				 * currentRuport.equals("a-1") || currentRuport.equals("b-0") ||
				 * currentRuport.equals("b-1") || currentRuport.equals("g-0") ||
				 * currentRuport.equals("g-1")) {
				 * if(currentRuport.equalsIgnoreCase(matchingRuport)) { Element
				 * mid_carrier_control_info_temp =
				 * doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
				 * "mid:carrier-control-info");
				 * mid_carrier_control_info_temp.setAttribute("nc:operation", "merge");
				 * mid_radio_unit_info1.appendChild(mid_carrier_control_info_temp);
				 * 
				 * 
				 * Element mid_carrier_index_temp = doc.createElement("mid:carrier-index");
				 * mid_carrier_control_info_temp.appendChild(mid_carrier_index_temp);
				 * mid_carrier_index_temp.appendChild(doc.createTextNode(String.valueOf(ruindex)
				 * )); ruindex++; Element mid_rssi_high_alarm_threshold_temp =
				 * doc.createElement("mid:rssi-high-alarm-threshold");
				 * mid_carrier_control_info_temp.appendChild(mid_rssi_high_alarm_threshold_temp)
				 * ; mid_rssi_high_alarm_threshold_temp.appendChild(doc.createTextNode(
				 * defaultOpAlrmThreshold)); }
				 * 
				 * 
				 * } if(ruindex==3) { Element mid_carrier_control_info_temp =
				 * doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
				 * "mid:carrier-control-info");
				 * mid_carrier_control_info_temp.setAttribute("nc:operation", "merge");
				 * mid_radio_unit_info1.appendChild(mid_carrier_control_info_temp);
				 * 
				 * 
				 * Element mid_carrier_index_temp = doc.createElement("mid:carrier-index");
				 * mid_carrier_control_info_temp.appendChild(mid_carrier_index_temp);
				 * mid_carrier_index_temp.appendChild(doc.createTextNode(String.valueOf(ruindex)
				 * )); ruindex++; Element mid_rssi_high_alarm_threshold_temp =
				 * doc.createElement("mid:rssi-high-alarm-threshold");
				 * mid_carrier_control_info_temp.appendChild(mid_rssi_high_alarm_threshold_temp)
				 * ; mid_rssi_high_alarm_threshold_temp.appendChild(doc.createTextNode(
				 * defaultOpAlrmThreshold)); } }
				 * 
				 * //} }
				 */

				Element mid_common_management = doc.createElement("mid:common-management");
				mid_managed_element.appendChild(mid_common_management);

				Element mid_time_sync_service = doc.createElement("mid:time-sync-service");
				mid_common_management.appendChild(mid_time_sync_service);

				Element mid_ntp_info1 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:ntp-info");
				mid_ntp_info1.setAttribute("nc:operation", "merge");
				// mid_ntp_info1.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				mid_time_sync_service.appendChild(mid_ntp_info1);

				Element mid_server_type1 = doc.createElement("mid:server-type");
				mid_ntp_info1.appendChild(mid_server_type1);
				String defaultOpServerType1 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_TYPE);
				mid_server_type1.appendChild(doc.createTextNode(defaultOpServerType1));

				Element mid_server_ip_address1 = doc.createElement("mid:server-ip-address");
				mid_ntp_info1.appendChild(mid_server_ip_address1);
				String defaultOpServerIp1 = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_IP);
				mid_server_ip_address1.appendChild(doc.createTextNode(defaultOpServerIp1));

				Element mid_ntp_info2 = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "mid:ntp-info");
				mid_ntp_info2.setAttribute("nc:operation", "merge");
				// mid_ntp_info2.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				mid_time_sync_service.appendChild(mid_ntp_info2);

				Element mid_server_type2 = doc.createElement("mid:server-type");
				mid_ntp_info2.appendChild(mid_server_type2);
				String defaultOpServerType2 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_TYPE2);
				mid_server_type2.appendChild(doc.createTextNode(defaultOpServerType2));

				Element mid_server_ip_address2 = doc.createElement("mid:server-ip-address");
				mid_ntp_info2.appendChild(mid_server_ip_address2);
				String defaultOpServerIp2 = resultMapForConstants
						.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_SEREVER_IP2);
				mid_server_ip_address2.appendChild(doc.createTextNode(defaultOpServerIp2));
			}
			Element mid_ip_system = doc.createElement("mid:ip-system");
			mid_managed_element.appendChild(mid_ip_system);

			Element mid_ip_interface = doc.createElement("mid:ip-interface");
			mid_ip_system.appendChild(mid_ip_interface);

			List<Integer> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());

			List<Integer> vlanIP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());
			String vlan1;
			String vlan2;

			String OAM_VLAN, s_bVlan;

			//
			List<CIQDetailsModel> listCiq = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)).collect(Collectors.toList());
			if (listCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				List<Integer> objVlan1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				List<Integer> vlanIP1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				vlan1 = objVlan1.get(0).toString();

				vlan2 = objVlan1.get(1).toString();

				if (vlanIP1.get(0).toString().contains("1")) {
					OAM_VLAN = vlan1;
					s_bVlan = vlan2;

				} else {
					OAM_VLAN = vlan2;
					s_bVlan = vlan1;

				}
			} else {
				vlan1 = objVlan.get(0).toString();

				vlan2 = objVlan.get(1).toString();
				if (vlanIP.get(0).toString().contains("1")) {
					OAM_VLAN = vlan1;
					s_bVlan = vlan2;

				} else {
					OAM_VLAN = vlan2;
					s_bVlan = vlan1;

				}
			}

			String IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			Element mid_external_interfaces = doc.createElement("mid:external-interfaces");
			mid_ip_interface.appendChild(mid_external_interfaces);

			ENB_SNB_VLAN = s_bVlan;
			Enb_SNB_IP = IP2;

			Element mid_interface_name = doc.createElement("mid:interface-name");
			mid_external_interfaces.appendChild(mid_interface_name);
			mid_interface_name.appendChild(doc.createTextNode("ge_0_0_1." + ENB_SNB_VLAN));

			Element mid_ipv6_address = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0",
					"mid:ipv6-address");
			mid_ipv6_address.setAttribute("nc:operation", "merge");
			// mid_ipv6_address.setAttribute("xmlns:mid",
			// "urn:ietf:params:xml:ns:netconf:base:1.0");
			mid_external_interfaces.appendChild(mid_ipv6_address);

			Element mid_ip = doc.createElement("mid:ip");
			mid_ipv6_address.appendChild(mid_ip);
			mid_ip.appendChild(doc.createTextNode(Enb_SNB_IP));

			Element mid_prefix_length = doc.createElement("mid:prefix-length");
			mid_ipv6_address.appendChild(mid_prefix_length);
			mid_prefix_length.appendChild(doc.createTextNode("64"));

			Element mid_signal_s1 = doc.createElement("mid:signal-s1");
			mid_ipv6_address.appendChild(mid_signal_s1);
			mid_signal_s1.appendChild(doc.createTextNode("true"));

			Element mid_signal_x1 = doc.createElement("mid:signal-x2");
			mid_ipv6_address.appendChild(mid_signal_x1);
			mid_signal_x1.appendChild(doc.createTextNode("true"));

			Element mid_signal_s2 = doc.createElement("mid:bearer-s1");
			mid_ipv6_address.appendChild(mid_signal_s2);
			mid_signal_s2.appendChild(doc.createTextNode("true"));

			Element mid_signal_x2 = doc.createElement("mid:bearer-x2");
			mid_ipv6_address.appendChild(mid_signal_x2);
			mid_signal_x2.appendChild(doc.createTextNode("true"));
			
			// mid_interface_name

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			writeXMLattribute(xmlFile);

			status = true;
		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
//************************************fsu****************************************8//
	public boolean getComisionScriptFileFSU(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filepath,
			String DAS) {
		boolean status = false;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			///Document doc = dBuilder.newDocument();
			// add elements to Document
			String DBxml = resultMapForConstants
					.get("FSU_COMM_SCRIPT_ADMIN_UNLOCK");
			Document doc = dBuilder.parse(new InputSource(new StringReader(DBxml)));
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			writeXMLattribute(xmlFile);

			status = true;

		}
		catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	
		}
	
	public boolean getComisionScriptFileNTPFSU(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filepath,
			String DAS) {
		boolean status = false;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			// add elements to Document
			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			//String defaultOpNone = resultMapForConstants.get(Constants.ORAN_COMM_SCRIPT_DEFAULT_OP_NONE);
			String defaultOpNone = "none";

			nc_default_operation.appendChild(doc.createTextNode(defaultOpNone));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element cfsu_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/cfsu", "cfsu:managed-element");
			// mid_managed_element.setAttribute("xmlns:mid",
			// "http://www.samsung.com/global/business/4GvRAN/ns/macro_indoor_dist");

			nc_config.appendChild(cfsu_managed_element);

			Element cfsu_common_management = doc.createElement("cfsu:common-management");
				cfsu_managed_element.appendChild(cfsu_common_management);

				Element cfsu_time_sync_service = doc.createElement("cfsu:time-sync-service");
				cfsu_common_management.appendChild(cfsu_time_sync_service);

				Element cfsu_ntp_info1 = doc.createElement("cfsu:ntp-info");
				cfsu_ntp_info1.setAttribute("nc:operation", "merge");
				// mid_ntp_info1.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				cfsu_time_sync_service.appendChild(cfsu_ntp_info1);

				Element cfsu_server_type1 = doc.createElement("cfsu:server-type");
				cfsu_ntp_info1.appendChild(cfsu_server_type1);
				String defaultOpServerType1 = resultMapForConstants
					.get(Constants.NTP_COMM_SCRIPT_PRIMARY_SEREVER_TYPE);
				
				cfsu_server_type1.appendChild(doc.createTextNode(defaultOpServerType1));

				Element cfsu_server_ip_address1 = doc.createElement("cfsu:server-ip-address");
				cfsu_ntp_info1.appendChild(cfsu_server_ip_address1);
				String defaultOpServerIp1 = resultMapForConstants.get(Constants.NTP_COMM_SCRIPT_PRIMARY_SEREVER_IP);
				

				cfsu_server_ip_address1.appendChild(doc.createTextNode(defaultOpServerIp1));

				Element cfsu_ntp_info2 = doc.createElement("cfsu:ntp-info");
				cfsu_ntp_info2.setAttribute("nc:operation", "merge");
				// mid_ntp_info2.setAttribute("xmlns:mid",
				// "urn:ietf:params:xml:ns:netconf:base:1.0");
				cfsu_time_sync_service.appendChild(cfsu_ntp_info2);

				Element cfsu_server_type2 = doc.createElement("cfsu:server-type");
				cfsu_ntp_info2.appendChild(cfsu_server_type2);
				String defaultOpServerType2 = resultMapForConstants
				.get(Constants.NTP_COMM_SCRIPT_SECONDARY_SEREVER_TYPE);
				cfsu_server_type2.appendChild(doc.createTextNode(defaultOpServerType2));

				Element cfsu_server_ip_address2 = doc.createElement("cfsu:server-ip-address");
				cfsu_ntp_info2.appendChild(cfsu_server_ip_address2);
				String defaultOpServerIp2 = resultMapForConstants
						.get(Constants.NTP_COMM_SCRIPT_SECONDARY_SEREVER_IP);
				
				cfsu_server_ip_address2.appendChild(doc.createTextNode(defaultOpServerIp2));
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}

			writeXMLattribute(xmlFile);

			status = true;

		}
		catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	
		}
	public static void writeXMLattribute(File xmlFilePath) {
		String oldString = "xmlns:mid";
		String newString = "xmlns:nc";
		String replaceTag = "mid:managed-element";
		String xmlVerTag = "<?xml version=\"1.0\"";
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		FileWriter writer = null;
		File xmlFile = xmlFilePath;
		try {
			br = new BufferedReader(new FileReader(xmlFile));
			String line = br.readLine();
			while (line != null) {
				if (!line.contains(xmlVerTag)) {
					if (line.contains("<nc:running> </nc:running>")) {
						sb.append(line.replace("<nc:running> </nc:running>", "<nc:running></nc:running>"));
						sb.append("\n");
					} else if (!(line.contains(replaceTag)) && line.contains(oldString)) {
						sb.append(line.replace(oldString, newString));
						sb.append("\n");
					} else if (line.contains("xmlns:gnbcp=\"urn:ietf:params:xml:ns:netconf:base:1.0\"")) {
						sb.append(line.replace("xmlns:gnbcp", "xmlns:nc"));
						sb.append("\n");
					} else {
						sb.append(line + "\n");
					}
				}
				line = br.readLine();
			}
			writer = new FileWriter(xmlFile);
			writer.write(sb.toString());
			// System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean generateFile(String filePath, StringBuffer fileContent) {
		boolean status = false;
		try {
			if (CommonUtil.isValidObject(fileContent)) {
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(fileContent.toString());
					fileContent.delete(0, fileContent.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			status = true;
		} catch (Exception e) {
			logger.error("csvFileGeneration() generateFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getDeActivateScriptFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();

			StringBuffer chLockConf = new StringBuffer();
			StringBuffer chIdle = new StringBuffer();
			StringBuffer chFunc = new StringBuffer();
			StringBuffer chShed = new StringBuffer();
			StringBuffer chUnLockConf = new StringBuffer();

			chLockConf.append("#lock cell\n");
			chUnLockConf.append("#unlock cell\n");
			String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.ORAN_COMM_SCRIPT_Cell_ID);
			String ORAN_DEACTIVATE_LOCKED = resultMapForConstants.get(Constants.ORAN_DEACTIVATE_LOCKED);
			String ORAN_DEACTIVATE_UN_LOCKED = resultMapForConstants.get(Constants.ORAN_DEACTIVATE_UN_LOCKED);
			String ORAN_DEACTIVATE_TM8 = resultMapForConstants.get(Constants.ORAN_DEACTIVATE_TM8);
			String ORAN_DEACTIVATE_NO_USE = resultMapForConstants.get(Constants.ORAN_DEACTIVATE_NO_USE);
			String ORAN_DEACTIVATE_CBMASK8_TXFOR2_RX_UE = resultMapForConstants
					.get(Constants.ORAN_DEACTIVATE_CBMASK8_TXFOR2_RX_UE);
			String ORAN_DEACTIVATE_CBMASK8_TXFOR4_RX_UE = resultMapForConstants
					.get(Constants.ORAN_DEACTIVATE_CBMASK8_TXFOR4_RX_UE);

			for (String cellId : cellIds) {
				chLockConf.append("CHG-CELL-CONF:" + enbName + "::::CELL_NUM=\"" + cellId + "\",ADMINISTRATIVE_STATE=\""
						+ ORAN_DEACTIVATE_LOCKED + "\";\n");
				chIdle.append("CHG-CSIRS-IDLE:" + enbName + "::::CELL_NUM=\"" + cellId + "\",CSI_RS_USAGE=\""
						+ ORAN_DEACTIVATE_NO_USE + "\"; 1 \n");
				chFunc.append("CHG-DPHYANT-FUNC:" + enbName + "::::CELL_NUM=\"" + cellId + "\",CBMASK8_TXFOR2_RX_UE=\""
						+ ORAN_DEACTIVATE_CBMASK8_TXFOR2_RX_UE + "\",CBMASK8_TXFOR4_RX_UE=\""
						+ ORAN_DEACTIVATE_CBMASK8_TXFOR4_RX_UE + "\"; 1\n");
				chShed.append("CHG-DL-SCHED:" + enbName + "::::CELL_NUM=\"" + cellId + "\",DL_MIMO_MODE=\""
						+ ORAN_DEACTIVATE_TM8 + "\"; 1\n");
				chUnLockConf.append("CHG-CELL-CONF:" + enbName + "::::CELL_NUM=\"" + cellId
						+ "\",ADMINISTRATIVE_STATE=\"" + ORAN_DEACTIVATE_UN_LOCKED + "\";\n");
			}

			sb.append(chLockConf);
			sb.append(System.getProperty("line.separator"));

			sb.append(chIdle);
			sb.append(System.getProperty("line.separator"));

			sb.append(chFunc);
			sb.append(System.getProperty("line.separator"));

			sb.append(chShed);
			sb.append(System.getProperty("line.separator"));

			sb.append(chUnLockConf);
			sb.append(System.getProperty("line.separator"));

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}

		} catch (Exception e) {
			logger.error("csvFileGeneration() getDeActivateScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript1File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();

			sb.append("#########################################################################################\n");
			sb.append("BKUP-PLD:" + enbName + "::::PLD_BACKUP_FILE_REF=\"/log/conf/ENB/" + enbId + "/eNB" + enbId
					+ "_backuppld.tar.gz\"; 1");
			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript1File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript2File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_2_LOCKED = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_2_LOCKED);
			String ORAN_SPRINT_COMM_SCRIPT_2_CELL_ARRAY = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_2_CELL_ARRAY);

			/*
			 * String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel,
			 * listCIQDetailsModel.get(0).getSheetAliasName(),
			 * Constants.ORAN_COMM_SCRIPT_Cell_ID);
			 */
			String[] cellIds = ORAN_SPRINT_COMM_SCRIPT_2_CELL_ARRAY.split(",");

			sb.append("#########################################################################################\n");
			for (String cellId : cellIds) {
				sb.append("CHG-CELL-CONF:" + enbName + "::::CELL_NUM=\"" + cellId + "\",ADMINISTRATIVE_STATE=\""
						+ ORAN_SPRINT_COMM_SCRIPT_2_LOCKED + "\"; 1\n");
			}

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript2File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript3File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_3_LINK_UN_LOCKED = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_3_LINK_UN_LOCKED);
			String ORAN_SPRINT_COMM_SCRIPT_3_STATUS_EQUIP = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_3_STATUS_EQUIP);
			String ORAN_SPRINT_COMM_SCRIPT_3_PORT_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_3_PORT_ID);
			String ORAN_SPRINT_COMM_SCRIPT_3_VR_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_3_VR_ID);
			sb.append("#########################################################################################\n");
			sb.append("RTRV-ELINK-CONF:" + enbName + "::::; 1\n");
			sb.append("CHG-ELINK-CONF:" + enbName + "::::PORT_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_3_PORT_ID + "\",VR_ID=\""
					+ ORAN_SPRINT_COMM_SCRIPT_3_VR_ID + "\",STATUS=\"" + ORAN_SPRINT_COMM_SCRIPT_3_STATUS_EQUIP
					+ "\",ADMINISTRATIVE_STATE=\"" + ORAN_SPRINT_COMM_SCRIPT_3_LINK_UN_LOCKED + "\"; 1\n");
			sb.append("RTRV-ELINK-CONF:" + enbName + "::::; 1\n");
			sb.append("RTRV-ELINK-STS:" + enbName + "::::; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript3File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript4File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			String eNB_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);
			String CSR_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_CSR_OAM_IP);
			String eNB_OAM_VLAN = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_4_eNB_OAM_VLAN);

			StringBuffer sb = new StringBuffer();
			sb.append(
					"############### loging to current eNB using OAM IP from LSM and navigate to LMD path  ################\n");
			sb.append("printenv\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("setenv p BOOTPORT PORT_2_0_1\n");
			sb.append("setenv p PORT_2_0_1_IPVER 4\n");
			sb.append("setenv p PORT_2_0_1_IPV4_IP " + CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3) + "\n");
			sb.append("setenv p PORT_2_0_1_IPV4_NM 30\n");
			sb.append("setenv p PORT_2_0_1_IPV4_GW " + CommonUtil.getLeftSubStringWithLen(CSR_OAM_IP, 3) + "\n");
			sb.append("setenv p PORT_2_0_1_VLANID " + eNB_OAM_VLAN + "\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("printenv\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("delenv p PORT_2_0_0_IPVER\n");
			sb.append("delenv p PORT_2_0_0_IPV4_IP\n");
			sb.append("delenv p PORT_2_0_0_IPV4_NM\n");
			sb.append("delenv p PORT_2_0_0_IPV4_GW\n");
			sb.append("delenv p PORT_2_0_0_VLANID\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("printenv\n");
			sb.append(System.getProperty("line.separator"));

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript4File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript5File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			NeMappingEntity neMappingEntity, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_5_IP_PFX_LEN = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_IP_PFX_LEN);
			String ORAN_SPRINT_COMM_SCRIPT_5_OAM = resultMapForConstants.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_OAM);
			String ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_S1);
			String ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_X2);
			String ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_S1);
			String ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_X2);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("CHG-IP-ADDR:" + enbName + "::::DB_INDEX=\"2\",IF_NAME=\"ge_2_0_0."
					+ neMappingEntity.getEnbSbVlan() + "\",IP_ADDR=\"" + neMappingEntity.getEnbSbIp()
					+ "\",IP_PFX_LEN=\"" + ORAN_SPRINT_COMM_SCRIPT_5_IP_PFX_LEN + "\"," + "OAM=\""
					+ ORAN_SPRINT_COMM_SCRIPT_5_OAM + "\",LTE_SIGNAL_S1=\"" + ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_S1
					+ "\",LTE_SIGNAL_X2=\"" + ORAN_SPRINT_COMM_SCRIPT_5_LTE_SIGNAL_X2 + "\"," + "LTE_BEARER_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_S1 + "\",LTE_BEARER_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_5_LTE_BEARER_X2 + "\"; 1\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript5File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript6File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_6_IP_ADDR_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_6_IP_ADDR_DB_INDEX);
			String ORAN_SPRINT_COMM_SCRIPT_6_VLAN_CONF_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_6_VLAN_CONF_DB_INDEX);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");
			sb.append("DLT-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_6_IP_ADDR_DB_INDEX
					+ "\"; 1\n");
			sb.append("DLT-VLAN-CONF:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_6_VLAN_CONF_DB_INDEX
					+ "\"; 1\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");
			sb.append(
					"#INIT-SYS:CHCNILIWBBULTE0516943::::FORCED_MODE=\"ON\"; 1							#INIT Command will be run manually#\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript6File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript7File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_7_VLAN_CONF_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_7_VLAN_CONF_DB_INDEX);
			String ORAN_SPRINT_COMM_SCRIPT_7_VR_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_7_VR_ID);
			String ORAN_SPRINT_COMM_SCRIPT_7_IF_NAME = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_7_IF_NAME);
			String ORAN_SPRINT_COMM_SCRIPT_7_LINK_UN_LOCKED = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_7_LINK_UN_LOCKED);
			String ORAN_SPRINT_COMM_SCRIPT_7_NON_OAM = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_7_NON_OAM);
			String eNB_S_B_VLAN = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_7_eNB_S_B_VLAN);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");
			sb.append("CRTE-VLAN-CONF:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_7_VLAN_CONF_DB_INDEX
					+ "\",VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_7_VR_ID + "\",IF_NAME=\""
					+ ORAN_SPRINT_COMM_SCRIPT_7_IF_NAME + "\",VLAN_ID=\"" + eNB_S_B_VLAN + "\",ADMINISTRATIVE_STATE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_7_LINK_UN_LOCKED + "\",DESCRIPTION=\"" + ORAN_SPRINT_COMM_SCRIPT_7_NON_OAM
					+ "\"; 1\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript7File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript8File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IP_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IP_DB_INDEX);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IF_NAME = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IF_NAME);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_IP_PFX_LEN = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_IP_PFX_LEN);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_IP_GET_TYPE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONFIP_GET_TYPE);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_OAM = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_OAM);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_S1);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_X2);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_S1);
			String ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_X2);

			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_DB_INDEX);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_IF_NAME = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IF_NAME);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_IP_PFX_LEN = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CHG_IP_PFX_LEN);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_OAM = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_OAM);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_S1);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_X2);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_S1);
			String ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_X2);

			String eNB_S_B_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IP_ADDR);
			String eNB_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("CRTE-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IP_DB_INDEX
					+ "\",IF_NAME=\"" + ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IF_NAME + "\",IP_ADDR=\""
					+ CommonUtil.getLeftSubStringWithLen(eNB_S_B_IP, 3) + "\"," + "IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_IP_PFX_LEN + "\",IP_GET_TYPE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_IP_GET_TYPE + "\",OAM=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_OAM + "\"," + "LTE_SIGNAL_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_S1 + "\",LTE_SIGNAL_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_SIGNAL_X2 + "\"," + "LTE_BEARER_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_S1 + "\",LTE_BEARER_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CRTE_CONF_LTE_BEARER_X2 + "\"; 1\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("# Update New OAM Interface to OAM Only\n");
			sb.append("CHG-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_DB_INDEX
					+ "\",IF_NAME=\"" + ORAN_SPRINT_COMM_SCRIPT_8_CHG_IF_NAME + "\",IP_ADDR=\""
					+ CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3) + "\"," + "IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_IP_PFX_LEN + "\",OAM=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_OAM + "\",LTE_SIGNAL_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_S1 + "\"" + ",LTE_SIGNAL_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_SIGNAL_X2 + "\",LTE_BEARER_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_S1 + "\",LTE_BEARER_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_8_CHG_CONF_LTE_BEARER_X2 + "\"; 1\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript8File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript9File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			NeMappingEntity neMappingEntity, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_9_VR_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_VR_ID);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_1);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PREFIX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PREFIX);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_2);
			String ORAN_SPRINT_COMM_SCRIPT_9_DLT_DB_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_DLT_DB_INDEX);

			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_3 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_3);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_4 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_4);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN_1);

			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_5 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_5);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_6 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_6);
			String ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_7 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_7);

			String CSR_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_CSR_OAM_IP);
			String CSR_S_B_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_CSR_S_B_IP);
			String LSM_IP_Address_SouthBound = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_LSM_IP_Address_SouthBound);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-IP-ROUTE:" + enbName + "::::; 1\n");
			sb.append("CRTE-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_1 + "\",IP_PREFIX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PREFIX + "\"," + "IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN + "\",IP_GW=\""
					+ CommonUtil.getLeftSubStringWithLen(CSR_OAM_IP, 3) + "\",DISTANCE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE + "\"; 1\n");
			sb.append("CRTE-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_2 + "\",IP_PREFIX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PREFIX + "\"," + "IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN + "\",IP_GW=\""
					+ CommonUtil.getLeftSubStringWithLen(CSR_S_B_IP, 3) + "\",DISTANCE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE + "\"; 1\n");
			sb.append(System.getProperty("line.separator"));

			sb.append("DLT-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_DLT_DB_INDEX
					+ "\"; 1     #Make sure no duplicate on new and exisitng Index that need to be deleted# \n");
			sb.append(System.getProperty("line.separator"));

			sb.append("CRTE-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_3 + "\",IP_PREFIX=\"" + LSM_IP_Address_SouthBound + "\","
					+ "IP_PFX_LEN=\"" + ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN_1 + "\",IP_GW=\""
					+ CommonUtil.getLeftSubStringWithLen(CSR_OAM_IP, 3) + "\",DISTANCE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE + "\"; 1\n");
			sb.append("CRTE-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_4 + "\",IP_PREFIX=\"" + neMappingEntity.getBtsIp() + "\","
					+ "IP_PFX_LEN=\"" + ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_PFX_LEN_1 + "\",IP_GW=\""
					+ CommonUtil.getLeftSubStringWithLen(CSR_OAM_IP, 3) + "\",DISTANCE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_IP_DISTANCE + "\"; 1\n");
			sb.append("RTRV-IP-ROUTE:" + enbName + "::::; 1\n");
			sb.append(System.getProperty("line.separator"));

			sb.append("DLT-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_5 + "\"; 1\n");
			sb.append("DLT-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_6 + "\"; 1\n");
			sb.append("DLT-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_9_VR_ID + "\",DB_INDEX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_9_CRTE_DB_INDEX_7 + "\"; 1\n");
			sb.append("RTRV-IP-ROUTE:" + enbName + "::::; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript9File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript10File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_1);
			String ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_2);
			String ORAN_SPRINT_COMM_SCRIPT_10_VR_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_VR_ID);
			String ORAN_SPRINT_COMM_SCRIPT_10_PORT_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_PORT_ID);
			String ORAN_SPRINT_COMM_SCRIPT_10_N_EQUIP = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_N_EQUIP);
			String ORAN_SPRINT_COMM_SCRIPT_10_ADMINISTRATIVE_STATE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_10_ADMINISTRATIVE_STATE);

			sb.append("#########################################################################################\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("DLT-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_1 + "\"; 1\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");
			sb.append(
					"DLT-VLAN-CONF:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_10_DB_INDEX_2 + "\"; 1\n");
			sb.append("RTRV-VLAN-CONF:" + enbName + "::::; 1\n");
			sb.append("RTRV-IP-ADDR:" + enbName + "::::; 1\n");
			sb.append("RTRV-ELINK-CONF:" + enbName + "::::; 1\n");
			sb.append("CHG-ELINK-CONF:" + enbName + "::::PORT_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_10_PORT_ID
					+ "\",VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_10_VR_ID + "\"," + "STATUS=\""
					+ ORAN_SPRINT_COMM_SCRIPT_10_N_EQUIP + "\",ADMINISTRATIVE_STATE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_10_ADMINISTRATIVE_STATE + "\"; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript10File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScript11File(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String ORAN_SPRINT_COMM_SCRIPT_11_UN_LOCKED = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_11_UN_LOCKED);
			String ORAN_SPRINT_COMM_SCRIPT_11_CELL_ARRAY = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_11_CELL_ARRAY);

			/*
			 * String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel,
			 * listCIQDetailsModel.get(0).getSheetAliasName(),
			 * Constants.ORAN_COMM_SCRIPT_Cell_ID);
			 */
			String[] cellIds = ORAN_SPRINT_COMM_SCRIPT_11_CELL_ARRAY.split(",");

			sb.append("#########################################################################################\n");
			for (String cellId : cellIds) {
				sb.append("CHG-CELL-CONF:" + enbName + "::::CELL_NUM=\"" + cellId + "\",ADMINISTRATIVE_STATE=\""
						+ ORAN_SPRINT_COMM_SCRIPT_11_UN_LOCKED + "\"; 1\n");
			}

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript11File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScriptMMEFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		String MME_Info_1 = "";
		String[] MME_Info_array = null;
		try {
			StringBuffer sb = new StringBuffer();

			String ORAN_SPRINT_COMM_SCRIPT_MME_INDEX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_MME_INDEX);
			String ORAN_SPRINT_COMM_SCRIPT_MME_STATUS = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_MME_STATUS);
			String ORAN_SPRINT_COMM_SCRIPT_MME_ACTIVE_STATE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_MME_ACTIVE_STATE);
			String ORAN_SPRINT_COMM_SCRIPT_MME_IP_VER = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_MME_IP_VER);
			int MME_INDEX = 0;
			String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.ORAN_COMM_SCRIPT_Cell_ID);

			String mmeInfo = "";

			sb.append("#Building MMEs#\n");
			sb.append(System.getProperty("line.separator"));
			for (int i = 0; i < 13; i++) {

				mmeInfo = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
						listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
						Constants.ORAN_SPRINT_COMM_SCRIPT_MME_Info + "_" + (i + 1));

				if (CommonUtil.isValidObject(MME_Info_1)) {
					MME_Info_array = mmeInfo.split("\\|");
				}
				if (CommonUtil.isValidObject(MME_Info_array) && MME_Info_array.length >= 4) {
					sb.append("CHG-MME-CONF:" + enbName + "::::MME_INDEX=\"" + MME_INDEX + "\",STATUS=\""
							+ ORAN_SPRINT_COMM_SCRIPT_MME_STATUS + "\",ACTIVE_STATE=\""
							+ ORAN_SPRINT_COMM_SCRIPT_MME_ACTIVE_STATE + "\",IP_VER=\""
							+ ORAN_SPRINT_COMM_SCRIPT_MME_IP_VER + "\",MME_IPV4=\"" + MME_Info_array[0]
							+ "\",ADMINISTRATIVE_STATE=\"unlocked\",SECONDARY_MME_IPV4=\"" + MME_Info_array[1]
							+ "\",MME_MCC=\"" + MME_Info_array[2] + "\",MME_MNC=\"" + MME_Info_array[3] + "\"; 1\n");
					MME_INDEX = MME_INDEX + 1;
				}

			}
			/*
			 * for (String cellId : cellIds) { List<CIQDetailsModel> ciqRows =
			 * fileUploadRepository.getCiqRowsByCellId(dbcollectionFileName,
			 * listCIQDetailsModel.get(0).getSheetAliasName(), enbId, cellId); if
			 * (CommonUtil.isValidObject(ciqRows) && ciqRows.size() > 0) { MME_Info_1 =
			 * ciqRows.get(0).getCiqMap().get(Constants.ORAN_SPRINT_COMM_SCRIPT_MME_Info_1)
			 * .getHeaderValue(); if (CommonUtil.isValidObject(MME_Info_1)) { MME_Info_array
			 * = MME_Info_1.split("\\|"); } if (CommonUtil.isValidObject(MME_Info_array) &&
			 * MME_Info_array.length >= 4) { sb.append("CHG-MME-CONF:" + enbName +
			 * "::::MME_INDEX=\"" + MME_INDEX + "\",STATUS=\"" +
			 * ORAN_SPRINT_COMM_SCRIPT_MME_STATUS + "\",ACTIVE_STATE=\"" +
			 * ORAN_SPRINT_COMM_SCRIPT_MME_ACTIVE_STATE + "\",IP_VER=\"" +
			 * ORAN_SPRINT_COMM_SCRIPT_MME_IP_VER + "\",MME_IPV4=\"" + MME_Info_array[0] +
			 * "\",ADMINISTRATIVE_STATE=\"unlocked\",SECONDARY_MME_IPV4=\"" +
			 * MME_Info_array[1] + "\",MME_MCC=\"" + MME_Info_array[2] + "\",MME_MNC=\"" +
			 * MME_Info_array[3] + "\"; 1\n"); MME_INDEX = MME_INDEX + 1; } } }
			 */

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScriptMMEFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScriptENVFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			String eNB_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_8_CHG_IP_ADDR);
			String CSR_OAM_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_CSR_OAM_IP);
			String eNB_OAM_VLAN = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_4_eNB_OAM_VLAN);
			String LSM_IP_Address_SouthBound = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_LSM_IP_Address_SouthBound);

			String ORAN_SPRINT_COMM_SCRIPT_ENV_REBOOT_TIMER = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_ENV_REBOOT_TIMER);
			String ORAN_SPRINT_COMM_SCRIPT_ENV_FTPTELNET_ON = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_ENV_FTPTELNET_ON);
			String ORAN_SPRINT_COMM_SCRIPT_ENV_ENCRYPT_TYPE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_ENV_ENCRYPT_TYPE);

			sb.append("setenv p BOOTMODE static\n");
			sb.append("setenv p BOOTPORT PORT_2_0_1\n");
			sb.append("setenv p PORT_2_0_1_IPVER 4\n");
			sb.append("setenv p PORT_2_0_1_IPV4_IP " + CommonUtil.getLeftSubStringWithLen(eNB_OAM_IP, 3) + "\n");

			sb.append("setenv p PORT_2_0_1_IPV4_NM 30\n");
			sb.append("setenv p PORT_2_0_1_IPV4_GW " + CommonUtil.getLeftSubStringWithLen(CSR_OAM_IP, 3) + "\n");
			sb.append("setenv p PORT_2_0_1_VLANID " + eNB_OAM_VLAN + "\n");
			sb.append("setenv p NE_ID " + enbId + "\n");

			sb.append("setenv p RS_IP " + LSM_IP_Address_SouthBound + "\n");
			sb.append("setenv p REBOOT_TIMER " + ORAN_SPRINT_COMM_SCRIPT_ENV_REBOOT_TIMER + "\n");
			sb.append("setenv p FTPTELNET_ON " + ORAN_SPRINT_COMM_SCRIPT_ENV_FTPTELNET_ON + "\n");
			sb.append("setenv p ENCRYPT_TYPE " + ORAN_SPRINT_COMM_SCRIPT_ENV_ENCRYPT_TYPE + "\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript11File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	public boolean getScriptCOMMFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();

			String ORAN_SPRINT_COMM_SCRIPT_COMM_IF_NAME = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_IF_NAME);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_2);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_VR_ID = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_VR_ID);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_ADMINISTRATIVE_STATE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_ADMINISTRATIVE_STATE);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_DESCRIPTION = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_DESCRIPTION);

			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2);

			String ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_IP_GET_TYPE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_IP_GET_TYPE);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_OAM = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_OAM);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1_1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2_1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1_1);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2_1 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2_1);

			String ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PREFIX = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PREFIX);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN_0 = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN_0);
			String ORAN_SPRINT_COMM_SCRIPT_COMM_DISTANCE = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_DISTANCE);

			String ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER);
			String ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER);
			String ORAN_SPRINT_COMM_SCRIPT_IP_VER = resultMapForConstants.get(Constants.ORAN_SPRINT_COMM_SCRIPT_IP_VER);

			String ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER_IP = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER_IP);
			String ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER_IP = resultMapForConstants
					.get(Constants.ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER_IP);

			String eNB_S_B_VLAN = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_COMM_eNB_S_B_VLAN);
			String eNB_S_B_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_8_CRTE_IP_ADDR);
			String CSR_S_B_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
					listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
					Constants.ORAN_SPRINT_COMM_SCRIPT_9_CSR_S_B_IP);

			sb.append("#Run in LSM#\n");
			sb.append(System.getProperty("line.separator"));
			sb.append("CRTE-VLAN-CONF:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_1
					+ "\",VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_VR_ID + "\",IF_NAME=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_IF_NAME + "\",VLAN_ID=\"" + eNB_S_B_VLAN
					+ "\",ADMINISTRATIVE_STATE=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_ADMINISTRATIVE_STATE
					+ "\",DESCRIPTION=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_DESCRIPTION + "\"; 1\n");
			sb.append("CHG-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_1
					+ "\",LTE_SIGNAL_S1=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1 + "\",LTE_SIGNAL_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2 + "\",LTE_BEARER_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1 + "\",LTE_BEARER_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2 + "\"; 1\n");
			sb.append("CRTE-IP-ADDR:" + enbName + "::::DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_2
					+ "\",IF_NAME=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_IF_NAME + "." + eNB_S_B_VLAN + "\",IP_ADDR=\""
					+ CommonUtil.getLeftSubStringWithLen(eNB_S_B_IP, 3) + "\",IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN + "\",IP_GET_TYPE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_IP_GET_TYPE + "\"," + "OAM=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_OAM
					+ "\",LTE_SIGNAL_S1=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_S1_1 + "\",LTE_SIGNAL_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_SIGNAL_X2_1 + "\",LTE_BEARER_S1=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_S1_1 + "\",LTE_BEARER_X2=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_LTE_BEARER_X2_1 + "\"; 1\n");

			sb.append("CRTE-IP-ROUTE:" + enbName + "::::VR_ID=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_VR_ID
					+ "\",DB_INDEX=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_DB_INDEX_2 + "\"," + "IP_PREFIX=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PREFIX + "\",IP_PFX_LEN=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_IP_PFX_LEN_0 + "\",IP_GW=\""
					+ CommonUtil.getLeftSubStringWithLen(CSR_S_B_IP, 3) + "\",DISTANCE=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_DISTANCE + "\"; 1\n");

			sb.append("CHG-NTP-CONF:" + enbName + "::::SVR_TYPE=\"" + ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER
					+ "\",IP_VER=\"" + ORAN_SPRINT_COMM_SCRIPT_IP_VER + "\",NTP_IPV4=\""
					+ ORAN_SPRINT_COMM_SCRIPT_COMM_PRIMARY_NTP_SERVER_IP + "\"; 1\n");
			sb.append("CHG-NTP-CONF:" + enbName + "::::SVR_TYPE=\"" + ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER
					+ "\",IP_VER=\"" + ORAN_SPRINT_COMM_SCRIPT_IP_VER + "\",NTP_IPV4=\""
					+ ORAN_SPRINT_COMM_SCRIPT_SECONDARY_NTP_SERVER_IP + "\"; 1\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript11File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public JSONObject commissionScriptFileGeneration(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks) {
		logger.error("Into commissionScriptFileGeneration");
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		String commScriptFilename = "";
		String sheetName = "";
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);

			
				sheetName = Constants.VZ_GROW_CIQUpstateNY;
			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			if (listCIQDetailsModel != null && listCIQDetailsModel.size() > 0) {
				logger.error("Comm script - Let's get started.");
				String cascade = fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
						listCIQDetailsModel.get(0).getSheetAliasName(), enbId,
						Constants.ORAN_SPRINT_COMM_SCRIPT_CASCADE);

				List<GrowConstantsEntity> objListProgDetails = growConstantsRepository.getGrowConstantsDetails();

				if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_DEACTIVATE)) {
					commScriptFilename = "COMM_DEACTIVATE_TM9_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_DEACTIVATE")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getDeActivateScriptFile(resultMapForConstants, ciqFileName, enbId, enbName,
							dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT1)) {
					commScriptFilename = "COMM_BACKUP_PLD_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript1File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT2)) {
					commScriptFilename = "COMM_LOCK_CELL_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript2File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT3)) {
					commScriptFilename = "COMM_BACKHAUL_PORT_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript3File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT4)) {
					commScriptFilename = "ENV_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript4File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT5)) {
					commScriptFilename = "COMM_DISABLE_SBIP_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript5File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, neMappingEntity, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT6)) {
					commScriptFilename = "COMM_DLT_IPVLAN_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript6File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT7)) {
					commScriptFilename = "COMM_CRTE_IPVLAN_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript7File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT8)) {
					commScriptFilename = "COMM_CRTE_SBOAMIP_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript8File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT9)) {
					commScriptFilename = "COMM_CRTE_DLT_ROUTE_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript9File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, neMappingEntity, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT10)) {
					commScriptFilename = "COMM_CHG_ELINK_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript10File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_SCRIPT11)) {
					commScriptFilename = "COMM_UNLOCK_CELL_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScript11File(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_MME)) {
					commScriptFilename = "COMM_MME_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScriptMMEFile(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_ENV)) {
					commScriptFilename = "COMM_ENV_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScriptENVFile(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_COMM)) {
					commScriptFilename = "COMM_SCRIPT_" + enbId + "_" + cascade + dateString + ".txt";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_SPRINT_COMM_SCRIPT")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getScriptCOMMFile(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, fileBuilder.toString() + commScriptFilename);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML)) {
					String DAS = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId, "DAS");
					commScriptFilename = "COMM_" + enbName + dateString + ".xml";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_COMM")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getComisionScriptFile(resultMapForConstants, ciqFileName, enbId, enbName,
							dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString() + commScriptFilename,
							DAS);
				} else if (CommonUtil.isValidObject(fileType)
						&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_VBS)) {
					commScriptFilename = "COMM_" + enbName + dateString + ".vbs";
					Map<String, String> resultMapForConstants = objListProgDetails.stream()
							.filter(X -> X.getLabel().startsWith("ORAN_LEGACY_VBS_COMM")
									&& X.getProgramDetailsEntity().getId().equals(programId))
							.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
					status = getVBSScriptFile(resultMapForConstants, ciqFileName, enbId, enbName, dbcollectionFileName,
							listCIQDetailsModel, neMappingEntity, fileBuilder.toString() + commScriptFilename);
				}
			}

		} catch (Exception e) {
			logger.error("csvFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", commScriptFilename);
			/*
			 * User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			 * GenerateInfoAuditEntity objInfo = new GenerateInfoAuditEntity(); if (status)
			 * {
			 * 
			 * String fileSavePath = fileBuilder.toString(); fileSavePath =
			 * fileSavePath.replace(LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
			 * , ""); objInfo.setFilePath(fileSavePath);
			 * objInfo.setCiqFileName(ciqFileName); objInfo.setNeName(enbName);
			 * objInfo.setRemarks(remarks); objInfo.setFileName(commScriptFilename);
			 * objInfo.setFileType(Constants.FILE_TYPE_COMM_SCRIPT);
			 * objInfo.setGenerationDate(new Date());
			 * objInfo.setGeneratedBy(user.getUserName()); CustomerDetailsEntity
			 * programDetailsEntity = new CustomerDetailsEntity();
			 * programDetailsEntity.setId(programId);
			 * objInfo.setProgramDetailsEntity(programDetailsEntity);
			 * 
			 * objGenerateCsvRepository.saveCsvAudit(objInfo); }
			 */
		}
		return fileGenerateResult;
	}
//**************************************************************fsu COMM*************************************//
	public JSONObject commissionScriptFileGenerationFSU(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		String commScriptFilename = "";
		String sheetName = "";
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);

			
				sheetName = Constants.VZ_GROW_CIQUpstateNY;
			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			List<GrowConstantsEntity> objListProgDetails = growConstantsRepository.getGrowConstantsDetails();
			if (CommonUtil.isValidObject(fileType)
					&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML)) {
				String DAS = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId, "DAS");
				commScriptFilename = "COMM_ADMIN_STATE_SET_" + enbName + dateString + ".xml";
				Map<String, String> resultMapForConstants = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith("FSU_COMM_SCRIPT")
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
				status = getComisionScriptFileFSU(resultMapForConstants, ciqFileName, enbId, enbName,
						dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString() + commScriptFilename,
						DAS);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", commScriptFilename);
			
		}
		return fileGenerateResult;
	}
	
	public JSONObject commissionScriptFileGenerationNTPFSU(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks) {
		boolean status = false;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilder = new StringBuilder();
		String commScriptFilename = "";
		String sheetName = "";
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			
			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			List<GrowConstantsEntity> objListProgDetails = growConstantsRepository.getGrowConstantsDetails();
			if (CommonUtil.isValidObject(fileType)
					&& fileType.equalsIgnoreCase(Constants.GROW_COMM_SCRIPT_FILE_TYPE_XML)) {
				String DAS = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId, "DAS");
				commScriptFilename = "COMM_" + "NTP_IP_SET_" +enbName + dateString + ".xml";
				Map<String, String> resultMapForConstants = objListProgDetails.stream()
						.filter(X -> X.getLabel().startsWith("NTP_COMM_SCRIPT")
								&& X.getProgramDetailsEntity().getId().equals(programId))
						.collect(Collectors.toMap(GrowConstantsEntity::getLabel, GrowConstantsEntity::getValue));
				status = getComisionScriptFileNTPFSU(resultMapForConstants, ciqFileName, enbId, enbName,
						dbcollectionFileName, listCIQDetailsModel, fileBuilder.toString() + commScriptFilename,
						DAS);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", commScriptFilename);
			
		}
		return fileGenerateResult;
	}

//**************************************************************fsu COMM*************************************//

			
	private boolean getVBSScriptFile(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			NeMappingEntity neMappingEntity, String filePath) {
		boolean status = false;
		try {
			StringBuffer sb = new StringBuffer();
			/// mAHESH CODE
			String Dir = "Dir";
			String adid = "aDid";
			String cascasde_id = "";
			String eNodeID = enbId;
			String Cdu_IP = ""; //
			String pkgVer = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion();
			String cur_rel_ver = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();
			String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss")
					.format(new Timestamp(System.currentTimeMillis()));
			String if_name_index = "1";
			String ENB_SNB_VLAN = "";
			String Enb_SNB_IP = "";
			String Enb_SNB_GW_IP = "";
			String Prefix_vlan = "";
			String boardId = "";
			String portId = "";
			String CSL_IP = get_csl_ip(enbId);
			String Alias_Name = "";
			String cellId = "";
			String sheetNames;

			String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);
			if (StringUtils.isNotEmpty(mkt) && Constants.VZ_GROW_UNY.equals(mkt)) {
				sheetNames = Constants.VZ_GROW_CIQUpstateNY;

			} else if (mkt.equals(Constants.VZ_GROW_NE)) {
				sheetNames = Constants.VZ_GROW_CIQNewEngland;
			} else {
				sheetNames = "";
			}

			ENB_SNB_VLAN = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_eNB_OAM_VLAN);

			Enb_SNB_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP);

			Enb_SNB_GW_IP = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Gateway_IP);

			Prefix_vlan = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix);
			Cdu_IP = Enb_SNB_GW_IP;

			cascasde_id = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_eNB_Name);

			sb.append("Sub Main").append("\n");
			sb.append("xsh.Session.LogFilePath = \"" + Dir + "\\RanCommTool\\Comm\\" + adid + "\\ORAN\\CMD-LOG\\"
					+ cascasde_id + "_" + eNodeID + "_Log_" + timeStamp + ".txt\"").append("\n");
			sb.append("xsh.Session.StartLog").append("\n");
			sb.append("xsh.Screen.send \"ssh lteuser@" + CommonUtil.formatIPV6Address(Cdu_IP) + "\"").append("\n"); // ============================
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Screen.send \"YES\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 8000").append("\n");
			sb.append("xsh.Screen.send \"samsunglte\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"su -\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"S@msung1te\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"vrctl 31 bash\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"cd /pkg/" + pkgVer + "/ENB/" + cur_rel_ver + "/bin\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Screen.send \"cli.ohm\"").append("\n");
			// sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Screen.send \"ROOT\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"ROOT\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");

			// crte_vlan_conf()
			sb.append("xsh.Session.Sleep 5000").append("\n");
			sb.append("xsh.Screen.send \"CRTE-VLAN-CONF:DB_INDEX=1,VR_ID=0,IF_NAME=ge_0_0_" + if_name_index
					+ ",VLAN_ID=" + ENB_SNB_VLAN
					+ ",ADMINISTRATIVE_STATE=linkUnlocked,DESCRIPTION=\"\"Signaling + Bearer\"\";\"").append("\n");
			// "xsh.Screen.send
			// \"CRTE-VLAN-CONF:DB_INDEX=1,VR_ID=0,IF_NAME=ge_0_0_"+if_name_index+",VLAN_ID="+ENB_SNB_VLAN+",ADMINISTRATIVE_STATE=linkUnlocked,DESCRIPTION=\"\"Signaling
			// + Bearer\"\";\""
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"Y\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");

			// chg_ipv6_addr()
			sb.append(
					"xsh.Screen.send \"CHG-IPV6-ADDR:DB_INDEX=0,LTE_SIGNAL_S1=False,LTE_SIGNAL_X2=False,LTE_BEARER_S1=False,LTE_BEARER_X2=False;\"")
					.append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"ROOT\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 5000").append("\n");

			// crte_ipv6_addr()
			sb.append("xsh.Screen.send \"CRTE-IPV6-ADDR:DB_INDEX=1,IF_NAME=ge_0_0_" + if_name_index + "." + ENB_SNB_VLAN
					+ ",IPV6_ADDR=\"\"" + CommonUtil.formatIPV6Address(Enb_SNB_IP) + "\"\",IPV6_PFX_LEN=" + Prefix_vlan
					+ ",IPV6_GET_TYPE=STATIC,OAM=False,LTE_SIGNAL_S1=True,LTE_SIGNAL_X2=True,LTE_BEARER_S1=True,LTE_BEARER_X2=True;\"")
					.append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"Y\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 5000").append("\n");

			// crte_ipv6_route()
			sb.append(
					"xsh.Screen.send \"CRTE-IPV6-ROUTE:VR_ID=0,DB_INDEX=2,IPV6_PREFIX=\"\"000:000:000:000:000:000:000:000\"\",IPV6_PFX_LEN=0,IPV6_GW=\"\""
							+ CommonUtil.formatIPV6Address(Enb_SNB_GW_IP) + "\"\",DISTANCE=1;\"")
					.append("\n");
			// "xsh.Screen.send
			// \"CRTE-IPV6-ROUTE:VR_ID=0,DB_INDEX=2,IPV6_PREFIX=\"\"000:000:000:000:000:000:000:000\"\",IPV6_PFX_LEN=0,IPV6_GW=\"\""+Enb_SNB_GW_IP+"\"\",DISTANCE=1;\""
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"ROOT\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 5000").append("\n");

			// chg_csl_inf ()
			sb.append("xsh.Screen.send \"CHG-CSL-INF:IP_VER=IPV6,CSL_SERVER_IP_V6=\"\"" + CSL_IP
					+ "\"\",UDP_ACK_CONTROL=1,CSL_REPORT_CONTROL=On;\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"Y\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 5000").append("\n");

			// chg_ntp_conf()
			sb.append(
					"xsh.Screen.send \"CHG-NTP-CONF:SVR_TYPE=PRIMARY_NTP_SERVER,IP_VER=IPV6,NTP_IPV6=\"\"2001:4888:0A00:0001:0644:0022:0000:0000\"\";\"")
					.append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"Y\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");

			sb.append(
					"xsh.Screen.send \"CHG-NTP-CONF:SVR_TYPE=SECONDARY_NTP_SERVER,IP_VER=IPV6,NTP_IPV6=\"\"2001:4888:0A00:0001:0644:0022:0000:0001\"\";\"")
					.append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 2000").append("\n");
			sb.append("xsh.Screen.send \"Y\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 5000").append("\n");

			for (int i = 0; i < listCIQDetailsModel.size(); i++) {

				if (listCIQDetailsModel.get(i).getSheetAliasName().equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)
						|| listCIQDetailsModel.get(i).getSheetAliasName()
								.equalsIgnoreCase(Constants.VZ_GROW_CIQUpstateNY)) {

					String portid = listCIQDetailsModel.get(i).getCiqMap().get("CPRI_Port_Assignment").getHeaderValue();
					if ((portid.contains("LCC") != false) && (portid.contains("2") != false)) {
						boardId = "1";
					} else {
						boardId = "0";
					}

					if ((portid.contains("LCC") != false) || (portid.contains("-") != false)
							|| (portid.contains("(") != false)) {
						portId = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetNames, enbId,
								Constants.VZ_GROW_CPRI_Port_Assignment);
						/*
						 * portId = listCIQDetailsModel.get(i).getCiqMap().get("CPRI_Port_Assignment")
						 * .getHeaderValue();
						 */
					} else {
						portId = portid;
					}

					sb.append("xsh.Screen.send \"CHG-RRH-CONF:CONNECT_BOARD_ID=" + boardId + ",CONNECT_PORT_ID="
							+ portId + ",CASCADE_RRH_ID=0,RSSI_HIGH_ALARM_TH=\"\"-880,-880,-880,-880\"\";\"")
							.append("\n");
					sb.append("xsh.Screen.Send VbCr").append("\n");
					sb.append("xsh.Session.Sleep 2000").append("\n");
					sb.append("xsh.Screen.send \"ROOT\"").append("\n");
					sb.append("xsh.Screen.Send VbCr").append("\n");
					sb.append("xsh.Session.Sleep 2000").append("\n");

				}
			}

			for (int i = 0; i < listCIQDetailsModel.size(); i++) {

				if (listCIQDetailsModel.get(i).getSheetAliasName().equalsIgnoreCase(Constants.VZ_GROW_CIQNewEngland)
						|| listCIQDetailsModel.get(i).getSheetAliasName()
								.equalsIgnoreCase(Constants.VZ_GROW_CIQUpstateNY)) {

					Alias_Name = listCIQDetailsModel.get(i).getCiqMap().get(Constants.ORAN_LSM_COMM_SCRIPT_ALIAS_NAME)
							.getHeaderValue();
					cellId = listCIQDetailsModel.get(i).getCiqMap().get(Constants.VZ_GROW_Cell_ID).getHeaderValue();
					// trim($alias_name) ne "TBD"
					if (!(Alias_Name.trim()).equals("TBD")) {

						sb.append("xsh.Screen.send \"CHG-CELL-CONF:CELL_NUM=" + cellId + ",CELL_NAME=\"\"" + Alias_Name
								+ "\"\";\"").append("\n");
						// "xsh.Screen.send
						// \"CHG-CELL-CONF:CELL_NUM="+cellId+",CELL_NAME=\"\""+Alias_Name+"\"\";\""
						sb.append("xsh.Screen.Send VbCr").append("\n");
						sb.append("xsh.Session.Sleep 2000").append("\n");
						sb.append("xsh.Screen.send \"ROOT\"").append("\n");
						sb.append("xsh.Screen.Send VbCr").append("\n");
						sb.append("xsh.Session.Sleep 2000").append("\n");
					}

				}
			}

			sb.append("xsh.Screen.send \"exit\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 1200").append("\n");
			sb.append("xsh.Screen.send \"exit\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 1200").append("\n");
			sb.append("xsh.Screen.send \"exit\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 1200").append("\n");
			sb.append("xsh.Screen.send \"exit\"").append("\n");
			sb.append("xsh.Screen.Send VbCr").append("\n");
			sb.append("xsh.Session.Sleep 1200").append("\n");
			sb.append("xsh.Session.StopLog").append("\n");
			sb.append("End Sub").append("\n");

			if (CommonUtil.isValidObject(sb)) {
				status = generateFile(filePath, sb);
			}
		} catch (Exception e) {
			logger.error("csvFileGeneration() getScript11File" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean deleteGeneratedFileDetails(GenerateInfoAuditModel csvAuditdetails) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			status = objGenerateCsvRepository.deleteGeneratedFileDetails(csvAuditdetails.getId());
		} catch (Exception e) {
			logger.error(
					"Exception  getCiqAuditDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private String[] getCarrierId(List<CIQDetailsModel> listCIQDetailsModel, String sheetName) {

		String[] cell_num = null;
		String[] splitValue = null;
		String[] carrierValues = null;

		try {
			List<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> X.getSheetAliasName().equals(sheetName))
					.map(X -> X.getCiqMap().get(Constants.VZ_GROW_Cell_ID).getHeaderValue())
					.collect(Collectors.toList());

			if (objCellIdList != null && objCellIdList.size() > 0) {
				cell_num = new String[objCellIdList.size()];

				cell_num = objCellIdList.toArray(cell_num);

				carrierValues = new String[cell_num.length];

				for (int i = 0; i < cell_num.length; i++) {
					if (cell_num[i].length() > 1) {
						splitValue = cell_num[i].split("");
						carrierValues[i] = splitValue[1];
					} else {
						carrierValues[i] = "1";
					}
				}
			}

		} catch (Exception e) {
			logger.error("Exception getCarrierId() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return carrierValues;
	}

	private String[] dataCiqIndex(List<CIQDetailsModel> listCIQDetailsModel, String key, String sheetAliasName) {

		String[] result = null;

		try {
			List<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> X.getSheetAliasName().equals(sheetAliasName))
					.map(X -> X.getCiqMap().get(key).getHeaderValue()).collect(Collectors.toList());
			if (objCellIdList != null && objCellIdList.size() > 0) {
				result = new String[objCellIdList.size()];

				result = objCellIdList.toArray(result);
			}
		} catch (Exception e) {
			logger.error("Exception dataCiqIndex() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return result;

	}
	
	private String[] dataCiqIndexForMmeip(String enbId,List<CIQDetailsModel> listCIQDetailsModel, String key, String sheetAliasName) {

		String[] result = null;
		//String enbIdForMme = listCIQDetailsModel.get(0).geteNBId().toString();
		String enbIdForMme = enbId;
		String enbIdPrefix = enbIdForMme.substring(0, 3);
		String enbIdPrefix2 = enbIdForMme.substring(0, 2);


		try {
//			List<String> objCellIdList = listCIQDetailsModel.stream()
//					.filter(X -> X.getSheetAliasName().equals(sheetAliasName) && X.getCiqMap().get("Market_Prefix").getHeaderValue().toString().startsWith(enbIdPrefix))
//					.map(X -> X.getCiqMap().get(key).getHeaderValue()).collect(Collectors.toList());
			List<String> list = new ArrayList<>();
			for(CIQDetailsModel objCellIdList : listCIQDetailsModel) {
				if(objCellIdList.getSheetAliasName().equals(sheetAliasName)) {
					if(objCellIdList.getCiqMap().containsKey("Market_Prefix") && StringUtils.isNotEmpty(objCellIdList.getCiqMap().get("Market_Prefix").getHeaderValue().toString())) {
					if(objCellIdList.getCiqMap().get("Market_Prefix").getHeaderValue().toString().startsWith(enbIdPrefix)) {
						list.add(objCellIdList.getCiqMap().get(key).getHeaderValue());
					}
					else if(objCellIdList.getCiqMap().get("Market_Prefix").getHeaderValue().toString().length() == 2 &&
							objCellIdList.getCiqMap().get("Market_Prefix").getHeaderValue().toString().startsWith(enbIdPrefix2)) {
						list.add(objCellIdList.getCiqMap().get(key).getHeaderValue());
					}
					}
				}
			}
			result = new String[list.size()];
			result  = list.toArray(result);
			
			
//			if (objCellIdList != null && objCellIdList.size() > 0) {
//				result = new String[objCellIdList.size()];
//
//				result = objCellIdList.toArray(result);
//			}
		} catch (Exception e) {
			logger.error("Exception dataCiqIndex() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return result;

	}

	private String[] getColumnValuesBySheet(List<CIQDetailsModel> listCIQDetailsModel, String sheetAliasName,
			String column) {

		String[] result = null;

		try {
			List<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> sheetAliasName.equals(X.getSheetAliasName()) && X.getCiqMap().containsKey(column))
					.map(X -> X.getCiqMap().get(column).getHeaderValue()).collect(Collectors.toList());
			if (objCellIdList != null && objCellIdList.size() > 0) {
				result = new String[objCellIdList.size()];

				result = objCellIdList.toArray(result);
			}
		} catch (Exception e) {
			logger.error("Exception getColumnValuesBySheet() in GenerateCsvServiceImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return result;

	}

	private String[] getPortByCascadeId(List<CIQDetailsModel> listCIQDetailsModel, String sheetName) {
		String[] result = null;

		try {
			LinkedHashSet<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> X.getSheetAliasName().equals(sheetName))
					.map(X -> X.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue())
					.collect(Collectors.toCollection(LinkedHashSet::new));
			if (objCellIdList != null && objCellIdList.size() > 0) {
				result = new String[objCellIdList.size()];

				result = objCellIdList.toArray(result);
			}
		} catch (Exception e) {
			logger.error(
					"Exception getPortByCascadeId() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return result;

	}

	@Override
	public boolean updateGeneratedFileDetails(GenerateInfoAuditEntity generateInfoAuditEntity) {
		boolean status = false;
		try {
			status = objGenerateCsvRepository.updateGeneratedFileDetails(generateInfoAuditEntity);
		} catch (Exception e) {
			logger.error("Exception updateGeneratedFileDetails() in GenerateCsvServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private StringBuilder csvStringForSprint(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel,
			NeMappingEntity neMappingEntity) {
		StringBuilder sb = new StringBuilder();
		try {

			String sheetNames = Constants.SPT_GROW_SHEET_FDD_TDD;
			listCIQDetailsModel = listCIQDetailsModel.stream().filter(X -> sheetNames.equals(X.getSheetAliasName()))
					.collect(Collectors.toList());
			String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.ORAN_COMM_SCRIPT_Cell_ID);

			// ##################################################@SysInfo#############################################

			sb.append(
					"@SysInfo##ID,Type,Version,ProfileName,Group,NeName,SerialNo,Location,RelVersion,UDSS,PuncturingMode,TwoSectorMode,InterConnectionMode,InitialAutoPCIAllocation,InitialRACHOptimization,InitialNRTConfiguation,InitialNRTCDMA1XRTTConfiguration,InitialNRTCDMAHRPDConfiguration,InitialNRTNRConfiguration,,\n");

			sb.append(enbId).append(","); // SysInfoID
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_Type)).append(","); // Type
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()).append(","); // pkgVer
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_ProfileName)).append(","); // ProfileName
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_Group)).append(","); // Group In CIQ we have only
																							// CDU30 but in template we
																							// have 5G_CDU30
			sb.append(enbName).append(","); // enbName
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_SerialNo)).append(","); // SerialNo
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_Location)).append(","); // Location
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeRelVersion()).append(","); // RelVersion

			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_UDSS)).append(","); // UDSS
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_PuncturingMode)).append(","); // PuncturingMode
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_TwoSectorMode)).append(","); // TwoSectorMode
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InterConnectionMode)).append(","); // InterConnectionMode
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialAutoPCIAllocation)).append(","); // InitialAutoPCIAllocation
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialRACHOptimization)).append(","); // InitialRACHOptimization
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialNRTConfiguation)).append(","); // InitialNRTConfiguation
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialNRTCDMA1XRTTConfiguration)).append(","); // InitialNRTCDMA1XRTTConfiguration
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialNRTCDMAHRPDConfiguration)).append(","); // InitialNRTCDMAHRPDConfiguration
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_InitialNRTNRConfiguration)).append("\n"); // InitialNRTNRConfiguration

			// ##################################################@ExternalLink#############################################

			sb.append("@ExternalLink##No,extLink\n");

			String extLink = resultMapForConstants.get(Constants.ORAN_GROW_ExternalLink_extLink);
			String[] extLinkArr = extLink.split(",");
			if (extLinkArr != null && extLinkArr.length > 0) {
				for (int i = 0; i < extLinkArr.length; i++) {
					String[] extLinkValueArr = extLinkArr[i].split(":");
					if (extLinkValueArr != null && extLinkValueArr.length > 1) {
						sb.append(extLinkValueArr[0]).append(",");
						sb.append(extLinkValueArr[1]).append("\n");
					}
				}
			}

			// ##################################################@MMEIPInfo#############################################

			sb.append("@MMEIPInfo##No,IPType,IPV4,IPV6\n");

			String ip_type = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_IPType);
			/*
			 * String ipv4 = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_IPV4);
			 * //Swetha Need to check from where to get IPV4 and MME Info
			 * 
			 * String[] mme_ip; mme_ip =
			 * objGenerateCsvRepository.getMmeIpDetails(Constants.VZ_GROW_UNY);
			 */

			int num;
			for (int i = 0; i < 16; i++) {
				num = i;
				sb.append(num).append(",");
				sb.append(ip_type).append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			}

			// ##################################################@GPS#############################################

			sb.append("@GPS##UserUpdateFlag,Latitude,Longitude,Height\n");

			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_GPS_UserUpdateFlag)).append(",");
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_GPS_Latitude)).append(",");
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_GPS_Longitude)).append(",");
			sb.append(resultMapForConstants.get(Constants.ORAN_GROW_GPS_Height)).append("\n");

			// ##############################################@FA###########################################

			sb.append("@FA##CellNum,EarfcnDL,EarfcnUL,FrqBandInd\n");

			String[] earfcnDlList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.DL_EARFCN);
			String[] earfcnUlList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.UL_EARFCN);

			String Earfcn_DL_temp = resultMapForConstants.get(Constants.ORAN_GROW_FA_EarfcnDL);
			String Earfcn_UL_temp = resultMapForConstants.get(Constants.ORAN_GROW_FA_EarfcnUL);
			String FrqBandInd = resultMapForConstants.get(Constants.ORAN_GROW_FA_Frequency_Profile);

			for (int i = 0; i < cellIds.length; i++) {
				sb.append(cellIds[i]).append(",");
				sb.append(earfcnDlList[i]).append(",");
				sb.append(earfcnUlList[i]).append(",");
				sb.append(FrqBandInd).append("\n");
			}

			// ##############################################@Location#################################

			sb.append("@Location##CellNum,AutoGpsSetFlag,Latitude,Longitude,Height\n");

			String Auto_GPS = resultMapForConstants.get(Constants.ORAN_GROW_Location_Auto_GPS);
			String Latitude = resultMapForConstants.get(Constants.ORAN_GROW_Location_Latitude);
			String Longitude = resultMapForConstants.get(Constants.ORAN_GROW_Location_Longitude);
			String Height = resultMapForConstants.get(Constants.ORAN_GROW_Location_Height);

			for (String cellId : cellIds) {
				sb.append(cellId).append(",");
				sb.append(Auto_GPS).append(",");
				sb.append(Latitude).append(",");
				sb.append(Longitude).append(",");
				sb.append(Height).append("\n");
			}

			// ##############################################@Cell###############################################

			sb.append(
					"@Cell##CellNum,PCI,PCICoCell,RSI,Diversity,Path,PowerBoosting,TAC,EAID,HSF,ZCZC,CRS,CcID,CcPort,CacadeID,CcPortAdd,RrhPort,RRHAlias,BeamformingMode,eMTC,,\n");

			String[] pciList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.VZ_GROW_PCI);
			String[] rsiList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.SPT_GROW_RSI);
			String[] diversityList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.SPT_GROW_Diversity);
			String[] csrPortList = getColumnValuesBySheet(listCIQDetailsModel,
					listCIQDetailsModel.get(0).getSheetAliasName(), Constants.SPT_GROW_CSR_Port);
			String[] rrhAliasList = resultMapForConstants.get(Constants.ORAN_GROW_CELL_RRH_Alias).split(","); // Swetha
																												// Need
																												// to
																												// check
																												// for
																												// rrhAlias
																												// which
																												// column
																												// we
																												// need
																												// to
																												// take

			for (int i = 0; i < cellIds.length; i++) {
				sb.append(cellIds[i]).append(",");
				sb.append(pciList[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_PCICoCell)).append(",");
				sb.append(rsiList[i]).append(",");
				sb.append(diversityList[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_Path)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_PowerBoosting)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_TAC)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_EAID)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_HSF)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_ZCZC)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CRS)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CcID)).append(",");
				String csrPort = "";
				try {
					String[] csrPortDetils = csrPortList[i].split("/"); // Swetha Need to check
					csrPort = csrPortDetils[csrPortDetils.length - 1];
				} catch (Exception ex) {
					logger.error("Exception csvStringForSprint() in GenerateCsvServiceImpl :"
							+ ExceptionUtils.getFullStackTrace(ex));
				}
				sb.append(csrPort).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cascade_ID)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CcPort_Add)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_RrhPort)).append(",");
				if (i <= rrhAliasList.length - 1) {
					sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CcID) + "_" + rrhAliasList[i] + "_"
							+ resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cascade_ID)).append(",");
				} else {
					sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CcID) + "_" + rrhAliasList[0] + "_"
							+ resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cascade_ID)).append(",");
				}
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_BeamformingMode)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_eMTC)).append("\n");
			}

			// ##############################################@NetworkShareInfo###############################################

			sb.append("@NetworkShareInfo##CellNum,Common,PLMN0,PLMN1,PLMN2,PLMN3,PLMN4,PLMN5\n");

			for (int i = 0; i < cellIds.length; i++) {
				sb.append(cellIds[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_Common)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN0)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN1)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN2)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN3)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN4)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NetworkShareInfo_PLMN5)).append("\n");
			}

			// ##############################################@RRH###############################################

			sb.append("@RRH##RRHAlias,RRHType,SubType,MauID,FAStartEarfcn1,FAStartEarfcn2,FAStartEarfcn3\n");

			String[] mauIdList = resultMapForConstants.get(Constants.ORAN_GROW_RRH_Mau_ID).split(",");
			for (int i = 0; i < rrhAliasList.length; i++) {
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_CELL_CcID) + "_" + rrhAliasList[i] + "_"
						+ resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cascade_ID)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_RRH_Type)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_RRH_Sub_Type)).append(",");
				sb.append(mauIdList[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn1)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn2)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn3)).append("\n");
			}

			// ###############################################@NBIoT###############################################
			sb.append(
					"@NBIoT##STATUS,AdminState,CellID,CellNum,ParentCellNum,NBIoTPCI,NBIoTTAC,InitialNprach,NprachStartTimeCE_LEVEL0,NprachSubcarrierOffsetCE_LEVEL0,NprachStartTimeCE_LEVEL1,NprachSubcarrierOffsetCE_LEVEL1,NprachStartTimeCE_LEVEL2,NprachSubcarrierOffsetCE_LEVEL2,OperationMode,GuardBand,AvoidInterfering,DlRB,UlRB,EarfcnDL,EarfcnOffsetDL,EarfcnUL,EarfcnOffsetUL,,,,,,\n");

			for (int i = 0; i < cellIds.length; i++) {
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_STATUS)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_AdminState)).append(",");
				sb.append(cellIds[i]).append(",");
				sb.append(cellIds[i]).append(",");
				sb.append(cellIds[i]).append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NBIoTPCI)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NBIoTTAC)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_InitialNprach)).append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCE_LEVEL0)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCE_LEVEL0))
						.append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCE_LEVEL1)).append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCE_LEVEL1))
						.append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCE_LEVEL2)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCE_LEVEL2))
						.append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_OperationMode)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_GuardBand)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_AvoidInterfering)).append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_DlRB)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_UlRB)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_EarfcnDL)).append(",");

				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_EarfcnOffsetDL)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_EarfcnUL)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_EarfcnOffsetUL)).append("\n");

			}
		} catch (Exception e) {
			logger.error(
					"Exception csvStringForSprint() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return sb;

	}

	private StringBuilder csvStringForLegacy(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel) {

		StringBuilder sb = new StringBuilder();

		String[] ParentCellNumber;
		String[] port_Split;
		String port_rrh;
		String sheetNames;

		try {
			String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
					Constants.VZ_GROW_Market);
			if (StringUtils.isNotEmpty(mkt) && Constants.VZ_GROW_UNY.equals(mkt)) {
				sheetNames = Constants.VZ_GROW_CIQUpstateNY;

			} else if (mkt.equals(Constants.VZ_GROW_NE)) {
				sheetNames = Constants.VZ_GROW_CIQNewEngland;
			} else {
				sheetNames = "";
			}

			listCIQDetailsModel = listCIQDetailsModel.stream().filter(X -> sheetNames.equals(X.getSheetAliasName()))
					.collect(Collectors.toList());

			// ###############################################@SysInfo###############

			sb.append(
					"@SysInfo##ID,Type,Version,RelVersion,ProfileName,Group,NeName,SerialNo,EnbType,InterConnectionMode,Location,SmartCell,Initial PCI,Use Parent PCI for Guard-band NB-IoT,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT 1XRTT NRT,Initial Inter-RAT HRPD NRT,,,,,,\n");

			String profile_name;
			String port_BandLen = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetNames, enbId,
					Constants.VZ_GROW_CPRI_Port_Assignment);

			String LCC_Check = "LCC";

			if ((port_BandLen.indexOf(LCC_Check) != -1) || (port_BandLen.indexOf("-") != -1)
					|| (port_BandLen.indexOf("(") != -1)) {

				profile_name = resultMapForConstants.get(Constants.ORAN_GROW_ProfileName1);
			} else {
				profile_name = resultMapForConstants.get(Constants.ORAN_GROW_ProfileName);
			}

			String SysInfoID = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetNames, enbId,
					Constants.VZ_GROW_Samsung_eNB_ID);
			/* 19 th col in consolidated csv i.e Target_eNB_ID(Samsung_eNB_I */
			String Type = resultMapForConstants.get(Constants.ORAN_GROW_Type);
			String pkgVer = resultMapForConstants.get(Constants.ORAN_GROW_Version);
			String RelVersion = resultMapForConstants.get(Constants.ORAN_GROW_RelVersion);
			String mkt_cli = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetNames, enbId,
					Constants.VZ_GROW_Market_CLLI_Code);
			String[] ar1 = objGenerateCsvRepository.getBucketDetails(mkt_cli);
			String Group = null;
			if (ar1 != null && ar1.length > 0) {
				Group = ar1[0];
			} else {
				Group = "MyGroup$$$$";
			}

			String series_6 = enbId.substring(0, 1);

			if (series_6.equals("6")) {
				Group = Group + "_1";
			}

			String Ne_Name = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetNames, enbId,
					Constants.VZ_GROW_eNB_Name);
			String Serial_Num = resultMapForConstants.get(Constants.ORAN_GROW_SerialNo);
			String Enb_Type = resultMapForConstants.get(Constants.ORAN_GROW_EnbType);
			String Interconnection_Mode = resultMapForConstants.get(Constants.ORAN_GROW_InterConnectionMode);
			String Location = resultMapForConstants.get(Constants.ORAN_GROW_Location);
			String Smart_Cell = resultMapForConstants.get(Constants.ORAN_GROW_SmartCell);
			String Initial_PCI = resultMapForConstants.get(Constants.ORAN_GROW_Initial_PCI);
			String UPCI = resultMapForConstants.get(Constants.ORAN_GROW_Use_Parent_PCI_for_Guard_band_NB_IoT);
			String Initial_RSI = resultMapForConstants.get(Constants.ORAN_GROW_Initial_RSI);
			String ILN = resultMapForConstants.get(Constants.ORAN_GROW_Initial_Intra_LTE_NRT);
			String IRN = resultMapForConstants.get(Constants.ORAN_GROW_Initial_Inter_RAT_1XRTT_NRT);
			String IRH = resultMapForConstants.get(Constants.ORAN_GROW_Initial_Inter_RAT_HRPD_NRT);

			sb.append(SysInfoID).append(",");
			sb.append(Type).append(",");
			sb.append(pkgVer).append(",");
			sb.append(RelVersion).append(",");
			sb.append(profile_name).append(",");
			sb.append(Group).append(",");
			if (StringUtils.isNotEmpty(Ne_Name)) {
				sb.append(Ne_Name.toUpperCase()).append(",");
			} else {
				Ne_Name = "";
				sb.append(Ne_Name).append(",");
			}

			sb.append(Serial_Num).append(",");
			sb.append(Enb_Type).append(",");
			sb.append(Interconnection_Mode).append(",");
			sb.append(Location).append(",");
			sb.append(Smart_Cell).append(",");
			sb.append(Initial_PCI).append(",");
			sb.append(UPCI).append(",");
			sb.append(Initial_RSI).append(",");
			sb.append(ILN).append(",");
			sb.append(IRN).append(",");
			sb.append(IRH).append(",,,,,,\n");

			// ###############################################@InterConnection#####

			sb.append("@InterConnection##");
			sb.append(
					"InterConnectionGroupID,InterConnectionRackID,InterConnectionNodeID,MateINodeID,,,,,,,,,,,,,,,,,,,,\n");

			// ###############################################@ClockMode##########

			sb.append("@ClockMode##");
			sb.append("No,Clock Source,Priority Level,Quality Level,,,,,,,,,,,,,,,,,,,,\n");

			String No = resultMapForConstants.get(Constants.ORAN_GROW_ClockMode_No);
			String Clock_Source = resultMapForConstants.get(Constants.ORAN_GROW_ClockMode_Clock_Source);
			String Priority_Level = resultMapForConstants.get(Constants.ORAN_GROW_ClockMode_Priority_Level);
			String Quality_Level = resultMapForConstants.get(Constants.ORAN_GROW_ClockMode_Quality_Level);

			sb.append(No).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priority_Level).append(",");
			sb.append(Quality_Level).append(",,,,,,,,,,,,,,,,,,,,\n");

			// ###############################################@ConfigDetail#########

			sb.append("@ConfigDetail##");
			sb.append(
					"IPVersion,First Master IP,Second Master IP,First Gateway IP,Second Gateway IP,ClockProfile,PTPDomain,,,,,,,,,,,,,,,,,\n");

			sb.append(",,,,,,,,,,,,,,,,,,,,,,\n");

			// ###############################################@ExternalLink#######

			sb.append("@ExternalLink##");
			sb.append("No,extLink,,,,,,,,,,,,,,,,,,,,,,\n");

			String extLink = resultMapForConstants.get(Constants.ORAN_GROW_ExternalLink_extLink);

			for (int i = 0; i < 4; i++) { // HARDCODED IN PERL AS 4
				sb.append(i).append(",");
				;
				sb.append(extLink).append(",,,,,,,,,,,,,,,,,,,,,,\n");
				;
			}

			// ###############################################@NWPhysicalLink############################################

			sb.append("@NWPhysicalLink##");
			sb.append(
					"No,IFName,IPVersion,IPAddress,IPPfxLen,OAM,LteSignalS1,LteSignalX2,LteBearerS1,LteBearerX2,LteBearerM1,LteSignalM2,IEEE1588,,,,,,,,,,,\n");

			// ###############################################@NWRoute###################################################

			sb.append("@NWRoute##");
			sb.append("No,IPVersion,IPPrefix,IPPfxLen,IPGW,,,,,,,,,,,,,,,,,,,\n");

			// ##################################################@MMEIPInfo#############################################

			sb.append("@MMEIPInfo##");
			sb.append(
					"No,IPType,IPV4,IPV6,ServicePurpose,AttachWithoutPDNConnectivity,CPOptimization,UPOptimization,,,,,,,,,,,,,,,,\n");

			String ip_type = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_IPType);
			String ipv4 = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_IPV4);

			String[] mme_ip;

			if (mkt.equals(Constants.VZ_GROW_UNY)) {
				mme_ip = objGenerateCsvRepository.getMmeIpDetails(Constants.VZ_GROW_UNY);
			} else {
				mme_ip = objGenerateCsvRepository.getMmeIpDetails(Constants.VZ_GROW_NE);
			}

			String service_purpose = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_ServicePurpose);
			String AWPDNC = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_AttachWithoutPDNConnectivity);
			String CP_Optimazation = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_CPOptimization);
			String UP_Optimazation = resultMapForConstants.get(Constants.ORAN_GROW_MMEIPInfo_UPOptimization);
			int num;

			for (int i = 0; i < 16; i++) { // HARDCODED IN PERL AS 16
				if (mkt.equals(Constants.VZ_GROW_NE)) {
					num = i;

				} else {
					num = i + 4;
				}
				if (i < mme_ip.length) {
					sb.append(num).append(",");
					sb.append(ip_type).append(",");
					sb.append(ipv4).append(",");
					sb.append(CommonUtil.formatIPV6Address(mme_ip[i])).append(",");
					sb.append(service_purpose).append(",");
					sb.append(AWPDNC).append(",");
					sb.append(CP_Optimazation).append(",");
					sb.append(UP_Optimazation).append(",,,,,,,,,,,,,,,,\n");

				} else {
					sb.append(num).append(",");
					sb.append(ip_type).append(",,,,,,,,,,,,,,,,,,,,,,\n");
				}

			}

			// ###############################################@GPS##############################################

			sb.append("@GPS##");
			sb.append("UserUpdateFlag,Latitude,Longitude,Height,,,,,,,,,,,,,,,,,,,,\n");

			String UserUpdateFlag = resultMapForConstants.get(Constants.ORAN_GROW_GPS_UserUpdateFlag);
			String Latitude = resultMapForConstants.get(Constants.ORAN_GROW_GPS_Latitude);
			String Longitude = resultMapForConstants.get(Constants.ORAN_GROW_GPS_Longitude);
			String Height = resultMapForConstants.get(Constants.ORAN_GROW_GPS_Height);

			sb.append(UserUpdateFlag).append(",");
			sb.append(Latitude).append(",");
			sb.append(Longitude).append(",");
			sb.append(Height).append(",,,,,,,,,,,,,,,,,,,,\n");

			// ##############################################@Cell###############################################

			sb.append("@Cell##");
			sb.append(
					"Index,State,Sector ID,Carrier ID,CC ID,DSP ID,Cell Index in DSP,PCI,RSI,Diversity,VirtualRFPortMapping,MultiCarrier Type,eMTC,CRS,TAC,EAID,HSF,ZCZC,CPRI Port ID,Cascade ID,RRH Port ID,RRH Conf,ULCoMP,DLMaxTxPower\n");

			int bandCnt = listCIQDetailsModel.stream().filter(x -> sheetNames.equalsIgnoreCase(x.getSheetAliasName()))
					.collect(Collectors.toList()).size();
			int bandLngth;
			int Index;
			int carrierId_Def = Integer.parseInt(resultMapForConstants.get(Constants.ORAN_GROW_CELL_carrierId_Def));
			String carrier_ID;
			String CC_ID = "";
			String[] DSPID, DSPID1, DSPID2;
			String[] Cell_Index_DSP;

			if ((port_BandLen.indexOf(LCC_Check) != -1) || (port_BandLen.indexOf("-") != -1)
					|| (port_BandLen.indexOf("(") != -1)) {
				bandLngth = 24;
				Cell_Index_DSP = resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cell_Index_in_DSP1).split(",");
			} else {
				bandLngth = 12;
				Cell_Index_DSP = resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cell_Index_in_DSP2).split(",");
			}

			String[] Band1 = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_BandName, sheetNames, bandLngth);

			String[] carrier_output = getCarrierId_lsm(listCIQDetailsModel, sheetNames, bandLngth);

			String diversity;
			int Dl_Max_Tx_Pwr;
			String Dl_Max_Tx_Pwr_def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_Dl_Max_Tx_Pwr_def);
			String[] Bndwidth = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_Bandwidth, sheetNames,
					bandLngth);
			String[] Earfcn_DL = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_DL, sheetNames,
					bandLngth);
			String[] Earfcn_UL = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_UL, sheetNames,
					bandLngth);
			ParentCellNumber = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_Cell_ID, sheetNames, bandLngth);
			String ParentCellNum_Def = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_ParentCellNum_Def);
			String[] tac = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_TAC, sheetNames, bandLngth);
			String tac_def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_tac_def);
			String[] pci = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_PCI, sheetNames, bandLngth);
			String pci_def_cell = resultMapForConstants.get(Constants.ORAN_GROW_CELL_pci_def_cell);
			String rsi_def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_rsi_def);
			String diversity_def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_diversity_def);
			String[] rsi = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_RACH, sheetNames, bandLngth);
			String[] portId = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_CPRI_Port_Assignment, sheetNames,
					bandLngth);

			String[] portId_def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_portId_def).split(",");
			String rrh_conf_def;
			String rrh_conf;
			String Virtual_RF_Port_Mapping = resultMapForConstants.get(Constants.ORAN_GROW_CELL_VirtualRFPortMapping);
			String MultiCarrier_Type_Def = resultMapForConstants.get(Constants.ORAN_GROW_CELL_MultiCarrier_Type_Def);
			String eMTC = resultMapForConstants.get(Constants.ORAN_GROW_CELL_eMTC);
			String CRS = null;
			String EAID = resultMapForConstants.get(Constants.ORAN_GROW_CELL_EAID);
			String HSF = resultMapForConstants.get(Constants.ORAN_GROW_CELL_HSF);
			String ZCZC = resultMapForConstants.get(Constants.ORAN_GROW_CELL_ZCZC);
			String rrh_port_id = resultMapForConstants.get(Constants.ORAN_GROW_CELL_RRH_Port_ID);
			String CascadeID = resultMapForConstants.get(Constants.ORAN_GROW_CELL_Cascade_ID);
			String Card = "";
			String[] Pwr = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_Output_Power, sheetNames, bandLngth);
			String[] Tx_Diversity = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_Tx_Diversity, sheetNames,
					bandLngth);
			String[] Rx_Diversity = dataCiqIndex_lsm(listCIQDetailsModel, Constants.VZ_GROW_Rx_Diveristy, sheetNames,
					bandLngth);

			String UL_Comp = resultMapForConstants.get(Constants.ORAN_GROW_CELL_ULCoMP);
			int sector_ID = 0, sectorId_None = 0;
			String curBand;
			int bandIndex = 0;
			String MultiCarrier_Type = null;
			String DSP_ID;
			String cell_num;

			int j1 = 0, j2 = 0;

			for (int i = 0; i < bandLngth; i++) {

				Index = i;

				if ((portId[i].indexOf("LCC-1") != -1) || (portId[i].indexOf("LCC1") != -1)) {
					DSPID1 = resultMapForConstants.get(Constants.ORAN_GROW_CELL_DSP_ID).split(",");
					DSP_ID = DSPID1[j1];
					j1++;
				} else {
					DSPID2 = resultMapForConstants.get(Constants.ORAN_GROW_CELL_DSP_ID).split(",");
					DSP_ID = DSPID2[j2];
					j2++;
				}
				int Cell_Index_in_DSP = Integer.parseInt(Cell_Index_DSP[i].trim());

				cell_num = ParentCellNumber[i];

				if (!(cell_num.equals("1234"))) {
					if (cell_num.length() < 2) {
						sector_ID = Integer.parseInt(cell_num);
						carrier_ID = "1";
					} else {
						sector_ID = Integer.parseInt(cell_num.substring(0, 1));
						carrier_ID = cell_num.substring(1);
					}

				} else {
					cell_num = ParentCellNum_Def;
				}

				if (sectorId_None == 3) {
					sectorId_None = 0;
				}
				carrier_ID = carrier_output[i];

				curBand = Band1[i];
				if (curBand.equals("700")) {
					curBand = "700MHz";
				}

				if ("10MHZ".equalsIgnoreCase(Bndwidth[i])) {
					MultiCarrier_Type = "10/10/10/10/5/5/5/5_CONF.3";
				} else if ("20Mhz".equalsIgnoreCase(Bndwidth[i])) {
					MultiCarrier_Type = "20/20/20/10/5_CONF.3";
				} else if ("15MHz".equalsIgnoreCase(Bndwidth[i])) {
					MultiCarrier_Type = "f15/f15/f15/10/5_CONF.3";
				} else if ("5MHz".equalsIgnoreCase(Bndwidth[i])) {
					MultiCarrier_Type = "10/10/10/10/5/5/5/5_CONF.3";
				} else {
					MultiCarrier_Type = "";
				}

				if (!("1234".equals(Band1[i]))) {
					Dl_Max_Tx_Pwr = Math.round(10 * Float.parseFloat(Pwr[i]));
				} else {
					Dl_Max_Tx_Pwr = 0;
				}

				// Issue here portId[i] is a single digit
				if (portId[i].length() > 6) {
					String splitStr = portId[i].substring(1, 2);
					Card = portId[i].substring(6);

					if (splitStr.equals("-")) {
						port_Split = portId[i].split("-");
					}
					port_Split = portId[i].split("\\(");
					port_rrh = port_Split[0];

				}
				/* Hard coded as of now solve it */
				else {
					port_rrh = portId[i];
				}
				if (Card.equals("1)")) {
					CC_ID = "0";
				} else if (Card.equals("2)")) {
					CC_ID = "1";
				} else {
					CC_ID = "0";
				}
				rrh_conf = CC_ID + "_" + port_rrh + "_" + "0";
				rrh_conf_def = "0_" + portId_def[i] + "_0";

				if (("2".equals(Tx_Diversity[i])) && "2".equals(Rx_Diversity[i])) {
					CRS = "2CRS";
				} else if ("4".equals(Tx_Diversity[i]) && "4".equals(Rx_Diversity[i])) {
					CRS = "4CRS";
				} else if ("2".equals(Tx_Diversity[i]) && "4".equals(Rx_Diversity[i])) {
					CRS = "2CRS";
				} else {
					if (mkt.equals(Constants.VZ_GROW_NE)) {
						CRS = "4CRS";
					} else {
						CRS = "2CRS";
					}
				}

				if ((Earfcn_DL[i].equals("67086")) && ("0".equals(Rx_Diversity[i]))) {
					Rx_Diversity[i] = "4";
				}

				diversity = Tx_Diversity[i] + "Tx" + Rx_Diversity[i] + "Rx";

				sectorId_None = sectorId_None + 1;

				StringBuilder s_24 = new StringBuilder();

				s_24.append(Index).append(",");
				if ("1234".equals(Band1[i])) {
					s_24.append("NONE").append(",");
				} else {
					s_24.append("ADD").append(",");
				}
				if (!("1234".equals(Band1[i]))) {
					s_24.append(sector_ID).append(",");
					s_24.append(carrier_ID).append(",");
				} else {
					s_24.append(sectorId_None).append(",");
					s_24.append(carrierId_Def).append(",");

				}
				s_24.append(CC_ID).append(",");
				s_24.append(DSP_ID).append(",");
				s_24.append(Cell_Index_in_DSP).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(pci[i]).append(",");
					s_24.append(rsi[i]).append(",");
					s_24.append(diversity).append(",");
				} else {
					s_24.append(pci_def_cell).append(",");
					s_24.append(rsi_def).append(",");
					s_24.append(diversity_def).append(",");
				}
				s_24.append(Virtual_RF_Port_Mapping).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(MultiCarrier_Type).append(",");
				} else {
					s_24.append(MultiCarrier_Type_Def).append(",");
				}
				s_24.append(eMTC).append(",");
				s_24.append(CRS).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(tac[i]).append(",");
				} else {
					s_24.append(tac_def).append(",");
				}
				s_24.append(EAID).append(",");
				s_24.append(HSF).append(",");
				s_24.append(ZCZC).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(port_rrh).append(",");
				} else {
					s_24.append(portId_def[i]).append(",");
				}
				s_24.append(CascadeID).append(",");
				s_24.append(rrh_port_id).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(rrh_conf).append(",");
				} else {
					s_24.append(rrh_conf_def).append(",");
				}
				s_24.append(UL_Comp).append(",");
				if (!("1234".equals(Band1[i]))) {
					s_24.append(Dl_Max_Tx_Pwr).append("\n");
				} else {
					s_24.append(Dl_Max_Tx_Pwr_def).append("\n");
				}

				sb.append(s_24);
				Cell_Index_in_DSP++;
			}

			// ##############################################@FA###########################################

			sb.append("@FA##");
			sb.append("Index,Bandwidth,EarfcnDL,EarfcnUL,Frequency Profile,,,,,,,,,,,,,,,,,,,\n");

			String Earfcn_DL_temp = resultMapForConstants.get(Constants.ORAN_GROW_FA_EarfcnDL);
			String Earfcn_UL_temp = resultMapForConstants.get(Constants.ORAN_GROW_FA_EarfcnUL);
			String frequency_profile = resultMapForConstants.get(Constants.ORAN_GROW_FA_Frequency_Profile);
			int Index1;
			String Bandwidth;

			String[] Band1_def = resultMapForConstants.get(Constants.ORAN_GROW_FA_Band1_def).split(",");

			for (int i = 0; i < bandLngth; i++) {
				Index1 = i;

				curBand = Band1[i];
				Bandwidth = curBand + "/" + Bndwidth[i];

				if (Bandwidth.equals("AWS-2/" + Bndwidth[i])) {
					Bandwidth = "AWS-1/" + Bndwidth[i];
				}

				StringBuilder if_str0 = new StringBuilder();

				if_str0.append(Index1).append(",");
				if (!("1234".equals(Band1[i]))) {
					if_str0.append(Bandwidth).append(",");
					if_str0.append(Earfcn_DL[i]).append(",");
					if_str0.append(Earfcn_UL[i]).append(",");
				} else {
					if_str0.append(Band1_def[i]).append(",");
					if_str0.append(Earfcn_DL_temp).append(",");
					if_str0.append(Earfcn_UL_temp).append(",");
				}
				if_str0.append(frequency_profile).append(",,,,,,,,,,,,,,,,,,,\n");

				sb.append(if_str0);

			}

			// ##############################################@Location#################################

			sb.append("@Location##");
			sb.append("Index,Auto GPS,Latitude,Longitude,Height,,,,,,,,,,,,,,,,,,,\n");

			String Auto_GPS = resultMapForConstants.get(Constants.ORAN_GROW_Location_Auto_GPS);
			String Latitude1 = resultMapForConstants.get(Constants.ORAN_GROW_Location_Latitude);
			String Longitude1 = resultMapForConstants.get(Constants.ORAN_GROW_Location_Longitude);
			String Height1 = resultMapForConstants.get(Constants.ORAN_GROW_Location_Height);

			for (int i = 0; i < bandLngth; i++) {
				/* Condition checked in perl is skipped here.Please check once */
				sb.append(i).append(",");
				sb.append(Auto_GPS).append(",");
				sb.append(Latitude1).append(",");
				sb.append(Longitude1).append(",");
				sb.append(Height1).append(",,,,,,,,,,,,,,,,,,,\n");

			}

			// ###############################################@NBIoT###################################

			sb.append("@NBIoT##");
			sb.append(
					"Index,State,ParentCellNum,NBIoTPCI,NBIoTTAC,InitialNprach,NprachStartTimeCL1,NprachSubcarrierOffsetCL1,NprachStartTimeCL2,NprachSubcarrierOffsetCL2,NprachStartTimeCL3,NprachSubcarrierOffsetCL3,GuardBand,,,,,,,,,,,\n");

			String State = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_State);

			String NBIoTTAC = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NBIoTTAC);
			String InitialNprach = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_InitialNprach);
			String NprachStartTimeCL1 = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCL1);
			;
			String NprachSubcarrierOffsetCL1 = resultMapForConstants
					.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCL1);
			;
			String NprachStartTimeCL2 = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCL2);
			;
			String NprachSubcarrierOffsetCL2 = resultMapForConstants
					.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCL2);
			String NprachStartTimeCL3 = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_NprachStartTimeCL3);
			String NprachSubcarrierOffsetCL3 = resultMapForConstants
					.get(Constants.ORAN_GROW_NBIoT_NprachSubcarrierOffsetCL3);
			String GuardBand = resultMapForConstants.get(Constants.ORAN_GROW_NBIoT_GuardBand);

			for (int i = 0; i < bandLngth; i++) {
				String ParentCellNum;
				/*
				 * if (i < listCIQDetailsModel.size()) { ParentCellNum = ParentCellNumber[i]; }
				 * else { ParentCellNum = ParentCellNum_Def; }
				 */

				StringBuilder if_str1 = new StringBuilder();

				if_str1.append(i).append(",");
				if_str1.append(State).append(",");
				if (!("1234".equals(Band1[i]))) {
					if_str1.append(ParentCellNumber[i]).append(",");
				} else {
					if_str1.append(ParentCellNum_Def).append(",");
				}
				if_str1.append(i).append(",");
				if_str1.append(NBIoTTAC).append(",");
				if_str1.append(InitialNprach).append(",");
				if_str1.append(NprachStartTimeCL1).append(",");
				if_str1.append(NprachSubcarrierOffsetCL1).append(",");
				if_str1.append(NprachStartTimeCL2).append(",");
				if_str1.append(NprachSubcarrierOffsetCL2).append(",");
				if_str1.append(NprachStartTimeCL3).append(",");
				if_str1.append(NprachSubcarrierOffsetCL3).append(",");
				if_str1.append(GuardBand).append(",,,,,,,,,,,\n");

				sb.append(if_str1);
			}

			// ##################################@RRH############################################

			sb.append("@RRH##");
			sb.append(
					"RRH Conf,State,RRH Type,FAStartEarfcn1,FAStartEarfcn2,Azimuth,BeamWidth,SerialNo,AntennaCableLength0,AntennaCableLength1,AntennaCableLength2,AntennaCableLength3,AntennaCableLength4,AntennaCableLength5,AntennaCableLength6,AntennaCableLength7,,,,,,,,\n");

			String[] port_id_def = resultMapForConstants.get(Constants.ORAN_GROW_RRH_port_id_def).split(",");
			String RRH_Sub_Def = resultMapForConstants.get(Constants.ORAN_GROW_RRH_RRH_Sub_Def);

			String[] uniq_port = getPortByCascadeId(listCIQDetailsModel, sheetNames);

			String[] FA_StartEarfcn1_Def;

			if (bandLngth == 24) {
				FA_StartEarfcn1_Def = resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn11).split(",");
			} else {
				FA_StartEarfcn1_Def = resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn12).split(",");
			}

			String[] FA_StartEarfcn2_Def = resultMapForConstants.get(Constants.ORAN_GROW_RRH_FAStartEarfcn2).split(",");

			String[] Band12 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_BandName, sheetNames);
			String[] portId1 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_CPRI_Port_Assignment, sheetNames);

			String[] Earfcn_DL1 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_DL, sheetNames);
			String[] Earfcn_UL1 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_UL, sheetNames);
			String[] Rx_Diversity1 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Rx_Diveristy, sheetNames);

			String FA_StartEarfcn1;
			String FA_StartEarfcn2;
			String RRH_Sub = "";
			String RRH_Conf;
			String RRH_Conf_Def;
			String Azimuth = resultMapForConstants.get(Constants.ORAN_GROW_RRH_Azimuth);
			String BeamWidth = resultMapForConstants.get(Constants.ORAN_GROW_RRH_BeamWidth);
			String AntennaCableLength0;
			String AntennaCableLength1;
			String AntennaCableLength2;
			String AntennaCableLength3;
			String AntennaCableLength4;
			String AntennaCableLength5;
			String AntennaCableLength6;
			String AntennaCableLength7;
			String Antenna_Cable_Length = "";
			int sector_ID1 = Integer.parseInt(resultMapForConstants.get(Constants.ORAN_GROW_RRH_sector_ID));
			String curBand1;
			String current_Band;
			String Card1;
			String splitSamRRH;
			String FA_strt1;
			String FA_strt2;

			String[] rrh_earfcn1;
			String[] rrh_earfcn2;
			String[] RRH_Samsung;
			String[] rrh_code;
			int j = 0;
			int count = 0;
			int k = 0;

			String[] deployment = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Deployment, sheetNames);
			String[] band_uniq = getBandUniq(listCIQDetailsModel, sheetNames);

			String dep_type;

			for (int i = 0; i < uniq_port.length; i++) {
				if (j < band_uniq.length) {
					current_Band = band_uniq[j];
				} else {
					j = 0;
					current_Band = band_uniq[j];
				}
				/*
				 * if (Band12[i].equals(current_Band)) { FA_strt1 = FA_StartEarfcn1_Def[k];
				 * FA_strt2 = FA_StartEarfcn2_Def[k]; count++; } else { j = count + 1; k = k +
				 * 1; FA_strt1 = FA_StartEarfcn1_Def[k]; FA_strt2 = FA_StartEarfcn2_Def[k]; }
				 */
				if (Band12[i].equals(current_Band)) {
					if (Band12[i].equals("850MHz")) {
						FA_StartEarfcn1_Def[k] = "2400";
					} else {
						FA_StartEarfcn1_Def[k] = FA_StartEarfcn1_Def[k];
					}
					FA_strt1 = FA_StartEarfcn1_Def[k];
					FA_strt2 = FA_StartEarfcn2_Def[k];
				} else {
					j = j + 1;
					k = k + 1;
					if (Band12[i].equals("850MHz")) {
						FA_StartEarfcn1_Def[k] = "2400";
					} else {
						FA_StartEarfcn1_Def[k] = FA_StartEarfcn1_Def[k];
					}
					FA_strt1 = FA_StartEarfcn1_Def[k];
					FA_strt2 = FA_StartEarfcn2_Def[k];
				}

				if (sector_ID1 == 3) {
					sector_ID1 = 0;
				}

				curBand1 = Band12[i];
				if (CommonUtil.isValidObject(uniq_port[i]) && uniq_port[i].length() > 6) {

					String splitStr = uniq_port[i].substring(1, 2);
					Card1 = uniq_port[i].substring(6);
					if ("1)".equals(Card1)) {
						CC_ID = "0";
					} else if ("2)".equals(Card1)) {
						CC_ID = "1";
					}

					if ("-".equals(splitStr)) {
						port_Split = uniq_port[i].split("-");
					} else {
						port_Split = uniq_port[i].split("\\(");
					}

					port_rrh = port_Split[0];

				} else {
					CC_ID = "0";
					port_rrh = uniq_port[i];
				}

				RRH_Conf = CC_ID + "_" + port_rrh + "_" + "0";

				RRH_Conf_Def = "0" + "_" + port_id_def[i] + "_" + "0";

				rrh_code = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RRH_Code, sheetNames);
				/*
				 * Here rrh code was an array with all 61st colmn values for matching enb id's
				 */

				String SamRrhType = rrh_code[i];
				if (SamRrhType.indexOf(">") != -1) {
					RRH_Samsung = SamRrhType.split(">");
					splitSamRRH = RRH_Samsung[1];
				} else {
					splitSamRRH = SamRrhType;
				}

				if (deployment[0].indexOf("RAN") != -1) {
					dep_type = "OPEN RAN";
				} else {
					dep_type = "OPEN CPRI";
				}

				if ((curBand1.equals("AWS-1")) || (curBand1.equals("AWS-2")) || (curBand1.equals("AWS-3"))) {
					curBand1 = "AWS";
				}
				if ((curBand1.equals("PCS-2"))) {
					curBand1 = "PCS";
				}

				if ((splitSamRRH.equals("Samsung RRH")) || (splitSamRRH.equals("SAMSUNG RRH"))) {
					if ((curBand1.equals("700MHz")) || (curBand1.equals("850 LTE")) || (curBand1.equals("850LTE"))) {
						RRH_Sub = "RFV01U_D20";
					} else if ((curBand1.equals("AWS")) || (curBand1.equals("PCS"))) {
						RRH_Sub = "RFV01U_D10";
					}
				} else {
					// RRH_Sub = get_rrh_type_bycode(portId[i]);
					String oldModel = "";
					List<CIQDetailsModel> detailsModels1 = fileUploadRepository.getEnBData(dbcollectionFileName,
							sheetNames, enbId);
					if (CommonUtil.isValidObject(detailsModels1) && detailsModels1.size() > 0) {
						for (CIQDetailsModel details : detailsModels1) {
							if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
									&& CommonUtil.isValidObject(
											details.getCiqMap().containsKey(Constants.VZ_GROW_CPRI_Port_Assignment))) {
								String cpriPort = details.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment)
										.getHeaderValue();

								if (CommonUtil.isValidObject(cpriPort) && cpriPort.equals(uniq_port[i])) {
									if (CommonUtil.isValidObject(details)
											&& CommonUtil.isValidObject(details.getCiqMap())
											&& CommonUtil.isValidObject(
													details.getCiqMap().containsKey(Constants.VZ_GROW_RRH_Code))) {
										oldModel = details.getCiqMap().get(Constants.VZ_GROW_RRH_Code).getHeaderValue();
										break;
									}
								}
							}
						}
					}

					String[] om = objGenerateCsvRepository.getRrhAluDetails(oldModel);

					if (om != null && om.length > 0) {
						RRH_Sub = om[0];
					} else {
						RRH_Sub = "RRH_SUB$$$$";
					}
				}

				List<CIQDetailsModel> detailsModels = fileUploadRepository.getEnBData(dbcollectionFileName, sheetNames,
						enbId);
				if (CommonUtil.isValidObject(detailsModels) && detailsModels.size() > 0) {
					for (CIQDetailsModel details : detailsModels) {
						if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
								&& CommonUtil.isValidObject(
										details.getCiqMap().containsKey(Constants.VZ_GROW_CPRI_Port_Assignment))) {
							String cpriPort = details.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment)
									.getHeaderValue();
							if (CommonUtil.isValidObject(cpriPort) && cpriPort.equals(uniq_port[i])) {
								if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
										&& CommonUtil.isValidObject(details.getCiqMap()
												.containsKey(Constants.VZ_GROW_antennaPathDelayUL))) {
									Antenna_Cable_Length = details.getCiqMap().get(Constants.VZ_GROW_antennaPathDelayUL)
											.getHeaderValue();
									break;
								}
							}
						}
					}

				}

				// Antenna_Cable_Length = get_antennalength_by_enode(portId[i]);

				if ((Integer.parseInt(Rx_Diversity1[i]) == 4) && (!curBand1.equals("PCS"))
						&& (!curBand1.equals("PCS-2"))) {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = Antenna_Cable_Length;
					AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = "0";
				} else if ((Integer.parseInt(Rx_Diversity1[i]) == 2) && (!curBand1.equals("PCS"))
						&& (!curBand1.equals("PCS-2"))) {
					AntennaCableLength0 = AntennaCableLength1 = Antenna_Cable_Length;
					AntennaCableLength2 = AntennaCableLength3 = AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = "0";

				} else if ((Integer.parseInt(Rx_Diversity1[i]) == 4) && (dep_type.equals("OPEN RAN"))
						&& ((curBand1.equals("PCS")) || (curBand1.equals("PCS-2")))) {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = "0";
					AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = Antenna_Cable_Length;

				} else if ((Integer.parseInt(Rx_Diversity1[i]) == 2) && (dep_type.equals("OPEN RAN"))
						&& ((curBand1.equals("PCS")) || (curBand1.equals("PCS-2")))) {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = "0";
					AntennaCableLength4 = AntennaCableLength5 = Antenna_Cable_Length;
					AntennaCableLength6 = AntennaCableLength7 = "0";

				} else if ((Integer.parseInt(Rx_Diversity1[i]) == 4) && (dep_type.equals("OPEN CPRI"))
						&& ((curBand1.equals("PCS")) || (curBand1.equals("PCS-2")))) {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = Antenna_Cable_Length;
					AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = "0";

				} else if ((Integer.parseInt(Rx_Diversity1[i]) == 2) && (dep_type.equals("OPEN CPRI"))
						&& ((curBand1.equals("PCS")) || (curBand1.equals("PCS-2")))) {
					AntennaCableLength0 = AntennaCableLength1 = Antenna_Cable_Length;
					AntennaCableLength2 = AntennaCableLength3 = AntennaCableLength4 = AntennaCableLength5 = "0";
					AntennaCableLength6 = AntennaCableLength7 = "0";

				} else {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = "0";
					AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = "0";
				}

				if (CommonUtil.isValidObject(Antenna_Cable_Length) && Antenna_Cable_Length.equals("TBD")) {
					AntennaCableLength0 = AntennaCableLength1 = AntennaCableLength2 = AntennaCableLength3 = AntennaCableLength4 = AntennaCableLength5 = AntennaCableLength6 = AntennaCableLength7 = "0";
				}
				FA_StartEarfcn1 = Earfcn_DL1[i];
				FA_StartEarfcn2 = Earfcn_UL1[i];

				sector_ID1++;

				StringBuilder if_1 = new StringBuilder();

				if_1.append(RRH_Conf).append(",");
				if_1.append("ADD").append(",");
				if_1.append(RRH_Sub).append(",");
				if_1.append(FA_StartEarfcn1_Def[k]).append(",");
				if_1.append(FA_StartEarfcn2_Def[k]).append(",");
				if_1.append(Azimuth).append(",");
				if_1.append(BeamWidth).append(",").append(",");
				if_1.append(AntennaCableLength0).append(",");
				if_1.append(AntennaCableLength1).append(",");
				if_1.append(AntennaCableLength2).append(",");
				if_1.append(AntennaCableLength3).append(",");
				if_1.append(AntennaCableLength4).append(",");
				if_1.append(AntennaCableLength5).append(",");
				if_1.append(AntennaCableLength6).append(",");
				if_1.append(AntennaCableLength7).append(",,,,,,,,\n");

				StringBuilder else_1 = new StringBuilder();

				else_1.append(RRH_Conf_Def).append(",");
				else_1.append("NONE").append(",");
				else_1.append(RRH_Sub_Def).append(",");
				else_1.append(FA_StartEarfcn1_Def[k]).append(",");
				else_1.append(FA_StartEarfcn2_Def[k]).append(",");
				else_1.append(Azimuth).append(",");
				else_1.append(BeamWidth).append(",").append(",");
				else_1.append(AntennaCableLength0).append(",");
				else_1.append(AntennaCableLength1).append(",");
				else_1.append(AntennaCableLength2).append(",");
				else_1.append(AntennaCableLength3).append(",");
				else_1.append(AntennaCableLength4).append(",");
				else_1.append(AntennaCableLength5).append(",");
				else_1.append(AntennaCableLength6).append(",");
				else_1.append(AntennaCableLength7).append(",,,,,,,,\n");

				if (bandLngth == 24) {
					if (bandCnt == 24) {
						sb.append(if_1);
					}
					if ((bandCnt == 23) && (i < 23)) {
						sb.append(if_1);
					} else if (bandCnt == 23) {
						sb.append(else_1);
					}
					if ((bandCnt == 22) && (i < 22)) {
						sb.append(if_1);
					} else if (bandCnt == 22) {
						sb.append(else_1);
					}
					if ((bandCnt == 21) && (i < 21)) {
						sb.append(if_1);
					} else if (bandCnt == 21) {
						sb.append(else_1);
					}
					if ((bandCnt == 20) && (i < 20)) {
						sb.append(if_1);
					} else if (bandCnt == 20) {
						sb.append(else_1);
					}
					if ((bandCnt == 19) && (i < 19)) {
						sb.append(if_1);
					} else if (bandCnt == 19) {
						sb.append(else_1);
					}
					if ((bandCnt == 18) && (i < 18)) {
						sb.append(if_1);
					} else if (bandCnt == 18) {
						sb.append(else_1);
					}
					if ((bandCnt == 17) && (i < 17)) {
						sb.append(if_1);
					} else if (bandCnt == 17) {
						sb.append(else_1);
					}
					if ((bandCnt == 16) && (i < 16)) {
						sb.append(if_1);
					} else if (bandCnt == 16) {
						sb.append(else_1);
					}
					if ((bandCnt == 15) && (i < 15)) {
						sb.append(if_1);
					} else if (bandCnt == 15) {
						sb.append(else_1);
					}
					if ((bandCnt == 14) && (i < 14)) {
						sb.append(if_1);
					} else if (bandCnt == 14) {
						sb.append(else_1);
					}
					if ((bandCnt == 13) && (i < 13)) {
						sb.append(if_1);
					} else if (bandCnt == 13) {
						sb.append(else_1);
					}
					if ((bandCnt == 12) && (i < 12)) {
						sb.append(if_1);
					} else if (bandCnt == 12) {
						sb.append(else_1);
					}
					if ((bandCnt == 11) && (i < 11)) {
						sb.append(if_1);
					} else if (bandCnt == 11) {
						sb.append(else_1);
					}
					if ((bandCnt == 10) && (i < 10)) {
						sb.append(if_1);
					} else if (bandCnt == 10) {
						sb.append(else_1);
					}
					if ((bandCnt == 9) && (i < 9)) {
						sb.append(if_1);
					} else if (bandCnt == 9) {
						sb.append(else_1);
					}
					if ((bandCnt == 8) && (i < 8)) {
						sb.append(if_1);
					} else if (bandCnt == 8) {
						sb.append(else_1);
					}
					if ((bandCnt == 7) && (i < 7)) {
						sb.append(if_1);
					} else if (bandCnt == 7) {
						sb.append(else_1);
					}
					if ((bandCnt == 6) && (i < 6)) {
						sb.append(if_1);
					} else if (bandCnt == 6) {
						sb.append(else_1);
					}
					if ((bandCnt == 5) && (i < 5)) {
						sb.append(if_1);
					} else if (bandCnt == 5) {
						sb.append(else_1);
					}
					if ((bandCnt == 4) && (i < 4)) {
						sb.append(if_1);
					} else if (bandCnt == 4) {
						sb.append(else_1);
					}
					if ((bandCnt == 3) && (i < 3)) {
						sb.append(if_1);
					} else if (bandCnt == 3) {
						sb.append(else_1);
					}
					if ((bandCnt == 2) && (i < 2)) {
						sb.append(if_1);
					} else if (bandCnt == 2) {
						sb.append(else_1);
					}
					if ((bandCnt == 1) && (i < 1)) {
						sb.append(if_1);
					} else if (bandCnt == 1) {
						sb.append(else_1);
					}

				} else {
					if (bandCnt == 12) {
						sb.append(if_1);
					}
					if ((bandCnt == 11) && (i < 11)) {
						sb.append(if_1);
					} else if (bandCnt == 11) {
						sb.append(else_1);
					}
					if ((bandCnt == 10) && (i < 10)) {
						sb.append(if_1);
					} else if (bandCnt == 10) {
						sb.append(else_1);
					}
					if ((bandCnt == 9) && (i < 9)) {
						sb.append(if_1);
					} else if (bandCnt == 9) {
						sb.append(else_1);
					}
					if ((bandCnt == 8) && (i < 8)) {
						sb.append(if_1);
					} else if (bandCnt == 8) {
						sb.append(else_1);
					}
					if ((bandCnt == 7) && (i < 7)) {
						sb.append(if_1);
					} else if (bandCnt == 7) {
						sb.append(else_1);
					}
					if ((bandCnt == 6) && (i < 6)) {
						sb.append(if_1);
					} else if (bandCnt == 6) {
						sb.append(else_1);
					}
					if ((bandCnt == 5) && (i < 5)) {
						sb.append(if_1);
					} else if (bandCnt == 5) {
						sb.append(else_1);
					}
					if ((bandCnt == 4) && (i < 4)) {
						sb.append(if_1);
					} else if (bandCnt == 4) {
						sb.append(else_1);
					}
					if ((bandCnt == 3) && (i < 3)) {
						sb.append(if_1);
					} else if (bandCnt == 3) {
						sb.append(else_1);
					}
					if ((bandCnt == 2) && (i < 2)) {
						sb.append(if_1);
					} else if (bandCnt == 2) {
						sb.append(else_1);
					}
					if ((bandCnt == 1) && (i < 1)) {
						sb.append(if_1);
					} else if (bandCnt == 1) {
						sb.append(else_1);
					}
				}

			}

		} catch (Exception e) {
			logger.error("Exception csvString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return sb;

	}

	private StringBuilder getENBStringForVlsm(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel) {

		StringBuilder sb = new StringBuilder();

		String sheetName = "";

		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);

		if (mkt.equals(Constants.VZ_GROW_UNY)) {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		} else if (mkt.equals(Constants.VZ_GROW_NE)) {
			sheetName = Constants.VZ_GROW_CIQNewEngland;
		}

		try {

			String siteConfigType = "";
			String rsIp = "";
			String lsmName = "";

			siteConfigType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())) {
				rsIp = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
				lsmName = neMappingEntity.getNetworkConfigEntity().getNeName();
			}

			// #########@ENB###############
			sb.append("@ENB").append("\n");
			sb.append(
					"NE ID,NE Type,NE Version,RelVersion,Network,NE Name,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT 1XRTT NRT,Initial Inter-RAT HRPD NRT,Initial SRS Nrt,Initial SRS Pool Index,Initial Inter-RAT NRT NR,Customer NE Type,Rack ID,Time Offset,CBRS Mode,CBRS User ID,CBRS Measure Unit\n");

			String NE_ID = enbId;
			String NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Type);
			String NE_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Version);
			String RelVersion = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_RelVersion);
			String Ne_Name = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String Cascade_ID = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String enb_str = Cascade_ID.substring(0, 3);
			String Group = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Market_CLLI_Code) + "_"
					+ enb_str;

			String Initial_PCI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_PCI);
			String Initial_RSI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_RSI);
			String Initial_Intra_LTE_NRT = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_Intra_LTE_NRT);
			String Initial_Inter_RAT_1XRTT_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_1XRTT_NRT);
			String Initial_Inter_RAT_HRPD_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_HRPD_NRT);
			String Initial_SRS_Nrt = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Nrt);
			String Initial_SRS_Pool_Index = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Pool_Index);
			String Initial_Inter_RAT_NRT_NR = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_NRT_NR);
			String Customer_NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Customer_NE_Type);
			String Rack_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Rack_ID);
			String Multi_Time_Zone = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Multi_Time_Zone);
			String Time_Offset = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Time_Offset);

			String CBRS_Mode = "cbrs-on";
			String CBRS_User_ID = "Samsung";
			String CBRS_Measure_Unit = "10mhz";

			sb.append(NE_ID).append(",");
			sb.append(NE_Type).append(",");
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()).append(",");
			sb.append(RelVersion).append(",");
			sb.append(Group).append(",");
			sb.append(Ne_Name.toUpperCase()).append(",");
			sb.append(Initial_PCI).append(",");
			sb.append(Initial_RSI).append(",");
			sb.append(Initial_Intra_LTE_NRT).append(",");
			sb.append(Initial_Inter_RAT_1XRTT_NRT).append(",");
			sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
			sb.append(Initial_SRS_Nrt).append(",");
			sb.append(Initial_SRS_Pool_Index).append(",");
			sb.append(Initial_Inter_RAT_NRT_NR).append(",");
			sb.append(Customer_NE_Type).append(",");
			sb.append(Rack_ID).append(",");
			sb.append(Time_Offset).append(",");
			sb.append(CBRS_Mode).append(",");
			sb.append(CBRS_User_ID).append(",");
			sb.append(CBRS_Measure_Unit).append("\n");

			// ##############@ExtLinkInfo##########

			sb.append("@ExtLinkInfo").append("\n");
			sb.append("NE ID,UnitType,UnitID,PortId,VR ID,AdminState\n");

			String Unit_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_Type);
			String Unit_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_ID);
			String Port_Id = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Port_Id);
			String VR_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_VR_ID);
			String AdminState = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_AdminState);

			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Port_Id).append(",");
			sb.append(VR_ID).append(",");
			sb.append(AdminState).append("\n");

			// #########@Clock#############

			sb.append("@Clock").append("\n");
			sb.append(
					"NE ID,ID,Clock Source,Priority Level,Quality Level,IP Version,First Master IP,Second Master IP,ClockProfile,PTPDomain\n");

			String ID = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_ID);
			String Clock_Source = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Clock_Source);
			String Priority_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Priority_Level);
			String Quality_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Quality_Level);

			sb.append(NE_ID).append(",");
			sb.append(ID).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priority_Level).append(",");
			sb.append(Quality_Level).append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append("\n");

			// ##########@InterConnection#############

			sb.append("@InterConnection").append("\n");
			sb.append("NE ID,InterConnectionGroupID,InterConnectionSwitch,InterConnectionNodeID\n");

			String ID1 = resultMapForConstants.get(Constants.ORAN_VGROW_InterConnection_ID);
			String InterConnectionGroupID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionGroupID);
			String InterConnectionSwitch = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionSwitch);
			String InterConnectionNodeID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionNodeID);

			sb.append("").append(",");
			sb.append(InterConnectionGroupID).append(",");
			sb.append(InterConnectionSwitch).append(",");
			sb.append(InterConnectionNodeID).append("\n");

			// ###########@InterEnbInfo###################

			sb.append("@InterEnbInfo").append("\n");
			sb.append("NE ID,InterNodeID,AdminState\n");

			String InterNodeID = "0";
			String AdminState1 = "unlocked";

			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append("\n");

			String User_Def_Mode = "false";
			String Latitude = "N 000:00:00.000";
			String Longitude = "E 000:00:00.000";
			String Height = "0.00m";

			sb.append("@SystemLocation").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");

			sb.append(NE_ID).append(",");
			sb.append(User_Def_Mode).append(",");
			sb.append(Latitude).append(",");
			sb.append(Longitude).append(",");
			sb.append(Height).append("\n");

			// ########@MMEInfo##############

			sb.append("@MMEInfo").append("\n");
			sb.append(
					"NE ID,Index,IPType,IPV4,IPV6,ServicePurpose,AttachWithoutPDNConnectivity,CPOptimization,UPOptimization\n");

			int Index;

			String market = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
					Constants.VZ_GROW_Market);
			String market1 = market.replaceAll("\\s", "");
			String[] mme_ip = objGenerateCsvRepository.getVlsmMmeIpDetails(market);

			String IPType = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPType);
			String IPV4 = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPV4);
			String ServicePurpose = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_ServicePurpose);
			String AttachWithoutPDNConnectivity = resultMapForConstants
					.get(Constants.ORAN_VGROW_MMEInfo_AttachWithoutPDNConnectivity);
			String CPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_CPOptimization);
			String UPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_UPOptimization);

			for (int i = 0; i < mme_ip.length; i++) {
				if (market1.equals("NewEngland")) {
					Index = i;
				} else {
					Index = i + 4;
				}

				sb.append(NE_ID).append(",");
				sb.append(Index).append(",");
				sb.append(IPType).append(",");
				sb.append(IPV4).append(",");
				sb.append(CommonUtil.formatIPV6Address(mme_ip[i])).append(",");
				sb.append(ServicePurpose).append(",");
				sb.append(AttachWithoutPDNConnectivity).append(",");
				sb.append(CPOptimization).append(",");
				sb.append(UPOptimization).append("\n");
			}

			// #######@ExternalInterfaces############

			sb.append("@ExternalInterfaces").append("\n");
			sb.append(
					"NE ID,IF Name,IP Version,IP,PrefixLength,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,Bearer M1,Signal M2,IEEE1588,Smart scheduler\n");

			List<Integer> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());
			List<CIQDetailsModel> vlanIpCiq = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)).collect(Collectors.toList());
			String vlan1;
			String vlan2;

			String oam_vlan, s_bVlan;
			if (vlanIpCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");

				List<Integer> objVlan1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				List<CIQDetailsModel> vlanIpCiq1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.collect(Collectors.toList());

				vlan1 = objVlan1.get(0).toString();
				vlan2 = objVlan1.get(1).toString();

				if (vlan1.startsWith("4")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;
				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;
				}
			} else {
				vlan1 = objVlan.get(0).toString();
				vlan2 = objVlan.get(1).toString();

				if (vlan1.startsWith("4")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;
				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;
				}
			}

			/*
			 * List<Integer>
			 * objVlanStrings=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().
			 * equals(Constants.VZ_GROW_IPPLAN)).map(x->Integer.valueOf(x.getCiqMap().get(
			 * "VLAN").getHeaderValue())).sorted().collect(Collectors.toList()); int
			 * vlan_min ; int vlam_max ;
			 * 
			 * if(objVlanStrings!=null) { vlan_min = objVlanStrings.get(0); vlam_max =
			 * objVlanStrings.get(1); }else { vlan_min = 0; vlam_max = 0; }
			 * 
			 * String
			 * oam_vlan=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().equals(
			 * Constants.VZ_GROW_IPPLAN) &&
			 * x.getCiqMap().get("VLAN").getHeaderValue().equals(String.valueOf(vlan_min))).
			 * map(x->x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue()).collect(Collectors
			 * .joining("")); String
			 * s_bVlan=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().equals(
			 * Constants.VZ_GROW_IPPLAN) &&
			 * x.getCiqMap().get("VLAN").getHeaderValue().equals(String.valueOf(vlam_max))).
			 * map(x->x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue()).collect(Collectors
			 * .joining(""));
			 */

			String eNB_oam = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String eNB_s_b = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			String[] IF_Name = { "ge_0_0_1." + oam_vlan, "ge_0_0_1." + s_bVlan };

			String IP1 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			String[] Management = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Management)
					.split(",");
			String IP_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IP_Version);
			String[] Signal_S1 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Signal_S1)
					.split(",");
			String IEEE1588 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IEEE1588);
			String Smart_scheduler = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Smart_scheduler);

			String[] IP = { CommonUtil.formatIPV6Address(IP1), CommonUtil.formatIPV6Address(IP2) };
			// String[] IP = { IP1, IP2 };

			for (int i = 0; i < IF_Name.length; i++) {

				sb.append(NE_ID).append(",");
				sb.append(IF_Name[i]).append(",");
				sb.append(IP_Version).append(",");
				sb.append(IP[i]).append(",");
				sb.append(eNB_s_b).append(",");
				sb.append(Management[i]).append(",");
				sb.append(Signal_S1[0]).append(",");
				sb.append(Signal_S1[1]).append(",");
				sb.append(Signal_S1[2]).append(",");
				sb.append(Signal_S1[3]).append(",");
				sb.append(Signal_S1[4]).append(",");
				sb.append(Signal_S1[5]).append(",");
				sb.append(Signal_S1[6]).append(",");
				sb.append(Smart_scheduler).append("\n");
			}

			// ##########@StaticRoute##############

			sb.append("@StaticRoute").append("\n");
			sb.append("NE ID,IPType,IPPrefix,IPGW\n");

			String IPType1 = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_IPType);
			String rsIpVlsm = rsIp + "/128";
			String csl_ip = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_csl_ip);

			String[] IPPrefix = { rsIpVlsm, "0:0:0:0:0:0:0:0/0", "0:0:0:0:0:0:0:0/0", csl_ip };

			String OAM_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String SB_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			OAM_GW_IP = CommonUtil.formatIPV6Address(OAM_GW_IP);
			SB_GW_IP = CommonUtil.formatIPV6Address(SB_GW_IP);

			String[] IPGW = { OAM_GW_IP, OAM_GW_IP, SB_GW_IP, OAM_GW_IP };

			for (int i = 0; i < IPPrefix.length; i++) {
				sb.append(NE_ID).append(",");
				sb.append(IPType1).append(",");
				sb.append(IPPrefix[i]).append(",");
				sb.append(IPGW[i]).append("\n");

			}

		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb;
	}

	private String dataByEnode(String dbcollectionFileName, String ciqFileName, String enbId, String key) {
		String res = "";

		try {
			res = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, ciqFileName, enbId, key);

		} catch (Exception e) {
			logger.error("Exception dataByEnode() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return res;
	}

	public StringBuilder getConsCellStringForVlsm(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel) {
		logger.error("fetching data from ciq for GT");
		StringBuilder sb = new StringBuilder();
		String[] maxexipr = null;
		String[] Preferred_Earfcn = null;
		String[] Ru_Port = null;
		String[] cell_index_dsp = null;
		String[] dsp_id = null;
		String[] antennaGain = null;
		String[] cbrsFccId = null;
		String[] additionalBoard = null;

		String sheetName = "";

		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);
		logger.error("getConsCellStringForVlsm-mkt:"+mkt);
		if (mkt.equals(Constants.VZ_GROW_UNY)) {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		} else if (mkt.equals(Constants.VZ_GROW_NE)) {
			sheetName = Constants.VZ_GROW_CIQNewEngland;
		} else {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
			mkt = Constants.VZ_GROW_UNY;
		}
		logger.error("getConsCellStringForVlsm-mkt-final:"+mkt);
		try {
			String[] cellIds = getColumnValuesBySheet(listCIQDetailsModel, sheetName, Constants.VZ_GROW_Cell_ID);

			List<Integer> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue()))
					.distinct().collect(Collectors.toList());

			List<CIQDetailsModel> listCiq = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)).collect(Collectors.toList());

			String oam_vlan, s_bVlan, vlan1, vlan2;

			if (listCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				List<Integer> objVlan1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue()))
						.distinct().collect(Collectors.toList());
				vlan1 = objVlan1.get(0).toString();
				vlan2 = objVlan1.get(1).toString();
				if (vlan1.startsWith("4")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;
				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;
				}
			} else {
				vlan1 = objVlan.get(0).toString();
				vlan2 = objVlan.get(1).toString();
				if (vlan1.startsWith("4")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;
				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;
				}
			}

			String Prefix_vlan = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN,
					enbId, Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix);

			String GW_IP1 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			// GW_IP1 = CommonUtil.formatIPV6Address(GW_IP1);

			String GW_IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			// GW_IP2 = CommonUtil.formatIPV6Address(GW_IP2);

			String ENB_IP1 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			// ENB_IP1 = CommonUtil.formatIPV6Address(ENB_IP1);

			String ENB_IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			// ENB_IP2 = CommonUtil.formatIPV6Address(ENB_IP2);

			String[] Bndwidth = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Bandwidth, sheetName);
			String[] BandName = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_BandName, sheetName);
			String[] Earfcn_DL = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_DL, sheetName);
			String[] Earfcn_UL = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_UL, sheetName);
			String[] tac = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_TAC, sheetName);
			String[] pci = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PCI, sheetName);
			String[] rsi = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RACH, sheetName);
			String[] Tx_Diversity = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Tx_Diversity, sheetName);
			String[] Rx_Diversity = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Rx_Diveristy, sheetName);
			String[] rrh_type = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RRH_Type, sheetName);
			String[] rrh_code = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RRH_Code, sheetName);
			String[] Electrical_Tilt = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Electrical_Tilt, sheetName);
			String[] Pwr = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Output_Power, sheetName);
			String[] lccCard = dataCiqIndex(listCIQDetailsModel, "lCCCard", sheetName);
			String[] CRPIPortID = dataCiqIndex(listCIQDetailsModel, "CRPIPortID", sheetName);

			String[] card_Count = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Card_Count_eNB, sheetName);
			String[] mkt_cli = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Market_CLLI_Code, sheetName); // fileUploadRepository.getEnBDataByPath(dbcollectionFileName,
																													// sheetName,
																													// enbId,
																													// Constants.VZ_GROW_Market_CLLI_Code);
			String[] antenna_path_delay_DL = dataCiqIndex(listCIQDetailsModel,
					Constants.VZ_GROW_antennaPathDelayDL_only, sheetName);
			String[] antenna_path_delay_UL = dataCiqIndex(listCIQDetailsModel,
					Constants.VZ_GROW_antennaPathDelayUL_only, sheetName);
			String[] antenna_path_delay_DLm = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_antennaPathDelayDL,
					sheetName);
			String[] antenna_path_delay_ULm = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_antennaPathDelayUL,
					sheetName);
			String[] DAS = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DAS, sheetName);
			String[] Alias_Name = dataCiqIndex(listCIQDetailsModel, Constants.ORAN_LSM_COMM_SCRIPT_ALIAS_NAME,
					sheetName);// fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
								// Constants.ORAN_LSM_COMM_SCRIPT_ALIAS_NAME);
			String[] Das_Output_Power = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Das_Output_Power,
					sheetName);
			String[] NB_IoT_TAC = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_NB_IoT_TAC, sheetName);
			String[] SDL = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_SDL, sheetName);
			String[] PreambleFormat_prachIndex = dataCiqIndex(listCIQDetailsModel,
					Constants.VZ_GROW_PreambleFormat_prachIndex, sheetName);
			String[] Pa = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Pa, sheetName);
			String[] Pb = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Pb, sheetName);
			String[] PrachCS = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PrachCS, sheetName);
			String[] mct = dataCiqIndex(listCIQDetailsModel, "mcType", sheetName);
			String[] pracformat = dataCiqIndex(listCIQDetailsModel, "prachConfigIndex", sheetName);
			//fetching eMTC
			String[] eMTC = dataCiqIndex(listCIQDetailsModel, "eMTC", sheetName);
			//fetching administrative-state Meenal
			String[] adstate = dataCiqIndex(listCIQDetailsModel, "administrative-state", sheetName);
			String[] nbiot = dataCiqIndex(listCIQDetailsModel, "nbIOT", sheetName);
			// fetching 22A new columns Meenal
			String[] preferredHighestChannel = dataCiqIndex(listCIQDetailsModel, "preferredHighestChannel", sheetName);
			String[] preferredLowestChannel = dataCiqIndex(listCIQDetailsModel, "preferredLowestChannel", sheetName);
			String[] preference = dataCiqIndex(listCIQDetailsModel, "preference", sheetName);
			// fdd mmu
			String[] mmuBisectorMode = dataCiqIndex(listCIQDetailsModel, "mmuBisectorMode", sheetName);
			
			String[] rfPortOpertionMode = dataCiqIndex(listCIQDetailsModel, "RF_Port_operation_mode", sheetName);

			if (mkt.equals(Constants.VZ_GROW_UNY)) {
				maxexipr = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_MAXTHIR, sheetName);
				Preferred_Earfcn = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PREFERRED_EARFCN, sheetName);
				Ru_Port = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RU_PORT, sheetName);
				cell_index_dsp = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_CELL_INDEX, sheetName);
				dsp_id = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_ID, sheetName);
				antennaGain = dataCiqIndex(listCIQDetailsModel, Constants.ANTENNA_GAIN_DBI, sheetName);
				cbrsFccId = dataCiqIndex(listCIQDetailsModel, Constants.CBRS_FCC_ID, sheetName);
				int counta = 0;
				int countb = 0;
				int countc = 0;
				for (int i = 0; i < Ru_Port.length; i++) {
					if (Ru_Port[i].equals("a-1")) {
						counta++;
					}
					if (Ru_Port[i].equals("b-1")) {
						countb++;
					}
					if (Ru_Port[i].equals("g-1")) {
						countc++;
					}
				}
				additionalBoard = new String[cellIds.length];

			} else if (mkt.equals(Constants.VZ_GROW_NE)) {
				maxexipr = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_MAXTHIR, sheetName);
				Preferred_Earfcn = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PREFERRED_EARFCN, sheetName);
				Ru_Port = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RU_PORT, sheetName);
				cell_index_dsp = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_CELL_INDEX, sheetName);
				dsp_id = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_ID, sheetName);
				antennaGain = dataCiqIndex(listCIQDetailsModel, Constants.ANTENNA_GAIN_DBI, sheetName);
				cbrsFccId = dataCiqIndex(listCIQDetailsModel, Constants.CBRS_FCC_ID, sheetName);
				int counta = 0;
				int countb = 0;
				int countc = 0;
				for (int i = 0; i < Ru_Port.length; i++) {
					if (Ru_Port[i].equals("a-1")) {
						counta++;
					}
					if (Ru_Port[i].equals("b-1")) {
						countb++;
					}
					if (Ru_Port[i].equals("g-1")) {
						countc++;
					}
				}
				additionalBoard = new String[cellIds.length];
				additionalBoard[0] = Integer.toString(counta);
				additionalBoard[1] = Integer.toString(countb);
				additionalBoard[2] = Integer.toString(countc);

			}

			ArrayList<String> al = new ArrayList<>();
			for (int i = 0; i < Ru_Port.length; i++) {
				if (Ru_Port[i].isEmpty()) {
					al.add("0");
				} else {
					al.add("1");
				}
			}
			
			ArrayList<String> al2 = new ArrayList<>();
			for (int i = 0; i < Ru_Port.length; i++) {
				if (Ru_Port[i].contains("-")) {
					al2.add(StringUtils.substringBefore(Ru_Port[i], "("));
				} else if (Ru_Port[i].isEmpty()){
					al2.add("0");
				}else {
					al2.add(Ru_Port[i]);
				}
			}
			sb.append(
					"MARKETNAME,Cascade,FDD/TDD,Sector ID,Source_eNB Name,Source_eNB ID,Source_LSM,Source_Cell ID,Source_eNB OAM VLAN,Source_eNB OAM VLAN prefix (/30),Source_OAM GW IP,Source_eNB OAM IP,Source_eNB S&B VLAN,Source_eNB S&B VLAN  prefix (/30),Source_S&B GW IP,Source_eNB S&B IP,Source_BH Port,Source_Cabinet_Type,Target_eNB Name,Target_eNB ID,Target_LSM,Target_Cell ID,Target_eNB OAM VLAN,Target_eNB OAM VLAN prefix (/30),Target_OAM GW IP,Target_eNB OAM IP,Target_eNB S&B VLAN,Target_eNB S&B VLAN prefix (/30),Target_S&B GW IP,Target_eNB S&B IP,Target_BH Port,Target_Cabinet_Type,TAC(Hex),PCI,RACH,Band,DL Center Freq MHz,UL Center Freq MHz,Bandwidth (MHz),EARFCN_DL,EARFCN_UL,Start EARFCN1,Start EARFCN2,MME IP,Tx Diversity,Rx Diveristy,Tx Path Assignment (AB/CD/ABCD/ABCDEFGH),Attenuation/Path,RS Boost,EAIU_Type,EAIU SN,Expansion Cabinet,RRH Type,RRH subtype,No. of ALD,Electrical Tilt,Samsung Output Power (watt),Center Line (Ft),CPRI Port Assignment,Card Count per eNB,Deployment,RRH Code,Market CLLI Code,aliasName,antennaPathDelayDL,antennaPathDelayUL,antennaPathDelayDL (m),antennaPathDelayUL (m),DAS,DAS OUTPUT POWER,NB-IoT TAC,PreambleFormat prachIndex,"
//					+ "pa,pb," //commenting because Arun asked
					+ "prachCS(ZCZC),SDL,Optic Distance(km),CPRI_VALUE,ADDITIONAL_PORT,ADDITIONAL_BOARD_ID,CBRSFCCID,MAX_EIRP_THRESHOLD,ANTENNA_GAIN_DBI,PREFERRED_EARFCN,DSP_CELL_INDEX,DSP_ID,RUPortID,MCT,PRACFORMAT,eMTC,ADMSTATE,NBIOT,preferredHighestChannel,preferredLowestChannel,preference,mmuBisectorMode,rfPortOpertionMode");
			for (int i = 0; i < cellIds.length; i++) {
				sb.append("\n");
				sb.append(mkt).append(",");
				sb.append(enbName).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(enbName).append(",");
				sb.append(enbId).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Target_LSM)).append(",");
				sb.append(cellIds[i]).append(",");
				sb.append(oam_vlan).append(",");
				sb.append(Prefix_vlan).append(",");
				sb.append(GW_IP1).append(",");
				sb.append(ENB_IP1).append(",");
				sb.append(s_bVlan).append(",");
				sb.append(Prefix_vlan).append(",");
				sb.append(GW_IP2).append(",");
				sb.append(ENB_IP2).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(tac[i]).append(",");
				sb.append(pci[i]).append(",");
				sb.append(rsi[i]).append(",");
				if (BandName[i].equals("B2")) {
					sb.append("1900 PCS").append(",");
				} else if (BandName[i].equals("B4")) {
					sb.append("AWS-1").append(",");
				} else if (BandName[i].equals("B5")) {
					sb.append("850MHz").append(",");
				} else if (BandName[i].equals("B13")) {
					sb.append("700 c").append(",");
				} else {
					sb.append(BandName[i]).append(",");
				}
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(Bndwidth[i]).append(",");
				if (StringUtils.isNotBlank(Earfcn_DL[i])) {
					sb.append(Earfcn_DL[i]).append(",");
				} else {
					sb.append(0).append(",");
				}
				if (StringUtils.isNotBlank(Earfcn_UL[i])) {
					sb.append(Earfcn_UL[i]).append(",");
				} else {
					sb.append(0).append(",");
				}
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD)).append(",");
				if (Tx_Diversity[i].contains(">")) {
					String tx = StringUtils.substringAfter(Tx_Diversity[i], ">");
					sb.append(tx).append(",");
				} else if (Tx_Diversity[i].contains("T")) {
					String tx = StringUtils.substringBefore(Tx_Diversity[i], "T");
					sb.append(tx).append(",");
				} else {
					sb.append(Tx_Diversity[i]).append(",");
				}
				if (Rx_Diversity[i].contains(">")) {
					String rx = StringUtils.substringAfter(Rx_Diversity[i], ">");
					sb.append(rx).append(",");
				} else if (Rx_Diversity[i].contains("T")) {
					String rx = StringUtils.substringBefore(Rx_Diversity[i], "T");
					sb.append(rx).append(",");
				} else {
					sb.append(Rx_Diversity[i]).append(",");
				}
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(rrh_type[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD)).append(",");
				sb.append(Electrical_Tilt[i]).append(",");
				if (Pwr[i].contains("->")) {
					String powerAfter = StringUtils.substringAfter(Pwr[i], "->");
					sb.append(powerAfter).append(",");
				} else if (Pwr[i].contains(">")) {
					String powerAfter = StringUtils.substringAfter(Pwr[i], ">");
					sb.append(powerAfter).append(",");
				} else {
					sb.append(Pwr[i]).append(",");
				}
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_NA)).append(",");
				String channel=String.valueOf(Integer.parseInt(lccCard[i])+1);
				String val= CRPIPortID[i]+"("+"LCC-"+channel+")";
				sb.append(val).append(",");
				sb.append(0).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Deploy_Open_Cpri)).append(",");
				sb.append(rrh_code[i]).append(",");
				sb.append(mkt_cli[i]).append(",");
				sb.append(Alias_Name[i]).append(",");
				if (Pattern.matches("[^0-9]*", antenna_path_delay_DL[i])) {
					sb.append("0").append(",");
				} else {
					sb.append(antenna_path_delay_DL[i]).append(",");
				}
				if (Pattern.matches("[^0-9]*", antenna_path_delay_UL[i])) {
					sb.append("0").append(",");
				} else {
					sb.append(antenna_path_delay_UL[i]).append(",");
				}
				/*
				 * sb.append(antenna_path_delay_DL[i]).append(",");
				 * sb.append(antenna_path_delay_UL[i]).append(",");
				 */
				sb.append(antenna_path_delay_DLm[i]).append(",");
				sb.append(antenna_path_delay_ULm[i]).append(",");
				if (StringUtils.isNotBlank(Das_Output_Power[i])) {
					sb.append(Das_Output_Power[i]).append(","); // Swetha:Need to check, In place of das also das output
																// power is there, Actual das value will be like Y/N, if
																// we need to use DAS use DAS[i]
					sb.append(Das_Output_Power[i]).append(",");
				} else {
					sb.append(" ").append(",");
					sb.append(" ").append(",");
				}
				if (StringUtils.isNotBlank(NB_IoT_TAC[i])) {
					sb.append(NB_IoT_TAC[i]).append(",");
				} else {
					sb.append(" ").append(",");
				}
				sb.append(PreambleFormat_prachIndex[i]).append(",");
				//commenting because Arun asked
//				sb.append(Pa[i]).append(",");
//				sb.append(Pb[i]).append(",");
				sb.append(PrachCS[i]).append(",");
				sb.append(SDL[i]).append(",");
				sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD)).append(",");
				// changed based on mkt
				if (mkt.equals(Constants.VZ_GROW_UNY)) {
					sb.append(CRPIPortID[i]).append(",");
					sb.append(al2.get(i)).append(",");
					sb.append("").append(",");
					if (StringUtils.isNotBlank(cbrsFccId[i])) {
						sb.append(cbrsFccId[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(maxexipr[i])) {
						sb.append(maxexipr[i]).append(",");
					} else {
						sb.append(0).append(",");
					}
					if (StringUtils.isNotBlank(antennaGain[i]) && antennaGain[i].contains(".")) {
						String s = antennaGain[i];
						double margin = 10 * (Double.parseDouble(s));
						sb.append((int) margin).append(",");
					} else if (StringUtils.isNotBlank(antennaGain[i])) {
						String f = antennaGain[i];
						int aGain = 10 * (Integer.parseInt(f));
						sb.append(Integer.toString(aGain)).append(",");
					} else {

						sb.append(0).append(",");
					}
					if (StringUtils.isNotBlank(Preferred_Earfcn[i])) {
						sb.append(Preferred_Earfcn[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(cell_index_dsp[i])) {
						sb.append(cell_index_dsp[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(dsp_id[i])) {
						sb.append(dsp_id[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					sb.append(al.get(i)).append(",");
					sb.append(mct[i]).append(",");
					sb.append(pracformat[i]).append(",");
					//eMTC
					if (StringUtils.isNotBlank(eMTC[i])) {
						sb.append(eMTC[i]).append(",");
					} else {
						sb.append("").append(",");
					}
//					administrative state Meenal
					if (StringUtils.isNotBlank(adstate[i])) {
						sb.append(adstate[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(nbiot[i])) {
						sb.append(nbiot[i]).append(",");
					} else {
						sb.append("0").append(",");
					}
					logger.error("Appending of data done");
					// 22A new columns Meenal
					if (StringUtils.isNotBlank(preferredHighestChannel[i])) {
						sb.append(preferredHighestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preferredLowestChannel[i])) {
						sb.append(preferredLowestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preference[i])) {
						sb.append(preference[i]).append(",");
					} else {
						sb.append("-").append(",");
					}
//					 fdd mmu
					if (StringUtils.isNotBlank(mmuBisectorMode[i])) {
						sb.append(mmuBisectorMode[i]).append(",");
					} else {
						sb.append("bisector-mode-off").append(",");
					}
					if (StringUtils.isNotBlank(rfPortOpertionMode[i])) {
						sb.append(rfPortOpertionMode[i]);
					} else {
						sb.append("-");
					}

				} else if (mkt.equals(Constants.VZ_GROW_NE)) {
					sb.append(0).append(",");
					sb.append(al2.get(i)).append(",");
					if (StringUtils.isNotBlank(additionalBoard[i])) {
						sb.append(additionalBoard[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(cbrsFccId[i])) {
						sb.append(cbrsFccId[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(maxexipr[i])) {
						sb.append(maxexipr[i]).append(",");
					} else {
						sb.append(0).append(",");
					}

					if (StringUtils.isNotBlank(antennaGain[i])) {
						String s = antennaGain[i];
						int margin = 10 * (Integer.parseInt(s));
						sb.append(Integer.toString(margin)).append(",");
					} else {

						sb.append(0).append(",");
					}
					if (StringUtils.isNotBlank(Preferred_Earfcn[i])) {
						sb.append(Preferred_Earfcn[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(cell_index_dsp[i])) {
						sb.append(cell_index_dsp[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(dsp_id[i])) {
						sb.append(dsp_id[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					sb.append(al.get(i)).append(",");
					sb.append(mct[i]).append(",");
					//eMTC
					if (StringUtils.isNotBlank(eMTC[i])) {
						sb.append(eMTC[i]).append(",");
					} else {
						sb.append("").append(",");
					}
//					administrative state Meenal
					if (StringUtils.isNotBlank(adstate[i])) {
						sb.append(adstate[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(nbiot[i])) {
						sb.append(nbiot[i]).append(",");
					} else {
						sb.append("0").append(",");
					}
					// 22A new columns Meenal
					if (StringUtils.isNotBlank(preferredHighestChannel[i])) {
						sb.append(preferredHighestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preferredLowestChannel[i])) {
						sb.append(preferredLowestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preference[i])) {
						sb.append(preference[i]).append(",");
					} else {
						sb.append("-").append(",");
					}
					// fdd mmu
					if (StringUtils.isNotBlank(mmuBisectorMode[i])) {
						sb.append(mmuBisectorMode[i]).append(",");
					} else {
						sb.append("bisector-mode-off").append(",");
					}
					if (StringUtils.isNotBlank(rfPortOpertionMode[i])) {
						sb.append(rfPortOpertionMode[i]);
					} else {
						sb.append("-");
					}

				} else {
					sb.append("").append(",");
					sb.append("37").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append(0).append(",");
					sb.append(mct[i]).append(",");
					//eMTC
					if (StringUtils.isNotBlank(eMTC[i])) {
						sb.append(eMTC[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					//administrative state Meenal
					if (StringUtils.isNotBlank(adstate[i])) {
						sb.append(adstate[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(nbiot[i])) {
						sb.append(nbiot[i]).append(",");
					} else {
						sb.append("0").append(",");
					}
					// 22A new columns Meenal
					if (StringUtils.isNotBlank(preferredHighestChannel[i])) {
						sb.append(preferredHighestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preferredLowestChannel[i])) {
						sb.append(preferredLowestChannel[i]).append(",");
					} else {
						sb.append("").append(",");
					}
					if (StringUtils.isNotBlank(preference[i])) {
						sb.append(preference[i]).append(",");
					} else {
						sb.append("-").append(",");
					}
					// fdd mmu
					if (StringUtils.isNotBlank(mmuBisectorMode[i])) {
						sb.append(mmuBisectorMode[i]).append(",");
					} else {
						sb.append("bisector-mode-off").append(",");
					}
					if (StringUtils.isNotBlank(rfPortOpertionMode[i])) {
						sb.append(rfPortOpertionMode[i]);
					} else {
						sb.append("-");
					}
				}
				// sb.append("False").append(",");
				// sb.append(resultMapForConstants.get(Constants.ORAN_VGROW_Cons_Cell_TBD));
			}
		} catch (Exception e) {
			logger.error("Exception getCellStringForVlsm() in GenerateCsvServiceImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return sb;
	}

	public StringBuilder getCellStringForVlsm(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel) {

		StringBuilder sb = new StringBuilder();

		String sheetName = "";

		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);

		if (mkt.equals(Constants.VZ_GROW_UNY)) {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		} else if (mkt.equals(Constants.VZ_GROW_NE)) {
			sheetName = Constants.VZ_GROW_CIQNewEngland;
		}

		try {
			String sheetAliaName = sheetName;
			String siteConfigType = "";
			String rsIp = "";
			String lsmName = "";

			siteConfigType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())) {
				rsIp = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
				lsmName = neMappingEntity.getNetworkConfigEntity().getNeName();
			}

			// ###############@CELL##################
			sb.append("@CELL").append("\n");
			sb.append(
					"State,SectorID,CarrierID,Cell Index in DSP,DSP ID,CC ID,PortID,RUPortID,Rrh Conf,MultiCarrierType,VirtualRFPortMapping,DlMaxTxPower,Pucch center mode,PCI,Diversity,EarfcnDL,EarfcnUL,Bandwidth,CRS,eMTC,Frequency Profile,TAC,EAID,HSF,ZCZC,RSI,Auto GPS,Latitude,Longitude,Height\n");

			int bandCnt = listCIQDetailsModel.stream()
					.filter(x -> sheetAliaName.equalsIgnoreCase(x.getSheetAliasName())).collect(Collectors.toList())
					.size();
			int bandLngth;

			String state;
			int unitLcc;

			int carrierId_Def = Integer.parseInt(resultMapForConstants.get(Constants.ORAN_VGROW_Cell_carrierId_Def));
			String carrier_ID;
			String[] carrier_output = getCarrierId(listCIQDetailsModel, sheetName);
			String CC_ID = "";

			String[] Band1 = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_BandName, sheetName);
			String port_BandLen = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
					Constants.VZ_GROW_CPRI_Port_Assignment);

			String LCC_Check = "LCC";
			String[] Cell_Index_DSP;
			String[] FA_StartEarfcn1_Def;

			if ((port_BandLen.indexOf(LCC_Check) != -1) || (port_BandLen.indexOf("-") != -1)
					|| (port_BandLen.indexOf("(") != -1)) {
				bandLngth = 24;
				unitLcc = 1;
				Cell_Index_DSP = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Cell_Index_DSP_24).split(",");
				FA_StartEarfcn1_Def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_FA_StartEarfcn1_Def_24)
						.split(",");
			} else {
				bandLngth = 12;
				unitLcc = 0;
				Cell_Index_DSP = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Cell_Index_DSP_12).split(",");
				FA_StartEarfcn1_Def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_FA_StartEarfcn1_Def_12)
						.split(",");
			}

			String[] uniq_port = getPortByCascadeId(listCIQDetailsModel, sheetName);

			String diversity;
			int Dl_Max_Tx_Pwr;
			String Dl_Max_Tx_Pwr_def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Dl_Max_Tx_Pwr_def);
			String[] Bndwidth = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Bandwidth, sheetName);
			String[] Earfcn_DL = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_DL, sheetName);
			String[] Earfcn_UL = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_EARFCN_UL, sheetName);
			String[] ParentCellNumber = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Cell_ID, sheetName);
			String[] tac = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_TAC, sheetName);
			String tac_def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_tac_def);
			String[] pci = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PCI, sheetName);
			String pci_def_cell = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_pci_def_cell);
			String rsi_def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_rsi_def);
			String diversity_def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_diversity_def);
			String[] rsi = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RACH, sheetName);
			String[] portId = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_CPRI_Port_Assignment, sheetName);
			String[] portId_def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_portId_def).split(",");

			String rrh_conf_def;
			String rrh_conf;
			String Virtual_RF_Port_Mapping = resultMapForConstants
					.get(Constants.ORAN_VGROW_Cell_Virtual_RF_Port_Mapping);
			String MultiCarrier_Type_Def = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_MultiCarrier_Type_Def);
			String eMTC;
			String CRS = "";
			String EAID = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_EAID);
			String HSF = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_HSF);
			String ZCZC = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_ZCZC);
			String rrh_port_id = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_rrh_port_id);
			String CascadeID = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_CascadeID);
			String Card;
			String[] Pwr = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Output_Power, sheetName);
			String[] Tx_Diversity = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Tx_Diversity, sheetName);
			String[] Rx_Diversity = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Rx_Diveristy, sheetName);
			int sectorID_None = 0;
			String cell_num;
			String UL_Comp = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_UL_Comp);
			String sector_ID = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_sector_ID);
			String curBand = "";
			int bandIndex = Integer.parseInt(resultMapForConstants.get(Constants.ORAN_VGROW_Cell_bandIndex));
			int RU_PortID = Integer.parseInt(resultMapForConstants.get(Constants.ORAN_VGROW_Cell_RU_PortID));
			String Pucch_Center_Mode = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Pucch_Center_Mode);
			String Bandwidth;
			String Frequency_Profile = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Frequency_Profile);
			String Auto_GPS = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Auto_GPS);
			String Latitude = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Latitude);
			String Longitude = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Longitude);
			String Height = resultMapForConstants.get(Constants.ORAN_VGROW_Cell_Height);
			int j1 = 0;
			int j2 = 0;
			String DSP_ID;
			String MultiCarrier_Type = "";
			String[] port_Split;
			String port_rrh;

			for (int i = 0; i < ParentCellNumber.length; i++) {
				int Index = i;
				if ((portId[i].indexOf("LCC-1") != -1) || (portId[i].indexOf("LCC1") != -1)) {
					String[] DSPID1 = { "0", "0", "0", "1", "1", "1", "2", "2", "2", "0", "1", "2" };
					DSP_ID = DSPID1[j1];
					j1++;
				} else {
					String[] DSPID2 = { "0", "0", "0", "1", "1", "1", "2", "2", "2", "0", "1", "2" };
					DSP_ID = DSPID2[j2];
					j2++;
				}
				String Cell_Index_in_DSP = Cell_Index_DSP[i];

				cell_num = ParentCellNumber[i];
				if (cell_num.length() < 2) {
					sector_ID = cell_num;
					carrier_ID = "1";

				} else {
					sector_ID = cell_num.substring(0, 1);
					carrier_ID = cell_num.substring(1);

				}
				if (sectorID_None == 3) {
					sectorID_None = 0;
				}
				carrier_ID = carrier_output[i];

				curBand = Band1[i];
				if (curBand.equals("700")) {
					curBand = "700MHz";
				}

				curBand = Band1[i];
				Bandwidth = curBand + "/" + Bndwidth[i];
				if (Bandwidth.equals("AWS-2/" + Bndwidth[i])) {
					Bandwidth = "AWS-1/" + Bndwidth[i];
				}

				if (curBand.equals("700MHz")) {
					eMTC = "enable";
				} else {
					eMTC = "disable";
				}

				if (Bndwidth[i].equals("10MHz")) {
					MultiCarrier_Type = "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3";
				} else if (Bndwidth[i].equals("20MHz")) {
					MultiCarrier_Type = "cfg358-multi-carrier-20m-20m-20m-10m-5m-config3";
				} else if (Bndwidth[i].equals("15MHz")) {
					MultiCarrier_Type = "cfg388-multi-carrier-f15m-f15m-f15m-10m-5m-config3";
				} else if (Bndwidth[i].equals("5MHz")) {
					MultiCarrier_Type = "cfg355-multi-carrier-10m-10m-10m-10m-5m-5m-5m-5m-config3";
				}
				Dl_Max_Tx_Pwr = Math.round(10 * Float.parseFloat(Pwr[i]));

				if (portId[i].length() > 6) {
					String splitStr = portId[i].substring(1, 2);
					Card = portId[i].substring(6);
					if (splitStr.equals("-")) {
						port_Split = portId[i].split("-");
					}
					port_Split = portId[i].split("(");
					port_rrh = port_Split[0];
				} else {
					Card = "";
					port_rrh = portId[i];
				}

				if (Card.equals("1)")) {
					CC_ID = "0";
				} else if (Card.equals("2)")) {
					CC_ID = "1";
				} else {
					CC_ID = "0";
				}

				rrh_conf = CC_ID + "_" + port_rrh + "_" + "0";
				rrh_conf_def = "0_" + portId_def[i] + "_0";

				if ((Tx_Diversity[i].equals("2")) && (Rx_Diversity[i].equals("2"))) {
					CRS = "2CRS";
				} else if ((Tx_Diversity[i].equals("4")) && (Rx_Diversity[i].equals("4"))) {
					CRS = "4CRS";
				} else if ((Tx_Diversity[i].equals("2")) && (Rx_Diversity[i].equals("4"))) {
					CRS = "2CRS";
				}

				if ((Earfcn_DL[i].equals("67086")) && (Rx_Diversity[i].equals("0"))) {
					Rx_Diversity[i] = "4";
				}

				diversity = Tx_Diversity[i] + "Tx" + Rx_Diversity[i] + "Rx";
				sectorID_None++;

				StringBuilder if_1 = new StringBuilder();

				String State_if, State_else;

				if (siteConfigType.equals(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY)) {
					State_if = "NONE";
					State_else = "NONE";
				} else {
					State_if = "ADD";
					State_else = "NONE";
				}

				if_1.append(State_if).append(",");
				if_1.append(sector_ID).append(",");
				if_1.append(carrier_ID).append(",");
				if_1.append(Cell_Index_DSP[i]).append(",");
				if_1.append(DSP_ID).append(",");
				if_1.append(CC_ID).append(",");
				if_1.append(port_rrh).append(",");
				if_1.append(RU_PortID).append(",");
				if_1.append(rrh_conf).append(",");
				if_1.append(MultiCarrier_Type).append(",");
				if_1.append(Virtual_RF_Port_Mapping).append(",");
				if_1.append(Dl_Max_Tx_Pwr).append(",");
				if_1.append(Pucch_Center_Mode).append(",");
				if_1.append(pci[i]).append(",");
				if_1.append(diversity).append(",");
				if_1.append(Earfcn_DL[i]).append(",");
				if_1.append(Earfcn_UL[i]).append(",");
				if_1.append(Bandwidth).append(",");
				if_1.append(CRS).append(",");
				if_1.append(eMTC).append(",");
				if_1.append(Frequency_Profile).append(",");
				if_1.append(tac[i]).append(",");
				if_1.append(EAID).append(",");
				if_1.append(HSF).append(",");
				if_1.append(ZCZC).append(",");
				if_1.append(rsi[i]).append(",");
				if_1.append(Auto_GPS).append(",");
				if_1.append(Latitude).append(",");
				if_1.append(Longitude).append(",");
				if_1.append(Height).append("\n");

				StringBuilder else_1 = new StringBuilder();

				else_1.append(State_else).append(",");
				else_1.append(sectorID_None).append(",");
				else_1.append(carrierId_Def).append(",");
				else_1.append(Cell_Index_DSP[i]).append(",");
				else_1.append(DSP_ID).append(",");
				else_1.append(CC_ID).append(",");
				else_1.append(port_rrh).append(",");
				else_1.append(RU_PortID).append(",");
				else_1.append(rrh_conf).append(",");
				else_1.append(MultiCarrier_Type).append(",");
				else_1.append(Virtual_RF_Port_Mapping).append(",");
				else_1.append(Dl_Max_Tx_Pwr).append(",");
				else_1.append(Pucch_Center_Mode).append(",");
				else_1.append(pci[i]).append(",");
				else_1.append(diversity).append(",");
				else_1.append(Earfcn_DL[i]).append(",");
				else_1.append(Earfcn_UL[i]).append(",");
				else_1.append(Bandwidth).append(",");
				else_1.append(CRS).append(",");
				else_1.append(eMTC).append(",");
				else_1.append(Frequency_Profile).append(",");
				else_1.append(tac[i]).append(",");
				else_1.append(EAID).append(",");
				else_1.append(HSF).append(",");
				else_1.append(ZCZC).append(",");
				else_1.append(rsi[i]).append(",");
				else_1.append(Auto_GPS).append(",");
				else_1.append(Latitude).append(",");
				else_1.append(Longitude).append(",");
				else_1.append(Height).append(",");

				if (bandLngth == 24) {
					if (bandCnt == 24) {
						sb.append(if_1);
					}
					if ((bandCnt == 23) && (i < 23)) {
						sb.append(if_1);
					} else if (bandCnt == 23) {
						sb.append(else_1);
					}
					if ((bandCnt == 22) && (i < 22)) {
						sb.append(if_1);
					} else if (bandCnt == 22) {
						sb.append(else_1);
					}
					if ((bandCnt == 21) && (i < 21)) {
						sb.append(if_1);
					} else if (bandCnt == 21) {
						sb.append(else_1);
					}
					if ((bandCnt == 20) && (i < 20)) {
						sb.append(if_1);
					} else if (bandCnt == 20) {
						sb.append(else_1);
					}
					if ((bandCnt == 19) && (i < 19)) {
						sb.append(if_1);
					} else if (bandCnt == 19) {
						sb.append(else_1);
					}
					if ((bandCnt == 18) && (i < 18)) {
						sb.append(if_1);
					} else if (bandCnt == 18) {
						sb.append(else_1);
					}
					if ((bandCnt == 17) && (i < 17)) {
						sb.append(if_1);
					} else if (bandCnt == 17) {
						sb.append(else_1);
					}
					if ((bandCnt == 16) && (i < 16)) {
						sb.append(if_1);
					} else if (bandCnt == 16) {
						sb.append(else_1);
					}
					if ((bandCnt == 15) && (i < 15)) {
						sb.append(if_1);
					} else if (bandCnt == 15) {
						sb.append(else_1);
					}
					if ((bandCnt == 14) && (i < 14)) {
						sb.append(if_1);
					} else if (bandCnt == 14) {
						sb.append(else_1);
					}
					if ((bandCnt == 13) && (i < 13)) {
						sb.append(if_1);
					} else if (bandCnt == 13) {
						sb.append(else_1);
					}
					if ((bandCnt == 12) && (i < 12)) {
						sb.append(if_1);
					} else if (bandCnt == 12) {
						sb.append(else_1);
					}
					if ((bandCnt == 11) && (i < 11)) {
						sb.append(if_1);
					} else if (bandCnt == 11) {
						sb.append(else_1);
					}
					if ((bandCnt == 10) && (i < 10)) {
						sb.append(if_1);
					} else if (bandCnt == 10) {
						sb.append(else_1);
					}
					if ((bandCnt == 9) && (i < 9)) {
						sb.append(if_1);
					} else if (bandCnt == 9) {
						sb.append(else_1);
					}
					if ((bandCnt == 8) && (i < 8)) {
						sb.append(if_1);
					} else if (bandCnt == 8) {
						sb.append(else_1);
					}
					if ((bandCnt == 7) && (i < 7)) {
						sb.append(if_1);
					} else if (bandCnt == 7) {
						sb.append(else_1);
					}
					if ((bandCnt == 6) && (i < 6)) {
						sb.append(if_1);
					} else if (bandCnt == 6) {
						sb.append(else_1);
					}
					if ((bandCnt == 5) && (i < 5)) {
						sb.append(if_1);
					} else if (bandCnt == 5) {
						sb.append(else_1);
					}
					if ((bandCnt == 4) && (i < 4)) {
						sb.append(if_1);
					} else if (bandCnt == 4) {
						sb.append(else_1);
					}
					if ((bandCnt == 3) && (i < 3)) {
						sb.append(if_1);
					} else if (bandCnt == 3) {
						sb.append(else_1);
					}
					if ((bandCnt == 2) && (i < 2)) {
						sb.append(if_1);
					} else if (bandCnt == 2) {
						sb.append(else_1);
					}
					if ((bandCnt == 1) && (i < 1)) {
						sb.append(if_1);
					} else if (bandCnt == 1) {
						sb.append(else_1);
					}

				} else {
					if (bandCnt == 12) {
						sb.append(if_1);
					}
					if ((bandCnt == 11) && (i < 11)) {
						sb.append(if_1);
					} else if (bandCnt == 11) {
						sb.append(else_1);
					}
					if ((bandCnt == 10) && (i < 10)) {
						sb.append(if_1);
					} else if (bandCnt == 10) {
						sb.append(else_1);
					}
					if ((bandCnt == 9) && (i < 9)) {
						sb.append(if_1);
					} else if (bandCnt == 9) {
						sb.append(else_1);
					}
					if ((bandCnt == 8) && (i < 8)) {
						sb.append(if_1);
					} else if (bandCnt == 8) {
						sb.append(else_1);
					}
					if ((bandCnt == 7) && (i < 7)) {
						sb.append(if_1);
					} else if (bandCnt == 7) {
						sb.append(else_1);
					}
					if ((bandCnt == 6) && (i < 6)) {
						sb.append(if_1);
					} else if (bandCnt == 6) {
						sb.append(else_1);
					}
					if ((bandCnt == 5) && (i < 5)) {
						sb.append(if_1);
					} else if (bandCnt == 5) {
						sb.append(else_1);
					}
					if ((bandCnt == 4) && (i < 4)) {
						sb.append(if_1);
					} else if (bandCnt == 4) {
						sb.append(else_1);
					}
					if ((bandCnt == 3) && (i < 3)) {
						sb.append(if_1);
					} else if (bandCnt == 3) {
						sb.append(else_1);
					}
					if ((bandCnt == 2) && (i < 2)) {
						sb.append(if_1);
					} else if (bandCnt == 2) {
						sb.append(else_1);
					}
					if ((bandCnt == 1) && (i < 1)) {
						sb.append(if_1);
					} else if (bandCnt == 1) {
						sb.append(else_1);
					}
				}
			}

			// ######################@NBIoTCell#################################

			sb.append("@NBIoTCell").append("\n");
			sb.append(
					"State,ParentCellNumber,NBIoTPCI,OperationModeInfo,NBIoTTAC,Use Parent PCI for Guard-band,InitialNprach,NprachStartTimeCL1,NprachSubcarrierOffsetCL1,NprachStartTimeCL2,NprachSubcarrierOffsetCL2,NprachStartTimeCL3,NprachSubcarrierOffsetCL3,GuardBand,Avoid UL Interfering,DL RB,UL RB\n");

			String State = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_State);
			String ParentCellNum_Def = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_ParentCellNum_Def);
			String OperationModeInfo = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_OperationModeInfo);
			String[] NBIoTTAC = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_NB_IoT_TAC, sheetName);

			String[] NBIoTPCI = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_PCI, sheetName);
			String Use_Parent_PCI_for_Guard_band = resultMapForConstants
					.get(Constants.ORAN_VGROW_NBIoTCell_Use_Parent_PCI_for_Guard_band);
			String Use_Parent_Nb = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_Use_Parent_Nb);
			String InitialNprach = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_InitialNprach);
			String InitialNprach_nb = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_InitialNprach_nb);
			String NprachStartTimeCL1 = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_NprachStartTimeCL1);
			String NprachSubcarrieroffsetCL1 = resultMapForConstants
					.get(Constants.ORAN_VGROW_NBIoTCell_NprachSubcarrieroffsetCL1);
			String NprachStartTimeCL2 = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_NprachStartTimeCL2);
			String NprachSubcarrieroffsetCL2 = resultMapForConstants
					.get(Constants.ORAN_VGROW_NBIoTCell_NprachSubcarrieroffsetCL2);
			String NprachStartTimeCL3 = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_NprachStartTimeCL3);
			String NprachSubcarrieroffsetCL3 = resultMapForConstants
					.get(Constants.ORAN_VGROW_NBIoTCell_NprachSubcarrieroffsetCL3);
			String Avoid_UL_Interfering = resultMapForConstants
					.get(Constants.ORAN_VGROW_NBIoTCell_Avoid_UL_Interfering);
			String DL_RB = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_DL_RB);
			String UL_RB = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_UL_RB);
			String GuardBand = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_GuardBand);
			String State_nb = resultMapForConstants.get(Constants.ORAN_VGROW_NBIoTCell_State_nb);

			int checkVlsmOpt = 0;
			/* This value was taken from arg variable chk it */

			if (Constants.NE_CONFIG_TYPE_NB_IOT_NO.equals(siteConfigType)) {
				State_nb = "NONE";
				State = "NONE";

				Use_Parent_PCI_for_Guard_band = Use_Parent_Nb = "off";
				InitialNprach = InitialNprach_nb = "off";

			}

			for (int i = 0; i < ParentCellNumber.length; i++) {

				int Index = i;
				String ParentCellNum = ParentCellNumber[i];

				if (NBIoTPCI[i].length() > 0) {
					checkVlsmOpt = 2;
				}

				StringBuilder gsb1 = new StringBuilder();

				gsb1.append(NBIoTPCI[i]).append(",");
				gsb1.append(OperationModeInfo).append(",");
				if (NBIoTTAC[i].length() > 0 && (!(Constants.NE_CONFIG_TYPE_NB_IOT_NO.equals(siteConfigType)))) {
					gsb1.append(NBIoTTAC[i]);
				} else {
					gsb1.append("0");
				}
				StringBuilder gsb2 = new StringBuilder();

				gsb2.append(NprachStartTimeCL1).append(",");
				gsb2.append(NprachSubcarrieroffsetCL1).append(",");
				gsb2.append(NprachStartTimeCL2).append(",");
				gsb2.append(NprachSubcarrieroffsetCL2).append(",");
				gsb2.append(NprachStartTimeCL3).append(",");
				gsb2.append(NprachSubcarrieroffsetCL3).append(",");
				gsb2.append(GuardBand).append(",");
				gsb2.append(Avoid_UL_Interfering).append(",");
				gsb2.append(DL_RB).append(",");
				gsb2.append(UL_RB).append("\n");

				StringBuilder if_if = new StringBuilder();
				if_if.append(State_nb).append(",");
				if_if.append(ParentCellNum).append(",");
				if_if.append(gsb1).append(",");
				if_if.append(Use_Parent_Nb).append(",");
				if_if.append(InitialNprach_nb).append(",");
				if_if.append(gsb2);

				StringBuilder if_else = new StringBuilder();
				if_else.append(State_nb).append(",");
				if_else.append(ParentCellNum_Def).append(",");
				if_else.append(gsb1).append(",");
				if_else.append(Use_Parent_Nb).append(",");
				if_else.append(InitialNprach_nb).append(",");
				if_else.append(gsb2);

				StringBuilder else_if = new StringBuilder();
				else_if.append(State).append(",");
				else_if.append(ParentCellNum).append(",");
				else_if.append(gsb1).append(",");
				else_if.append(Use_Parent_PCI_for_Guard_band).append(",");
				else_if.append(InitialNprach).append(",");
				else_if.append(gsb2);

				StringBuilder else_else = new StringBuilder();
				else_else.append(State).append(",");
				else_else.append(ParentCellNum_Def).append(",");
				else_else.append(gsb1).append(",");
				else_else.append(Use_Parent_PCI_for_Guard_band).append(",");
				else_else.append(InitialNprach).append(",");
				else_else.append(gsb2);

				if (bandLngth == 24) {
					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if (bandCnt == 24) {
							sb.append(if_if);
						}
					} else {
						if (bandCnt == 24) {
							sb.append(else_if);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 23) && (i < 23)) {
							sb.append(if_if);
						} else if (bandCnt == 23) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 23) && (i < 23)) {
							sb.append(else_if);
						} else if (bandCnt == 23) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 22) && (i < 22)) {
							sb.append(if_if);
						} else if (bandCnt == 22) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 22) && (i < 22)) {
							sb.append(else_if);
						} else if (bandCnt == 22) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 21) && (i < 21)) {
							sb.append(if_if);
						} else if (bandCnt == 21) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 21) && (i < 21)) {
							sb.append(else_if);
						} else if (bandCnt == 21) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 20) && (i < 20)) {
							sb.append(if_if);
						} else if (bandCnt == 20) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 20) && (i < 20)) {
							sb.append(else_if);
						} else if (bandCnt == 20) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 19) && (i < 19)) {
							sb.append(if_if);
						} else if (bandCnt == 19) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 19) && (i < 19)) {
							sb.append(else_if);
						} else if (bandCnt == 19) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 18) && (i < 18)) {
							sb.append(if_if);
						} else if (bandCnt == 18) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 18) && (i < 18)) {
							sb.append(else_if);
						} else if (bandCnt == 18) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 17) && (i < 17)) {
							sb.append(if_if);
						} else if (bandCnt == 17) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 17) && (i < 17)) {
							sb.append(else_if);
						} else if (bandCnt == 17) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 16) && (i < 16)) {
							sb.append(if_if);
						} else if (bandCnt == 16) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 16) && (i < 16)) {
							sb.append(else_if);
						} else if (bandCnt == 16) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 15) && (i < 15)) {
							sb.append(if_if);
						} else if (bandCnt == 15) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 15) && (i < 15)) {
							sb.append(else_if);
						} else if (bandCnt == 15) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 14) && (i < 14)) {
							sb.append(if_if);
						} else if (bandCnt == 14) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 14) && (i < 14)) {
							sb.append(else_if);
						} else if (bandCnt == 14) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 13) && (i < 13)) {
							sb.append(if_if);
						} else if (bandCnt == 13) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 13) && (i < 13)) {
							sb.append(else_if);
						} else if (bandCnt == 13) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 12) && (i < 12)) {
							sb.append(if_if);
						} else if (bandCnt == 12) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 12) && (i < 12)) {
							sb.append(else_if);
						} else if (bandCnt == 12) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 11) && (i < 11)) {
							sb.append(if_if);
						} else if (bandCnt == 11) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 11) && (i < 11)) {
							sb.append(else_if);
						} else if (bandCnt == 11) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 10) && (i < 10)) {
							sb.append(if_if);
						} else if (bandCnt == 10) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 10) && (i < 10)) {
							sb.append(else_if);
						} else if (bandCnt == 10) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 9) && (i < 9)) {
							sb.append(if_if);
						} else if (bandCnt == 9) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 9) && (i < 9)) {
							sb.append(else_if);
						} else if (bandCnt == 9) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 8) && (i < 8)) {
							sb.append(if_if);
						} else if (bandCnt == 8) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 8) && (i < 8)) {
							sb.append(else_if);
						} else if (bandCnt == 8) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 7) && (i < 7)) {
							sb.append(if_if);
						} else if (bandCnt == 7) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 7) && (i < 7)) {
							sb.append(else_if);
						} else if (bandCnt == 7) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 6) && (i < 6)) {
							sb.append(if_if);
						} else if (bandCnt == 6) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 6) && (i < 6)) {
							sb.append(else_if);
						} else if (bandCnt == 6) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 5) && (i < 5)) {
							sb.append(if_if);
						} else if (bandCnt == 5) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 5) && (i < 5)) {
							sb.append(else_if);
						} else if (bandCnt == 5) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 4) && (i < 4)) {
							sb.append(if_if);
						} else if (bandCnt == 4) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 4) && (i < 4)) {
							sb.append(else_if);
						} else if (bandCnt == 4) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 3) && (i < 3)) {
							sb.append(if_if);
						} else if (bandCnt == 3) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 3) && (i < 3)) {
							sb.append(else_if);
						} else if (bandCnt == 3) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 2) && (i < 2)) {
							sb.append(if_if);
						} else if (bandCnt == 2) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 2) && (i < 2)) {
							sb.append(else_if);
						} else if (bandCnt == 2) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 1) && (i < 1)) {
							sb.append(if_if);
						} else if (bandCnt == 1) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 1) && (i < 1)) {
							sb.append(else_if);
						} else if (bandCnt == 1) {
							sb.append(else_else);
						}
					}
				} else {

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 12) && (i < 12)) {
							sb.append(if_if);
						}
					} else {
						if ((bandCnt == 12) && (i < 12)) {
							sb.append(else_if);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 11) && (i < 11)) {
							sb.append(if_if);
						} else if (bandCnt == 11) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 11) && (i < 11)) {
							sb.append(else_if);
						} else if (bandCnt == 11) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 10) && (i < 10)) {
							sb.append(if_if);
						} else if (bandCnt == 10) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 10) && (i < 10)) {
							sb.append(else_if);
						} else if (bandCnt == 10) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 9) && (i < 9)) {
							sb.append(if_if);
						} else if (bandCnt == 9) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 9) && (i < 9)) {
							sb.append(else_if);
						} else if (bandCnt == 9) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 8) && (i < 8)) {
							sb.append(if_if);
						} else if (bandCnt == 8) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 8) && (i < 8)) {
							sb.append(else_if);
						} else if (bandCnt == 8) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 7) && (i < 7)) {
							sb.append(if_if);
						} else if (bandCnt == 7) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 7) && (i < 7)) {
							sb.append(else_if);
						} else if (bandCnt == 7) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 6) && (i < 6)) {
							sb.append(if_if);
						} else if (bandCnt == 6) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 6) && (i < 6)) {
							sb.append(else_if);
						} else if (bandCnt == 6) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 5) && (i < 5)) {
							sb.append(if_if);
						} else if (bandCnt == 5) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 5) && (i < 5)) {
							sb.append(else_if);
						} else if (bandCnt == 5) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 4) && (i < 4)) {
							sb.append(if_if);
						} else if (bandCnt == 4) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 4) && (i < 4)) {
							sb.append(else_if);
						} else if (bandCnt == 4) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 3) && (i < 3)) {
							sb.append(if_if);
						} else if (bandCnt == 3) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 3) && (i < 3)) {
							sb.append(else_if);
						} else if (bandCnt == 3) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 2) && (i < 2)) {
							sb.append(if_if);
						} else if (bandCnt == 2) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 2) && (i < 2)) {
							sb.append(else_if);
						} else if (bandCnt == 2) {
							sb.append(else_else);
						}
					}

					if ((checkVlsmOpt == 2) && (Band1[i]).equals("700MHz")) {
						if ((bandCnt == 1) && (i < 1)) {
							sb.append(if_if);
						} else if (bandCnt == 1) {
							sb.append(if_else);
						}
					} else {
						if ((bandCnt == 1) && (i < 1)) {
							sb.append(else_if);
						} else if (bandCnt == 1) {
							sb.append(else_else);
						}
					}
				}
			}
			// ##########@Unit#############
			sb.append("@Unit").append("\n");
			sb.append("UnitType,UnitID,BoardType\n");

			String UnitType = resultMapForConstants.get(Constants.ORAN_VGROW_Unit_UnitType);
			String UnitID1 = resultMapForConstants.get(Constants.ORAN_VGROW_Unit_UnitID1);
			String UnitID2 = resultMapForConstants.get(Constants.ORAN_VGROW_Unit_UnitID2);
			String BoardType = resultMapForConstants.get(Constants.ORAN_VGROW_Unit_BoardType);

			StringBuilder s1 = new StringBuilder();
			s1.append(UnitType).append(",");
			s1.append(UnitID1).append(",");
			s1.append(BoardType).append("\n");

			StringBuilder s2 = new StringBuilder();
			s2.append(UnitType).append(",");
			s2.append(UnitID2).append(",");
			s2.append(BoardType).append("\n");

			if (unitLcc == 1) {
				sb.append(s1);
				sb.append(s2);
			} else {
				sb.append(s1);
			}

			// ########@CPRIPort#######
			sb.append("@CPRIPort").append("\n");
			sb.append("UnitType,UnitID,PortID\n");

			String UnitID;
			String Unit = "";

			for (int i = 0; i < uniq_port.length; i++) {
				if (uniq_port[i].length() > 6) {
					String splitStr = uniq_port[i].substring(1, 2);
					Unit = uniq_port[i].substring(6);
					if (splitStr.equals("-")) {
						port_Split = uniq_port[i].split("-");
					}
					port_Split = uniq_port[i].split("(");
					port_rrh = port_Split[0];
				} else {
					port_rrh = uniq_port[i];
				}

				if (Unit.equals("1)")) {
					UnitID = "0";
				} else if (Unit.equals("2)")) {
					UnitID = "1";
				} else {
					UnitID = "0";
				}

				sb.append(UnitType).append(",");
				sb.append(UnitID).append(",");
				sb.append(port_rrh).append("\n");

			}
			// ##########@RRH#################
			sb.append("@RRH").append("\n");
			sb.append(
					"Rrh Conf,Connected DU BoardType,RRH Type,StartEarfcn1,StartEarfcn2,SerialNumber,Azimuth,Beamwidth\n");

			String Connected_DU_BoardType = resultMapForConstants.get(Constants.ORAN_VGROW_RRH_Connected_DU_BoardType);
			String RRH_Sub = "";
			int j = 0;
			String SerialNumber = resultMapForConstants.get(Constants.ORAN_VGROW_RRH_SerialNumber);

			String Azimuth = resultMapForConstants.get(Constants.ORAN_VGROW_RRH_Azimuth);
			String Beamwidth = resultMapForConstants.get(Constants.ORAN_VGROW_RRH_Beamwidth);

			String[] FA_StartEarfcn2_Def = resultMapForConstants.get(Constants.ORAN_VGROW_RRH_FA_StartEarfcn2_Def)
					.split(",");
			String[] RRH_Samsung;
			String[] rrh_code;
			String[] deployment = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_Deployment, sheetName);
			String dep_type;
			// String curBand;
			String current_Band;
			int k = 0;
			String FA_strt1, FA_strt2, splitSamRRH;
			String[] band_uniq = getBandUniq(listCIQDetailsModel, sheetName);
			String splitStr, RRH_Conf;

			for (int i = 0; i < uniq_port.length; i++) {

				current_Band = band_uniq[j];

				if (Band1[i].equals(current_Band)) {
					if (Band1[i].equals("850MHz")) {
						FA_StartEarfcn1_Def[k] = "2400";
					} else {
						FA_StartEarfcn1_Def[k] = FA_StartEarfcn1_Def[k];
					}
					FA_strt1 = FA_StartEarfcn1_Def[k];
					FA_strt2 = FA_StartEarfcn2_Def[k];
				} else {
					j++;
					k++;
					if (Band1[i].equals("850MHz")) {
						FA_StartEarfcn1_Def[k] = "2400";
					} else {
						FA_StartEarfcn1_Def[k] = FA_StartEarfcn1_Def[k];
					}
					FA_strt1 = FA_StartEarfcn1_Def[k];
					FA_strt2 = FA_StartEarfcn2_Def[k];
				}

				if (uniq_port[i].length() > 6) {
					splitStr = uniq_port[i].substring(1, 2);
					Card = uniq_port[i].substring(1, 2);
					if (Card.equals("1)")) {
						CC_ID = "0";
					} else if (Card.equals("2)")) {
						CC_ID = "1";
					}

					if (splitStr.equals("-")) {
						port_Split = uniq_port[i].split("-");
					} else {
						port_Split = uniq_port[i].split("(");
					}
					port_rrh = port_Split[0];
					CC_ID = "0";

				} else {

					port_rrh = uniq_port[i];
				}
				RRH_Conf = CC_ID + "_" + port_rrh + "_" + "0";
				rrh_code = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_RRH_Code, sheetName);
				String SamRrhType = rrh_code[i];
				if (SamRrhType.indexOf(">") != -1) {
					RRH_Samsung = SamRrhType.split(">");
					splitSamRRH = RRH_Samsung[1];
				} else {
					splitSamRRH = SamRrhType;
				}

				if (deployment[0].indexOf("RAN") != -1) {
					dep_type = "OPEN RAN";
				} else {
					dep_type = "OPEN CPRI";
				}

				if ((curBand.equals("AWS-1")) || (curBand.equals("AWS-2")) || (curBand.equals("AWS-3"))) {
					curBand = "AWS";
				}
				if ((curBand.equals("PCS-2"))) {
					curBand = "PCS";
				}

				if ((splitSamRRH.equals("Samsung RRH")) || (splitSamRRH.equals("SAMSUNG RRH"))) {
					if ((curBand.equals("700MHz")) || (curBand.equals("850 LTE")) || (curBand.equals("850LTE"))) {
						RRH_Sub = "RFV01U_D20";
					} else if ((curBand.equals("AWS")) || (curBand.equals("PCS"))) {
						RRH_Sub = "RFV01U_D10";
					}
				} else {

					// RRH_Sub = get_rrh_type_bycode(portId[i]);
					String oldModel = "";
					List<CIQDetailsModel> detailsModels1 = fileUploadRepository.getEnBData(dbcollectionFileName,
							sheetName, enbId);
					if (CommonUtil.isValidObject(detailsModels1) && detailsModels1.size() > 0) {
						for (CIQDetailsModel details : detailsModels1) {
							if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
									&& CommonUtil.isValidObject(
											details.getCiqMap().containsKey(Constants.VZ_GROW_CPRI_Port_Assignment))) {
								String cpriPort = details.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment)
										.getHeaderValue();

								if (CommonUtil.isValidObject(cpriPort) && cpriPort.equals(portId[i])) {
									if (CommonUtil.isValidObject(details)
											&& CommonUtil.isValidObject(details.getCiqMap())
											&& CommonUtil.isValidObject(
													details.getCiqMap().containsKey(Constants.VZ_GROW_RRH_Code))) {
										oldModel = details.getCiqMap().get(Constants.VZ_GROW_RRH_Code).getHeaderValue();
										break;
									}
								}
							}
						}
					}

					String[] om = objGenerateCsvRepository.getRrhAluDetails(oldModel);

					if (om != null && om.length > 0) {
						RRH_Sub = om[0];
					} else {
						RRH_Sub = "RRH_SUB$$$$";
					}

				}

				RRH_Sub = RRH_Sub.replaceAll("_", "-");
				RRH_Sub = RRH_Sub.toLowerCase();
				if (RRH_Sub.equals("unsupported-473966a.101")) {
					RRH_Sub = "asl-ahca-01";
				}
				if (!(siteConfigType.equals(Constants.NE_CONFIG_TYPE_NB_IOT_ONLY))) {
					sb.append(RRH_Conf).append(",");
					sb.append(Connected_DU_BoardType).append(",");
					sb.append(RRH_Sub).append(",");
					sb.append(FA_StartEarfcn1_Def[k]).append(",");
					sb.append(FA_StartEarfcn2_Def[k]).append(",");
					sb.append(SerialNumber).append(",");
					sb.append(Azimuth).append(",");
					sb.append(Beamwidth).append("\n");
				}
			}

			// ##########@RRHAntennaPort########
			sb.append("@RRHAntennaPort").append("\n");
			sb.append("Rrh Conf,Connected DU BoardType,AntennaPortID,AntennaCableLength\n");

			String AntennaPortID = resultMapForConstants.get(Constants.ORAN_VGROW_RRHAntennaPort_AntennaPortID);

			for (int i = 0; i < uniq_port.length; i++) {

				String Antenna_Cable_Length = "";

				List<CIQDetailsModel> detailsModels = fileUploadRepository.getEnBData(dbcollectionFileName, sheetName,
						enbId);
				if (CommonUtil.isValidObject(detailsModels) && detailsModels.size() > 0) {
					for (CIQDetailsModel details : detailsModels) {
						if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
								&& CommonUtil.isValidObject(
										details.getCiqMap().containsKey(Constants.VZ_GROW_CPRI_Port_Assignment))) {
							String cpriPort = details.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment)
									.getHeaderValue();
							if (CommonUtil.isValidObject(cpriPort) && cpriPort.equals(portId[i])) {
								if (CommonUtil.isValidObject(details) && CommonUtil.isValidObject(details.getCiqMap())
										&& CommonUtil.isValidObject(details.getCiqMap()
												.containsKey(Constants.VZ_GROW_antennaPathDelayDL))) {
									Antenna_Cable_Length = details.getCiqMap().get(Constants.VZ_GROW_antennaPathDelayDL)
											.getHeaderValue();
									break;
								}
							}
						}
					}

				}

				if (uniq_port[i].length() > 6) {
					splitStr = uniq_port[i].substring(1, 2);
					Card = uniq_port[i].substring(6);

					if (Card.equals("1)")) {
						CC_ID = "0";
					} else if (Card.equals("2)")) {
						CC_ID = "1";
					}

					if (splitStr.equals("-")) {
						port_Split = uniq_port[i].split("-");
					} else {
						port_Split = uniq_port[i].split("(");
					}
					port_rrh = port_Split[0];

				} else {
					port_rrh = uniq_port[i];
					CC_ID = "0";
				}
				RRH_Conf = CC_ID + "_" + port_rrh + "_" + "0";

				sb.append(RRH_Conf).append(",");
				sb.append(Connected_DU_BoardType).append(",");
				sb.append(AntennaPortID).append(",");
				sb.append(Antenna_Cable_Length).append("\n");
			}

			// ##############@DSP#####################
			sb.append("@DSP").append("\n");
			sb.append("UnitID,DSP ID,OPTIC_DISTANCE\n");

			String OPTIC_DISTANCE = resultMapForConstants.get(Constants.ORAN_VGROW_DSP_OPTIC_DISTANCE);
			String[] unit_ID_lcc1 = resultMapForConstants.get(Constants.ORAN_VGROW_DSP_unit_ID_lcc1).split(",");
			String[] unit_ID_lcc2 = resultMapForConstants.get(Constants.ORAN_VGROW_DSP_unit_ID_lcc2).split(",");
			String[] DSP_ID_lcc1 = resultMapForConstants.get(Constants.ORAN_VGROW_DSP_DSP_ID_lcc1).split(",");
			String[] DSP_ID_lcc2 = resultMapForConstants.get(Constants.ORAN_VGROW_DSP_DSP_ID_lcc2).split(",");

			if (unitLcc == 1) {
				for (int i = 0; i < 6; i++) {
					sb.append(unit_ID_lcc2[i]).append(",");
					sb.append(DSP_ID_lcc2[i]).append(",");
					sb.append(OPTIC_DISTANCE).append("\n");

				}
			} else {
				for (int i = 0; i < 3; i++) {
					sb.append(unit_ID_lcc1[i]).append(",");
					sb.append(DSP_ID_lcc1[i]).append(",");
					sb.append(OPTIC_DISTANCE).append("\n");
				}
			}

		} catch (Exception e) {
			logger.error("Exception getCellStringForVlsm() in GenerateCsvServiceImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return sb;
	}

	private String[] getBandUniq(List<CIQDetailsModel> listCIQDetailsModel, String sheetName) {
		String[] result = null;

		try {
			LinkedHashSet<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> X.getSheetAliasName().equals(sheetName))
					.map(X -> X.getCiqMap().get(Constants.VZ_GROW_BandName).getHeaderValue())
					.collect(Collectors.toCollection(LinkedHashSet::new));
			if (objCellIdList != null && objCellIdList.size() > 0) {
				result = new String[objCellIdList.size()];

				result = objCellIdList.toArray(result);
			}
		} catch (Exception e) {
			logger.error(
					"Exception getPortByCascadeId() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return result;

	}

	private String[] dataCiqIndex_lsm(List<CIQDetailsModel> listCIQDetailsModel, String key, String sheetName,
			int bandLength) {

		String[] result = null;

		if (bandLength == 12) {
			result = new String[12];

			try {
				List<String> objCellIdList = listCIQDetailsModel.stream()
						.filter(X -> X.getSheetAliasName().equals(sheetName))
						.map(X -> X.getCiqMap().get(key).getHeaderValue()).collect(Collectors.toList());
				if (objCellIdList != null && objCellIdList.size() > 0) {
					int s = 12 - objCellIdList.size();
					for (int i = 0; i < s; i++) {
						objCellIdList.add("1234");
					}
					result = objCellIdList.toArray(result);
				}
			} catch (Exception e) {
				logger.error(
						"Exception dataCiqIndex() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			}
		} else {

			try {
				result = new String[24];
				List<String> a1 = listCIQDetailsModel.stream().filter(x -> sheetName.equals(x.getSheetAliasName())
						&& (x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue())
								.contains("LCC-1")
						|| x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue().contains("LCC1"))
						.map(x -> x.getCiqMap().get(key).getHeaderValue()).collect(Collectors.toList());

				List<String> a2 = listCIQDetailsModel.stream().filter(x -> sheetName.equals(x.getSheetAliasName())
						&& (x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue())
								.contains("LCC-2")
						|| x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue().contains("LCC2"))
						.map(x -> x.getCiqMap().get(key).getHeaderValue()).collect(Collectors.toList());

				if (a1 != null && a1.size() > 0) {
					int s = 12 - a1.size();
					for (int i = 0; i < s; i++) {
						a1.add("1234");
					}
				}

				if (a2 != null && a2.size() > 0) {
					int s = 12 - a2.size();
					for (int i = 0; i < s; i++) {
						a2.add("1234");
					}
				}

				// Adding 2 lists
				a1.addAll(a2);
				result = a1.toArray(result);
			} catch (Exception e) {
				logger.error(
						"Exception dataCiqIndex() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			}
		}
		return result;
	}

	private String[] getCarrierId_lsm(List<CIQDetailsModel> listCIQDetailsModel, String sheetName, int bandLength) {

		String[] cell_num = null;
		String[] splitValue = null;
		String[] carrierValues = null;

		if (bandLength == 12) {
			cell_num = new String[12];
			carrierValues = new String[12];

			try {
				List<String> objCellIdList = listCIQDetailsModel.stream()
						.filter(X -> X.getSheetAliasName().equals(sheetName))
						.map(X -> X.getCiqMap().get(Constants.VZ_GROW_Cell_ID).getHeaderValue())
						.collect(Collectors.toList());

				if (objCellIdList != null && objCellIdList.size() > 0) {
					int s = 12 - objCellIdList.size();
					for (int i = 0; i < s; i++) {
						objCellIdList.add("1234");
					}

					cell_num = objCellIdList.toArray(cell_num);

					for (int i = 0; i < 12; i++) {
						if (cell_num[i].length() > 1) {
							splitValue = cell_num[i].split("");
							carrierValues[i] = splitValue[1];
						} else {
							carrierValues[i] = "1";
						}
					}
				}

			} catch (Exception e) {
				logger.error(
						"Exception getCarrierId() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			}
		} else {
			cell_num = new String[24];
			carrierValues = new String[24];

			try {
				List<String> a1 = listCIQDetailsModel.stream().filter(x -> sheetName.equals(x.getSheetAliasName())
						&& (x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue())
								.contains("LCC-1")
						|| x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue().contains("LCC1"))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Cell_ID).getHeaderValue())
						.collect(Collectors.toList());

				List<String> a2 = listCIQDetailsModel.stream().filter(x -> sheetName.equals(x.getSheetAliasName())
						&& (x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue())
								.contains("LCC-2")
						|| x.getCiqMap().get(Constants.VZ_GROW_CPRI_Port_Assignment).getHeaderValue().contains("LCC2"))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Cell_ID).getHeaderValue())
						.collect(Collectors.toList());

				if (a1 != null && a1.size() > 0) {
					int s = 12 - a1.size();
					for (int i = 0; i < s; i++) {
						a1.add("1234");
					}
				}

				if (a2 != null && a2.size() > 0) {
					int s = 12 - a2.size();
					for (int i = 0; i < s; i++) {
						a2.add("1234");
					}
				}

				// Adding 2 lists
				a1.addAll(a2);

				cell_num = a1.toArray(cell_num);

				for (int i = 0; i < 24; i++) {
					if (cell_num[i].length() > 1) {
						splitValue = cell_num[i].split("");
						carrierValues[i] = splitValue[1];
					} else {
						carrierValues[i] = "1";
					}
				}

			} catch (Exception e) {
				logger.error(
						"Exception getCarrierId() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
			}

		}
		return carrierValues;
	}

	@Override
	public GenerateInfoAuditEntity getGenerateInfoAuditById(Integer id) {
		GenerateInfoAuditEntity generateInfoAuditEntity = null;
		try {
			generateInfoAuditEntity = objGenerateCsvRepository.getGenerateInfoAuditById(id);
		} catch (Exception e) {
			logger.error("Exception Exception GenerateCsvServiceImpl.getGenerateInfoAuditById() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return generateInfoAuditEntity;
	}

	@Override
	public JSONObject saveCsvAudit(GenerateInfoAuditEntity objInfo) {
		boolean status = false; 
		JSONObject result = new JSONObject();
		try {
			result = objGenerateCsvRepository.saveCsvAudit(objInfo);
		} catch (Exception e) {
			logger.error("Exception Exception GenerateCsvServiceImpl.saveCsvAudit() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}

	@Override
	public JSONObject deleteUploadScriptFileDetails(String neId, String filePath, String sessionId) {
		String useCaseName = null;
		try {
			StringBuilder scriptFilePath = new StringBuilder();
			scriptFilePath.append(Constants.SEPARATOR).append(filePath).append(Constants.SEPARATOR);
			useCaseName = Constants.COMMISION_USECASE + "_" + neId;
			UseCaseBuilderEntity useCaseBuilder = useCaseBuilderService.getUseCaseBuilderEntity(useCaseName);
			if (CommonUtil.isValidObject(useCaseBuilder)) {
				if (useCaseBuilder.getUseCount() > 0) {
					return CommonUtil.buildResponseJson(Constants.FAIL,
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GENERATE_FILE)
									+ " as NE is in progress",
							sessionId, "");
				} else {
					useCaseBuilderService.deleteUseCaseBuilder(useCaseBuilder.getId());
				}
			}
			List<UploadFileEntity> uploadFileEntityList = uploadFileService
					.getUploadFileEntity(scriptFilePath.toString());
			for (UploadFileEntity uploadFileEnt : uploadFileEntityList) {
				UploadFileEntity deleteUploadFileEntity = uploadFileService.getUploadScriptByPath(scriptFilePath,
						uploadFileEnt.getFileName());
				if (CommonUtil.isValidObject(deleteUploadFileEntity) && CommonUtil.isValidObject(useCaseBuilder)) {
					UseCaseBuilderParamEntity useCaseBuilderParamEntity = useCaseBuilderService
							.getUseCaseBuilderParam(useCaseBuilder.getId(), deleteUploadFileEntity.getId());
					if (CommonUtil.isValidObject(useCaseBuilderParamEntity)
							&& CommonUtil.isValidObject(useCaseBuilderParamEntity.getId())) {
						useCaseBuilderRepository.deleteUseCaseParamBuilder(useCaseBuilderParamEntity.getId());
					}
				}
				uploadFileService.deleteUploadScript(deleteUploadFileEntity.getId());
			}
		} catch (Exception e) {
			logger.error("Exception Exception GenerateCsvServiceImpl.deleteUploadScriptFileDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return CommonUtil.buildResponseJson(Constants.SUCCESS, "Success", sessionId, "");
	}

	// fsu template adding method
	private StringBuilder getENBStringForFsuV9(String ciqFileName, String neVersion, String enbId, String enbName,
			String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel) {

		StringBuilder sb = new StringBuilder();
		String sheetName = "";
		try {
			String NE_ID = enbId;

			/////////////////////////// @FSU/////////////////////////////////
			if (neVersion.equals("19.A.0")) {
				sb.append("@FSU").append("\n");
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,AdministrativeState,NE Serial Number\n");
				NE_ID = enbId;
				String NE_TYPE = "c_fsu";
				String NE_Version = neVersion;
				String Release_Version = "r_0102";
				String Network = listCIQDetailsModel.get(0).getCiqMap().get("Network").getHeaderValue();
				String NE_Name = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
				NE_Name = NE_Name.trim().replaceAll(" ", "_");
				String AdministrativeState = "locked";
				String NE_Serial_Number = "";
				sb.append(NE_ID).append(",");
				sb.append(NE_TYPE).append(",");
				sb.append(NE_Version).append(",");
				sb.append(Release_Version).append(",");
				sb.append(Network).append(",");
				sb.append("GROW_").append(NE_Name).append(",");
				sb.append(AdministrativeState).append(",");
				sb.append(NE_Serial_Number).append("\n");
			} else if (neVersion.equals("20.A.0")) {
				sb.append("@FSU").append("\n");
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,AdministrativeState,Local Time Off,NE Serial Number\n");
				NE_ID = enbId;
				String NE_TYPE = "c_fsu";
				String NE_Version = neVersion;
				String Release_Version = "r_0100";
				String Network = listCIQDetailsModel.get(0).getCiqMap().get("Network").getHeaderValue();
				String NE_Name = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
				NE_Name = NE_Name.trim().replaceAll(" ", "_");
				String AdministrativeState = "unlocked";
				String Local_Time_Off = "-63";
				String NE_Serial_Number = "";
				sb.append(NE_ID).append(",");
				sb.append(NE_TYPE).append(",");
				sb.append(NE_Version).append(",");
				sb.append(Release_Version).append(",");
				sb.append(Network).append(",");
				sb.append("GROW_").append(NE_Name).append(",");
				sb.append(AdministrativeState).append(",");
				sb.append(Local_Time_Off).append(",");
				sb.append(NE_Serial_Number).append("\n");
				sb.append("@SERVER_INFORMATION").append("\n");
				sb.append("NE ID,CFM,PSM\n");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}else if (neVersion.equals("20.C.0")) {
				sb.append("@FSU").append("\n");
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,AdministrativeState,Local Time Offset,NE Serial Number,FW Auto Fusing\n");
				String NE_TYPE = "c_fsu";
				String NE_Version = neVersion;
				String Release_Version = "r_0100";
				String Network = listCIQDetailsModel.get(0).getCiqMap().get("Network").getHeaderValue();
				String NE_Name = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
				NE_Name = NE_Name.trim().replaceAll(" ", "_");
				String AdministrativeState = "unlocked";
				String Local_Time_Off = "0";
				String NE_Serial_Number = "";
				sb.append(NE_ID).append(",");
				sb.append(NE_TYPE).append(",");
				sb.append(NE_Version).append(",");
				sb.append(Release_Version).append(",");
				sb.append(Network).append(",");
				sb.append("GROW_").append(NE_Name).append(",");
				sb.append("").append(",");
				sb.append(AdministrativeState).append(",");
				sb.append("0").append(",");
				sb.append(NE_Serial_Number).append(",");
				sb.append("off").append("\n");
				sb.append("@SERVER_INFORMATION").append("\n");
				sb.append("NE ID,CFM,PSM\n");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}

			if(neVersion.equals("20.C.0")){
			/////////////////////////////// @CHANNEL_BOARD_INFORMATION//////////////////////
			sb.append("@CHANNEL_BOARD_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
			String Unit_Type = "fsip";
			String Unit_ID = "0";
			String Board_Type = "fea2-a1a";
			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Board_Type).append("\n");
			/////////////////////// @CLOCK_INFORMATION////////////////////////////
			NE_ID = enbId;
			sb.append("@CLOCK_INFORMATION").append("\n");
			sb.append("NE ID,Clock Source ID,Clock Source,Priority Level,Quality Level\n");
			String Clock_Source_ID = "0";
			String Clock_Source = "gps-type";
			String Priorit_Level = "1";
			String Quality_Level = "dnu";
			sb.append(NE_ID).append(",");
			sb.append(Clock_Source_ID).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priorit_Level).append(",");
			sb.append(Quality_Level).append("\n");
			}
			
			
			else{

			/////////////////////// @CLOCK_INFORMATION////////////////////////////
			NE_ID = enbId;
			sb.append("@CLOCK_INFORMATION").append("\n");
			sb.append("NE ID,Clock Source ID,Clock Source,Priority Level,Quality Level\n");
			String Clock_Source_ID = "0";
			String Clock_Source = "gps-type";
			String Priorit_Level = "1";
			String Quality_Level = "dnu";
			sb.append(NE_ID).append(",");
			sb.append(Clock_Source_ID).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priorit_Level).append(",");
			sb.append(Quality_Level).append("\n");
			
			
			
			/////////////////////////////// @CHANNEL_BOARD_INFORMATION//////////////////////
			sb.append("@CHANNEL_BOARD_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
			String Unit_Type = "fsip";
			String Unit_ID = "0";
			String Board_Type = "fea2-a1a";
			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Board_Type).append("\n");
			}
			
			/////////////////////// @PTP_INFORMATION////////////////////////////
			if(neVersion.equals("20.C.0"))
			{
		sb.append("@PTP_INFORMATION").append("\n");
		sb.append("NE ID,IP Version,First Master IP,Second Master IP,Clock Profile,PTP Domain\n");
		sb.append("").append(",");
		sb.append("ipv4").append(",");
		sb.append("0.0.0.0").append(",");
		sb.append("0.0.0.0").append(",");
		sb.append("telecom-2008").append(",");
		sb.append("0").append("\n");
			}
			
			////////////////////// @PORT_INFORMATION///////////////////////////
			sb.append("@PORT_INFORMATION").append("\n");
			if(neVersion.equals("20.C.0"))
			sb.append("NE ID,Port ID,VR ID,Port Administrative State,Connect Type,UDE Type,MTU\n");
			else
				sb.append("NE ID,Port ID,Port Administrative State,Connect Type,UDE Type,MTU\n");	
			String Port_ID = "0";
			String Port_Administrative_State = "unlocked";
			String Connect_Type = "backhaul";
			String UDE_Type = "ude-none";
			String MTU = "1500";
			sb.append(NE_ID).append(",");
			sb.append(Port_ID).append(",");
			if(neVersion.equals("20.C.0"))
				sb.append("0").append(",");
			sb.append(Port_Administrative_State).append(",");
			sb.append(Connect_Type).append(",");
			sb.append(UDE_Type).append(",");
			sb.append(MTU).append("\n");
			
/////////////////////// @VIRTUAL_ROUTING_INFORMATION////////////////////////////
	if(neVersion.equals("20.C.0"))
	{
sb.append("@VIRTUAL_ROUTING_INFORMATION").append("\n");
sb.append("NE ID,VR ID\n");
sb.append("").append(",");
sb.append("").append("\n");
	}
	
			///////////////////// @IP_INFORMATION////////////////////////////////////////////
			sb.append("@IP_INFORMATION").append("\n");
			if(neVersion.equals("20.C.0"))
			sb.append("NE ID,CPU ID,External Interface Name,IP Address,IP Prefix Length,IP Get Type,Management,IEEE1588\n");
			else
				sb.append("NE ID,Cpu ID,External Interface Name,IP Address,IP Prefix Length,IP Get Type,Management\n");
			String Cpu_ID = listCIQDetailsModel.get(0).getCiqMap().get("CPU_ID").getHeaderValue();
			String ext = listCIQDetailsModel.get(0).getCiqMap().get("VLAN_ID").getHeaderValue();
			String IP_Address = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue();
			String IP_Prefix_Length = "64";
			String IP_Get_Type = "static";
			String Management = "true";

			String External_Interface_Name="";
			if(neVersion.equals("20.C.0")) {
				External_Interface_Name = "ge_0_0_0."+ext;
			IP_Prefix_Length =listCIQDetailsModel.get(0).getCiqMap().get("IP_Prefix_Length").getHeaderValue();
			IP_Get_Type =listCIQDetailsModel.get(0).getCiqMap().get("IP_Get_Type").getHeaderValue();
			Management =listCIQDetailsModel.get(0).getCiqMap().get("Management").getHeaderValue();
			if(Management.equals("TRUE"))
				Management="true";
			else if(Management.equals("FALSE"))
				Management="false";
			}
				
			else 
				External_Interface_Name = "ge_0_0_0.410";
			// String IP_Address = "2001:4888:2a18:303c:101:406:0:0";
			
			sb.append(NE_ID).append(",");
			if(neVersion.equals("20.A.0"))
				sb.append("0").append(",");
			else
			sb.append(Cpu_ID).append(",");
			sb.append(External_Interface_Name).append(",");
			sb.append(IP_Address).append(",");
			sb.append(IP_Prefix_Length).append(",");
			sb.append(IP_Get_Type).append(",");
			if(neVersion.equals("20.C.0")) {
				sb.append(Management).append(",");
				sb.append("false").append("\n");
			}else
			sb.append(Management).append("\n");
			///////////////////// @VLAN_INFORMATION/////////////////////////////////
			sb.append("@VLAN_INFORMATION").append("\n");
			if(neVersion.equals("20.C.0")) 
				sb.append("NE ID,CPU ID,VLAN Interface Name,VLAN ID,VR ID\n");
			else
			sb.append("NE ID,Cpu ID,VLAN Interface Name,VLAN ID\n");
			String VLAN_Interface_Name = listCIQDetailsModel.get(0).getCiqMap().get("VLAN_Interface_Name").getHeaderValue();
			String VLAN_ID = listCIQDetailsModel.get(0).getCiqMap().get("VLAN_ID").getHeaderValue();
			sb.append(NE_ID).append(",");
			sb.append(Cpu_ID).append(",");
			if(neVersion.equals("20.C.0")) {
				sb.append(VLAN_Interface_Name).append(",");
				sb.append(VLAN_ID).append(",");
				sb.append("0").append("\n");
			}else {
				sb.append("ge_0_0_0").append(",");
				sb.append(VLAN_ID).append("\n");
			}
			///////////////////////// @ROUTE_INFORMATION///////////////////////////////
			sb.append("@ROUTE_INFORMATION").append("\n");
			if(neVersion.equals("20.C.0")) 
			sb.append("NE ID,CPU ID,VR ID,IP Prefix,IP Prefix Length,IP Gateway\n");
			else
				sb.append("NE ID,Cpu ID,IP Prefix,IP Prefix Length,IP Gateway\n");
			String IP_Prefix = "::";
			 String IP_Gateway = "";
			
				IP_Gateway = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Gateway_IP_Address").getHeaderValue();
			
			String ipprefixlength = "0";
			sb.append(NE_ID).append(",");
			sb.append(Cpu_ID).append(",");
			if(neVersion.equals("20.C.0"))
				sb.append("0").append(",");
			sb.append(IP_Prefix).append(",");
			sb.append(ipprefixlength).append(",");
			sb.append(IP_Gateway).append("\n");
			//////////////////////////////////////// @SYSTEM_LOCATION_INFORMATION/////////////////////////////
			sb.append("@SYSTEM_LOCATION_INFORMATION").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");
			String User_Defined_Mode = listCIQDetailsModel.get(0).getCiqMap().get("User_Defined_Mode").getHeaderValue();;
			String Latitude = listCIQDetailsModel.get(0).getCiqMap().get("Latitude").getHeaderValue();
			String Longitude = listCIQDetailsModel.get(0).getCiqMap().get("Longitude").getHeaderValue();
			String Height = null;
			if (neVersion.equals("19.A.0")) {
				Height = "000.00m";
			} else {
				Height = "0000.00m";
			}
			sb.append(NE_ID).append(",");
			if(neVersion.equals("20.C.0")) {
				if(User_Defined_Mode.equals("TRUE")) 
					User_Defined_Mode="true";
				else if(User_Defined_Mode.equals("FALSE"))
					User_Defined_Mode="false";
				
			sb.append(User_Defined_Mode).append(",");
			sb.append(Latitude).append(",");
			sb.append(Longitude).append(",");
			sb.append(listCIQDetailsModel.get(0).getCiqMap().get("Height").getHeaderValue()).append("\n");

			}
			else {
				sb.append("false").append(",");
				sb.append("N 000:00:00.000").append(",");
				sb.append("E 000:00:00.000").append(",");
				sb.append(Height).append("\n");

			}

			
			////////////////////////////// @CPRI_PORT_INFORMATION//////////////////////////////////
			sb.append("@CPRI_PORT_INFORMATION").append("\n");
			sb.append("NE ID,RU PortID,Connected DU PortID,DU Port Mode\n");

			List<CIQDetailsModel> data = fileUploadRepository.getEnBData(dbcollectionFileName, "FSUCIQ", enbId);
			for (CIQDetailsModel info : data) {
				String portMode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();
				if (neVersion.equals("19.A.0")) {
					if (!portMode.equalsIgnoreCase("Not Used")) {
						String ruport = info.getCiqMap().get("RU_PortID").getHeaderValue();
						String connectedduportid = info.getCiqMap().get("Connected_DU_PortID").getHeaderValue();
						String duportmode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();

						sb.append(NE_ID).append(",");
						sb.append(ruport).append(",");
						sb.append(connectedduportid).append(",");
						sb.append("pass-through").append("\n");
					}
				} else if (neVersion.equals("20.A.0")) {
					if (!portMode.equalsIgnoreCase("Not Used")) {
						String ruport = info.getCiqMap().get("RU_PortID").getHeaderValue();
						String connectedduportid = info.getCiqMap().get("Connected_DU_PortID").getHeaderValue();
						String duportmode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();
						sb.append(NE_ID).append(",");
						sb.append(ruport).append(",");
						sb.append(connectedduportid).append(",");
						sb.append("pass-through").append("\n");

					}
				}else if (neVersion.equals("20.C.0")) {
					if (!portMode.equalsIgnoreCase("Not Used")) {
						String ruport = info.getCiqMap().get("RU_PortID").getHeaderValue();
						String connectedduportid = info.getCiqMap().get("Connected_DU_PortID").getHeaderValue();
						String duportmode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();
						sb.append(NE_ID).append(",");
						sb.append(ruport).append(",");
						sb.append(connectedduportid).append(",");
						sb.append("pass-through").append("\n");

					}
				}

			}

			///////////////////////// @INTER_CONNECTION_INFORMATION//////////////////////////

			sb.append("@INTER_CONNECTION_INFORMATION").append("\n");
			sb.append("NE ID,Inter Connection Group ID,Inter Connection Switch,Inter Connection Node ID\n");
			if (neVersion.equals("19.A.0")) {

				String Inter_Connection_Group_ID = "";
				String Inter_Connection_Switch = "inter-connection-off";
				String Inter_Connection_Node_ID = "31";
				sb.append(NE_ID).append(",");
				sb.append(Inter_Connection_Group_ID).append(",");
				sb.append(Inter_Connection_Switch).append(",");
				sb.append(Inter_Connection_Node_ID).append("\n");
			} else if (neVersion.equals("20.A.0")) {

				// String Inter_Connection_Group_ID =
				// listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name").getHeaderValue();
				// String Inter_Connection_Group_ID = ;
				String Inter_Connection_Group_ID1 = listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name")
						.getHeaderValue();
				String Inter_Connection_Group_ID = Inter_Connection_Group_ID1.substring(1);
				String Inter_Connection_Switch = "inter-connection-on";
				String Inter_Connection_Node_ID = "31";
				sb.append(NE_ID).append(",");
				sb.append(Inter_Connection_Group_ID).append(",");
				sb.append(Inter_Connection_Switch).append(",");
				sb.append(Inter_Connection_Node_ID).append("\n");

			}else if (neVersion.equals("20.C.0")) {
				String Inter_Connection_Group_ID1 = listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name")
						.getHeaderValue();
				//String Inter_Connection_Group_ID = Inter_Connection_Group_ID1.substring(1);
				String Inter_Connection_Switch = "inter-connection-on";
				String Inter_Connection_Node_ID = "31";
				sb.append(NE_ID).append(",");
				sb.append(Inter_Connection_Group_ID1).append(",");
				sb.append(Inter_Connection_Switch).append(",");
				sb.append(Inter_Connection_Node_ID).append("\n");

			}

			/////////////////////////// @VDU_INFORMATION///////////////////////////////
			sb.append("@VDU_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Port ID,IP Address\n");
			String neid = "";

			String unit_id = "";
			String port_id = "";
			String ipaddress = "";
			sb.append(neid).append(",");
			if (neVersion.equals("19.A.0")||neVersion.equals("20.C.0")) {
				String unittype = listCIQDetailsModel.get(0).getCiqMap().get("Unit_Type")
						.getHeaderValue();
				sb.append(unittype).append(",");
			} else if (neVersion.equals("20.A.0")) {
				String unit_type = "";
				sb.append(unit_type).append(",");
			}

			sb.append(unit_id).append(",");
			sb.append(port_id).append(",");
			sb.append(ipaddress).append("\n");

			////////////////////////// @ECPRI_PORT_INFORMATION//////////////////////////////
			sb.append("@ECPRI_PORT_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Port ID,Port Administrative State,Configured Speed,Fec Mode\n");
			if (neVersion.equals("19.A.0")) {
				String unittype = "";
				String port_administrative_state = "";
				String configured_speed = "";
				String fecmode = "";
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			} else if (neVersion.equals("20.A.0")) {
				String unittype = "fsip";
				String unit_id1 = "0";
				String port_id1 = "0";
				String port_administrative_state = "unlocked";
				String configured_speed = "s25g";
				String fecmode = "rs-fec";
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			}else if (neVersion.equals("20.C.0")) {
				String unittype = listCIQDetailsModel.get(0).getCiqMap().get("Unit_Type")
						.getHeaderValue();
				String unit_id1 = listCIQDetailsModel.get(0).getCiqMap().get("Unit_ID")
						.getHeaderValue();
				String port_id1 = "0";
				String port_administrative_state = listCIQDetailsModel.get(0).getCiqMap().get("Port_AdministrativeState")
						.getHeaderValue();
				String configured_speed = listCIQDetailsModel.get(0).getCiqMap().get("Configured_Speed")
						.getHeaderValue();
				String fecmode = listCIQDetailsModel.get(0).getCiqMap().get("Fec_Mode")
						.getHeaderValue();

				sb.append(NE_ID).append(",");
				sb.append(unittype).append(",");
				sb.append(unit_id1).append(",");
				sb.append(port_id1).append(",");
				sb.append(port_administrative_state).append(",");
				sb.append(configured_speed).append(",");
				sb.append(fecmode).append("\n");

			}

			/////////////////// @ECPRI_TRAFFIC_PLANE_INFORMATION/////////////////////
			sb.append("@ECPRI_TRAFFIC_PLANE_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,Ecpri Interface Name,Vlan ID,MTU,Management,Control/User,IEEE1588,SYNCE\n");
			String cpuid = null;
			String ecpri_interface_name = null;

			if (neVersion.equals("19.A.0")) {
				cpuid = "";
				ecpri_interface_name = "";
				String vlanid = "";
				String mtu = "1500";
				String management = "false";
				String control_user = "false";
				String ieee1588 = "false";
				String synce = "false";
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			}else if (neVersion.equals("20.C.0")) {
				cpuid = listCIQDetailsModel.get(0).getCiqMap().get("CPU_ID")
						.getHeaderValue();
				ecpri_interface_name = "";
				String vlanid = "";
				String mtu = "1500";
				String management = "false";
				String control_user = "false";
				String ieee1588 = "false";
				String synce = "false";
				for(int i=0;i<2;i++) {
				sb.append(NE_ID).append(",");
				sb.append(cpuid).append(",");
				sb.append("fh_0_1_0_"+i).append(",");
				
				if(i==0) {
					sb.append("").append(",");
					sb.append("1500").append(",");
					sb.append("true").append(",");
					sb.append("false").append(",");
					sb.append("true").append(",");
					sb.append("true").append("\n");
				}
				else if(i==1) {
					sb.append("950").append(",");
				sb.append("9000").append(",");
				sb.append("false").append(",");
				sb.append("true").append(",");
				sb.append("false").append(",");
				sb.append("false").append("\n");
				
				}
				
				
				}
			}
			// If we want to handle in a Dynamic way(Vijay)
			// List<CIQDetailsModel> data =
			// fileUploadRepository.getEnBData(dbcollectionFileName, "FSUCIQ", enbId);
			/*
			 * if (neVersion.equals("20.A.0")) { for (CIQDetailsModel info : data) { String
			 * NE_ID1 = info.getCiqMap().get("NE_ID").getHeaderValue();
			 * 
			 * String Ecpri_Interface_Name =
			 * info.getCiqMap().get("Ecpri_Interface_Name").getHeaderValue(); String Vlan_ID
			 * = info.getCiqMap().get("VLAN_ID").getHeaderValue(); String mtu =
			 * info.getCiqMap().get("MTU").getHeaderValue(); String management =
			 * info.getCiqMap().get("Management").getHeaderValue(); String CPU_ID =
			 * info.getCiqMap().get("Cpu_ID").getHeaderValue();
			 * sb.append(NE_ID1).append(","); sb.append(CPU_ID).append(",");
			 * sb.append(Ecpri_Interface_Name).append(","); sb.append(Vlan_ID).append(",");
			 * sb.append(mtu).append(","); sb.append(management).append("/n");
			 * 
			 * } }
			 */
			else if (neVersion.equals("20.A.0")) {

				cpuid = "0";
				ecpri_interface_name = "";
				String vlanid = "";
				String mtu = "1500";
				String management = "true";
				String control_user = "false";
				String ieee1588 = "true";
				String synce = "true";
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}

			/////////////////////// @ECPRI_IP_INFORMATION////////////////////////
			sb.append("@ECPRI_IP_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,Ecpri Interface Name,IP Address,IP Prefix Length\n");
			if (neVersion.equals("19.A.0")) {
				String ip_prefixlength = "";
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			} else if (neVersion.equals("20.A.0")) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}else if (neVersion.equals("20.C.0")) {
				sb.append(NE_ID).append(",");
				sb.append(cpuid).append(",");
				String Ecpri_Interface_Name=listCIQDetailsModel.get(0).getCiqMap().get("Ecpri_Interface_Name")
				.getHeaderValue();
				
				sb.append(Ecpri_Interface_Name).append(",");
				sb.append("fd00:4888:2A:10:0:406::1").append(",");
				sb.append(IP_Prefix_Length).append("\n");

			}

		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return sb;

	}

	// method for 20.A.0 ver grow enb/////////////////////////////
	private StringBuilder getENBStringForV20A(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel, String version) {

		StringBuilder sb = new StringBuilder();
		String sheetName = "";
		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);
//		ip_present = true;
		setIpPresent(true);
		System.out.println(enbId+"getENBStringForV20A ip present updating true");
		sheetName = Constants.VZ_GROW_CIQUpstateNY;

		try {

			String siteConfigType = "";
			String rsIp = "";
			String lsmName = "";
			String RelVersion = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();

			siteConfigType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())) {
				rsIp = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
				lsmName = neMappingEntity.getNetworkConfigEntity().getNeName();
			}
			// #########@ENB###############
			sb.append("@ENB").append("\n");
			if (version.equals("20.C.0")||version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")) {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,Customer NE Type,Rack ID,Local Time Offset,CBRS Mode,CBRS User ID,CBRS Measure Unit,FW Auto Fusing\n");
			} else if (version.equals("21.D.0") || version.equals("22.A.0")  || version.equals("22.C.0")) {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,Customer NE Type,Rack ID,Local Time Offset,CBRS Mode,CBRS Measure Unit,FW Auto Fusing\n");
			}
			else {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,Customer NE Type,Rack ID,Time Offset,CBRS Mode,CBRS User ID,CBRS Measure Unit\n");
			}
			String NE_ID = enbId;
			String NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Type);
			if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("20.B.0")) {
				String NE_Version = "20.B.0";
				// RelVersion = "r_0102";
			} else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("20.C.0")) {
				String NE_Version = "20.C.0";
				// RelVersion = "r_0102";
			} else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")) {
				String NE_Version = "21.B.0";
				// RelVersion = "r_0102";
			} else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0")) {
				String NE_Version = "21.C.0";
				// RelVersion = "r_0102";
			} else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0")) {
				String NE_Version = "21.D.0";

			} else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) { // 22A
				String NE_Version = "22.A.0";

			}else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0")) { // 22A
				String NE_Version = "22.C.0";

			} else {
				String NE_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Version);
				// RelVersion = "r_0101";
			}
			// String RelVersion =
			// resultMapForConstants.get(Constants.ORAN_VGROW_ENB_RelVersion);
			String Ne_Name = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String Network = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Network);
			//System.out.println(Network);
			String Cascade_ID = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String enb_str = Cascade_ID.substring(0, 3);
			String Group = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Market_CLLI_Code) + "_"
					+ enb_str;
			String Customer_NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Customer_NE_Type);
			String Rack_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Rack_ID);
			String Time_Offset = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Time_Offset);
			String CBRS_Mode = "cbrs-on";
			String CBRS_User_ID = "Samsung";
			String CBRS_Measure_Unit = "10mhz";
			sb.append(NE_ID).append(",");
			sb.append(NE_Type).append(",");
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()).append(",");

			sb.append(RelVersion).append(",");
//			if(listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue().equals("TRI") || listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue().equals("CTX")) {
//				String ne=NE_ID.replaceAll("^0+(?!$)", "");
//				if(ne.length()==6)
//					sb.append(ne.substring(0, 3)).append(",");
//				else if(ne.length()==5)
//					sb.append(ne.substring(0, 2)).append(",");
//			}else {
//				sb.append(Group).append(",");
//			}
			sb.append(Network).append(",");
			sb.append("GROW_" + Ne_Name.toUpperCase()).append(",");

			if (version.equals("20.C.0") || version.equals("21.A.0") || version.equals("21.B.0")
					|| version.equals("21.C.0") || version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
			}
			sb.append(Customer_NE_Type).append(",");
			sb.append(Rack_ID).append(",");
			if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.A.0")
					|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")
					|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0")
					|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0")
					|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")
					|| neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0")) // 22A
				sb.append("0").append(",");
			else
			sb.append(Time_Offset).append(",");
			sb.append(CBRS_Mode).append(",");
			if(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.A.0")
					||neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.B.0")
					||neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.C.0"))
					//||neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("21.D.0"))
			{
			sb.append(CBRS_User_ID).append(",");
			}
			if (version.equals("20.C.0")) {
				sb.append(CBRS_Measure_Unit).append(",");
				sb.append("on").append("\n");
			}else if (version.equals("21.A.0")||version.equals("21.B.0")||version.equals("21.C.0")) {
				sb.append(CBRS_Measure_Unit).append(",");
				sb.append("on").append("\n");
			} else if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) { // 22A
				sb.append(CBRS_Measure_Unit).append(",");
				sb.append("on").append("\n");
				
			}
				else {
				sb.append(CBRS_Measure_Unit).append("\n");
			}
			///////////////////// @SERVER_INFORMATION/////////////////////////////////
			sb.append("@SERVER_INFORMATION").append("\n");
			sb.append("NE ID,CFM,PSM,CDP\n");
if (version.equals("21.A.0")||version.equals("21.B.0")||version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
		    	sb.append("").append("\n");

}

			// ##############@SON_INFORMATION##########
			sb.append("@SON_INFORMATION").append("\n");
			if (!(version.equals("21.A.0")||version.equals("21.B.0")||version.equals("21.C.0")||version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0"))) {
			sb.append(
					"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT 1XRTT NRT,Initial Inter-RAT HRPD NRT,Initial SRS Nrt,Initial SRS Pool Index,Initial Inter-RAT NRT NR\n");
			}else if(version.equals("22.C.0")){
				sb.append(
						"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT HRPD NRT,Initial Inter-RAT NRT NR,Initial ZCZC,Initial PRACH Config Index\n");
					
			}else {
				sb.append(
						"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT HRPD NRT,Initial Inter-RAT NRT NR\n");
					
			}
			String Initial_PCI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_PCI);
			String Initial_RSI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_RSI);
			String Initial_Intra_LTE_NRT = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_Intra_LTE_NRT);
			String Initial_Inter_RAT_1XRTT_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_1XRTT_NRT);
			String Initial_Inter_RAT_HRPD_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_HRPD_NRT);
			String Initial_SRS_Nrt = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Nrt);
			String Initial_SRS_Pool_Index = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Pool_Index);
			String Initial_Inter_RAT_NRT_NR = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_NRT_NR);
			sb.append(NE_ID).append(",");
			sb.append(Initial_PCI).append(",");
			sb.append(Initial_RSI).append(",");
			sb.append(Initial_Intra_LTE_NRT).append(",");
			sb.append(Initial_Inter_RAT_1XRTT_NRT).append(",");
			if (!(version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") ||  version.equals("22.C.0"))) {
				sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
				sb.append(Initial_SRS_Nrt).append(",");
				sb.append(Initial_SRS_Pool_Index).append(",");
				sb.append(Initial_Inter_RAT_NRT_NR).append("\n");
			}else if(version.equals("22.C.0")){
				sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
				sb.append("off").append(",");
				sb.append("off").append("\n");
			} else {
				sb.append(Initial_Inter_RAT_HRPD_NRT).append("\n");
			}

			// ##############@EXTERNAL_LINK_INFORMATION##########

			sb.append("@EXTERNAL_LINK_INFORMATION").append("\n");
			if (version.equals("20.C.0") || version.equals("21.A.0") || version.equals("21.B.0")
					|| version.equals("21.C.0") || version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("NE ID,Unit Type,Unit ID,Port Id,VR ID,Admin State,Connect Type,UDE Type,Speed Duplex,MTU\n");
			} else {
				sb.append("NE ID,Unit Type,Unit ID,Port Id,VR ID,Admin State,Connect Type,UDE Type,MTU\n");
			}

			String Unit_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_Type);
			String Unit_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_ID);
			String Port_Id = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Port_Id);
			String VR_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_VR_ID);
			String AdminState = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_AdminState);

			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Port_Id).append(",");
			sb.append(VR_ID).append(",");
			sb.append(AdminState).append(",");
			sb.append("backhaul").append(",");
			sb.append("ude-none").append(",");
			if (version.equals("20.C.0")) {
				sb.append("").append(",");
			}
			if (version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
			}
			sb.append("1500").append("\n");

			// #########@CLOCK_SOURCE_INFORMATION#############
			if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
					|| version.equals("22.A.0")  || version.equals("22.C.0")) {
				sb.append("@CLOCK_INFORMATION").append("\n");
			} else {
				sb.append("@CLOCK_SOURCE_INFORMATION").append("\n");
			}			
			sb.append("NE ID,ID,Clock Source,Priority Level,Quality Level\n");

			String ID = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_ID);
			String Clock_Source = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Clock_Source);
			String Priority_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Priority_Level);
			String Quality_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Quality_Level);
			
			//ptp column fetch
			
			String primaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "primaryClockSource");
			String secondaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "secondaryClockSource");
			String grandMasterIP = dataByEnode(dbcollectionFileName, sheetName, enbId, "grandMasterIP");
			String PTPHeight = dataByEnode(dbcollectionFileName, sheetName, enbId, "PTPHeight");
			
			if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 1
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append("gps-type").append(",");
				sb.append("1").append(",");
				sb.append("ssu-a").append("\n");
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("ssu-a").append("\n");
			} else if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 2
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("prc").append("\n");
			}  else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 3
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append("gps-type").append(",");
				sb.append("1").append(",");
				sb.append("ssu-a").append("\n");
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("prc").append("\n"); //ptp end
			} else {
				sb.append(NE_ID).append(",");
				sb.append(ID).append(",");
				sb.append(Clock_Source).append(",");
				sb.append(Priority_Level).append(",");
				sb.append(Quality_Level).append("\n");
			}

			// #########@PTP_INFORMATION#############
			sb.append("@PTP_INFORMATION").append("\n");
			sb.append("NE ID,IP Version,First Master IP,Second Master IP,Clock Profile,PTP Domain\n");
			if (version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")  ) {
				
				if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 1

					sb.append(NE_ID).append(",");
					sb.append("ipv6").append(",");
					sb.append(grandMasterIP).append(",");
					sb.append("::").append(",");
					sb.append("telecom-2008").append(",");
					sb.append("0").append("\n");
					
				} else if(primaryClockSource.equals("ieee1588-phasetype") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 2

					sb.append(NE_ID).append(",");
					sb.append("ipv4").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("itu-g8275-1").append(",");
					sb.append("24").append("\n");
					
				}  else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //scenerio 3
 
					sb.append(NE_ID).append(",");
					sb.append("ipv4").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("itu-g8275-1").append(",");
					sb.append("24").append("\n");//ptp end
					
				} else {
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append("\n");
				}
			}else {
			sb.append("").append(",");
			sb.append("ipv4").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("telecom-2008").append(",");
			sb.append("").append("\n");
			}
			// ##########@INTER_CONNECTION_INFORMATION#############

			sb.append("@INTER_CONNECTION_INFORMATION").append("\n");
			sb.append("NE ID,Inter Connection Group ID,Inter Connection Switch,Inter Connection Node ID\n");

			String ID1 = resultMapForConstants.get(Constants.ORAN_VGROW_InterConnection_ID);
			String InterConnectionGroupID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionGroupID);
			String InterConnectionSwitch = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionSwitch);
			String InterConnectionNodeID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionNodeID);

			if (version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") ||  version.equals("22.C.0"))
				sb.append("").append(",");
			else
				sb.append(NE_ID).append(",");
			if (version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") ||  version.equals("22.C.0"))
				sb.append("0").append(",");
			else
			sb.append("").append(",");
			sb.append(InterConnectionSwitch).append(",");
			sb.append(InterConnectionNodeID).append("\n");

			// ###########@INTER_ENB_INFORMATION###################

			sb.append("@INTER_ENB_INFORMATION").append("\n");
			sb.append("NE ID,Inter Node ID,Admin State\n");

			String InterNodeID = "0";
			String AdminState1 = "unlocked";
                        sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("unlocked").append("\n");

			// ###########@SYSTEM_LOCATION_INFORMATION###################

			String User_Def_Mode = "false";
			String Latitude = "N 000:00:00.000";
			String Longitude = "E 000:00:00.000";
			String Height = "0.00m";
	
			String PTPlongitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Long");//ptp columns
			String PTPlatitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Lat");
			
			double Ptplat = Double.valueOf(PTPlatitude);
			double PTPLong = Double.valueOf(PTPlongitude);
			
			//latitude conversion
			StringBuilder PTPsbLat = new StringBuilder();
			String latResult = (Ptplat >= 0)? "N" : "S";
			PTPsbLat.append(latResult);
			PTPsbLat.append(" ");
			
			Double ddNew = Math.abs(Ptplat);
			Double degree=Math.floor(ddNew);
			
			PTPsbLat.append(String.valueOf(String.format("%03d", degree.intValue())));
			PTPsbLat.append(":");
			Double min=Math.floor((ddNew - degree) * 60);
			
			PTPsbLat.append(String.valueOf(String.format("%02d",min.intValue())));
			PTPsbLat.append(":");
			Double valSec = Math.floor((ddNew - degree - min / 60) * 3600 * 1000) / 1000;
			DecimalFormat df = new DecimalFormat("00.000");
			String data=df.format(valSec);
			PTPsbLat.append(data.toString());
			
			//longitude conversion
			String lngResult = (PTPLong >= 0)? "E" : "W";
			StringBuilder PTPsbLong = new StringBuilder();
			
			PTPsbLong.append(lngResult);
			PTPsbLong.append(" ");
			
			ddNew = Math.abs(PTPLong);
			degree=Math.floor(ddNew);
			
			PTPsbLong.append(String.valueOf(String.format("%03d", degree.intValue())));
			PTPsbLong.append(":");
			min=Math.floor((ddNew - degree) * 60);
			
			PTPsbLong.append(String.valueOf(String.format("%02d",min.intValue())));
			PTPsbLong.append(":");
			valSec = Math.floor((ddNew - degree - min / 60) * 3600 * 1000) / 1000;
			df = new DecimalFormat("00.000");
			data=df.format(valSec);
			PTPsbLong.append(data.toString());
			
			sb.append("@SYSTEM_LOCATION_INFORMATION").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");
			if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type") && (version.equals("22.A.0")|| version.equals("22.C.0"))) {//ptp scenerio 2
				sb.append(NE_ID).append(",");
				sb.append("true").append(",");
				sb.append(PTPsbLat).append(",");
				sb.append(PTPsbLong).append(",");
				sb.append(PTPHeight+"m").append("\n");
			} else if (primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") && (version.equals("22.A.0")|| version.equals("22.C.0"))) {//ptp scenerio 3
				sb.append(NE_ID).append(",");
				sb.append("true").append(",");
				sb.append(PTPsbLat).append(",");
				sb.append(PTPsbLong).append(",");
				sb.append(PTPHeight+"m").append("\n");
			}else {
				sb.append(NE_ID).append(",");
				sb.append(User_Def_Mode).append(",");
				sb.append(Latitude).append(",");
				sb.append(Longitude).append(",");
				sb.append(Height).append("\n");
			}


			// ########@MME_INFORMATION##############

			sb.append("@MME_INFORMATION").append("\n");
			sb.append(
					"NE ID,Index,IP Type,IP,Service Purpose,Attach Without PDN Connectivity,CP Optimization,UP Optimization\n");

			int Index;
			String subSheetname = "";
			String market = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
					Constants.VZ_GROW_Market);
			List<CIQDetailsModel> listCIQDetailsModels1 = fileUploadRepository.getEnbTableDetailss(ciqFileName,
					"New England CIQ", null, dbcollectionFileName);
			if (listCIQDetailsModels1 != null && listCIQDetailsModels1.size() > 0) {
				if (sheetName.equalsIgnoreCase(Constants.VZ_GROW_CIQUpstateNY)) {
					subSheetname = "UNY";
				} else {
					subSheetname = "New England";
				}
			} else {
				subSheetname = "New England";
			}
			List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetailss(ciqFileName,
					"MME IP'S", "UNY", dbcollectionFileName);
			String market1 = market.replaceAll("\\s", "");
			// String[] mme_ip = objGenerateCsvRepository.getVlsmMmeIpDetails(market);
			//String[] mme_ip = dataCiqIndex(listCIQDetailsModels, Constants.VZ_IP_ADDRESS, "MMEIPS");
			String[] mme_ip = dataCiqIndexForMmeip(enbId,listCIQDetailsModels, "MME_IP", "MMEIPS");
			String IPType = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPType);
			String IPV4 = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPV4);
			String ServicePurpose = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_ServicePurpose);
			String AttachWithoutPDNConnectivity = resultMapForConstants
					.get(Constants.ORAN_VGROW_MMEInfo_AttachWithoutPDNConnectivity);
			String CPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_CPOptimization);
			String UPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_UPOptimization);

			for (int i = 0; i < mme_ip.length; i++) {
				if (market1.equals("NewEngland")) {
					Index = i;
				} else {
					Index = i + 4;
				}

				sb.append(NE_ID).append(",");
				sb.append(i).append(",");
				sb.append(IPType).append(",");
				sb.append(CommonUtil.formatIPV6Address(mme_ip[i])).append(",");
				sb.append(ServicePurpose).append(",");
				sb.append(AttachWithoutPDNConnectivity).append(",");
				sb.append("true").append(",");
				sb.append(UPOptimization).append("\n");
			}

			// #######@MAIN_BOARD_INFORMATION############
			sb.append("@MAIN_BOARD_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append("lmd1-j1").append("\n");

			// #######@EXTERNAL_INTERFACE_INFORMATION############

			sb.append("@EXTERNAL_INTERFACE_INFORMATION").append("\n");
			if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append(
						"NE ID,IF Name,IP,Prefix Length,IP Get Type,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,IEEE1588,Smart scheduler\n");
			}else {
			sb.append(
					"NE ID,IF Name,IP,Prefix Length,IP Get Type,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,Bearer M1,Signal M2,IEEE1588,Smart scheduler\n");
			}

			String oam_vlan, s_bVlan, vlan1, vlan2;
			List<Integer> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());
			List<Integer> vlanIP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());

			List<CIQDetailsModel> listCiq = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)).collect(Collectors.toList());

			if (listCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				List<Integer> objVlan1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				List<Integer> vlanIP1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				vlan1 = objVlan1.get(0).toString();

				vlan2 = objVlan1.get(1).toString();

				if (vlanIP1.get(0).toString().contains("1")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;

				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;

				}
			} else {
				vlan1 = objVlan.get(0).toString();

				vlan2 = objVlan.get(1).toString();
				if (vlanIP.get(0).toString().contains("1")) {
					oam_vlan = vlan1;
					s_bVlan = vlan2;

				} else {
					oam_vlan = vlan2;
					s_bVlan = vlan1;

				}
			}

			String eNB_oam = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String eNB_s_b = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			String[] IF_Name = { "ge_0_0_1." + oam_vlan, "ge_0_0_1." + s_bVlan };

			String IP1 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			if (listCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				 IP1 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				 IP2 = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				 eNB_s_b = vplanIpSheet.stream()
							.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
									&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
							.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
							.collect(Collectors.joining(""));
				 eNB_oam = vplanIpSheet.stream()
							.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
									&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
							.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
							.collect(Collectors.joining(""));
			}

			String[] Management = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Management)
					.split(",");
			String IP_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IP_Version);
			String[] Signal_S1 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Signal_S1)
					.split(",");
			String IEEE1588 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IEEE1588);
			String Smart_scheduler = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Smart_scheduler);

			String[] IP = { CommonUtil.formatIPV6Address(IP1), CommonUtil.formatIPV6Address(IP2) };
			// String[] IP = { IP1, IP2 };
			if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				for (int i = 0; i < IF_Name.length; i++) {

					sb.append(NE_ID).append(",");
					sb.append(IF_Name[i]).append(",");
					sb.append(IP[i]).append(",");
					sb.append(eNB_s_b).append(",");
					sb.append("static").append(",");
					sb.append(Management[i]).append(",");
					sb.append(Signal_S1[0]).append(",");
					sb.append(Signal_S1[1]).append(",");
					sb.append(Signal_S1[2]).append(",");
					sb.append(Signal_S1[3]).append(",");
					//sb.append(Signal_S1[4]).append(",");
					//sb.append(Signal_S1[5]).append(",");
					if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") 
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //ptp scenerio 1
						sb.append("true").append(",");
					} else if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type")
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //ptp scenerio 2
						sb.append("true").append(",");
					} else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) && (version.equals("22.A.0")|| version.equals("22.C.0"))) { //ptp scenerio 3
						sb.append("true").append(",");
					}  else {
						sb.append(Signal_S1[6]).append(",");
					}
					sb.append(Smart_scheduler).append("\n");
				}
			}else {

			       for (int i = 0; i < IF_Name.length; i++) {

				        sb.append(NE_ID).append(",");
				        sb.append(IF_Name[i]).append(",");
				        sb.append(IP[i]).append(",");
				        sb.append(eNB_s_b).append(",");
				        sb.append("static").append(",");
				        sb.append(Management[i]).append(",");
				        sb.append(Signal_S1[0]).append(",");
				        sb.append(Signal_S1[1]).append(",");
				        sb.append(Signal_S1[2]).append(",");
				        sb.append(Signal_S1[3]).append(",");
				        sb.append(Signal_S1[4]).append(",");
				        sb.append(Signal_S1[5]).append(",");
				        sb.append(Signal_S1[6]).append(",");
				        sb.append(Smart_scheduler).append("\n");
			       }
			}
			// ##########@STATIC_ROUTE_INFORMATION##############

			sb.append("@STATIC_ROUTE_INFORMATION").append("\n");
			sb.append("NE ID,VR ID,IP Type,IP Prefix,IP GW,Route Interface Name\n");

			String IPType1 = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_IPType);
			String rsIpVlsm = rsIp + "/128";
			String csl_ip = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_csl_ip);

			String[] IPPrefix = { rsIpVlsm, "0:0:0:0:0:0:0:0/0", "0:0:0:0:0:0:0:0/0", csl_ip };

			String OAM_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String SB_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			
			if (listCiq.size() == 0) {
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				OAM_GW_IP = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				 SB_GW_IP = vplanIpSheet.stream()
							.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
									&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
							.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
							.collect(Collectors.joining(""));
				 
			}

			OAM_GW_IP = CommonUtil.formatIPV6Address(OAM_GW_IP);
			SB_GW_IP = CommonUtil.formatIPV6Address(SB_GW_IP);

			String[] IPGW = { OAM_GW_IP, OAM_GW_IP, SB_GW_IP, OAM_GW_IP };

			for (int i = 0; i < IPPrefix.length; i++) {
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append(IPType1).append(",");
				sb.append(IPPrefix[i]).append(",");
				sb.append(IPGW[i]).append(",");
				sb.append("-").append("\n");

			}

			// ##########ENB_SCHEDULAR_INFORMATION##############
			if (!(version.equals("21.D.0") || version.equals("22.A.0") ||  version.equals("22.C.0"))) {
				sb.append("@ENB_SCHEDULAR_INFORMATION").append("\n");
				sb.append("NE ID,RCC ID,Cluster ID,IP Version,Scheduler IP,Scheduler 2nd IP\n");
				sb.append("").append(",");
				sb.append("0").append(",");
				sb.append("").append(",");
				sb.append("ipv6").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			}
			// ##########@VIRTUAL_ROUTING_INFORMATION##############
			sb.append("@VIRTUAL_ROUTING_INFORMATION").append("\n");
			sb.append("NE ID,VR ID\n");

			sb.append(NE_ID).append(",");
			sb.append(VR_ID).append("\n");
			

			// ##########@VLAN_INFORMATION##############
			sb.append("@VLAN_INFORMATION").append("\n");
			sb.append("NE ID,VLAN ID,VR ID,VLAN Interface Name\n");

			String[] vlan_id = { oam_vlan, s_bVlan };
			for (int i = 0; i < 2; i++) {
				sb.append(NE_ID).append(",");
				sb.append(vlan_id[i]).append(",");
				sb.append("0").append(",");
				sb.append("ge_0_0_1").append("\n");
			}

			// ##########@LAG_INFORMATION##############
			sb.append("@LAG_INFORMATION").append("\n");
			sb.append("NE ID,LAG ID,VR ID,LAG Interface Name\n");
			sb.append("").append(",");

			sb.append("1").append(",");
			sb.append("0").append(",");

			sb.append("").append("\n");

			//////////////// @IPSEC_INFORMATION///////////////
			sb.append("@IPSEC_INFORMATION").append("\n");
			if (version.equals("20.C.0")|| version.contains("21.A.0")) { 
				sb.append(
						"NE ID,VR ID,Interface Name1,Peer IP Version,First Peer IP,Second Peer IP,Inner IP Version,Tunnel Mode,Interface Name2,Interface Name3,Crypto Algorithm,Hash Algorithm,Local ID Type,Local ID\n");
			} else if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
					|| version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append(
						"NE ID,VR ID,Interface Name1,Peer IP Version,First Peer IP,Second Peer IP,Inner IP Version,Tunnel Mode,Interface Name2,Interface Name3,Interface Name4,Interface Name5,Interface Name6,Crypto Algorithm,Hash Algorithm,Local ID Type,Local ID\n");
			} else {
				sb.append(
						"NE ID,Interface Name1,Peer IP Version,First Peer IP,Second Peer IP,Inner IP Version,Tunnel Mode,Interface Name2,Interface Name3,Crypto Algorithm,Hash Algorithm,Local ID Type,Local ID\n");
			}
			if (version.equals("21.A.0")){
				sb.append("").append(",");
							sb.append("").append(",");
							sb.append("").append(",");
				sb.append("").append(",");
							sb.append("").append(",");
							sb.append("").append(",");
				sb.append("").append(",");
							sb.append("").append(",");
							sb.append("").append(",");
				sb.append("").append(",");
							sb.append("").append(",");
							sb.append("").append(",");
				sb.append("").append(",");
								sb.append("").append("\n");

			} else if (version.equals("21.B.0") || version.equals("21.C.0") || version.equals("21.D.0")
					|| version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}

			////////////// @PKI_INFORMATION//////////////////////////
			sb.append("@PKI_INFORMATION").append("\n");
			sb.append("NE ID,IP Address,FQDN,Port,Path,DN,DN Domain,CA DN,Hash Algorithm\n");
			if (version.equals("21.A.0") || version.equals("21.B.0") || version.equals("21.C.0")
					|| version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");

				sb.append("").append("\n");

}
			
			if(version.contains("21") || version.contains("22")) {//22A
			////////////// @TCE_INFORMATION//////////////////////////
			sb.append("@TCE_INFORMATION").append("\n");
			sb.append("NE ID,TCE List Index,TCE ID,TCE IP Version,TCE IP,TCE Type\n");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("ipv6").append(",");
			sb.append("").append(",");
			sb.append("lsm-embedded").append("\n");
			
			String value = null;
			if (enbId.startsWith("0")) {
				value = enbId.substring(1, 3);
			} else {
				value = enbId.substring(0, 2);
			}
				Ip obj = rep.getip(value);
				if(obj==null) {
					obj=rep.getip(enbId.substring(0, 3));
					if(obj==null) {
						obj=rep.getip("0");
					}
				}
				//////////// @CSL_CONTROL_INFORMATION/////////////////////
				if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
					int Delta_OOS_Threshold = 1;
					String MR_Overwrite_Enable = "first-and-last-mr";
					String Single_Measure_Report_Control = "off";
					String EN_DC_CSL_Enable = "on";
					String EN_DC_CSL_Create_Condition = "all";

					sb.append("@CSL_CONTROL_INFORMATION").append("\n");
					sb.append("NE ID,Delta OOS Threshold,MR Overwrite Enable,Single Measure Report Control,EN-DC CSL Enable,EN-DC CSL Create Condition\n");
					sb.append("").append(",");
					sb.append(Delta_OOS_Threshold).append(",");
					sb.append(MR_Overwrite_Enable).append(",");
					sb.append(Single_Measure_Report_Control).append(",");
					sb.append(EN_DC_CSL_Enable).append(",");
					sb.append(EN_DC_CSL_Create_Condition).append("\n");
					
				}	
			//////////////@CSL_INFORMATION//////////////////////////
			sb.append("@CSL_INFORMATION").append("\n");
			if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				String CSL_IP_Version="ipv6";
				String CSL_Server_IP="2001:4888:a1f:c223:1b4:1a2:0:3";
				String CSL_Port_Number="50003";
				String Buffering_Time="2";
				String UDP_Ack_Control="r1";
				String Protocol_Selection="1";
				String CSL_Report_Control="on";
				String CSL_Encryption_Mask_Mode="0";
				String Second_CSL_IP_Version="ipv6";
				String Second_CSL_Server_IP="::";
				String Second_CSL_Port_Number="50003";
				String Second_Buffering_Time="2";
				String Second_UDPAck_Control="no-retransmission";
				String Second_Protocol_Selection="1";
				String Third_CSL_IP_Version="ipv6";
				String Third_CSL_Server_IP="2001:4888:a06:2283:f1:fef:0:506";
				String Third_CSL_Port_Number="50001";
				String Third_Buffering_Time="2";
				String Third_UDP_Ack_Control="no-retransmission";
				String Third_Protocol_Selection="0";
				String Fourth_CSL_IP_Version="ipv6";
				String Fourth_CSL_Server_IP="2001:4888:a06:2283:f1:fef:0:506";
				String Fourth_CSL_Port_Number="50001";
				String Fourth_Buffering_Time="2";
				String Fourth_UDP_Ack_Control="no-retransmission";
				//String CSL_Port_Number="";
				String Fourth_Protocol_Selection="0";
				sb.append("NE ID,CSL IP Version,CSL Server IP,CSL Port Number,Buffering Time,UDP Ack Control,Protocol Selection,CSL Report Control,CSL Encryption Mask Mode,Second CSL IP Version,Second CSL Server IP,Second CSL Port Number,Second Buffering Time,Second UDP Ack Control,Second Protocol Selection,Third CSL IP Version,Third CSL Server IP,Third CSL Port Number,Third Buffering Time,Third UDP Ack Control,Third Protocol Selection,Fourth CSL IP Version,Fourth CSL Server IP,Fourth CSL Port Number,Fourth Buffering Time,Fourth UDP Ack Control,Fourth Protocol Selection\n");
				sb.append("").append(",");
				sb.append(CSL_IP_Version).append(",");
				sb.append(CSL_Server_IP).append(",");
				sb.append(CSL_Port_Number).append(",");
				sb.append(Buffering_Time).append(",");
				sb.append(UDP_Ack_Control).append(",");
				sb.append(Protocol_Selection).append(",");
				sb.append(CSL_Report_Control).append(",");
				sb.append(CSL_Encryption_Mask_Mode).append(",");
				sb.append(Second_CSL_IP_Version).append(",");
				sb.append(Second_CSL_Server_IP).append(",");
				sb.append(Second_CSL_Port_Number).append(",");
				sb.append(Second_Buffering_Time).append(",");
				sb.append(Second_UDPAck_Control).append(",");
				sb.append(Second_Protocol_Selection).append(",");
				sb.append(Third_CSL_IP_Version).append(",");
				sb.append(Third_CSL_Server_IP).append(",");
				sb.append(Third_CSL_Port_Number).append(",");
				sb.append(Third_Buffering_Time).append(",");
				sb.append(Third_UDP_Ack_Control).append(",");
				sb.append(Third_Protocol_Selection).append(",");
				sb.append(Fourth_CSL_IP_Version).append(",");
				sb.append(Fourth_CSL_Server_IP).append(",");
				sb.append(Fourth_CSL_Port_Number).append(",");
				sb.append(Fourth_Buffering_Time).append(",");
				sb.append(Fourth_UDP_Ack_Control).append(",");
				sb.append(Fourth_Protocol_Selection).append("\n");
			}else {
			sb.append("NE ID,CSL Server IPv6,CSL Port Number,Second CSL Server IPv6,Second CSL Port Number\n");
			
			sb.append(NE_ID).append(",");
			sb.append(obj.getCslServerIpv6()).append(",");
			sb.append(obj.getCslPortNum()).append(",");
			sb.append(obj.getSecondCslServerIpv6()).append(",");
			sb.append(obj.getSecondCslPortNum()).append("\n");
			}
			//////////////@DSS_INFORMATION//////////////////////////
			sb.append("@DSS_INFORMATION").append("\n");
			sb.append("NE ID,DSS Index,IP Version,gNB IP\n");
			sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("ipv6").append(",");
			sb.append("").append("\n");
			}

		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb;
	}

	private StringBuilder getENBStringForDummyIP(Map<String, String> resultMapForConstants, String ciqFileName,
			String enbId, String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel, String version, String dummy_IP) {
		
		StringBuilder sb = new StringBuilder();
		String sheetName = "";
		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);

		
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		
		try {

			String siteConfigType = "";
			String rsIp = "";
			String lsmName = "";
			String RelVersion = neMappingEntity.getNetworkConfigEntity().getNeRelVersion();

			siteConfigType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())) {
				rsIp = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
				lsmName = neMappingEntity.getNetworkConfigEntity().getNeName();
			}
			//dummy IP
			List<String> objVlanDummy = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("VLAN_ID_Dummy"))
					.map(x -> x.getCiqMap().get("VLAN_ID_Dummy").getHeaderValue()).distinct()
					.collect(Collectors.toList());

			List<String> vlanIPDummy = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("VLAN"))
					.map(x -> x.getCiqMap().get("VLAN").getHeaderValue()).distinct()
					.collect(Collectors.toList());
			
			List<String> ipCheck = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("ENB_OAM_IP_Dummy"))
					.map(x -> x.getCiqMap().get("ENB_OAM_IP_Dummy").getHeaderValue()).distinct()
					.collect(Collectors.toList());
			
			//Double check with respect to Sheetname 
			if(objVlanDummy.isEmpty() && vlanIPDummy.isEmpty() && ipCheck.isEmpty()) {
				listCIQDetailsModel = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, enbId, enbName, dbcollectionFileName, "IPPLAN", "");
				 objVlanDummy = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("VLAN_ID_Dummy"))
						.map(x -> x.getCiqMap().get("VLAN_ID_Dummy").getHeaderValue()).distinct()
						.collect(Collectors.toList());

				 vlanIPDummy = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("VLAN"))
						.map(x -> x.getCiqMap().get("VLAN").getHeaderValue()).distinct()
						.collect(Collectors.toList());
				
				 ipCheck = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)&&null!=x.getCiqMap().get("ENB_OAM_IP_Dummy"))
						.map(x -> x.getCiqMap().get("ENB_OAM_IP_Dummy").getHeaderValue()).distinct()
						.collect(Collectors.toList());
			}
			
			if(!objVlanDummy.isEmpty() && !vlanIPDummy.isEmpty() && !ipCheck.isEmpty()) {
//				ip_present = true;
				System.out.println(enbId+" getENBStringForDummyIP ip present updating true");
				setIpPresent(true);
			// #########@ENB###############
			sb.append("@ENB").append("\n");
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,Customer NE Type,Rack ID,Local Time Offset,CBRS Mode,CBRS Measure Unit,FW Auto Fusing\n");
			}
			else {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,Customer NE Type,Rack ID,Time Offset,CBRS Mode,CBRS User ID,CBRS Measure Unit\n");
			}
			String NE_ID = enbId;
			String NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Type);
			if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0")) { // 22A
				String NE_Version = "22.A.0";

			}else if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0")) { // 22A
				String NE_Version = "22.C.0";

			} else {
				String NE_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Version);
				// RelVersion = "r_0101";
			}
			// String RelVersion =
			// resultMapForConstants.get(Constants.ORAN_VGROW_ENB_RelVersion);
			String Ne_Name = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String Network = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Network);
			//System.out.println(Network);
			String Cascade_ID = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String enb_str = Cascade_ID.substring(0, 3);
			String Group = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Market_CLLI_Code) + "_"
					+ enb_str;
			String Customer_NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Customer_NE_Type);
			String Rack_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Rack_ID);
			String Time_Offset = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Time_Offset);
			String CBRS_Mode = "cbrs-on";
			String CBRS_User_ID = "Samsung";
			String CBRS_Measure_Unit = "10mhz";
			sb.append(NE_ID).append(",");
			sb.append(NE_Type).append(",");
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()).append(",");
			
			sb.append(RelVersion).append(",");
//			if(listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue().equals("TRI") || listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue().equals("CTX")) {
//				String ne=NE_ID.replaceAll("^0+(?!$)", "");
//				if(ne.length()==6)
//					sb.append(ne.substring(0, 3)).append(",");
//				else if(ne.length()==5)
//					sb.append(ne.substring(0, 2)).append(",");
//			}else {
//				sb.append(Group).append(",");
//			}
			sb.append(Network).append(",");
			sb.append("GROW_" + Ne_Name.toUpperCase()).append(",");

			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
			}
			sb.append(Customer_NE_Type).append(",");
			sb.append(Rack_ID).append(",");
			if (neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.A.0") || neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion().equals("22.C.0")) // 22A
				sb.append("0").append(",");
			else
			sb.append(Time_Offset).append(",");
			sb.append(CBRS_Mode).append(",");
			if (version.equals("22.A.0") | version.equals("22.C.0")) { // 22A
				sb.append(CBRS_Measure_Unit).append(",");
				sb.append("on").append("\n");
				
			}
				else {
				sb.append(CBRS_Measure_Unit).append("\n");
			}
			///////////////////// @SERVER_INFORMATION/////////////////////////////////
			sb.append("@SERVER_INFORMATION").append("\n");
			sb.append("NE ID,CFM,PSM,CDP\n");
			if (version.equals("21.A.0")||version.equals("21.B.0")||version.equals("21.C.0")
								|| version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			
			}

			// ##############@SON_INFORMATION##########
			sb.append("@SON_INFORMATION").append("\n");
			if (!(version.equals("22.A.0")) && !(version.equals("22.C.0"))) {
			sb.append(
					"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT 1XRTT NRT,Initial Inter-RAT HRPD NRT,Initial SRS Nrt,Initial SRS Pool Index,Initial Inter-RAT NRT NR\n");
			}else if(version.equals("22.C.0")){
				sb.append(
						"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT HRPD NRT,Initial Inter-RAT NRT NR,Initial ZCZC,Initial PRACH Config Index\n");
					
			}else {
				sb.append(
						"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT HRPD NRT,Initial Inter-RAT NRT NR\n");
					
			}
			String Initial_PCI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_PCI);
			String Initial_RSI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_RSI);
			String Initial_Intra_LTE_NRT = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_Intra_LTE_NRT);
			String Initial_Inter_RAT_1XRTT_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_1XRTT_NRT);
			String Initial_Inter_RAT_HRPD_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_HRPD_NRT);
			String Initial_SRS_Nrt = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Nrt);
			String Initial_SRS_Pool_Index = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Pool_Index);
			String Initial_Inter_RAT_NRT_NR = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_NRT_NR);
			sb.append(NE_ID).append(",");
			sb.append(Initial_PCI).append(",");
			sb.append(Initial_RSI).append(",");
			sb.append(Initial_Intra_LTE_NRT).append(",");
			sb.append(Initial_Inter_RAT_1XRTT_NRT).append(",");
			if (!(version.equals("22.A.0")) && !(version.equals("22.C.0"))) {
				sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
				sb.append(Initial_SRS_Nrt).append(",");
				sb.append(Initial_SRS_Pool_Index).append(",");
				sb.append(Initial_Inter_RAT_NRT_NR).append("\n");
			}else if(version.equals("22.C.0")){
				sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
				sb.append("off").append(",");
				sb.append("off").append("\n");
			} else {
				sb.append(Initial_Inter_RAT_HRPD_NRT).append("\n");
			}

			// ##############@EXTERNAL_LINK_INFORMATION##########

			sb.append("@EXTERNAL_LINK_INFORMATION").append("\n");
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("NE ID,Unit Type,Unit ID,Port Id,VR ID,Admin State,Connect Type,UDE Type,Speed Duplex,MTU\n");
			} else {
				sb.append("NE ID,Unit Type,Unit ID,Port Id,VR ID,Admin State,Connect Type,UDE Type,MTU\n");
			}

			String Unit_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_Type);
			String Unit_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_ID);
			String Port_Id = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Port_Id);
			String VR_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_VR_ID);
			String AdminState = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_AdminState);

			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Port_Id).append(",");
			sb.append(VR_ID).append(",");
			sb.append(AdminState).append(",");
			sb.append("backhaul").append(",");
			sb.append("ude-none").append(",");
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
			}
			sb.append("1500").append("\n");

			// #########@CLOCK_SOURCE_INFORMATION#############
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("@CLOCK_INFORMATION").append("\n");
			} else {
				sb.append("@CLOCK_SOURCE_INFORMATION").append("\n");
			}			
			sb.append("NE ID,ID,Clock Source,Priority Level,Quality Level\n");

			String ID = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_ID);
			String Clock_Source = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Clock_Source);
			String Priority_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Priority_Level);
			String Quality_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Quality_Level);

		
			//ptp column fetch
			
			String primaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "primaryClockSource");
			String secondaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "secondaryClockSource");
			String grandMasterIP = dataByEnode(dbcollectionFileName, sheetName, enbId, "grandMasterIP");
			String PTPHeight = dataByEnode(dbcollectionFileName, sheetName, enbId, "PTPHeight");
			
			if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 1
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append("gps-type").append(",");
				sb.append("1").append(",");
				sb.append("ssu-a").append("\n");
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("ssu-a").append("\n");
			} else if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 2
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("prc").append("\n");
			}  else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 3
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append("gps-type").append(",");
				sb.append("1").append(",");
				sb.append("ssu-a").append("\n");
				sb.append(NE_ID).append(",");
				sb.append("1").append(",");
				sb.append("ieee1588-phasetype").append(",");
				sb.append("2").append(",");
				sb.append("prc").append("\n"); //ptp end
			} else {
				sb.append(NE_ID).append(",");
				sb.append(ID).append(",");
				sb.append(Clock_Source).append(",");
				sb.append(Priority_Level).append(",");
				sb.append(Quality_Level).append("\n");
			}

			// #########@PTP_INFORMATION#############
			sb.append("@PTP_INFORMATION").append("\n");
			sb.append("NE ID,IP Version,First Master IP,Second Master IP,Clock Profile,PTP Domain\n");
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				
				if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 1

					sb.append(NE_ID).append(",");
					sb.append("ipv6").append(",");
					sb.append(grandMasterIP).append(",");
					sb.append("::").append(",");
					sb.append("telecom-2008").append(",");
					sb.append("0").append("\n");
					
				} else if(primaryClockSource.equals("ieee1588-phasetype") && (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 2

					sb.append(NE_ID).append(",");
					sb.append("ipv4").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("itu-g8275-1").append(",");
					sb.append("24").append("\n");
					
				}  else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //scenerio 3
 
					sb.append(NE_ID).append(",");
					sb.append("ipv4").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("0.0.0.0").append(",");
					sb.append("itu-g8275-1").append(",");
					sb.append("24").append("\n");//ptp end
					
				} else {
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append(",");
					sb.append("").append("\n");
				}
			}else {
			sb.append("").append(",");
			sb.append("ipv4").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("telecom-2008").append(",");
			sb.append("").append("\n");
			}
			// ##########@INTER_CONNECTION_INFORMATION#############

			sb.append("@INTER_CONNECTION_INFORMATION").append("\n");
			sb.append("NE ID,Inter Connection Group ID,Inter Connection Switch,Inter Connection Node ID\n");

			String ID1 = resultMapForConstants.get(Constants.ORAN_VGROW_InterConnection_ID);
			String InterConnectionGroupID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionGroupID);
			String InterConnectionSwitch = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionSwitch);
			String InterConnectionNodeID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionNodeID);

			if ( (version.equals("22.A.0") || version.equals("22.C.0")))
				sb.append("").append(",");
			else
				sb.append(NE_ID).append(",");
			if ( (version.equals("22.A.0") || version.equals("22.C.0")))
				sb.append("0").append(",");
			else
			sb.append("").append(",");
			sb.append(InterConnectionSwitch).append(",");
			sb.append(InterConnectionNodeID).append("\n");

			// ###########@INTER_ENB_INFORMATION###################

			sb.append("@INTER_ENB_INFORMATION").append("\n");
			sb.append("NE ID,Inter Node ID,Admin State\n");

			String InterNodeID = "0";
			String AdminState1 = "unlocked";
                        sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("unlocked").append("\n");

			// ###########@SYSTEM_LOCATION_INFORMATION###################

			String User_Def_Mode = "false";
			String Latitude = "N 000:00:00.000";
			String Longitude = "E 000:00:00.000";
			String Height = "0.00m";
	
			String PTPlongitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Long");//ptp columns
			String PTPlatitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Lat");
			
			double Ptplat = Double.valueOf(PTPlatitude);
			double PTPLong = Double.valueOf(PTPlongitude);
			
			//latitude conversion
			StringBuilder PTPsbLat = new StringBuilder();
			String latResult = (Ptplat >= 0)? "N" : "S";
			PTPsbLat.append(latResult);
			PTPsbLat.append(" ");
			
			Double ddNew = Math.abs(Ptplat);
			Double degree=Math.floor(ddNew);
			
			PTPsbLat.append(String.valueOf(String.format("%03d", degree.intValue())));
			PTPsbLat.append(":");
			Double min=Math.floor((ddNew - degree) * 60);
			
			PTPsbLat.append(String.valueOf(String.format("%02d",min.intValue())));
			PTPsbLat.append(":");
			Double valSec = Math.floor((ddNew - degree - min / 60) * 3600 * 1000) / 1000;
			DecimalFormat df = new DecimalFormat("00.000");
			String data=df.format(valSec);
			PTPsbLat.append(data.toString());
			
			//longitude conversion
			String lngResult = (PTPLong >= 0)? "E" : "W";
			StringBuilder PTPsbLong = new StringBuilder();
			
			PTPsbLong.append(lngResult);
			PTPsbLong.append(" ");
			
			ddNew = Math.abs(PTPLong);
			degree=Math.floor(ddNew);
			
			PTPsbLong.append(String.valueOf(String.format("%03d", degree.intValue())));
			PTPsbLong.append(":");
			min=Math.floor((ddNew - degree) * 60);
			
			PTPsbLong.append(String.valueOf(String.format("%02d",min.intValue())));
			PTPsbLong.append(":");
			valSec = Math.floor((ddNew - degree - min / 60) * 3600 * 1000) / 1000;
			df = new DecimalFormat("00.000");
			data=df.format(valSec);
			PTPsbLong.append(data.toString());
			
			sb.append("@SYSTEM_LOCATION_INFORMATION").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");

			if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) {//ptp scenerio 2
				sb.append(NE_ID).append(",");
				sb.append("true").append(",");
				sb.append(PTPsbLat).append(",");
				sb.append(PTPsbLong).append(",");
				sb.append(PTPHeight+"m").append("\n");
			} else if (primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type") &&  (version.equals("22.A.0") || version.equals("22.C.0"))) {//ptp scenerio 3
				sb.append(NE_ID).append(",");
				sb.append("true").append(",");
				sb.append(PTPsbLat).append(",");
				sb.append(PTPsbLong).append(",");
				sb.append(PTPHeight+"m").append("\n");
			}else {
				sb.append(NE_ID).append(",");
				sb.append(User_Def_Mode).append(",");
				sb.append(Latitude).append(",");
				sb.append(Longitude).append(",");
				sb.append(Height).append("\n");
			}

			// ########@MME_INFORMATION##############

			sb.append("@MME_INFORMATION").append("\n");
			sb.append(
					"NE ID,Index,IP Type,IP,Service Purpose,Attach Without PDN Connectivity,CP Optimization,UP Optimization\n");

			int Index;
			String subSheetname = "";
			String market = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
					Constants.VZ_GROW_Market);
			List<CIQDetailsModel> listCIQDetailsModels1 = fileUploadRepository.getEnbTableDetailss(ciqFileName,
					"New England CIQ", null, dbcollectionFileName);
			if (listCIQDetailsModels1 != null && listCIQDetailsModels1.size() > 0) {
				if (sheetName.equalsIgnoreCase(Constants.VZ_GROW_CIQUpstateNY)) {
					subSheetname = "UNY";
				} else {
					subSheetname = "New England";
				}
			} else {
				subSheetname = "New England";
			}
			List<CIQDetailsModel> listCIQDetailsModels = fileUploadRepository.getEnbTableDetailss(ciqFileName,
					"MME IP'S", "UNY", dbcollectionFileName);
			String market1 = market.replaceAll("\\s", "");
			// String[] mme_ip = objGenerateCsvRepository.getVlsmMmeIpDetails(market);
			//String[] mme_ip = dataCiqIndex(listCIQDetailsModels, Constants.VZ_IP_ADDRESS, "MMEIPS");
			String[] mme_ip = dataCiqIndexForMmeip(enbId,listCIQDetailsModels, "MME_IP", "MMEIPS");
			String IPType = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPType);
			String IPV4 = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPV4);
			String ServicePurpose = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_ServicePurpose);
			String AttachWithoutPDNConnectivity = resultMapForConstants
					.get(Constants.ORAN_VGROW_MMEInfo_AttachWithoutPDNConnectivity);
			String CPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_CPOptimization);
			String UPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_UPOptimization);

			for (int i = 0; i < mme_ip.length; i++) {
				if (market1.equals("NewEngland")) {
					Index = i;
				} else {
					Index = i + 4;
				}

				sb.append(NE_ID).append(",");
				sb.append(i).append(",");
				sb.append(IPType).append(",");
				sb.append(CommonUtil.formatIPV6Address(mme_ip[i])).append(",");
				sb.append(ServicePurpose).append(",");
				sb.append(AttachWithoutPDNConnectivity).append(",");
				sb.append("true").append(",");
				sb.append(UPOptimization).append("\n");
			}

			// #######@MAIN_BOARD_INFORMATION############
			sb.append("@MAIN_BOARD_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append("lmd1-j1").append("\n");

			// #######@EXTERNAL_INTERFACE_INFORMATION############

			sb.append("@EXTERNAL_INTERFACE_INFORMATION").append("\n");
			if ( (version.equals("22.A.0") || version.equals("22.C.0"))) {
				sb.append(
						"NE ID,IF Name,IP,Prefix Length,IP Get Type,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,IEEE1588,Smart scheduler\n");
			}else {
			sb.append(
					"NE ID,IF Name,IP,Prefix Length,IP Get Type,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,Bearer M1,Signal M2,IEEE1588,Smart scheduler\n");
			}
			List<String> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue()).distinct()
					.collect(Collectors.toList());
			List<String> vlanIP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> x.getCiqMap().get("VLAN").getHeaderValue()).distinct()
					.collect(Collectors.toList());
			String vlan1 = objVlan.get(0).toString();
			String vlan2 = objVlan.get(1).toString();
			String oam_vlan, s_bVlan;
			
			if (vlanIP.get(0).toString().contains("1")) {
				oam_vlan = vlan1;
				s_bVlan = vlan2;
				
			} else {
				oam_vlan = vlan2;
				s_bVlan = vlan1;
				
			}
			
			String vlan1Dummy;
			String vlan2Dummy;
			String eNB_oamDummy="", eNB_s_b_Dummy = "", IP1_Dummy = "", IP2_Dummy = "";
			String[] IF_Name_Dummy = new String[2] ;
			String[] IP_Dummy = new String[2];
			//if(!objVlanDummy.isEmpty() && !vlanIPDummy.isEmpty()) {
				String oam_vlanDummy , s_bVlanDummy;
				vlan1Dummy = objVlanDummy.get(0).toString();
				vlan2Dummy = objVlan.get(1).toString();

				if (vlanIP.get(0).toString().contains("1")) {
					oam_vlanDummy = vlan1Dummy;
					s_bVlanDummy = vlan2Dummy;

				} else {
					oam_vlanDummy = vlan2Dummy;
					s_bVlanDummy = vlan1Dummy;

				}
				try {
				eNB_oamDummy = listCIQDetailsModel.stream()
				.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
						&& x.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy).getHeaderValue().equals(oam_vlanDummy))
				.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
				.collect(Collectors.joining(""));
				}catch (Exception e) {
					logger.error("Exception eNBString() in GenerateCsvServiceImpl while fetching through Stream  :" + ExceptionUtils.getFullStackTrace(e));
				}
				if( null!=eNB_oamDummy&&eNB_oamDummy.isEmpty()) {
					for(CIQDetailsModel ciqDetails : listCIQDetailsModel) {
						if(ciqDetails.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)) {
							if( null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy)&&!ciqDetails.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy).getHeaderValue().isEmpty()) {
								if(null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy).getHeaderValue() && ciqDetails.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy).getHeaderValue().equals(oam_vlanDummy)) {
									if(!ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue().isEmpty()&&ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()!=null) {
									eNB_oamDummy=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue().toString();
								System.out.println(eNB_oamDummy);
									
								}
							}
						}
					}
					}
				}
				try {
				eNB_s_b_Dummy = listCIQDetailsModel.stream()
				.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
						&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlanDummy))
				.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
				.collect(Collectors.joining(""));
				}catch (Exception e) {
					logger.error("Exception eNBString() in GenerateCsvServiceImpl while fetching through Stream  :" + ExceptionUtils.getFullStackTrace(e));
				}
				if(null!=eNB_s_b_Dummy && eNB_s_b_Dummy.isEmpty())
				for(CIQDetailsModel ciqDetails : listCIQDetailsModel) {
					if(ciqDetails.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)) {
						if( null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN) && !ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().isEmpty()) {
							if(ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlanDummy)) {
								if(null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix)&&!ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue().isEmpty()) {
									eNB_s_b_Dummy=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue().toString();
							System.out.println(eNB_s_b_Dummy);
								
							}
						}
					}
				}
				}

				

				IF_Name_Dummy[0] = "ge_0_0_1." + oam_vlanDummy;
				IF_Name_Dummy[1] = "ge_0_0_1." + s_bVlanDummy;
				
				 
				 try {
				 IP1_Dummy = listCIQDetailsModel.stream()
	                        .filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
	                                && x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
	                        .map(x -> x.getCiqMap().get(Constants.VZ_GROW_ENB_OAM_IP_Dummy).getHeaderValue()).distinct()
	                        .collect(Collectors.joining(""));
				 } catch (Exception e) {
						logger.error("Exception eNBString() in GenerateCsvServiceImpl while fetching through Stream  :" + ExceptionUtils.getFullStackTrace(e));
					}
				 if(null!=IP1_Dummy && IP1_Dummy.isEmpty())
				 {
					 for(CIQDetailsModel ciqDetails : listCIQDetailsModel) {
							if(ciqDetails.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)) {
								if( null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN) && !ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().isEmpty()) {
									if(ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan)) {
										if(null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_ENB_OAM_IP_Dummy)&&!ciqDetails.getCiqMap().get(Constants.VZ_GROW_ENB_OAM_IP_Dummy).getHeaderValue().isEmpty()) {
											IP1_Dummy=ciqDetails.getCiqMap().get(Constants.VZ_GROW_ENB_OAM_IP_Dummy).getHeaderValue().toString();
									System.out.println(IP1_Dummy);
										
									}
								}
							}
						}
						}
				 }
				/*IP1_Dummy = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_VLAN_ID_Dummy).getHeaderValue().equals(oam_vlanDummy))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_ENB_OAM_IP_Dummy).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));*/
				 
				 try {
				IP2_Dummy = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlanDummy))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				 }catch (Exception e) {
						logger.error("Exception eNBString() in GenerateCsvServiceImpl while fetching through Stream  :" + ExceptionUtils.getFullStackTrace(e));
					}
				 if(null!=IP2_Dummy && IP2_Dummy.isEmpty()) {
					 for(CIQDetailsModel ciqDetails : listCIQDetailsModel) {
							if(ciqDetails.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)) {
								if( null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN) && !ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().isEmpty()) {
									if(ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlanDummy)) {
										if(null!=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP)&&!ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue().isEmpty()) {
											IP2_Dummy=ciqDetails.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue().toString();
									System.out.println(IP2_Dummy);
										
									}
								}
							}
						}
						}
				 }
				IP_Dummy [0] = CommonUtil.formatIPV6Address(IP1_Dummy);
				IP_Dummy [1] = CommonUtil.formatIPV6Address(IP2_Dummy);
				
				String[] Management = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Management)
						.split(",");
				String IP_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IP_Version);
				String[] Signal_S1 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Signal_S1)
						.split(",");
				String IEEE1588 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IEEE1588);
				String Smart_scheduler = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Smart_scheduler);
				String[] IF_Name = { "ge_0_0_1." + oam_vlanDummy, "ge_0_0_1." + s_bVlanDummy };
				
				
				if (version.equals("22.A.0") || version.equals("22.C.0")) {
					for (int i = 0; i < IF_Name.length; i++) {

						sb.append(NE_ID).append(",");
						sb.append(IF_Name[i]).append(",");
						sb.append(IP_Dummy[i]).append(",");
						sb.append(eNB_s_b_Dummy).append(",");
						sb.append("static").append(",");
						sb.append(Management[i]).append(",");
						sb.append(Signal_S1[0]).append(",");
						sb.append(Signal_S1[1]).append(",");
						sb.append(Signal_S1[2]).append(",");
						sb.append(Signal_S1[3]).append(",");
						//sb.append(Signal_S1[4]).append(",");
						//sb.append(Signal_S1[5]).append(",");
					if(primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype") 
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //ptp scenerio 1
						sb.append("true").append(",");
					} else if(primaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type")
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //ptp scenerio 2
						sb.append("true").append(",");
					} else if(primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")
							&& (IF_Name[i].contains("301") || IF_Name[i].contains("302")) &&  (version.equals("22.A.0") || version.equals("22.C.0"))) { //ptp scenerio 3
						sb.append("true").append(",");
					}  else {
						sb.append(Signal_S1[6]).append(",");
					}
						sb.append(Smart_scheduler).append("\n");
					}
				}
				//}
			//}
			//dummy IP end
			/*else {

				String eNB_oam = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				String eNB_s_b = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));

				String[] IF_Name = { "ge_0_0_1." + oam_vlan, "ge_0_0_1." + s_bVlan };

				String IP1 = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));
				String IP2 = listCIQDetailsModel.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
								&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
						.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
						.collect(Collectors.joining(""));

				String[] Management = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Management)
						.split(",");
				String IP_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IP_Version);
				String[] Signal_S1 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Signal_S1)
						.split(",");
				String IEEE1588 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IEEE1588);
				String Smart_scheduler = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Smart_scheduler);

				String[] IP = { CommonUtil.formatIPV6Address(IP1), CommonUtil.formatIPV6Address(IP2) };
				// String[] IP = { IP1, IP2 };
				if (version.equals("22.A.0")) {
					for (int i = 0; i < IF_Name.length; i++) {

						sb.append(NE_ID).append(",");
						sb.append(IF_Name[i]).append(",");
						sb.append(IP[i]).append(",");
						sb.append(eNB_s_b).append(",");
						sb.append("static").append(",");
						sb.append(Management[i]).append(",");
						sb.append(Signal_S1[0]).append(",");
						sb.append(Signal_S1[1]).append(",");
						sb.append(Signal_S1[2]).append(",");
						sb.append(Signal_S1[3]).append(",");
						//sb.append(Signal_S1[4]).append(",");
						//sb.append(Signal_S1[5]).append(",");
						sb.append(Signal_S1[6]).append(",");
						sb.append(Smart_scheduler).append("\n");
					}
				}else {

				       for (int i = 0; i < IF_Name.length; i++) {

					        sb.append(NE_ID).append(",");
					        sb.append(IF_Name[i]).append(",");
					        sb.append(IP[i]).append(",");
					        sb.append(eNB_s_b).append(",");
					        sb.append("static").append(",");
					        sb.append(Management[i]).append(",");
					        sb.append(Signal_S1[0]).append(",");
					        sb.append(Signal_S1[1]).append(",");
					        sb.append(Signal_S1[2]).append(",");
					        sb.append(Signal_S1[3]).append(",");
					        sb.append(Signal_S1[4]).append(",");
					        sb.append(Signal_S1[5]).append(",");
					        sb.append(Signal_S1[6]).append(",");
					        sb.append(Smart_scheduler).append("\n");
				       }
				}
			}*/
			


			// ##########@STATIC_ROUTE_INFORMATION##############

			sb.append("@STATIC_ROUTE_INFORMATION").append("\n");
			sb.append("NE ID,VR ID,IP Type,IP Prefix,IP GW,Route Interface Name\n");

			String IPType1 = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_IPType);
			String rsIpVlsm = rsIp + "/128";
			String csl_ip = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_csl_ip);

			String[] IPPrefix = { rsIpVlsm, "0:0:0:0:0:0:0:0/0", "0:0:0:0:0:0:0:0/0", csl_ip };

			String OAM_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String SB_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			OAM_GW_IP = CommonUtil.formatIPV6Address(OAM_GW_IP);
			SB_GW_IP = CommonUtil.formatIPV6Address(SB_GW_IP);

			String[] IPGW = { OAM_GW_IP, OAM_GW_IP, SB_GW_IP, OAM_GW_IP };

			for (int i = 0; i < IPPrefix.length; i++) {
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append(IPType1).append(",");
				sb.append(IPPrefix[i]).append(",");
				sb.append(IPGW[i]).append(",");
				sb.append("-").append("\n");

			}

			// ##########ENB_SCHEDULAR_INFORMATION##############
			if (!(version.equals("22.A.0")) && !(version.equals("22.C.0"))) {
				sb.append("@ENB_SCHEDULAR_INFORMATION").append("\n");
				sb.append("NE ID,RCC ID,Cluster ID,IP Version,Scheduler IP,Scheduler 2nd IP\n");
				sb.append("").append(",");
				sb.append("0").append(",");
				sb.append("").append(",");
				sb.append("ipv6").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
			}
			// ##########@VIRTUAL_ROUTING_INFORMATION##############
			sb.append("@VIRTUAL_ROUTING_INFORMATION").append("\n");
			sb.append("NE ID,VR ID\n");
			
			sb.append(NE_ID).append(",");
			sb.append(VR_ID).append("\n");
			

			// ##########@VLAN_INFORMATION##############
			sb.append("@VLAN_INFORMATION").append("\n");
			sb.append("NE ID,VLAN ID,VR ID,VLAN Interface Name\n");
			
			String[] vlan_id = { oam_vlan, s_bVlan };

//			if(!objVlanDummy.isEmpty() && !vlanIPDummy.isEmpty()) {
				//String oam_vlanDummy , s_bVlanDummy;
//				vlan1Dummy = objVlanDummy.get(0).toString();
//				vlan2Dummy = vlanIPDummy.get(1).toString();
							
/*				if (vlanIP.get(0).toString().contains("1")) {
					oam_vlanDummy = vlan1Dummy;
					s_bVlanDummy = vlan2Dummy;
					
				} else {
					oam_vlanDummy = vlan2Dummy;
					s_bVlanDummy = vlan1Dummy;
					
				}*/
				String[] vlan_id_dummy = {oam_vlanDummy, s_bVlanDummy};
				for (int i = 0; i < 2; i++) {
					sb.append(NE_ID).append(",");
					sb.append(vlan_id_dummy[i]).append(",");
					sb.append("0").append(",");
					sb.append("ge_0_0_1").append("\n");
				}
//			} else {
//				for (int i = 0; i < 2; i++) {
//					sb.append(NE_ID).append(",");
//					sb.append(vlan_id[i]).append(",");
//					sb.append("0").append(",");
//					sb.append("ge_0_0_1").append("\n");
//				}
//			}
			
			// ##########@LAG_INFORMATION##############
			sb.append("@LAG_INFORMATION").append("\n");
			sb.append("NE ID,LAG ID,VR ID,LAG Interface Name\n");
			sb.append("").append(",");
			
			sb.append("1").append(",");
			sb.append("0").append(",");
			
			sb.append("").append("\n");

			//////////////// @IPSEC_INFORMATION///////////////
			sb.append("@IPSEC_INFORMATION").append("\n");
			if (version.equals("22.A.0") || version.equals("22.C.0") ) {
				sb.append(
						"NE ID,VR ID,Interface Name1,Peer IP Version,First Peer IP,Second Peer IP,Inner IP Version,Tunnel Mode,Interface Name2,Interface Name3,Interface Name4,Interface Name5,Interface Name6,Crypto Algorithm,Hash Algorithm,Local ID Type,Local ID\n");
			}
				if (version.equals("22.A.0") || version.equals("22.C.0") ) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");

			}

			////////////// @PKI_INFORMATION//////////////////////////
			sb.append("@PKI_INFORMATION").append("\n");
			sb.append("NE ID,IP Address,FQDN,Port,Path,DN,DN Domain,CA DN,Hash Algorithm\n");
			if (version.equals("22.A.0") || version.equals("22.C.0")) {
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append(",");

				sb.append("").append("\n");

}
			
			if(version.equals("22.A.0") || version.equals("22.C.0")) {//22A
			////////////// @TCE_INFORMATION//////////////////////////
			sb.append("@TCE_INFORMATION").append("\n");
			sb.append("NE ID,TCE List Index,TCE ID,TCE IP Version,TCE IP,TCE Type\n");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("ipv6").append(",");
			sb.append("").append(",");
			sb.append("lsm-embedded").append("\n");
			
			String value = null;
			if (enbId.startsWith("0")) {
				value = enbId.substring(1, 3);
			} else {
				value = enbId.substring(0, 2);
			}
				Ip obj = rep.getip(value);
				if(obj==null) {
					obj=rep.getip(enbId.substring(0, 3));
					if(obj==null) {
						obj=rep.getip("0");
					}
				}
				if (version.equals("22.A.0") || version.equals("22.C.0")) {
					int Delta_OOS_Threshold = 1;
					String MR_Overwrite_Enable = "first-and-last-mr";
					String Single_Measure_Report_Control = "off";
					String EN_DC_CSL_Enable = "on";
					String EN_DC_CSL_Create_Condition = "all";

					sb.append("@CSL_CONTROL_INFORMATION").append("\n");
					sb.append("NE ID,Delta OOS Threshold,MR Overwrite Enable,Single Measure Report Control,EN-DC CSL Enable,EN-DC CSL Create Condition\n");
					sb.append("").append(",");
					sb.append(Delta_OOS_Threshold).append(",");
					sb.append(MR_Overwrite_Enable).append(",");
					sb.append(Single_Measure_Report_Control).append(",");
					sb.append(EN_DC_CSL_Enable).append(",");
					sb.append(EN_DC_CSL_Create_Condition).append("\n");
					
				}	
			//////////////@CSL_INFORMATION//////////////////////////
			sb.append("@CSL_INFORMATION").append("\n");
			if (version.equals("21.D.0") || version.equals("22.A.0") || version.equals("22.C.0")) {
				String CSL_IP_Version="ipv6";
				String CSL_Server_IP="2001:4888:a1f:c223:1b4:1a2:0:3";
				String CSL_Port_Number="50003";
				String Buffering_Time="2";
				String UDP_Ack_Control="r1";
				String Protocol_Selection="1";
				String CSL_Report_Control="on";
				String CSL_Encryption_Mask_Mode="0";
				String Second_CSL_IP_Version="ipv6";
				String Second_CSL_Server_IP="::";
				String Second_CSL_Port_Number="50003";
				String Second_Buffering_Time="2";
				String Second_UDPAck_Control="no-retransmission";
				String Second_Protocol_Selection="1";
				String Third_CSL_IP_Version="ipv6";
				String Third_CSL_Server_IP="2001:4888:a06:2283:f1:fef:0:506";
				String Third_CSL_Port_Number="50001";
				String Third_Buffering_Time="2";
				String Third_UDP_Ack_Control="no-retransmission";
				String Third_Protocol_Selection="0";
				String Fourth_CSL_IP_Version="ipv6";
				String Fourth_CSL_Server_IP="2001:4888:a06:2283:f1:fef:0:506";
				String Fourth_CSL_Port_Number="50001";
				String Fourth_Buffering_Time="2";
				String Fourth_UDP_Ack_Control="no-retransmission";
				//String CSL_Port_Number="";
				String Fourth_Protocol_Selection="0";
				sb.append("NE ID,CSL IP Version,CSL Server IP,CSL Port Number,Buffering Time,UDP Ack Control,Protocol Selection,CSL Report Control,CSL Encryption Mask Mode,Second CSL IP Version,Second CSL Server IP,Second CSL Port Number,Second Buffering Time,Second UDP Ack Control,Second Protocol Selection,Third CSL IP Version,Third CSL Server IP,Third CSL Port Number,Third Buffering Time,Third UDP Ack Control,Third Protocol Selection,Fourth CSL IP Version,Fourth CSL Server IP,Fourth CSL Port Number,Fourth Buffering Time,Fourth UDP Ack Control,Fourth Protocol Selection\n");
				sb.append("").append(",");
				sb.append(CSL_IP_Version).append(",");
				sb.append(CSL_Server_IP).append(",");
				sb.append(CSL_Port_Number).append(",");
				sb.append(Buffering_Time).append(",");
				sb.append(UDP_Ack_Control).append(",");
				sb.append(Protocol_Selection).append(",");
				sb.append(CSL_Report_Control).append(",");
				sb.append(CSL_Encryption_Mask_Mode).append(",");
				sb.append(Second_CSL_IP_Version).append(",");
				sb.append(Second_CSL_Server_IP).append(",");
				sb.append(Second_CSL_Port_Number).append(",");
				sb.append(Second_Buffering_Time).append(",");
				sb.append(Second_UDPAck_Control).append(",");
				sb.append(Second_Protocol_Selection).append(",");
				sb.append(Third_CSL_IP_Version).append(",");
				sb.append(Third_CSL_Server_IP).append(",");
				sb.append(Third_CSL_Port_Number).append(",");
				sb.append(Third_Buffering_Time).append(",");
				sb.append(Third_UDP_Ack_Control).append(",");
				sb.append(Third_Protocol_Selection).append(",");
				sb.append(Fourth_CSL_IP_Version).append(",");
				sb.append(Fourth_CSL_Server_IP).append(",");
				sb.append(Fourth_CSL_Port_Number).append(",");
				sb.append(Fourth_Buffering_Time).append(",");
				sb.append(Fourth_UDP_Ack_Control).append(",");
				sb.append(Fourth_Protocol_Selection).append("\n");
			}else {
			sb.append("NE ID,CSL Server IPv6,CSL Port Number,Second CSL Server IPv6,Second CSL Port Number\n");
			
			sb.append(NE_ID).append(",");
			sb.append(obj.getCslServerIpv6()).append(",");
			sb.append(obj.getCslPortNum()).append(",");
			sb.append(obj.getSecondCslServerIpv6()).append(",");
			sb.append(obj.getSecondCslPortNum()).append("\n");
			}
			//////////////@DSS_INFORMATION//////////////////////////
			sb.append("@DSS_INFORMATION").append("\n");
			sb.append("NE ID,DSS Index,IP Version,gNB IP\n");
			sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("ipv6").append(",");
			sb.append("").append("\n");
			}
			} else {
				System.out.println(enbId+"ip present updating false");
				setIpPresent(false);
//				ip_present = false;
			}
		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb;
		
	}

	private StringBuilder getENBStringForV9(Map<String, String> resultMapForConstants, String ciqFileName, String enbId,
			String enbName, String dbcollectionFileName, NeMappingEntity neMappingEntity,
			List<CIQDetailsModel> listCIQDetailsModel) {

		StringBuilder sb = new StringBuilder();

		String sheetName = "";

		String mkt = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, Constants.VZ_GROW_IPPLAN, enbId,
				Constants.VZ_GROW_Market);

		if (mkt.equals(Constants.VZ_GROW_UNY)) {
			sheetName = Constants.VZ_GROW_CIQUpstateNY;
		} else if (mkt.equals(Constants.VZ_GROW_NE)) {
			sheetName = Constants.VZ_GROW_CIQNewEngland;
		}

		try {

			String siteConfigType = "";
			String rsIp = "";
			String lsmName = "";

			siteConfigType = neMappingEntity.getSiteConfigType();
			if (CommonUtil.isValidObject(neMappingEntity.getNetworkConfigEntity())) {
				rsIp = neMappingEntity.getNetworkConfigEntity().getNeRsIp();
				lsmName = neMappingEntity.getNetworkConfigEntity().getNeName();
			}

			// #########@ENB###############
			sb.append("@ENB").append("\n");
			sb.append(
					"NE ID,NE Type,NE Version,Release Version,Network,NE Name,Customer NE Type,Rack ID,Time Offset,CBRS Mode,CBRS User ID,CBRS Measure Unit\n");

			String NE_ID = enbId;
			String NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Type);
			String NE_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_NE_Version);
			String RelVersion = "r_0101";
			// String RelVersion =
			// resultMapForConstants.get(Constants.ORAN_VGROW_ENB_RelVersion);
			String Ne_Name = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String Cascade_ID = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_eNB_Name);
			String enb_str = Cascade_ID.substring(0, 3);
			String Group = dataByEnode(dbcollectionFileName, sheetName, enbId, Constants.VZ_GROW_Market_CLLI_Code) + "_"
					+ enb_str;
			String Customer_NE_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Customer_NE_Type);
			String Rack_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Rack_ID);
			String Time_Offset = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Time_Offset);
			String CBRS_Mode = "cbrs-on";
			String CBRS_User_ID = "Samsung";
			String CBRS_Measure_Unit = "10mhz";
			sb.append(NE_ID).append(",");
			sb.append(NE_Type).append(",");
			sb.append(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()).append(",");
			sb.append(RelVersion).append(",");
			sb.append(Group).append(",");
			sb.append(Ne_Name.toUpperCase()).append(",");
			sb.append(Customer_NE_Type).append(",");
			sb.append(Rack_ID).append(",");
			sb.append(Time_Offset).append(",");
			sb.append(CBRS_Mode).append(",");
			sb.append(CBRS_User_ID).append(",");
			sb.append(CBRS_Measure_Unit).append("\n");

			// ##############@SON_INFORMATION##########
			sb.append("@SON_INFORMATION").append("\n");
			sb.append(
					"NE ID,Initial PCI,Initial RSI,Initial Intra-LTE NRT,Initial Inter-RAT 1XRTT NRT,Initial Inter-RAT HRPD NRT,Initial SRS Nrt,Initial SRS Pool Index,Initial Inter-RAT NRT NR\n");
			String Initial_PCI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_PCI);
			String Initial_RSI = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_RSI);
			String Initial_Intra_LTE_NRT = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_Intra_LTE_NRT);
			String Initial_Inter_RAT_1XRTT_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_1XRTT_NRT);
			String Initial_Inter_RAT_HRPD_NRT = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_HRPD_NRT);
			String Initial_SRS_Nrt = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Nrt);
			String Initial_SRS_Pool_Index = resultMapForConstants.get(Constants.ORAN_VGROW_ENB_Initial_SRS_Pool_Index);
			String Initial_Inter_RAT_NRT_NR = resultMapForConstants
					.get(Constants.ORAN_VGROW_ENB_Initial_Inter_RAT_NRT_NR);
			sb.append(NE_ID).append(",");
			sb.append(Initial_PCI).append(",");
			sb.append(Initial_RSI).append(",");
			sb.append(Initial_Intra_LTE_NRT).append(",");
			sb.append(Initial_Inter_RAT_1XRTT_NRT).append(",");
			sb.append(Initial_Inter_RAT_HRPD_NRT).append(",");
			sb.append(Initial_SRS_Nrt).append(",");
			sb.append(Initial_SRS_Pool_Index).append(",");
			sb.append(Initial_Inter_RAT_NRT_NR).append("\n");

			// ##############@EXTERNAL_LINK_INFORMATION##########

			sb.append("@EXTERNAL_LINK_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Port Id,VR ID,Admin State,Connect Type,UDE Type,MTU\n");

			String Unit_Type = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_Type);
			String Unit_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Unit_ID);
			String Port_Id = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_Port_Id);
			String VR_ID = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_VR_ID);
			String AdminState = resultMapForConstants.get(Constants.ORAN_VGROW_ExtLinkInfo_AdminState);

			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append(Port_Id).append(",");
			sb.append(VR_ID).append(",");
			sb.append(AdminState).append(",");
			sb.append("backhaul").append(",");
			sb.append("ude-none").append(",");
			sb.append("1500").append("\n");

			// #########@CLOCK_SOURCE_INFORMATION#############

			sb.append("@CLOCK_SOURCE_INFORMATION").append("\n");
			sb.append("NE ID,ID,Clock Source,Priority Level,Quality Level\n");

			String ID = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_ID);
			String Clock_Source = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Clock_Source);
			String Priority_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Priority_Level);
			String Quality_Level = resultMapForConstants.get(Constants.ORAN_VGROW_Clock_Quality_Level);

			sb.append(NE_ID).append(",");
			sb.append(ID).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priority_Level).append(",");
			sb.append(Quality_Level).append("\n");

			// #########@PTP_INFORMATION#############
			sb.append("@PTP_INFORMATION").append("\n");
			sb.append("NE ID,IP Version,First Master IP,Second Master IP,Clock Profile,PTP Domain\n");
			sb.append("").append(",");
			sb.append("ipv4").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("0.0.0.0").append(",");
			sb.append("telecom-2008").append(",");
			sb.append("").append("\n");

			// ##########@INTER_CONNECTION_INFORMATION#############

			sb.append("@INTER_CONNECTION_INFORMATION").append("\n");
			sb.append("NE ID,Inter Connection Group ID,Inter Connection Switch,Inter Connection Node ID\n");

			String ID1 = resultMapForConstants.get(Constants.ORAN_VGROW_InterConnection_ID);
			String InterConnectionGroupID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionGroupID);
			String InterConnectionSwitch = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionSwitch);
			String InterConnectionNodeID = resultMapForConstants
					.get(Constants.ORAN_VGROW_InterConnection_InterConnectionNodeID);

			sb.append(NE_ID).append(",");
			sb.append("").append(",");
			sb.append(InterConnectionSwitch).append(",");
			sb.append(InterConnectionNodeID).append("\n");

			// ###########@INTER_ENB_INFORMATION###################

			sb.append("@INTER_ENB_INFORMATION").append("\n");
			sb.append("NE ID,Inter Node ID,Admin State\n");

			String InterNodeID = "0";
			String AdminState1 = "unlocked";

			sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("unlocked").append("\n");

			// ###########@SYSTEM_LOCATION_INFORMATION###################

			String User_Def_Mode = "false";
			String Latitude = "N 000:00:00.000";
			String Longitude = "E 000:00:00.000";
			String Height = "0.00m";

			sb.append("@SYSTEM_LOCATION_INFORMATION").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");

			sb.append(NE_ID).append(",");
			sb.append(User_Def_Mode).append(",");
			sb.append(Latitude).append(",");
			sb.append(Longitude).append(",");
			sb.append(Height).append("\n");

			// ########@MME_INFORMATION##############

			sb.append("@MME_INFORMATION").append("\n");
			sb.append(
					"NE ID,Index,IP Type,IP,Service Purpose,Attach Without PDN Connectivity,CP Optimization,UP Optimization\n");

			int Index;

			String market = fileUploadRepository.getEnBDataByPath(dbcollectionFileName, sheetName, enbId,
					Constants.VZ_GROW_Market);
			String market1 = market.replaceAll("\\s", "");
			String[] mme_ip = objGenerateCsvRepository.getVlsmMmeIpDetails(market);

			String IPType = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPType);
			String IPV4 = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_IPV4);
			String ServicePurpose = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_ServicePurpose);
			String AttachWithoutPDNConnectivity = resultMapForConstants
					.get(Constants.ORAN_VGROW_MMEInfo_AttachWithoutPDNConnectivity);
			String CPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_CPOptimization);
			String UPOptimization = resultMapForConstants.get(Constants.ORAN_VGROW_MMEInfo_UPOptimization);

			for (int i = 0; i < mme_ip.length; i++) {
				if (market1.equals("NewEngland")) {
					Index = i;
				} else {
					Index = i + 4;
				}

				sb.append(NE_ID).append(",");
				sb.append(Index).append(",");
				sb.append(IPType).append(",");
				sb.append(CommonUtil.formatIPV6Address(mme_ip[i])).append(",");
				sb.append(ServicePurpose).append(",");
				sb.append(AttachWithoutPDNConnectivity).append(",");
				sb.append("true").append(",");
				sb.append(UPOptimization).append("\n");
			}

			// #######@MAIN_BOARD_INFORMATION############
			sb.append("@MAIN_BOARD_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
			sb.append(NE_ID).append(",");
			sb.append(Unit_Type).append(",");
			sb.append(Unit_ID).append(",");
			sb.append("lmd1-j1").append("\n");

			// #######@EXTERNAL_INTERFACE_INFORMATION############

			sb.append("@EXTERNAL_INTERFACE_INFORMATION").append("\n");
			sb.append(
					"NE ID,IF Name,IP,Prefix Length,Management,Signal S1,Signal X2,Bearer S1,Bearer X2,Bearer M1,Signal M2,IEEE1588,Smart scheduler\n");

			List<Integer> objVlan = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());
			List<Integer> vlanIP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
					.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
					.collect(Collectors.toList());
			
			List<CIQDetailsModel> listCiq = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)).collect(Collectors.toList());
			
			String oam_vlan, s_bVlan,vlan1,vlan2;
			if (listCiq.size() == 0) {
				
				List<CIQDetailsModel> vplanIpSheet = fileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId,
						dbcollectionFileName, "IPPLAN", "eNBId");
				objVlan = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				vlanIP = vplanIpSheet.stream()
						.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN))
						.map(x -> Integer.valueOf(x.getCiqMap().get("VLAN").getHeaderValue())).distinct()
						.collect(Collectors.toList());
				vlan1 = objVlan.get(0).toString();
				vlan2 = objVlan.get(1).toString();
			}else {
				vlan1 = objVlan.get(0).toString();
				vlan2 = objVlan.get(1).toString();
			}
			
			System.out.println("OAM" + " " + vlan1);
			System.out.println("OAM" + " " + vlan2);

			
			System.out.println("VLANID " + vlanIP.get(0).toString());

			if (vlanIP.get(0).toString().contains("2")) {
				oam_vlan = vlan1;
				s_bVlan = vlan2;
				System.out.println("OAM inside if" + " " + oam_vlan);
				System.out.println("sb inside if" + " " + s_bVlan);
			} else {
				oam_vlan = vlan2;
				s_bVlan = vlan1;
				System.out.println("OAM inside else" + " " + oam_vlan);
				System.out.println("sb inside else" + " " + s_bVlan);
			}

			/*
			 * List<Integer>
			 * objVlanStrings=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().
			 * equals(Constants.VZ_GROW_IPPLAN)).map(x->Integer.valueOf(x.getCiqMap().get(
			 * "VLAN").getHeaderValue())).sorted().collect(Collectors.toList()); int
			 * vlan_min ; int vlam_max ;
			 * 
			 * if(objVlanStrings!=null) { vlan_min = objVlanStrings.get(0); vlam_max =
			 * objVlanStrings.get(1); }else { vlan_min = 0; vlam_max = 0; }
			 * 
			 * String
			 * oam_vlan=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().equals(
			 * Constants.VZ_GROW_IPPLAN) &&
			 * x.getCiqMap().get("VLAN").getHeaderValue().equals(String.valueOf(vlan_min))).
			 * map(x->x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue()).collect(Collectors
			 * .joining("")); String
			 * s_bVlan=listCIQDetailsModel.stream().filter(x->x.getSheetAliasName().equals(
			 * Constants.VZ_GROW_IPPLAN) &&
			 * x.getCiqMap().get("VLAN").getHeaderValue().equals(String.valueOf(vlam_max))).
			 * map(x->x.getCiqMap().get("eNB_OAM_VLAN").getHeaderValue()).collect(Collectors
			 * .joining(""));
			 */

			String eNB_oam = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String eNB_s_b = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_SB_VLAN_prefix).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			String[] IF_Name = { "ge_0_0_1." + oam_vlan, "ge_0_0_1." + s_bVlan };

			String IP1 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String IP2 = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_IP_eNB_SB_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			String[] Management = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Management)
					.split(",");
			String IP_Version = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IP_Version);
			String[] Signal_S1 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Signal_S1)
					.split(",");
			String IEEE1588 = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_IEEE1588);
			String Smart_scheduler = resultMapForConstants.get(Constants.ORAN_VGROW_ExternalInterfaces_Smart_scheduler);

			String[] IP = { CommonUtil.formatIPV6Address(IP1), CommonUtil.formatIPV6Address(IP2) };
			// String[] IP = { IP1, IP2 };

			for (int i = 0; i < IF_Name.length; i++) {

				sb.append(NE_ID).append(",");
				sb.append(IF_Name[i]).append(",");
				sb.append(IP[i]).append(",");
				sb.append(eNB_s_b).append(",");
				sb.append(Management[i]).append(",");
				sb.append(Signal_S1[0]).append(",");
				sb.append(Signal_S1[1]).append(",");
				sb.append(Signal_S1[2]).append(",");
				sb.append(Signal_S1[3]).append(",");
				sb.append(Signal_S1[4]).append(",");
				sb.append(Signal_S1[5]).append(",");
				sb.append(Signal_S1[6]).append(",");
				sb.append(Smart_scheduler).append("\n");
			}

			// ##########@STATIC_ROUTE_INFORMATION##############

			sb.append("@STATIC_ROUTE_INFORMATION").append("\n");
			sb.append("NE ID,VR ID,IP Type,IP Prefix,IP GW\n");

			String IPType1 = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_IPType);
			String rsIpVlsm = rsIp + "/128";
			String csl_ip = resultMapForConstants.get(Constants.ORAN_VGROW_StaticRoute_csl_ip);

			String[] IPPrefix = { rsIpVlsm, "0:0:0:0:0:0:0:0/0", "0:0:0:0:0:0:0:0/0", csl_ip };

			String OAM_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(oam_vlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));
			String SB_GW_IP = listCIQDetailsModel.stream()
					.filter(x -> x.getSheetAliasName().equals(Constants.VZ_GROW_IPPLAN)
							&& x.getCiqMap().get(Constants.VZ_GROW_eNB_OAM_VLAN).getHeaderValue().equals(s_bVlan))
					.map(x -> x.getCiqMap().get(Constants.VZ_GROW_Gateway_IP).getHeaderValue()).distinct()
					.collect(Collectors.joining(""));

			OAM_GW_IP = CommonUtil.formatIPV6Address(OAM_GW_IP);
			SB_GW_IP = CommonUtil.formatIPV6Address(SB_GW_IP);

			String[] IPGW = { OAM_GW_IP, OAM_GW_IP, SB_GW_IP, OAM_GW_IP };

			for (int i = 0; i < IPPrefix.length; i++) {
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append(IPType1).append(",");
				sb.append(IPPrefix[i]).append(",");
				sb.append(IPGW[i]).append("\n");

			}

			// ##########@ENB_SCHEDULAR_INFORMATION##############
			sb.append("@ENB_SCHEDULAR_INFORMATION").append("\n");
			sb.append("NE ID,RCC ID,Cluster ID,IP Version,Scheduler IP,Scheduler 2nd IP\n");
			sb.append("").append(",");
			sb.append("0").append(",");
			sb.append("").append(",");
			sb.append("ipv6").append(",");
			sb.append("").append(",");
			sb.append("").append("\n");

			// ##########@VIRTUAL_ROUTING_INFORMATION##############
			sb.append("@VIRTUAL_ROUTING_INFORMATION").append("\n");
			sb.append("NE ID,VR ID\n");
			sb.append(NE_ID).append(",");
			sb.append(VR_ID).append("\n");

			// ##########@VLAN_INFORMATION##############
			sb.append("@VLAN_INFORMATION").append("\n");
			sb.append("NE ID,VLAN ID,VR ID,VLAN Interface Name\n");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append(",");
			sb.append("").append("\n");

			// ##########@LAG_INFORMATION##############
			sb.append("@LAG_INFORMATION").append("\n");
			sb.append("NE ID,LAG ID,VR ID,LAG Interface Name\n");
			sb.append("").append(",");
			sb.append("1").append(",");
			sb.append("0").append(",");
			sb.append("").append("\n");

		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return sb;
	}

	@Override
	public JSONObject csvFileGeneration(String ciqFileName, String enbId, String enbName, Integer programId)
			throws IOException {
		StringBuilder commPath = new StringBuilder();
		JSONObject fileGenerateResult = new JSONObject();
		StringBuilder sreader = new StringBuilder();
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		commPath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH)).append(Constants.CUSTOMER)
				.append(Constants.SEPARATOR).append(programId)
				.append(Constants.PRE_MIGRATION_CSV
						.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
						.replace("enbId", enbId).replaceAll(" ", "_"));
		StringBuilder enbGrowPath = new StringBuilder();
		StringBuilder cellGrowPath = new StringBuilder();
		enbGrowPath.append(commPath).append("GROW_ENB_" + enbName.replaceAll(" ", "_") + dateString + ".csv");
		cellGrowPath.append(commPath).append("GROW_CELL_" + enbName.replaceAll(" ", "_") + dateString + ".csv");
		StringBuilder mergeFileName = new StringBuilder();
		String mfileName = "pnp_macro_indoor_dist_" + enbName.replaceAll(" ", "_") + dateString + ".csv";
		mergeFileName.append(commPath).append(mfileName);
		File enbPath = new File(enbGrowPath.toString());
		File cellPath = new File(cellGrowPath.toString());
		if(!cellPath.exists()) {
			logger.error(enbId+"***enbId-GROW_CELL_file does not exit*** "+cellGrowPath);
		}else {
			logger.error(enbId+"***enbId-GROW_CELL_file_exit*** "+cellGrowPath);
		}
		File mergePath = new File(mergeFileName.toString());
		BufferedReader breader1 = new BufferedReader(new FileReader(enbPath));
		BufferedReader breader2 = new BufferedReader(new FileReader(cellPath));
		String reader = breader1.readLine();
		while (reader != null) {
			String str = reader + "\n";
			sreader.append(str);
			reader = breader1.readLine();
		}
		String reader2 = breader2.readLine();
		while (reader2 != null) {
			String str = reader2 + "\n";
			sreader.append(str);
			reader2 = breader2.readLine();
		}
		FileWriter fileWriter = new FileWriter(mergePath.toString());
		fileWriter.write(sreader.toString());
		fileWriter.close();
		fileGenerateResult.put("fileName", mfileName);
		return fileGenerateResult;

	}

	@Override
	public List<CIQDetailsModel> getCIQDetailsModelList(String enbId, String dbcollectionFileName) {
		List<CIQDetailsModel> resultList = null;
		Query query = new Query();
		query.addCriteria(Criteria.where("eNBId").is(enbId));
		try {
			resultList = mongoTemplate.find(query, CIQDetailsModel.class, dbcollectionFileName);

		} catch (Exception e) {
			logger.error(
					"Exception getCIQDetailsModelList() in RunTestServiceImpl :" + ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public String[] dataCiqIndexs(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowDspCellIndex,
			String vzGrowCiqupstateny) {
		String[] result = null;
		try {
			List<String> objCellIdList = listCIQDetailsModel.stream()
					.filter(X -> X.getSheetAliasName().equals(vzGrowCiqupstateny))
					.map(X -> X.getCiqMap().get(vzGrowDspCellIndex).getHeaderValue()).collect(Collectors.toList());
			if (objCellIdList != null && objCellIdList.size() > 0) {
				result = new String[objCellIdList.size()];

				result = objCellIdList.toArray(result);
			}
		} catch (Exception e) {
			logger.error("Exception dataCiqIndex() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return result;
	}

	@Override
	public JSONObject offsetFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation) {
		boolean status = false;
		String timestamp = null;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilders = new StringBuilder();

		String routeFileNames = "";
		String routeFileName = "";

		String sheetName = "";
		String enb = enbId;
		StringBuilder temp = new StringBuilder();
		temp.append(tempFolder);

		try {
			String version = neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion()
					.toString();
			if (version.contains("19")) {
				version = "19A";
			}
			if (version.contains("20.A")) {
				version = "20A";
			}
			if (version.contains("20.B")) {
				version = "20B";
			}
			
			if (version.contains("20.C") || version.contains("21.A") 
					|| version.contains("21.B") || version.contains("21.C") || version.contains("21.D") 
					|| version.contains("22.A") || version.contains("22.C")) {
				version = "20C";
			}
				temp.setLength(0);
				temp.append(filePath);				
				fileBuilders.setLength(0);
				fileBuilders.append(filePath);
				//fileBuilders.append(routeFileNames);
				List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
						enbName, dbcollectionFileName);
				String CC0_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC0_Offset").getHeaderValue();
				String CC1_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC1_Offset").getHeaderValue();
				String CC2_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC2_Offset").getHeaderValue();
				String CC3_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC3_Offset").getHeaderValue();
				String CC4_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC4_Offset").getHeaderValue();
				String CC5_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC5_Offset").getHeaderValue();
				String CC6_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC6_Offset").getHeaderValue();
				String CC7_Offset = listCIQDetailsModel.get(0).getCiqMap().get("CC7_Offset").getHeaderValue();
				Integer value=(CC0_Offset.isEmpty()||CC0_Offset.contains("TBD"))?0:((CC1_Offset.isEmpty()||CC1_Offset.contains("TBD"))?1:(CC2_Offset.isEmpty()||CC2_Offset.contains("TBD"))?2:(CC3_Offset.isEmpty()||CC3_Offset.contains("TBD"))?3:(CC4_Offset.isEmpty()||CC4_Offset.contains("TBD"))?4:(CC5_Offset.isEmpty()||CC5_Offset.contains("TBD"))?5:(CC6_Offset.isEmpty()||CC6_Offset.contains("TBD"))?6:(CC7_Offset.isEmpty()||CC7_Offset.contains("TBD"))?7:8);

				String auType="";
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
				{
					auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
				}
				routeFileName = "AU_SSB_offset_"+auType+"_" + Integer.toString(value)+"CC_"+ enbId + "_" + timeStamp + ".xml";
				fileBuilders.append(routeFileName);
				status=getRouteFiles(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
						fileBuilders.toString(), routeFileName, temp.toString(), date, validation,version);
			

		} catch (Exception e) {
			logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", routeFileName);

		}
		return fileGenerateResult;
	}

	@Override
	public JSONObject tilt(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation) {
		boolean status = false;
		String timestamp = null;
		JSONObject fileGenerateResult = new JSONObject();
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String dateString = "_" + new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
		StringBuilder fileBuilders = new StringBuilder();

		String routeFileNames = "";
		String sheetName = "";
		String enb = enbId;
		StringBuilder temp = new StringBuilder();
		temp.append(tempFolder);

		try {
			
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			String auType="";
			if(listCIQDetailsModel.get(0).getCiqMap().containsKey("AU_Type") && StringUtils.isNotEmpty(listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue()))
			{
				auType=listCIQDetailsModel.get(0).getCiqMap().get("AU_Type").getHeaderValue();
			}
			
				routeFileNames = "AU_tilt_" +auType+"_"+ enb + "_" + timeStamp + ".xml";
				temp.setLength(0);
				temp.append(filePath);				
				fileBuilders.setLength(0);
				fileBuilders.append(filePath);
				fileBuilders.append(routeFileNames);
				
				status=gettiltfiles(ciqFileName, enbId, enbName, dbcollectionFileName, listCIQDetailsModel,
						fileBuilders.toString(), routeFileNames, temp.toString(), date, validation);
			

		} catch (Exception e) {
			logger.error("routeFileGeneration() GenerateCsvServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", routeFileNames);

		}
		return fileGenerateResult;
	}

	private boolean gettiltfiles(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			List<CIQDetailsModel> listCIQDetailsModel, String filepath, String routeFileName, String temp, String date,
			String validation) {
		boolean status = false;
		try {
			String retValue = listCIQDetailsModel.get(0).getCiqMap().get("AU_RET").getHeaderValue();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element nc_rpc = doc.createElementNS("urn:ietf:params:xml:ns:netconf:base:1.0", "nc:rpc");
			// nc_rpc.setAttribute("xmlns:nc", "urn:ietf:params:xml:ns:netconf:base:1.0");
			doc.appendChild(nc_rpc);

			Element nc_edit_config = doc.createElement("nc:edit-config");
			nc_rpc.appendChild(nc_edit_config);

			Element nc_target = doc.createElement("nc:target");
			nc_edit_config.appendChild(nc_target);

			Element running = doc.createElement("nc:running");
			nc_target.appendChild(running);
			//running.setTextContent(" ");

			Element nc_default_operation = doc.createElement("nc:default-operation");
			nc_edit_config.appendChild(nc_default_operation);
			nc_default_operation.appendChild(doc.createTextNode("none"));

			Element nc_config = doc.createElement("nc:config");
			nc_edit_config.appendChild(nc_config);

			Element gnbcp_managed_element = doc.createElementNS(
					"http://www.samsung.com/global/business/5GvRAN/ns/gnb-au", "gnbau:managed-element");
			nc_config.appendChild(gnbcp_managed_element);

			Element gnb_cu_cp_function = doc.createElement("gnbau:hardware-management");
			gnbcp_managed_element.appendChild(gnb_cu_cp_function);

			Element gnb_cu_cp_function_cell = doc.createElement("gnbau:radio-unit");
			gnb_cu_cp_function.appendChild(gnb_cu_cp_function_cell);
			

			Element ssb_configuration = doc.createElement("gnbau:radio-unit-info");
			ssb_configuration.setAttribute("nc:operation", "merge");
			gnb_cu_cp_function_cell.appendChild(ssb_configuration);
			Element freq_offset = doc.createElement("gnbau:electrical-tilt");
			ssb_configuration.appendChild(freq_offset);
			freq_offset.setTextContent(retValue);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);

			if (validation.equals("RanConfig")) {
				File xmlFile1 = new File(temp + "Test");
				xmlFile1.mkdir();
				StreamResult result1 = new StreamResult(xmlFile1 + "/AU_tilt.xml");

				try {
					transformer.transform(source, result1);
					String xmlFile3 = xmlFile1.toString() + "/AU_tilt.xml";
					File xmlFile2 = new File(xmlFile3);
					writeXMLattribute(xmlFile2);
					status = true;

				} catch (Exception ex) {
					logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
				}

			}
			File xmlFile = new File(filepath);
			StreamResult result = new StreamResult(xmlFile);
			// StreamResult result = new StreamResult(new
			// File("/home/user/RCT/rctsoftware/Customer/39/PreMigration/Output/VZW_5GNR_CIQ_UPNY_AllMarkets_070920_v0.1/AU/07000110001/AU_ROUTE/AU_10001.xml"));
			try {
				transformer.transform(source, result);
			} catch (Exception ex) {
				logger.error("getRouteFile() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(ex));
			}
			writeXMLattribute(xmlFile);
			status = true;
		} catch (Exception e) {
			logger.error("csvFileGeneration() getComisionScriptFile" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	private String getReleaseVersion(Integer programId, NeMappingEntity neMappingEntity, String releasever) {
		String relver = releasever;
		try {
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(programId,
					Constants.RELEASE_VERSION_TEMPLATE);
			if(CommonUtil.isValidObject(programTemplateEntity) && StringUtils.isNotEmpty(programTemplateEntity.getValue())) {
				JSONObject objData = (JSONObject) new JSONParser().parse(programTemplateEntity.getValue());
				if(objData.containsKey(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion())) {
					Map relverMap = (Map)objData.get(neMappingEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion());
					relver = relverMap.get("ALL").toString();
				}
			}
		} catch (Exception e) {
			logger.error("getReleaseVersion() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return relver;
	}
private StringBuilder getENBStringForFsuV21(String ciqFileName, String neVersion, String enbId, String enbName,
			String dbcollectionFileName, List<CIQDetailsModel> listCIQDetailsModel) {


		StringBuilder sb = new StringBuilder();
		String sheetName = "";
		try {

			/////////////////////////// @FSU/////////////////////////////////
			
				sb.append("@FSU").append("\n");
			if (neVersion.equals("22.C.0")) {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,Administrative State,Local Time Offset,NE Serial Number,FW Auto Fusing\n");
			} else {
				sb.append(
						"NE ID,NE Type,NE Version,Release Version,Network,NE Name,GPL Version,AdministrativeState,Local Time Offset,NE Serial Number,FW Auto Fusing\n");
			}
				String NE_ID = enbId;
				String NE_TYPE = "c_fsu";
				String NE_Version = neVersion;
				String Release_Version = "r_0101";
				List<AuditConstantsEntity> auditConstantsList = auditConstantsRepository.getAuditConstantsEntityList(AuditConstants.PROGRAMNAME_4G_FSU, AuditConstants.FSU_RELEASE_VERSION);
				
				if(!ObjectUtils.isEmpty(auditConstantsList) && (neVersion.equals("21.B.0") || neVersion.equals("21.C.0") || neVersion.equals("21.D.0")
						|| neVersion.equals("22.A.0") ||  neVersion.equals("22.C.0"))) {
					Release_Version = auditConstantsList.get(0).getParameterValue();
				}
				if(listCIQDetailsModel.get(0).getCiqMap().containsKey("release_version")) {
					Release_Version = listCIQDetailsModel.get(0).getCiqMap().get("release_version").getHeaderValue().trim();
				}
				String Network = listCIQDetailsModel.get(0).getCiqMap().get("Network").getHeaderValue();
				String NE_Name = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
				NE_Name = NE_Name.trim().replaceAll(" ", "_");
				String AdministrativeState = "unlocked";
				String Local_Time_Off = "0";
				String NE_Serial_Number = "";
				sb.append(NE_ID).append(",");
				sb.append(NE_TYPE).append(",");
				sb.append(NE_Version).append(",");
				sb.append(Release_Version).append(",");
				sb.append(Network).append(",");
				sb.append("GROW_").append(NE_Name).append(",");
				sb.append("0").append(",");
				sb.append(AdministrativeState).append(",");
				sb.append(Local_Time_Off).append(",");
				sb.append(NE_Serial_Number).append(",");
				sb.append("on").append("\n");

				sb.append("@SERVER_INFORMATION").append("\n");
				sb.append("NE ID,CFM,PSM\n");
				sb.append("").append(",");
				sb.append("").append(",");
				sb.append("").append("\n");
				/////////////////////// @DIGITAL_UNIT_INFORMATION/////////////////////// 
				sb.append("@DIGITAL_UNIT_INFORMATION").append("\n");
				
				String boardType = listCIQDetailsModel.get(0).getCiqMap().get("BoardType").getHeaderValue();
				String UnitType = listCIQDetailsModel.get(0).getCiqMap().get("Unit_Type").getHeaderValue();
				if(neVersion.equals("22.A.0") || neVersion.equals("22.C.0")) {
					sb.append("NE ID,Unit Type,Unit ID,Board Type,Firmware Type\n");
					sb.append(NE_ID).append(",");
					sb.append(UnitType).append(",");
					sb.append("0").append(",");
					sb.append(boardType).append(",");
					sb.append("").append("\n");
				} else {
					sb.append("NE ID,Unit Type,Unit ID,Board Type\n");
					sb.append(NE_ID).append(",");
					sb.append(UnitType).append(",");
					sb.append("0").append(",");
					sb.append(boardType).append("\n");
				}
				
			
			/////////////////////// @CLOCK_INFORMATION////////////////////////////
			sb.append("@CLOCK_INFORMATION").append("\n");
			sb.append("NE ID,Clock Source ID,Clock Source,Priority Level,Quality Level\n");
			String Clock_Source_ID = "0";
			String Clock_Source = "gps-type";
			String Priorit_Level = "1";
			String Quality_Level = "dnu";
			sb.append(NE_ID).append(",");
			sb.append(Clock_Source_ID).append(",");
			sb.append(Clock_Source).append(",");
			sb.append(Priorit_Level).append(",");
			sb.append(Quality_Level).append("\n");
			
			// #########@PTP_INFORMATION#############
						sb.append("@PTP_INFORMATION").append("\n");
						sb.append("NE ID,IP Version,First Master IP,Second Master IP,Clock Profile,PTP Domain\n");
					
						sb.append("").append(",");
						sb.append("").append(",");
						sb.append("").append(",");
						sb.append("").append(",");
						sb.append("").append(",");
						sb.append("").append("\n");
						
						// #########@PTP_INFORMATION#############
						sb.append("@PORT_INFORMATION").append("\n");
						sb.append("NE ID,Port ID,VR ID,Port Administrative State,Connect Type,UDE Type,MTU\n");
						String Port_ID = "0";
						String Port_Administrative_State = "unlocked";
						String Connect_Type = "backhaul";
						String UDE_Type = "ude-none";
						String MTU = "1500";
						sb.append(NE_ID).append(",");
						sb.append("0").append(",");
						sb.append("0").append(",");
						sb.append(Port_Administrative_State).append(",");
						sb.append(Connect_Type).append(",");
						sb.append(UDE_Type).append(",");
						sb.append(MTU).append("\n");
	/////////////////////// @VIRTUAL_ROUTING_INFORMATION////////////////////////////
						
					sb.append("@VIRTUAL_ROUTING_INFORMATION").append("\n");
					sb.append("NE ID,VR ID\n");
					sb.append(NE_ID).append(",");
					sb.append("0").append("\n");
									
						
			
			
			///////////////////// @IP_INFORMATION////////////////////////////////////////////
			sb.append("@IP_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,External Interface Name,IP Address,IP Prefix Length,IP Get Type,Management,IEEE1588\n");
			String Cpu_ID = "0";
			String External_Interface_Name = "ge_0_0_0.410";
			// String IP_Address = "2001:4888:2a18:303c:101:406:0:0";
			String IP_Address = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Mgmt_IPv6_Address").getHeaderValue();
			String IP_Prefix_Length = "64";
			String IP_Get_Type = "static";
			String Management = "true";
			sb.append(NE_ID).append(",");
			sb.append(Cpu_ID).append(",");
			sb.append(External_Interface_Name).append(",");
			sb.append(IP_Address).append(",");
			sb.append(IP_Prefix_Length).append(",");
			sb.append(IP_Get_Type).append(",");
			sb.append(Management).append(",");
			sb.append("false").append("\n");

			///////////////////// @VLAN_INFORMATION/////////////////////////////////
			sb.append("@VLAN_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,VLAN Interface Name,VLAN ID,VR ID\n");
			String VLAN_Interface_Name = "ge_0_0_0";
			String VLAN_ID = listCIQDetailsModel.get(0).getCiqMap().get("VLAN_ID").getHeaderValue();
			sb.append(NE_ID).append(",");
			sb.append(Cpu_ID).append(",");
			sb.append(VLAN_Interface_Name).append(",");
			sb.append(VLAN_ID).append(",");
			sb.append("0").append("\n");
			///////////////////////// @ROUTE_INFORMATION///////////////////////////////
			sb.append("@ROUTE_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,VR ID,IP Prefix,IP Prefix Length,IP Gateway\n");
			String IP_Prefix = "::";
			// String IP_Gateway = "2001:4888:2a18:33e4:101:2a0:0:0";
			String IP_Gateway = listCIQDetailsModel.get(0).getCiqMap().get("FSU_Gateway_IP_Address").getHeaderValue();
			String ipprefixlength = "0";
			sb.append(NE_ID).append(",");
			sb.append(Cpu_ID).append(",");
			sb.append("0").append(",");
			sb.append(IP_Prefix).append(",");
			sb.append(ipprefixlength).append(",");
			sb.append(IP_Gateway).append("\n");
			//////////////////////////////////////// @SYSTEM_LOCATION_INFORMATION/////////////////////////////
			sb.append("@SYSTEM_LOCATION_INFORMATION").append("\n");
			sb.append("NE ID,User Defined Mode,Latitude,Longitude,Height\n");
			String User_Defined_Mode = "false";
			String Latitude = "N 000:00:00.000";
			String Longitude = "E 000:00:00.000";
			String Height = null;
			if (neVersion.equals("19.A.0")) {
				Height = "000.00m";
			} else {
				Height = "0000.00m";
			}
			sb.append(NE_ID).append(",");
			sb.append(User_Defined_Mode).append(",");
			sb.append(Latitude).append(",");
			sb.append(Longitude).append(",");
			sb.append(Height).append("\n");
			////////////////////////////// @CPRI_PORT_INFORMATION//////////////////////////////////
			sb.append("@CPRI_PORT_INFORMATION").append("\n");
			sb.append("NE ID,RU PortID,Connected DU PortID,Group ID,DU Port Mode\n");

			//List<CIQDetailsModel> data = fileUploadRepository.getEnBData(dbcollectionFileName, "FSUCIQ", enbId);
			for (CIQDetailsModel info : listCIQDetailsModel) {
				String portMode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();
				
					if (!portMode.equalsIgnoreCase("Not Used")) {
						String ruport = info.getCiqMap().get("RU_PortID").getHeaderValue();
						String connectedduportid = info.getCiqMap().get("Connected_DU_PortID").getHeaderValue();
						String duportmode = info.getCiqMap().get("DU_Port_Mode").getHeaderValue();
						//adding group id explicitly
						String groupid = info.getCiqMap().get("Group_ID").getHeaderValue();
						sb.append(NE_ID).append(",");
						sb.append(ruport).append(",");
						sb.append(connectedduportid).append(",");
						//sb.append(connectedduportid).append(",");
						sb.append(groupid).append(",");
						sb.append("pass-through").append("\n");
					}
			}

			///////////////////////// @INTER_CONNECTION_INFORMATION//////////////////////////

			sb.append("@INTER_CONNECTION_INFORMATION").append("\n");
			sb.append("NE ID,Inter Connection Group ID,Inter Connection Switch,Inter Connection Node ID\n");
			
			String Inter_Connection_Group_ID = listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name").getHeaderValue();

				String Inter_Connection_Switch = "inter-connection-on";
				String Inter_Connection_Node_ID = "31";
				sb.append(NE_ID).append(",");
				sb.append(Inter_Connection_Group_ID).append(",");
				sb.append(Inter_Connection_Switch).append(",");
				sb.append(Inter_Connection_Node_ID).append("\n");
			

			/////////////////////////// @VDU_INFORMATION///////////////////////////////
			sb.append("@VDU_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Port ID,IP Address\n");

			String unit_id = "0";
			String port_id = "0";
			String ipaddress = "fd00:4888:2A:10:0:406::0";
			sb.append(NE_ID).append(",");

				String unittype = listCIQDetailsModel.get(0).getCiqMap().get("Unit_Type")
						.getHeaderValue();
				sb.append(unittype).append(",");
			sb.append(unit_id).append(",");
			sb.append(port_id).append(",");
			sb.append(ipaddress).append("\n");

			////////////////////////// @ECPRI_PORT_INFORMATION//////////////////////////////
			sb.append("@ECPRI_PORT_INFORMATION").append("\n");
			sb.append("NE ID,Unit Type,Unit ID,Port ID,Port Administrative State,Configured Speed,Fec Mode\n");
			
				
				String unit_id1 = "0";
				String port_id1 = "0";
				String port_administrative_state = "unlocked";
				String configured_speed = "s25g";
				String fecmode = "rs-fec";
				sb.append(NE_ID).append(",");
				sb.append(unittype).append(",");
				sb.append(unit_id1).append(",");
				sb.append(port_id1).append(",");
				sb.append(port_administrative_state).append(",");
				sb.append(configured_speed).append(",");
				sb.append(fecmode).append("\n");
			

			/////////////////// @ECPRI_TRAFFIC_PLANE_INFORMATION/////////////////////
			sb.append("@ECPRI_TRAFFIC_PLANE_INFORMATION").append("\n");
			sb.append("NE ID,CPU ID,Ecpri Interface Name,Vlan ID,MTU,Management,Control/User,IEEE1588,SYNCE\n");
			String cpuid = listCIQDetailsModel.get(0).getCiqMap().get("CPU_ID")
					.getHeaderValue();
			for(int i=0;i<4;i++) {
				sb.append(NE_ID).append(",");
				sb.append(cpuid).append(",");
				if(unittype.equalsIgnoreCase("fsip"))
					sb.append("fh_0_1_0_"+i).append(",");
				else if(unittype.equalsIgnoreCase("fsup"))
					sb.append("fh_0_0_0_"+i).append(",");
				if(i==0) {
					sb.append("").append(",");
					sb.append("1500").append(",");
					sb.append("true").append(",");
					sb.append("false").append(",");
					sb.append("true").append(",");
					sb.append("true").append("\n");
				}
				else if(i==1) {
					sb.append("950").append(",");
				sb.append("9000").append(",");
				sb.append("false").append(",");
				sb.append("true").append(",");
				sb.append("false").append(",");
				sb.append("false").append("\n");
				}
				else {
				sb.append("").append(",");
				sb.append("1500").append(",");
				sb.append("false").append(",");
				sb.append("false").append(",");
				sb.append("false").append(",");
				sb.append("false").append("\n");
				}
}

			/////////////////////// @ECPRI_IP_INFORMATION////////////////////////
			sb.append("@ECPRI_IP_INFORMATION").append("\n");
			String ip_prefixlength = "";
			String Ecpri_Interface_Name=listCIQDetailsModel.get(0).getCiqMap().get("Ecpri_Interface_Name")
					.getHeaderValue();
			if(neVersion.equals("22.A.0") || neVersion.equals("22.C.0")) {
				sb.append("NE ID,CPU ID,Ecpri Interface Name,IP Address,IP Prefix Length,ORU IP Range Enable,First IP\n");
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append(Ecpri_Interface_Name).append(",");
				sb.append("fd00:4888:2A:10:0:406::1").append(",");
				sb.append("64").append(",");
				sb.append("true").append(",");
				sb.append("fd00:4888:2a:10:0:406:0:2").append("\n");
			} else {
				sb.append("NE ID,CPU ID,Ecpri Interface Name,IP Address,IP Prefix Length\n");
				sb.append(NE_ID).append(",");
				sb.append("0").append(",");
				sb.append(Ecpri_Interface_Name).append(",");
				sb.append("fd00:4888:2A:10:0:406::1").append(",");
				sb.append("64").append("\n");
			}
			
			
				
						

			

			
			

		} catch (Exception e) {
			logger.error("Exception eNBString() in GenerateCsvServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return sb;

	}
@Override
	public JSONObject cpriFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, NeMappingEntity neMappingEntity, String remarks) {
		StringBuilder sb = new StringBuilder();
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String cpriFilename = "";
		try {
			List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
					enbName, dbcollectionFileName);
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			cpriFilename = "LCC_CPRI_" + enbName + dateString + ".csv";
			sb.append("NE ID,Cell ID,Band Name,CPRI Port,Channel Card,RU Port\n");
			for (CIQDetailsModel ciq : listCIQDetailsModel) {
				if(ciq.getSheetName().equals("Upstate NY CIQ")) {
				sb.append(ciq.getCiqMap().get("Samsung_eNB_ID").getHeaderValue()).append(",");
				sb.append(ciq.getCiqMap().get("Cell_ID").getHeaderValue()).append(",");
				sb.append(ciq.getCiqMap().get("BandName").getHeaderValue()).append(",");
				sb.append(ciq.getCiqMap().get("CRPIPortID").getHeaderValue()).append(",");
				sb.append(ciq.getCiqMap().get("lCCCard").getHeaderValue()).append(",");
				sb.append(ciq.getCiqMap().get("RU_port").getHeaderValue()).append("\n");
				}

			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + cpriFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGeneration() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", cpriFilename);

		}
		return fileGenerateResult;

	}

@Override
public boolean checkEmptyValues(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowCiqupstateny) {
	boolean check=true;
	String[] mct= dataCiqIndex(listCIQDetailsModel, "mcType", Constants.VZ_GROW_CIQUpstateNY);
	String[] cell_index_dsp = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_CELL_INDEX, Constants.VZ_GROW_CIQUpstateNY);
	String[] dsp_id = dataCiqIndex(listCIQDetailsModel, Constants.VZ_GROW_DSP_ID, Constants.VZ_GROW_CIQUpstateNY);
	String[] pracformat= dataCiqIndex(listCIQDetailsModel, "prachConfigIndex", Constants.VZ_GROW_CIQUpstateNY);
	for(String mctType :mct) {
		if(mctType.isEmpty()) {
			check=false;
		}
	}
	for(String cell_index :cell_index_dsp) {
		if(cell_index.isEmpty()) {
			check=false;
		}
	}
	for(String dsp :dsp_id) {
		if(dsp.isEmpty()) {
			check=false;
		}
	}
	for(String prac :pracformat) {
		if(prac.isEmpty()) {
			check=false;
		}
	}
	return check;
}

	private boolean validateIP(String ip) {
		//boolean result =  false;
		return InetAddressValidator.getInstance().isValid(ip);
		
	}
	
	public StringBuffer checkEmptyValuesForPTP(String dbcollectionFileName,String enbId) {
		boolean check=true;
		StringBuffer ptpCheck = new StringBuffer();
		String sheetName = Constants.VZ_GROW_CIQUpstateNY;
		String PTPlongitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Long");//ptp columns		
		String PTPlatitude = dataByEnode(dbcollectionFileName, sheetName, enbId, "Lat");
		String PTPHeight = dataByEnode(dbcollectionFileName, sheetName, enbId, "PTPHeight");
		String grandMasterIP = dataByEnode(dbcollectionFileName, sheetName, enbId, "grandMasterIP");
		String primaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "primaryClockSource");
		String secondaryClockSource = dataByEnode(dbcollectionFileName, sheetName, enbId, "secondaryClockSource");
		
		String info = "";
		
//		if(primaryClockSource.isEmpty() && secondaryClockSource.isEmpty()) {
//			info = "Primary Clock Source and Secondary Clock Source";
//		}
		Boolean ipCheck = false;
		if(!grandMasterIP.isEmpty()) {
			ipCheck = validateIP(grandMasterIP);
		}

		if(!primaryClockSource.isEmpty()) {
			if(!primaryClockSource.equals("ieee1588-phasetype") && !primaryClockSource.equals("gps-type")) {
				info = "Primary Clock Source";
			}
		}
		if(primaryClockSource.isEmpty() && !secondaryClockSource.isEmpty()) {
			
			info = "Primary Clock Source";
		}

		if(!secondaryClockSource.isEmpty()) {
			if(!secondaryClockSource.equals("ieee1588-phasetype") && !secondaryClockSource.equals("gps-type"))
			info = "Secondary Clock Source";
		}
		if(!ipCheck && primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype")) {//scenario 1
			info = "Data in GrandMasterIP";
		}
		
		if(grandMasterIP.isEmpty() && primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype")) {//scenario 1
			info = "GrandMasterIP";
		}
		if(PTPHeight.isEmpty() && PTPlatitude.isEmpty() && PTPlongitude.isEmpty() 
				&& primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Longitude, Latitude and Height";
		
		}
		if(PTPHeight.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Height";
		
		}
		if(PTPHeight.isEmpty() && PTPlatitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Height and Latitude";
		
		}
		if(PTPHeight.isEmpty() && PTPlongitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Longitude and Height";
		
		}
		if(PTPlatitude.isEmpty() && PTPlongitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Longitude and Latitude";
		
		}
		if(PTPlatitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info = "Latitude";
		
		}
		if(PTPHeight.isEmpty() && PTPlatitude.isEmpty() && PTPlongitude.isEmpty() 
				&& primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.isEmpty()) {//scenario 2
			info="Longitude, Latitude and Height";
		
		}
		if(PTPlongitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info = "Longitude";
		}	
		if(PTPlatitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info = "latitude";
				
		}
		if(PTPHeight.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info = "Height";		
		}
		if(PTPlongitude.isEmpty() && PTPHeight.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info = "Longitude and Height";
		
		}
		if(PTPlongitude.isEmpty() && PTPlatitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info="Longitude and Latitude";
		
		}
		if(PTPHeight.isEmpty() && PTPlatitude.isEmpty() && primaryClockSource.equals("ieee1588-phasetype") && secondaryClockSource.equals("gps-type")) {//scenario 3
			info = "Height and Latitude";
		
		}

		
//		if(PTPlongitude.isEmpty() && grandMasterIP.isEmpty() && primaryClockSource.equals("gps-type") && secondaryClockSource.equals("ieee1588-phasetype")) {
//			info="Longitude and Grand Master IP";
	//	
//		}
//		if(PTPHeight.isEmpty() && grandMasterIP.isEmpty() && primaryClockSource.isEmpty() && secondaryClockSource.isEmpty()) {
//			info = "Primary Clock Source, Secondary Clock Source, Height and Grand Master IP";
	//	
//		}
		ptpCheck.append(info);
		return ptpCheck;
	}

@Override
public boolean checkEnbExistence(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowCiqupstateny) {
	boolean check=true;
	String[] mct= dataCiqIndex(listCIQDetailsModel, "mcType", Constants.VZ_GROW_CIQUpstateNY);
	if(mct==null) {
		check= false;
	}
	return check;
}

	@Override
	public JSONObject cpriFileGenerationFSU(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, NeMappingEntity neMappingEntity, String remarks, String fsuType,List<CIQDetailsModel> listCIQDetailsModel) {
		
		StringBuilder sb = new StringBuilder();
		JSONObject fileGenerateResult = new JSONObject();
		boolean status = false;
		StringBuilder fileBuilder = new StringBuilder();
		String cpriFilename = "";
		try {
			
			fileBuilder.setLength(0);
			fileBuilder.append(filePath);
			String dateString = "_"
					+ new SimpleDateFormat("MMddyyyy").format(new Timestamp(System.currentTimeMillis()));
			cpriFilename = "LCC_CPRI_" + enbName + dateString + ".csv";
			sb.append("FSU NE ID,FSU NE TYPE,ENB ID,RU PortID,Connected DU PortID,DU Port Mode,LCC Card,LCC Card Port,LCC Card Port (2nd CPRI)\n");
			for (CIQDetailsModel ciq : listCIQDetailsModel) {
				String fsuNeId = enbId.replaceAll("^0+(?!$)", "");
				String eNBID = "";
				String fsuNeType = "FSU10";
				String ruPortId = "";
				String connectedduportID = "";
				String duPortMode = "";
				String lccCard = "";
				String lccCardPort = "";
				String lccCartPort2 = "";
				
				if(ciq.getCiqMap().containsKey("eNB_Name")) {
					eNBID = ciq.getCiqMap().get("eNB_Name").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("Unit_Type")) {
					if(ciq.getCiqMap().get("Unit_Type").getHeaderValue().trim().equalsIgnoreCase("fsup")) {
						fsuNeType = "FSU20";
					}
				}
				if(ciq.getCiqMap().containsKey("RU_PortID")) {
					ruPortId = ciq.getCiqMap().get("RU_PortID").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("Connected_DU_PortID")) {
					connectedduportID = ciq.getCiqMap().get("Connected_DU_PortID").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("DU_Port_Mode")) {
					duPortMode = ciq.getCiqMap().get("DU_Port_Mode").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("lcc_card_no")) {
					lccCard = ciq.getCiqMap().get("lcc_card_no").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("lcc_card_port_no")) {
					lccCardPort = ciq.getCiqMap().get("lcc_card_port_no").getHeaderValue().trim();
				}
				if(ciq.getCiqMap().containsKey("lcc_card_port_no_2")) {
					lccCartPort2 = ciq.getCiqMap().get("lcc_card_port_no_2").getHeaderValue().trim();
				}
				sb.append(fsuNeId).append(",");
				sb.append(fsuNeType).append(",");
				sb.append(eNBID).append(",");
				sb.append(ruPortId).append(",");
				sb.append(connectedduportID).append(",");
				sb.append(duPortMode).append(",");
				sb.append(lccCard).append(",");
				sb.append(lccCardPort).append(",");
				sb.append(lccCartPort2).append("\n");

			}
			if (CommonUtil.isValidObject(sb)) {

				FileWriter fileWriter = new FileWriter(fileBuilder.toString() + cpriFilename);
				BufferedWriter bw = null;
				bw = new BufferedWriter(fileWriter);
				try {
					bw.write(sb.toString());
					sb.delete(0, sb.length());
					status = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bw.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("GenerateCsvServiceImpl.cpriFileGenerationFSU() " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			fileGenerateResult.put("status", status);
			fileGenerateResult.put("fileName", cpriFilename);

		}
		return fileGenerateResult;
	

	}
	
	@Override
	public JSONObject csvGenerationCBand(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = cBandGrowTemplate.generateGrowTemplate(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"vDUpnp", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationCBand() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject csvGenerationCBandCell(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = cBandGrowTemplate.generateGrowTemplateForCell(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"vDUcell", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationCBand() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject csvGenerationCBandADPF(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = cBandGrowTemplate.generateGrowTemplateForADPF(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"vDUgrow", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationCBand() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject csvGenerationDSS(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = dSSGrowTemplate.generateGrowTemplate(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"pnpGrow", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationDSS() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject csvGenerationDSSAu(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = dSSGrowTemplateAu.generateGrowTemplate(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"vDUCellGrow", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationDSS() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject csvGenerationDSSAuPf(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks) {
		JSONObject result = new JSONObject();
		try {
			result = dSSGrowTemplateAuPf.generateGrowTemplate(ciqFileName, enbId, enbName, dbcollectionFileName, programId, filePath, sessionId,"vDUGrow", neMappingEntity, remarks);
		} catch(Exception e) {
			logger.error("GenerateCsvServiceImpl.csvGenerationDSS() " + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public JSONObject envUploadToOVFromPremigration(JSONObject ovUpdateJson, GenerateInfoAuditEntity GenerateInfoAuditEntity1) {
		JSONObject result = new JSONObject();
		try {
			String neId=ovUpdateJson.get("enbId").toString();
			JSONObject trakerIdDetails  = PreMigrationToOV.getTrakerIdList(ovUpdateJson, GenerateInfoAuditEntity1);
			String TrackerID="";
			String WorkplanID="";
			String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss'");
			if (trakerIdDetails != null && trakerIdDetails.containsKey("trakerjson")) {
				List<TrackerDetailsModel> trakerdetails = (List<TrackerDetailsModel>) trakerIdDetails
						.get("trakerjson");
				if (!ObjectUtils.isEmpty(trakerdetails)) 
				{
									for (TrackerDetailsModel locTrackerDetailsModel : trakerdetails)
									{
										TrackerID=locTrackerDetailsModel.getTrackerId();
										logger.error("TrackerID:" + TrackerID);
									}
									ovUpdateJson.put("TrackerID",TrackerID);
									JSONObject updateJsonAPI = PreMigrationToOV.getOvEnvUploadDetails(ovUpdateJson, GenerateInfoAuditEntity1);
								
				}else
				{	
					
				
					PremigrationOvUpadteEntity premigrationOvUpadteEntity= new PremigrationOvUpadteEntity();
					premigrationOvUpadteEntity.setCurrentResult("["+date2+"]"+"-"+"failed to fetch the Tracker ID/ No Tracker ID on OV for: "+neId);
					GenerateInfoAuditEntity generateAudEntity = runTestRepository.getGenerateInfoAuditEntityEntity(GenerateInfoAuditEntity1.getId());
					premigrationOvUpadteEntity.setGenerateAudEntity(generateAudEntity);
					premigrationOvUpadteEntity.setFileName(ovUpdateJson.get("FileName").toString());
					premigrationOvUpadteEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					runTestRepository.updatePreMigrationOv(premigrationOvUpadteEntity);
					GenerateInfoAuditEntity1.setOvUpdateStatus("Failure");
					updateGeneratedFileDetails(GenerateInfoAuditEntity1);
					//runTestRepository.updateGeneratedFileDetails(runTestEntityMap.get(neId));
					
					
					logger.error("fail in 1nd Api" );
				}
				}else
				{
					 date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
					 System.out.println(date2);
					 PremigrationOvUpadteEntity premigrationOvUpadteEntity= new PremigrationOvUpadteEntity();
					if(trakerIdDetails.containsKey("reason")) {
					String reason=trakerIdDetails.get("reason").toString();
					premigrationOvUpadteEntity.setCurrentResult("["+date2+"]"+"-"+reason);
					}else {
						premigrationOvUpadteEntity.setCurrentResult("["+date2+"]"+"-"+"failed to fetch the Tracker ID/ No Tracker ID on OV for: "+neId);
					}
				
					//ovTestResultEntity.setCurrentResult("failed to fetch the Tracker ID");
					GenerateInfoAuditEntity generateAudEntity = runTestRepository.getGenerateInfoAuditEntityEntity(GenerateInfoAuditEntity1.getId());
					premigrationOvUpadteEntity.setGenerateAudEntity(generateAudEntity);
					premigrationOvUpadteEntity.setFileName(ovUpdateJson.get("FileName").toString());
					premigrationOvUpadteEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					runTestRepository.updatePreMigrationOv(premigrationOvUpadteEntity);
					GenerateInfoAuditEntity1.setOvUpdateStatus("Failure");
					
					//runTestEntityMap.get(neId).setOvUpdateReason("failed to fetch the Tracker ID " );
					
					updateGeneratedFileDetails(GenerateInfoAuditEntity1);
					
					logger.error("fail in 1nd Api" );
				}
			
		} catch(Exception e) {
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateENV from PreMigration API" + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	
}
