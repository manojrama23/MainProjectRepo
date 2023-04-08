package com.smart.rct.postmigration.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.common.models.SiteReportEnbModel;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.postmigration.dto.SiteDataDto;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.postmigration.repository.ReportsRepository;
import com.smart.rct.postmigration.service.SiteDataService;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.EmailUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class SiteDataController {

	final static Logger logger = LoggerFactory.getLogger(SiteDataController.class);

	@Autowired
	SiteDataService siteDataService;

	@Autowired
	SiteDataDto siteDataDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	FileUploadService fileUploadService;

	@Autowired
	EmailUtil emailUtil;

	@Autowired
	GenerateCsvService objGenerateCsvService;
	
	@Autowired
	NeMappingService neMappingService;
	
	@Autowired
	ReportsRepository reportsRepository;

	/**
	 * This api will get CSV Audit Details
	 * 
	 * @param siteDataDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@PostMapping(value = Constants.GET_SITE_DATA_DETAILS)
	public JSONObject getSiteDataDetails(@RequestBody JSONObject siteDataDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String searchStatus = null;
		Integer programId = null;
		String programName = null;
		List<SiteDataEntity> siteDataEntities = null;
		List<SiteDataModel> siteDataModels = null;
		SiteDataModel siteDataModel = null;
		try {
			sessionId = siteDataDetails.get("sessionId").toString();
			serviceToken = siteDataDetails.get("serviceToken").toString();
			searchStatus = siteDataDetails.get("searchStatus").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			programId = (Integer) siteDataDetails.get("programId");
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
			resultMap.put("searchStartDate", startDate);
			resultMap.put("searchEndDate", curdate);
		/*	Map<String, Integer> paginationData = (Map<String, Integer>) siteDataDetails.get("pagination");
			int count = paginationData.get("count");
			int page = paginationData.get("page");*/

			
			
			Map<String, Object> paginationData = (Map<String, Object>) siteDataDetails.get("pagination");
			int count;
			int page = (Integer) paginationData.get("page");
			if (paginationData.get("count") instanceof String) {
				count = Integer.parseInt((String) paginationData.get("count"));
			} else {
				count = (Integer) paginationData.get("count");
			}
			if (Constants.LOAD.equals(searchStatus)) {
				siteDataModel = new SiteDataModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				siteDataModel.setProgramDetailsEntity(programDetailsEntity);
				siteDataModel.setSearchStartDate(startDate);
				siteDataModel.setSearchEndDate(curdate);
			}
			if (Constants.SEARCH.equals(searchStatus)) {
				siteDataModel = new Gson().fromJson(
						siteDataDetails.toJSONString((Map) siteDataDetails.get("searchCriteria")), SiteDataModel.class);
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setProgramName(programName);
				programDetailsEntity.setId(programId);
				siteDataModel.setProgramDetailsEntity(programDetailsEntity);
			}
			Map<String, Object> siteDataList = siteDataService.getSiteDataDetails(siteDataModel, page, count);
			resultMap.put("pageCount", siteDataList.get("paginationcount"));
			resultMap.put("ciqList", siteDataList.get("ciqList"));
			resultMap.put("fileList", siteDataList.get("fileList"));
			resultMap.put("neNameList", siteDataList.get("neNameList"));
			siteDataEntities = (List<SiteDataEntity>) siteDataList.get("siteDataList");
			if (CommonUtil.isValidObject(siteDataEntities)) {
				siteDataModels = new ArrayList<SiteDataModel>();
				for (SiteDataEntity siteDataEntity : siteDataEntities) {
					siteDataModels.add(siteDataDto.getSiteDataDetailsModel(siteDataEntity));
				}
			}
			resultMap.put("siteDataModels", siteDataModels);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error(
					"Exception in SiteDataController.getSiteDataDetails(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_SITE_DATA_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This method will Add Site Config Details to DB
	 * 
	 * @param packDataDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.PACK_ENB_DATA, method = RequestMethod.POST)
	public JSONObject packEnbData(@RequestBody JSONObject packDataDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String ciqFileName = null;
		String enbName = null;
		String enbId = null;
		Integer programId = null;
		Boolean statuss;
		ReportsModel reports = new ReportsModel();
		List<ReportsModel> reportsModel = new ArrayList<>();
		
		ReportsEntity reportsEntity = null;

		String remarks = null;
		String timeStamp = new SimpleDateFormat("MMddyyyy_HH_mm_ss").format(new Timestamp(System.currentTimeMillis()));
		String siteDataZipFilePath = "";
		String siteDataFolderName = "";
		try {
			sessionId = packDataDetails.get("sessionId").toString();
			serviceToken = packDataDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			ciqFileName = packDataDetails.get("ciqFileName").toString();
			
			programId = Integer.parseInt(packDataDetails.get("programId").toString());
			remarks = packDataDetails.get("remarks").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			ObjectMapper objMapper = new ObjectMapper();
			String data = CommonUtil.convertObjectToJson(packDataDetails);
			JsonObject objData = CommonUtil.parseRequestDataToJson(data);
			List<SiteReportEnbModel> enbDetailsList = objMapper.readValue(
					objData.get("enbDetails").toString(), new TypeReference<List<SiteReportEnbModel>>() {
					});
			for(SiteReportEnbModel objSiteReportEnbModel:enbDetailsList)
			{
				enbName = objSiteReportEnbModel.getEnbName();
				enbId = objSiteReportEnbModel.getEnbId();
				StringBuilder siteDataZipFileFolderPath = new StringBuilder();
				StringBuilder siteDataPreMigrationPath = new StringBuilder();
				StringBuilder siteDataPreMigrationOutputPath = new StringBuilder();
				StringBuilder siteDataMigrationOutputPath = new StringBuilder();
				StringBuilder siteDataPostMigrationOutputPath = new StringBuilder();
				StringBuilder siteDataSavePath = new StringBuilder();
				StringBuilder preMigrationuploadPath = new StringBuilder();
				StringBuilder ciqUploadPath = new StringBuilder();
				StringBuilder scriptUploadPath = new StringBuilder();
				StringBuilder checklistUploadPath = new StringBuilder();
			
			siteDataFolderName = Constants.SITE_DATA + "_" + enbName + "_" + timeStamp;
			siteDataSavePath.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR).append(Constants.SITE_DATA);
			siteDataZipFileFolderPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR).append(Constants.SITE_DATA).append(Constants.SEPARATOR)
					.append(siteDataFolderName);
			File zipFileDir = new File(siteDataZipFileFolderPath.toString());
			if (!zipFileDir.exists()) {
				FileUtils.forceMkdir(zipFileDir); // Creating Sitedata Directory
			}
			preMigrationuploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR);
			ciqUploadPath.append(preMigrationuploadPath)
					.append(Constants.PRE_MIGRATION_CIQ
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));
			File preMigCIQDir = new File(ciqUploadPath.toString());
			File zipFileCIQDir = new File(
					siteDataZipFileFolderPath.toString() + Constants.SEPARATOR + preMigCIQDir.getName());
			if (!zipFileCIQDir.exists()) {
				FileUtils.forceMkdir(zipFileCIQDir); // Creating Sitedata CIQ Directory
			}
			if (preMigCIQDir.exists() && zipFileCIQDir.exists()) {
				FileUtils.copyDirectory(preMigCIQDir, zipFileCIQDir); // Copying Pre Migration CIQ File To Sitedata
																		// Directory
			}
			scriptUploadPath.append(preMigrationuploadPath)
					.append(Constants.PRE_MIGRATION_SCRIPT
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));
			File preMigScriptDir = new File(scriptUploadPath.toString() + enbId);

			File zipFileScriptDir = new File(siteDataZipFileFolderPath.toString() + Constants.SEPARATOR + "SCRIPT"
					+ Constants.SEPARATOR + preMigScriptDir.getName());
			if (!zipFileScriptDir.exists()) {
				FileUtils.forceMkdir(zipFileScriptDir); // Creating Sitedata Script Directory
			}
			if (preMigScriptDir.exists() && zipFileScriptDir.exists()) {
				FileUtils.copyDirectory(preMigScriptDir, zipFileScriptDir); // Copying Pre Migration Script Files To
																			// Sitedata Directory
			}
			checklistUploadPath.append(preMigrationuploadPath)
					.append(Constants.PRE_MIGRATION_CHECKLIST
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replaceAll(" ", "_"));
			checklistUploadPath.deleteCharAt(checklistUploadPath.length() - 1);
			File preMigCheckListDir = new File(checklistUploadPath.toString());
			File zipFileCheckListDir = new File(
					siteDataZipFileFolderPath.toString() + Constants.SEPARATOR + preMigCheckListDir.getName());
			if (!zipFileCheckListDir.exists()) {
				FileUtils.forceMkdir(zipFileCheckListDir); // Creating Sitedata CheckList Directory
			}
			if (preMigCheckListDir.exists() && zipFileCheckListDir.exists()) {
				FileUtils.copyDirectory(preMigCheckListDir, zipFileCheckListDir); // Copying Pre Migration Check List
																					// File To Sitedata Directory
			}
			siteDataPreMigrationPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SITE_DATA_PRE_MIGRATION_PATH
							.replace("filename", StringUtils.substringBeforeLast(ciqFileName.toString(), "."))
							.replace("enbId", enbId).replaceAll(" ", "_"));

			File preMigSiteDataDir = new File(siteDataPreMigrationPath.toString());
			if (preMigSiteDataDir.exists() && zipFileDir.exists()) {
				FileUtils.copyDirectory(preMigSiteDataDir, zipFileDir); // Copying Pre Migration Output Files To
																		// Sitedata Directory
			}
			siteDataPreMigrationOutputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.PREMIGRATION_OUTPUT_PATH.replace("enbId", enbId).replaceAll(" ", "_"));

			File mig1SiteDataDir = new File(siteDataPreMigrationOutputPath.toString());
			if (mig1SiteDataDir.exists() && zipFileDir.exists()) {
				FileUtils.copyDirectory(mig1SiteDataDir, zipFileDir); // Copying Pre Migration Output Files Uploaded in
																		// Migration To Sitedata Directory
			}
			siteDataMigrationOutputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.MIGRATION_OUTPUT_PATH.replace("enbId", enbId).replaceAll(" ", "_"));

			File mig2SiteDataDir = new File(siteDataMigrationOutputPath.toString());
			if (mig2SiteDataDir.exists() && zipFileDir.exists()) {
				FileUtils.copyDirectory(mig2SiteDataDir, zipFileDir); // Copying Migration Output Files To Sitedata
																		// Directory
			}
			siteDataPostMigrationOutputPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.POSTMIGRATION_OUTPUT_PATH.replace("enbId", enbId).replaceAll(" ", "_"));

			File mig3SiteDataDir = new File(siteDataPostMigrationOutputPath.toString());
			if (mig3SiteDataDir.exists() && zipFileDir.exists()) {
				FileUtils.copyDirectory(mig3SiteDataDir, zipFileDir); // Copying Post Migration Output Files To Sitedata
																		// Directory
			}
			siteDataZipFilePath = siteDataZipFileFolderPath + ".zip";
			boolean status = createSiteDataZip(siteDataZipFileFolderPath.toString(), siteDataZipFilePath.toString(), siteDataFolderName);
			if (status) {
				NeMappingModel neMappingModel = new NeMappingModel();
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				SiteDataEntity siteDataEntity = new SiteDataEntity();
				siteDataEntity.setFilePath(siteDataSavePath.toString());
				siteDataEntity.setCiqFileName(ciqFileName);
				siteDataEntity.setNeName(enbName);
				siteDataEntity.setRemarks(remarks);
				siteDataEntity.setFileName(siteDataFolderName + ".zip");
				siteDataEntity.setPackedDate(new Date());
				siteDataEntity.setPackedBy(user.getUserName());
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				siteDataEntity.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setEnbId(enbId);
				List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);
				reportsEntity = reportsRepository.getEntityData(neMappingEntity.getProgramDetailsEntity().getProgramName(),enbId);
				if(reportsEntity!=null) {
					reportsEntity.setSiteDataStatus("Completed");
				}
				reportsEntity = reportsRepository.createReports(reportsEntity);
				siteDataService.saveSiteDataAudit(siteDataEntity);
			}
			else {
				NeMappingModel neMappingModel = new NeMappingModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(programId);
				neMappingModel.setProgramDetailsEntity(programDetailsEntity);
				neMappingModel.setEnbId(enbId);
				List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);
				reportsEntity = reportsRepository.getEntityData(neMappingEntity.getProgramDetailsEntity().getProgramName(),enbId);
				if(reportsEntity!=null) {
					reportsEntity.setSiteDataStatus("Failure");
				}
				reportsEntity = reportsRepository.createReports(reportsEntity);
			}
			if (CommonUtil.isValidObject(siteDataZipFileFolderPath)) {
				FileUtil.deleteFileOrFolder(siteDataZipFileFolderPath.toString());
			}
			commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_SITE_DATA,
					Constants.ACTION_PACK, "Site Data Details Packed Successfully For NE: " + enbName, sessionId);
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.PACK_SITE_DATA_SUCCESSFULLY));
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_PACK_SITE_DATA));
			logger.error("Exception in SiteDataController.packEnbData(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api will createSiteDataZip
	 * 
	 * @param zipFileName,dirPath
	 * @return boolean
	 */
	private boolean createSiteDataZip(String dirPath, String zipFileName, String siteDataFolderName) throws Exception {
		boolean status = false;
		try {
			File dirObj = new File(dirPath);
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
			addSiteDataToDir(dirPath, dirObj, out, siteDataFolderName);
			out.close();
			status = true;
		} catch (Exception e) {
			logger.error("Exception in SiteDataController.createSiteDataZip(): " + ExceptionUtils.getFullStackTrace(e));
			status = false;
		}
		return status;
	}

	/**
	 * This api will addSiteDataToDir
	 * 
	 * @param dirObj,dirPath,out
	 * 
	 */
	private static void addSiteDataToDir(String dirPath, File dirObj, ZipOutputStream out, String siteDataFolderName) throws IOException {
		try {
			File[] files = dirObj.listFiles();
			byte[] tmpBuf = new byte[1024];

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					addSiteDataToDir(dirPath, files[i], out, siteDataFolderName);
					continue;
				}
				String filePath = files[i].getAbsolutePath();
				filePath = filePath.replace(dirPath, siteDataFolderName);
				FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
				out.putNextEntry(new ZipEntry(filePath));
				int len;
				while ((len = in.read(tmpBuf)) > 0) {
					out.write(tmpBuf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
		} catch (Exception e) {
			logger.error("Exception in SiteDataController.addSiteDataToDir(): " + ExceptionUtils.getFullStackTrace(e));

		}
	}

	/**
	 * This api will updateSiteDataDetails
	 * 
	 * @param updateSiteDataDetails
	 * @return boolean
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.UPDATE_SITE_DATA_DETAILS, method = RequestMethod.POST)
	public JSONObject updateSiteDataDetails(@RequestBody JSONObject updateSiteDataDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject mapObject = new JSONObject();
		try {
			sessionId = updateSiteDataDetails.get("sessionId").toString();
			serviceToken = updateSiteDataDetails.get("serviceToken").toString();
			mapObject.put("sessionId", sessionId);
			mapObject.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			SiteDataEntity siteDataEntity = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(
					updateSiteDataDetails.toJSONString((Map) updateSiteDataDetails.get("siteDataDetails")),
					SiteDataEntity.class);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			siteDataEntity.setPackedBy(user.getUserName());
			siteDataEntity.setPackedDate(new Date());
			if (siteDataEntity != null) {
				if (siteDataService.saveSiteDataAudit(siteDataEntity)) {
					mapObject.put("status", Constants.SUCCESS);
					mapObject.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.SITE_DATA_DETAILS_UPDATED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_SITE_DATA,
							Constants.ACTION_UPDATE,
							"Site Data Details Updated Successfully For NE: " + siteDataEntity.getNeName(), sessionId);
				} else {
					mapObject.put("status", Constants.FAIL);
					mapObject.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_SITE_DATA_DETAILS));
				}
			}
		} catch (Exception e) {
			mapObject.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPDATE_SITE_DATA_DETAILS));
			mapObject.put("status", Constants.FAIL);
			logger.info(
					"Exception in SiteDataController.updateSiteDataDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return mapObject;
	}

	/**
	 * This api will addSiteDataToDir
	 * 
	 * @param programId,filePath,fileName
	 * @return boolean
	 */
	private boolean deleteSiteDataSFile(Integer programId, String filePath, String fileName) {
		boolean status = false;
		try {
			if (CommonUtil.isValidObject(programId) && CommonUtil.isValidObject(fileName)) {
				filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath.toString();
				if (!filePath.endsWith(Constants.SEPARATOR)) {
					filePath = filePath + Constants.SEPARATOR;
				}
				logger.info("deleteSiteDataSFile filePath: " + filePath + ", fileName: " + fileName);
				File file = new File(filePath + fileName);
				if (file.exists()) {
					file.delete();
				}
			}
			status = true;
		} catch (Exception e) {
			logger.info(
					"Exception in SiteDataController.deleteSiteDataSFile(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will deleteSiteDataDetails
	 * 
	 * @param siteDataDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.DELETE_SITE_DATA_DETAILS)
	public JSONObject deleteSiteDataDetails(@RequestBody JSONObject siteDataDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		Integer id = null;
		String fileName = null;
		String filePath = null;
		JSONObject expiryDetails = null;
		try {
			sessionId = siteDataDetails.get("sessionId").toString();
			serviceToken = siteDataDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			programId = (Integer) siteDataDetails.get("programId");
			id = Integer.parseInt(siteDataDetails.get("id").toString());
			fileName = siteDataDetails.get("fileName").toString();
			filePath = siteDataDetails.get("filePath").toString();
			SiteDataModel siteDataModel = new SiteDataModel();
			siteDataModel.setId(id);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			boolean status = siteDataService.deleteSiteDataDetails(siteDataModel);
			if (status) {
				status = deleteSiteDataSFile(programId, filePath, fileName);
				if (!status) {
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_SITE_DATA_DETAILS));
					resultMap.put("status", Constants.FAIL);
					return resultMap;
				}
			}
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.SITE_DATA_DETAILS_DELETED_SUCCESSFULLY));
			commonUtil.saveAudit(Constants.EVENT_POST_MIGRATION, Constants.EVENT_POST_MIGRATION_SITE_DATA,
					Constants.ACTION_DELETE, "Site Data Details Deleted Successfully File Name: " + fileName,
					sessionId);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_SITE_DATA_DETAILS));
			logger.info(
					"Exception in SiteDataController.deleteSiteDataDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}

	/**
	 * This api will sendMailWithAttachment
	 * 
	 * @param mailDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = Constants.SEND_EMAIL_WITH_ATTACHMENT)
	public JSONObject sendMailWithAttachment(@RequestBody JSONObject mailDetails) {
		JSONObject resultMap = new JSONObject();
		String sessionId = null;
		String serviceToken = null;
		String emailId = null;
		String fileName = null;
		String filePath = null;
		String fileExtension = null;
		String source = null;
		JSONObject expiryDetails = null;
		Integer id = null;
		try {
			source = mailDetails.get("source").toString();
			sessionId = mailDetails.get("sessionId").toString();
			serviceToken = mailDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			id = (Integer) mailDetails.get("id");
			emailId = mailDetails.get("emailId").toString();
			fileName = mailDetails.get("fileName").toString();
			filePath = mailDetails.get("filePath").toString();
			filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath.toString();
			if (!filePath.endsWith(Constants.SEPARATOR)) {
				filePath = filePath + Constants.SEPARATOR;
			}
			File attachment = new File(filePath + fileName);
			fileExtension = FilenameUtils.getExtension(fileName);
			logger.info("fileExtension: " + fileExtension);
			if ("vbs".equalsIgnoreCase(fileExtension)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SEND_VBS_MAIL));
				return resultMap;
			}
			if (CommonUtil.isValidObject(id)) {
				if (Constants.MAIL_SOURCE_SITEDATA.equalsIgnoreCase(source)) {
					SiteDataEntity siteDataEntity = siteDataService.getSiteDataDetailsById(id);
					if (!CommonUtil.isValidObject(siteDataEntity)) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
						return resultMap;
					}
					if (attachment.exists() && CommonUtil.isValidObject(siteDataEntity)) {
						User user = UserSessionPool.getInstance().getSessionUser(sessionId);
						String[] toList = emailId.split(",");
						StringBuilder bodyText = new StringBuilder();
						bodyText.append("Dear " + user.getUserFullName() + ",");
						bodyText.append("<br/><br/>");
						bodyText.append("Please find the attached site data for:");
						bodyText.append("<br/><br/>");
						bodyText.append("Ciq File Name: " + siteDataEntity.getCiqFileName());
						bodyText.append("<br/>");
						bodyText.append("Ne Name: " + siteDataEntity.getNeName());
						bodyText.append("<br/><br/>");
						bodyText.append("Regards");
						bodyText.append("<br/>");
						bodyText.append("SMART Administrator");

						String subject = "Site data detail for SMART NE " + siteDataEntity.getNeName();

						if (emailUtil.sendEmail(toList, null, null, subject, bodyText.toString(), attachment,
								attachment.getName(), true)) {
							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.MAIL_SENT_SUCCESSFULLY));
						}
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.MAIL_ATTACHMENT_NOT_FOUND));
						return resultMap;
					}
				} else if (Constants.MAIL_SOURCE_GENERATE.equalsIgnoreCase(source)) {
					GenerateInfoAuditEntity generateInfoAuditEntity = objGenerateCsvService
							.getGenerateInfoAuditById(id);

					if (!CommonUtil.isValidObject(generateInfoAuditEntity)) {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
						return resultMap;
					}
					if (attachment.exists() && CommonUtil.isValidObject(generateInfoAuditEntity)) {
						User user = UserSessionPool.getInstance().getSessionUser(sessionId);
						String[] toList = emailId.split(",");
						StringBuilder bodyText = new StringBuilder();
						bodyText.append("Dear " + user.getUserFullName() + ",");
						bodyText.append("<br/><br/>");
						bodyText.append(
								"Please find the attached " + generateInfoAuditEntity.getFileType() + " file for:");
						bodyText.append("<br/><br/>");
						bodyText.append("Ciq File Name: " + generateInfoAuditEntity.getCiqFileName());
						bodyText.append("<br/>");
						bodyText.append("Ne Name: " + generateInfoAuditEntity.getNeName());
						bodyText.append("<br/><br/>");
						bodyText.append("Regards");
						bodyText.append("<br/>");
						bodyText.append("SMART Administrator");

						String subject = "Generated " + generateInfoAuditEntity.getFileType() + " for SMART NE "
								+ generateInfoAuditEntity.getNeName();

						if (emailUtil.sendEmail(toList, null, null, subject, bodyText.toString(), attachment,
								attachment.getName(), true)) {
							resultMap.put("status", Constants.SUCCESS);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.MAIL_SENT_SUCCESSFULLY));
						}
					} else {
						resultMap.put("status", Constants.FAIL);
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.MAIL_ATTACHMENT_NOT_FOUND));
						return resultMap;
					}
				}
			} else {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
				return resultMap;
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_SEND_MAIL));
			logger.info(
					"Exception in SiteDataController.sendMailWithAttachment(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
}
