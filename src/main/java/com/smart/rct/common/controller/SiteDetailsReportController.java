package com.smart.rct.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.standard.DateTimeContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.common.models.SiteCompletionModel;
import com.smart.rct.common.models.SiteModel;
import com.smart.rct.common.repository.SiteDetailsReportRepository;
import com.smart.rct.common.service.SiteDetailsReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.service.WorkFlowManagementService;
import com.smart.rct.postmigration.dto.Audit4GFsuSummaryDto;
import com.smart.rct.postmigration.dto.Audit4GSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GCBandSummaryDto;
import com.smart.rct.postmigration.dto.Audit5GDSSSummaryDto;
import com.smart.rct.postmigration.dto.SiteDataDto;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.entity.PartialSaveSiteReportEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class SiteDetailsReportController {

	final static Logger logger = LoggerFactory.getLogger(SiteDetailsReportController.class);
	@Autowired
	SiteDetailsReportService siteDetailsReportService;
	@Autowired
	CommonUtil commonUtil;

	@Autowired
	public FileUploadService fileUploadService;
	
	@Autowired
	Audit4GSummaryService audit4GSummaryService;
	@Autowired
	Audit4GFsuSummaryService audit4GFsuSummaryService;
	@Autowired
	Audit5GDSSSummaryService audit5GDSSSummaryService;//Audit5GCBandSummaryServiceImpl
	@Autowired
	Audit5GCBandSummaryService audit5GCBANDSummaryService;
	@Autowired
	Audit4GSummaryDto audit4GSummaryDto;
	@Autowired
	Audit5GDSSSummaryDto audit5GDSSSummaryDto;
	@Autowired
	Audit5GCBandSummaryDto audit5GCBandSummaryDto;
	@Autowired
	Audit4GFsuSummaryDto audit4GFsuSummaryDto;
	
	@Autowired
	WorkFlowManagementService workFlowManagementService;
	@Autowired
	FileUploadRepository fileUploadRepository;
	@Autowired
	SiteDetailsReportRepository siteDetailsReportRepository;
	
	/**
	 * This method will Download Site Report Details
	 * 
	 * @param siteDetails
	 * @param response
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = Constants.EXPORT_SITEREPORT_DETAILS, method = RequestMethod.POST)
	public void exportOverallDetails(@RequestBody JSONObject siteDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String programName = null;
		String programId = null;
		String siteReportStatus= null;
		String neName =null;
		JSONObject expiryDetails = null;
		try {
			sessionId = siteDetails.get("sessionId").toString();
			serviceToken = siteDetails.get("serviceToken").toString();

			if (StringUtils.isNotEmpty(siteDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(siteDetails.get("customerId").toString());
			}
			
			
			programName = siteDetails.get("programName").toString();
			programId = siteDetails.get("programId").toString();
			ObjectMapper mapper = new ObjectMapper();
			String data = CommonUtil.convertObjectToJson(siteDetails);
			JsonObject objData = CommonUtil.parseRequestDataToJson(data);
			SiteCompletionModel siteCompletionUsmModel=null;
			 siteCompletionUsmModel =  mapper.readValue(objData.get("reportDetails").toString(),
    					new TypeReference<SiteCompletionModel>() {
				});
			 neName = siteCompletionUsmModel.getNeName().toString();
			 siteReportStatus =siteCompletionUsmModel.getSiteReportStatus().toString();
			/*SiteCompletionModel siteCompletionModel = mapper.readValue(objData.get("reportDetails").toString(),
					new TypeReference<SiteCompletionModel>() {
					});*/
			String uniqId = DateUtil.dateToString(new Date(), "yyyyMMdd_HH_mm_ss");
			String fileName=(siteCompletionUsmModel.getProject()+ "-"+ siteCompletionUsmModel.getMarket() +"-"
					+ siteCompletionUsmModel.getNeName()+"-"+siteCompletionUsmModel.getFinalIntegStatus())+"_"+uniqId+".xlsx";
			filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
			.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
			.append(File.separator);
			String filePathDetail=filePath.toString();
			filePath.append(fileName);
			//SiteCompletionModel siteCompletionUsmModel=null;
			boolean statusReport=false;
                   if(Constants.USM_LIVE_4G_SITE.equalsIgnoreCase(programName))
                   {
                	    siteCompletionUsmModel =  mapper.readValue(objData.get("reportDetails").toString(),
           					new TypeReference<SiteCompletionModel>() {
   					});
                	   
                	   statusReport=siteDetailsReportService.createExcelSiteReportUsmLiveDetails(siteCompletionUsmModel,filePath.toString());
                   }else if(Constants.MM_5G_SITE.equalsIgnoreCase(programName)) {
                	   siteCompletionUsmModel = mapper.readValue(objData.get("reportDetails").toString(),
           					new TypeReference<SiteCompletionModel>() {
   					});
                	   statusReport= siteDetailsReportService.createExcelSiteReportDetails(siteCompletionUsmModel,filePath.toString());
                   }
                   else if(Constants.DSS_5G_SITE.equalsIgnoreCase(programName)) {
                	   siteCompletionUsmModel = mapper.readValue(objData.get("reportDetails").toString(),
           					new TypeReference<SiteCompletionModel>() {
   					});
                	   statusReport= siteDetailsReportService.createExcelSiteReportDetailsForDSS(siteCompletionUsmModel,filePath.toString());
                   }else if(Constants.CBAND_5G_SITE.equalsIgnoreCase(programName)) {
                	   siteCompletionUsmModel = mapper.readValue(objData.get("reportDetails").toString(),
           					new TypeReference<SiteCompletionModel>() {
   					});
                	   statusReport= siteDetailsReportService.createExcelSiteReportDetailsForCBANAD(siteCompletionUsmModel,filePath.toString());
                   }
                   else if(Constants.FSU_4G_SITE.equalsIgnoreCase(programName)) {
                	   siteCompletionUsmModel = mapper.readValue(objData.get("reportDetails").toString(),
           					new TypeReference<SiteCompletionModel>() {
   					});
                	   statusReport= siteDetailsReportService.createExcelSiteReportDetailsForFSU(siteCompletionUsmModel,filePath.toString());
                   }
			if (statusReport) {
				/*filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
						.append(File.separator).append(Constants.SITE_REPORT_XL);*/
				downloadFile = new File(filePath.toString());
				if (!downloadFile.exists()) {
					logger.error("downloadFile() file not found:" + filePath.toString());
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"Error While Downloading\"}");
					out.close();
					return;
				}
				inputStream = new FileInputStream(downloadFile);
				// MIME type of the file
				String mimeType = "application/octet-stream";
				// set content attributes for the response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());
				// set headers for the response
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);
				response.setHeader("sessionId", sessionId);
				response.setHeader("serviceToken", sessionId);
				// get output stream of the response
				outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				// write bytes read from the input stream into the output stream
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				outStream.flush();
				inputStream.close();
				outStream.close();
				
				SiteDataEntity statusSiteDataEntity=siteDetailsReportService.saveSiteDetails(siteCompletionUsmModel, siteDetails,filePathDetail,fileName);
				//JSONObject SiteReportuplaodResult=siteDetailsReportService.SiteReportUploadeToOV( siteDetails,statusSiteDataEntity)	;
				if(!ObjectUtils.isEmpty(statusSiteDataEntity) && siteDetails.containsKey("workFlowId") &&!ObjectUtils.isEmpty(siteDetails.get("workFlowId")))
				{
					Integer workFlowId = (Integer) siteDetails.get("workFlowId");
					WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(workFlowId);
					workFlowManagementEntity.setSiteReportId(statusSiteDataEntity.getId());
					workFlowManagementEntity.setSiteReportStatus(siteCompletionUsmModel.getSiteReportStatus());
					workFlowManagementEntity.setOvSiteReportStatus(statusSiteDataEntity.getOvUpdateStatus());
					workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
				}

				/*
				 * if (CommonUtil.isValidObject(filePath)) {
				 * FileUtil.deleteFileOrFolder(filePath.toString()); }
				 */
				/*
				 * commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
				 * Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG, Constants.ACTION_EXPORT,
				 * "Site Config Details File Exported Successfully", sessionId);
				 */
			} else {
				response.setContentType("application/json");
				PrintWriter out = response.getWriter();
				out.println("{\"status\": \""
						+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_NETWORK_CONFIG)
						+ "\"}");
				out.close();
				return;
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \""
					+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_NETWORK_CONFIG) + "\"}");
			out.close();
			return;
		}
		return;
	}

	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/ovToUploadSiteReport")
	public JSONObject retryMilestoneUpdatepre(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
	
		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");
			String enbName=reRunTestParams.get("neName").toString();
			String[] arrOfstring=enbName.split("_");
			String enbId=arrOfstring[0];
			String programName=reRunTestParams.get("programName").toString();
			int programId = Integer.parseInt(reRunTestParams.get("programId").toString());
			
			
			SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(runTestId);
			
			JSONObject ovUpdateJson=new JSONObject();
			
			ovUpdateJson.put("FileName",siteDataEntity.getFileName());
			ovUpdateJson.put("filePath",siteDataEntity.getFilePath());
			ovUpdateJson.put("programName",programName);
			ovUpdateJson.put("programId",programId);
			ovUpdateJson.put("ciqFileName",siteDataEntity.getCiqFileName());
			ovUpdateJson.put("enbName",enbName);
			ovUpdateJson.put("enbId",enbId);
			if (reRunTestParams.containsKey("workFlowId")) {
				int workFlowId = Integer.parseInt(reRunTestParams.get("workFlowId").toString());
				ovUpdateJson.put("workFlowId",workFlowId);
			}
		//	int premigration_Id=Integer.parseInt(doZipResultObject.get("premigration_Id").toString());
			
			JSONObject SiteReportuplaodResult=siteDetailsReportService.SiteReportUploadeToOV( ovUpdateJson,siteDataEntity)	;
			if(!ObjectUtils.isEmpty(siteDataEntity) && ovUpdateJson.containsKey("workFlowId") &&!ObjectUtils.isEmpty(ovUpdateJson.get("workFlowId")))
			{
				Integer workFlowId = (Integer) ovUpdateJson.get("workFlowId");
				WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
						.getWorkFlowManagementEntity(workFlowId);
				workFlowManagementEntity.setSiteReportId(siteDataEntity.getId());
				//workFlowManagementEntity.setSiteReportStatus(siteCompletionUsmModel.getSiteReportStatus());
				workFlowManagementEntity.setOvSiteReportStatus(siteDataEntity.getOvUpdateStatus());
				workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
			}
			//String[] RetryMilestone=new String[];
			resultMap.put("status", Constants.SUCCESS);
		resultMap.put("sessionId", sessionId);
		resultMap.put("serviceToken", serviceToken);
		resultMap.put("reason", "Retrying to Update the Site Report");
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateMilestone API" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}
	@SuppressWarnings({ "unchecked" })
	@PostMapping(value = "/reportUploadtoOV")
	public JSONObject reportUploadtoOV(@RequestBody JSONObject reRunTestParams) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		boolean useCurrPassword;
		String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
	
		try {
			sessionId = reRunTestParams.get("sessionId").toString();
			serviceToken = reRunTestParams.get("serviceToken").toString();

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);

			if (expiryDetails != null) {
				return expiryDetails;
			}
			int runTestId = (int) reRunTestParams.get("runTestId");
			String enbName=reRunTestParams.get("neName").toString();
			String[] arrOfstring=enbName.split("_");
			String enbId=arrOfstring[0];
			String programName=reRunTestParams.get("programName").toString();
			int programId = Integer.parseInt(reRunTestParams.get("programId").toString());
			
			
			SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(runTestId);
			
			JSONObject ovUpdateJson=new JSONObject();
			
			ovUpdateJson.put("FileName",siteDataEntity.getFileName());
			ovUpdateJson.put("filePath",siteDataEntity.getFilePath());
			ovUpdateJson.put("programName",programName);
			ovUpdateJson.put("programId",programId);
			ovUpdateJson.put("ciqFileName",siteDataEntity.getCiqFileName());
			ovUpdateJson.put("enbName",enbName);
			ovUpdateJson.put("enbId",enbId);
			if (reRunTestParams.containsKey("workFlowId")) {
				int workFlowId = Integer.parseInt(reRunTestParams.get("workFlowId").toString());
				ovUpdateJson.put("workFlowId",workFlowId);
			}
		//	int premigration_Id=Integer.parseInt(doZipResultObject.get("premigration_Id").toString());
			
			JSONObject SiteReportuplaodResult=siteDetailsReportService.SiteReportUploadeToOV( ovUpdateJson,siteDataEntity)	;
			if(!ObjectUtils.isEmpty(siteDataEntity) && ovUpdateJson.containsKey("workFlowId") &&!ObjectUtils.isEmpty(ovUpdateJson.get("workFlowId")))
			{
				Integer workFlowId = (Integer) ovUpdateJson.get("workFlowId");
				WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
						.getWorkFlowManagementEntity(workFlowId);
				workFlowManagementEntity.setSiteReportId(siteDataEntity.getId());
				//workFlowManagementEntity.setSiteReportStatus(siteCompletionUsmModel.getSiteReportStatus());
				workFlowManagementEntity.setOvSiteReportStatus(siteDataEntity.getOvUpdateStatus());
				workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
			}
			//String[] RetryMilestone=new String[];
			resultMap.put("status", Constants.SUCCESS);
		resultMap.put("sessionId", sessionId);
		resultMap.put("serviceToken", serviceToken);
		resultMap.put("reason", "Uploading Site-Report to OV");
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateMilestone API" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;

	}
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/siteReportInfo")
	public JSONObject siteReportInfo(@RequestBody JSONObject messageInfoParams) {
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer runTestId = null;
		JSONObject resultMap = new JSONObject();
		//List result = new ArrayList<>();
		try {
			sessionId = messageInfoParams.get("sessionId").toString();
			serviceToken = messageInfoParams.get("serviceToken").toString();
			runTestId = (Integer) messageInfoParams.get("testId");
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			runTestId = (int) messageInfoParams.get("testId");
			List<SiteReportOVEntity> runTestResultEntityList = siteDetailsReportService.getSiteReportOVEntity(runTestId);
			List<RunTestModel> objRunTestModelList = new ArrayList<>();
			//String useCaseList = messageInfoParams.get("useCaseName").toString();
			 
				
				
				for (SiteReportOVEntity entity : runTestResultEntityList) {
					String MileStone2=entity.getFileName();
							 
								RunTestModel runModel = new RunTestModel();
								runModel.setName(MileStone2);
								String resultout= entity.getCurrentResult();
								if(resultout.contains("Successfully")) {
									runModel.setStatus("pass");
								}else {
									runModel.setStatus("fail");
								}
								objRunTestModelList.add(runModel);
								
								break;
				}
				
			
			StringBuilder sb = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			for (SiteReportOVEntity entity : runTestResultEntityList) {
			 String resultout= entity.getCurrentResult();
			sb.append(resultout) ;
			sb.append("\n") ;
			 }
			String result=sb.toString();
			
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("milestones", objRunTestModelList);
			resultMap.put("history", result);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			resultMap.put("reason", e.getMessage());
			logger.info("Exception in runTest() in RunTestController" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultMap;
	}
	


	/********************saving the partial data of site report 
	 * @return ************************************************/
	@SuppressWarnings("unused")
	@RequestMapping(value = "/saveSiteReportDetails", method = RequestMethod.POST)
	public JSONObject saveOverallDetails(@RequestBody JSONObject siteDetails)
			 {
		JSONObject resultMap = new JSONObject();
		
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		String programName = null;
		String programId = null;
		String siteReportStatus= null;
		String neName =null;
		JSONObject expiryDetails = null;
		try {
			sessionId = siteDetails.get("sessionId").toString();
			serviceToken = siteDetails.get("serviceToken").toString();

			if (StringUtils.isNotEmpty(siteDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(siteDetails.get("customerId").toString());
			}
			PartialSaveSiteReportEntity statusSiteDataEntity=null;
			
			programName = siteDetails.get("programName").toString();
			programId = siteDetails.get("programId").toString();
			ObjectMapper mapper = new ObjectMapper();
			String data = CommonUtil.convertObjectToJson(siteDetails);
			JsonObject objData = CommonUtil.parseRequestDataToJson(data);
			SiteCompletionModel siteCompletionUsmModel=null;
			 siteCompletionUsmModel =  mapper.readValue(objData.get("reportDetails").toString(),
    					new TypeReference<SiteCompletionModel>() {
				});
			 neName = siteCompletionUsmModel.getNeName().toString();
					
			 siteReportStatus =siteCompletionUsmModel.getSiteReportStatus().toString();
			 
			 String neId =  siteCompletionUsmModel.getNeId();
			 String expectedsiteReportStatus = "";
			 
				User user1 = UserSessionPool.getInstance().getSessionUser(sessionId);
	            Integer currentRole1 = user1.getRoleId();
	            
	            
			if (programName.equals("VZN-4G-USM-LIVE")) {
				List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));
			  if (audit4GSummaryEntityList.isEmpty()) {
				  expectedsiteReportStatus = "Completion";
			  } else {
				  expectedsiteReportStatus = "Expection";
				  
			  }
			  if (!siteReportStatus.equalsIgnoreCase(expectedsiteReportStatus)) {
				  if (!(user1.getRole() == "Commission Manager")) {
					  
						String reason = GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNAUTHORIZED_ACCESS);
						resultMap.put("status","Fail");
						resultMap.put(reason,reason);
						return resultMap;
				  } 
				
			  }
			} else if (programName.equals("4G-FSU")) {
				List<Audit4GFsuSummaryEntity> audit4gFsuSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));
				if (audit4gFsuSummaryEntityList.isEmpty()) {
					expectedsiteReportStatus = "Completion";
				} else {
					expectedsiteReportStatus = "Expection";
				}
				if (!siteReportStatus.equalsIgnoreCase(expectedsiteReportStatus)) {
					if (!(user1.getRole() == "Commission Manager")) {

						String reason = GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNAUTHORIZED_ACCESS);
						resultMap.put("status","Fail");
						resultMap.put(reason,reason);
						return resultMap;
					}
				}
 			} else if (programName.equals("5G-CBAND")) {
 				List<Audit5GCBandSummaryEntity> audit5gcBandSummaryEntityList = audit5GCBANDSummaryService.getAudit5GCBandSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));
 				if (audit5gcBandSummaryEntityList.isEmpty()) {
					expectedsiteReportStatus = "Completion";
				} else {
					expectedsiteReportStatus = "Expection";
				}
				if (!siteReportStatus.equalsIgnoreCase(expectedsiteReportStatus)) {
					if (!(user1.getRole() == "Commission Manager")) {

						String reason = GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNAUTHORIZED_ACCESS);
						resultMap.put("status","Fail");
						resultMap.put(reason,reason);
						return resultMap;
					}
				}
 			} else if (programName.equals("5G-DSS")) {
 				List<Audit5GDSSSummaryEntity> audit5gdssSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));
 			   if (audit5gdssSummaryEntityList.isEmpty()) {
 				  expectedsiteReportStatus = "Completion";
 			   } else {
 				  expectedsiteReportStatus = "Expection";
 			   } if (!siteReportStatus.equalsIgnoreCase(expectedsiteReportStatus)) {
 				  if (!(user1.getRole() == "Commission Manager")) {

						String reason = GlobalInitializerListener.faultCodeMap.get(FaultCodes.UNAUTHORIZED_ACCESS);
						resultMap.put("status","Fail");
						resultMap.put(reason,reason);
						return resultMap;
					}
 			   }
 			}
			
			 if(programName.equals("VZN-5G-MM")) {
				String NeidfiveG= siteCompletionUsmModel.getNeId();
				 String [] neidFiveGp =NeidfiveG.split("\\|");
				 for( int i=0; i<neidFiveGp.length; i++) {
					String neidfiveG=	neidFiveGp[i];
					 siteCompletionUsmModel.setNeId(neidFiveGp[i]);
					 statusSiteDataEntity=siteDetailsReportService.savePartialSiteDetails(siteCompletionUsmModel, siteDetails,neidfiveG);
				 }
				// statusSiteDataEntity=siteDetailsReportService.savePartialSiteDetails(siteCompletionUsmModel, siteDetails);
			 }else {
			 statusSiteDataEntity=siteDetailsReportService.savePartialSiteDetails(siteCompletionUsmModel, siteDetails,"");
			 }	
				if(!ObjectUtils.isEmpty(statusSiteDataEntity) && siteDetails.containsKey("workFlowId") &&!ObjectUtils.isEmpty(siteDetails.get("workFlowId")))
				{
					Integer workFlowId = (Integer) siteDetails.get("workFlowId");
					WorkFlowManagementEntity workFlowManagementEntity = workFlowManagementService
							.getWorkFlowManagementEntity(workFlowId);
				//	workFlowManagementEntity.setSiteReportId(workFlowId);
					workFlowManagementEntity.setSiteReportStatus("Partial Save");
					workFlowManagementService.mergeWorkFlowMangement(workFlowManagementEntity);
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
				}

				/*
				 * if (CommonUtil.isValidObject(filePath)) {
				 * FileUtil.deleteFileOrFolder(filePath.toString()); }
				 */
				/*
				 * commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
				 * Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG, Constants.ACTION_EXPORT,
				 * "Site Config Details File Exported Successfully", sessionId);
				 */
			
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",e);
		}
		return resultMap;
	}

	/**
	 * This method will get the getSiteReportInputDetails
	 * 
	 * @param params
	 * @return JSONObject
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/getSiteReportInputDetails", method = RequestMethod.POST)

	public JSONObject getSiteReportInputDetails(@RequestBody JSONObject params) {
		JSONObject resultMap = new JSONObject();
		JSONObject resultMap1 = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		String programName = null;
		String ciqName= null;
		String enbName="";
		boolean isPartialReport=true;
		try {
			sessionId = params.get("sessionId").toString();
			serviceToken = params.get("serviceToken").toString();
			programId = (Integer) params.get("programId");
			programName = params.get("programName").toString();
			ciqName=params.get("ciqName").toString();
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqName);
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			List<Map<String, String>> enbList =(List<Map<String, String>>)params.get("neDetails");
			String neId = enbList.get(0).get("enbId");
			resultMap=siteDetailsReportService.getSiteDetailsForSavefile(neId);
			if(resultMap== null) {
				 resultMap = new JSONObject();	
				 isPartialReport=false;
			}
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(programId,
					Constants.SITE_REPORT_INPUTS_TEMPLATE);
			if (!ObjectUtils.isEmpty(programTemplateEntity)) {
				resultMap.put("siteInputs", programTemplateEntity.getValue());
			} else {
				resultMap.put("siteinputs", null);
			}
			if(programName.equals("VZN-5G-DSS")) {
			List<CIQDetailsModel> listCIQDetailsModelDay1 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, neId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
			/*String ENBSW=listCIQDetailsModelDay1.get(0).getCiqMap().get("eNBVersion").getHeaderValue().trim();
			String ENODEBNAME=listCIQDetailsModelDay1.get(0).getCiqMap().get("850NRCarriersite").getHeaderValue().trim();
			String  VDUSW=listCIQDetailsModelDay1.get(0).getCiqMap().get("vDU_Version").getHeaderValue().trim();
			String FSUSW=listCIQDetailsModelDay1.get(0).getCiqMap().get("FSUVersion").getHeaderValue().trim();*/
			//List<SiteCompletionModel> audit5GCBANDSummaryModelList = SiteDataDto.getSiteDataDetailsModel(listCIQDetailsModelDay1);
			 SiteModel automated=(SiteModel) SiteDataDto.getSiteDataDetailsModeld(listCIQDetailsModelDay1);
			List<Audit5GDSSSummaryEntity> audit5GDSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
			List<Audit4GSummaryModel> audit5GSummaryModelList = audit5GDSSSummaryDto.getAudit5GDSSSummaryReportModelList(audit5GDSummaryEntityList);
			String firstTwoDigit="";
			String Matket="";
			int digit = 0;
			if(neId.length()==6 ||neId.length()==11)
			{
				firstTwoDigit =neId.substring(0,3);
				digit =Integer.parseInt(firstTwoDigit);
			}
			else {
				firstTwoDigit =neId.substring(0,2);
				digit =Integer.parseInt(firstTwoDigit);
			}
			if(digit ==31 || digit ==36) {
				Matket ="SAC";
			}else if(digit >=56 && digit <= 68) {
				Matket ="NE";
			}else if(digit >=70 && digit <= 74) {
				Matket ="UNY";
			}else if(digit >=78 && digit <= 85) {
				Matket ="NYM";
			}else if(digit >=86 && digit <= 102) {
				Matket ="TRI";
			}else if(digit >=106 && digit <= 117) {
				Matket ="WBV";
			}else if(digit >=120 && digit <= 129) {
				Matket ="HOU TX";
			}else if(digit >=131 && digit <= 140) {
				Matket ="CTX";
			}else if(digit >=180 && digit <= 186) {
				Matket ="CGC";
			}else if(digit >=229 && digit <= 255) {
				Matket ="OPW";
			}else if(digit >=407 && digit <= 417) {
				Matket ="WBV";
			} 
			automated.setMarket(Matket);
			resultMap.put("isPartialReport", isPartialReport);
			resultMap.put("siteDetails", automated);
			resultMap.put("postAuditIssues", audit5GSummaryModelList);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			}
			else if(programName.equals("VZN-5G-CBAND")) {
				Date today1 = new Date();
				String time = today1.getHours() + ":" + today1.getMinutes() + ":" + today1.getSeconds();
				System.out.println("fetching of data from CIQ Started at:"+time);
				logger.error("fetching of data from CIQ Started at:"+time);
				List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, neId, enbName, dbcollectionFileName, "Day0_1", "");
				Date today2 = new Date();
				String time2 = today2.getHours() + ":" + today2.getMinutes() + ":" + today2.getSeconds();
				logger.error("fetching of data from CIQ end at:"+time2);
				 SiteModel automated=(SiteModel) SiteDataDto.getSiteDataDetailsModelCd(listCIQDetailsModelDay01);
				 Date today3 = new Date();
				 String time3 = today3.getHours() + ":" + today3.getMinutes() + ":" + today3.getSeconds();
				 logger.error("fetching of  audit issuse from Db  stated at:"+time3);
				 List<Audit5GCBandSummaryEntity> audit5GCBANDSummaryEntityList = audit5GCBANDSummaryService.getAudit5GCBandSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
				 Date today4 = new Date();	
				 String time4 = today4.getHours() + ":" + today4.getMinutes() + ":" + today4.getSeconds();
				 logger.error("fetching of  audit issusefrom Db end at:"+time4);
				 Date today5 = new Date();	
				 String time5 = today5.getHours() + ":" + today5.getMinutes() + ":" + today5.getSeconds();
				 logger.error("converting of  audit issuse to model  at:"+time5);
				 List<Audit4GSummaryModel> audit5GCBANDSummaryModelList = audit5GCBandSummaryDto.getAudit5GCBandSummaryReportModelList(audit5GCBANDSummaryEntityList);
				 Date today6 = new Date();
				 String time6 = today6.getHours() + ":" + today6.getMinutes() + ":" + today6.getSeconds();
				 logger.error("converting of  audit issuse to model end at:"+time6);
				 String firstTwoDigit="";
					String Matket="";
					int digit = 0;
					if(neId.length()==6 ||neId.length()==11)
					{
						firstTwoDigit =neId.substring(0,3);
						digit =Integer.parseInt(firstTwoDigit);
					}
					else {
						firstTwoDigit =neId.substring(0,2);
						digit =Integer.parseInt(firstTwoDigit);
					}
					if(digit ==31 || digit ==36) {
						Matket ="SAC";
					}else if(digit >=56 && digit <= 68) {
						Matket ="NE";
					}else if(digit >=70 && digit <= 74) {
						Matket ="UNY";
					}else if(digit >=78 && digit <= 85) {
						Matket ="NYM";
					}else if(digit >=86 && digit <= 102) {
						Matket ="TRI";
					}else if(digit >=106 && digit <= 117) {
						Matket ="WBV";
					}else if(digit >=120 && digit <= 129) {
						Matket ="HOU TX";
					}else if(digit >=131 && digit <= 140) {
						Matket ="CTX";
					}else if(digit >=180 && digit <= 186) {
						Matket ="CGC";
					}else if(digit >=229 && digit <= 255) {
						Matket ="OPW";
					}else if(digit >=407 && digit <= 417) {
						Matket ="WBV";
					} 
					automated.setMarket(Matket);
				 resultMap.put("siteDetails", automated);
				resultMap.put("postAuditIssues", audit5GCBANDSummaryModelList);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				}
			else if(programName.equals("VZN-4G-USM-LIVE")) {
				List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
				List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GSummaryDto.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
				List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, neId, enbName, dbcollectionFileName, "CIQUpstateNY", "");
				String Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("ne_version").getHeaderValue().trim();
				SiteModel automated=(SiteModel) SiteDataDto.getSiteDataDetailsModelusm(listCIQDetailsModelDay01);
				String firstTwoDigit="";
				String Matket="";
				int digit = 0;
				if(neId.length()==6 ||neId.length()==11)
				{
					firstTwoDigit =neId.substring(0,3);
					digit =Integer.parseInt(firstTwoDigit);
				}
				else {
					firstTwoDigit =neId.substring(0,2);
					digit =Integer.parseInt(firstTwoDigit);
				}
				if(digit ==31 || digit ==36) {
					Matket ="SAC";
				}else if(digit >=56 && digit <= 68) {
					Matket ="NE";
				}else if(digit >=70 && digit <= 74) {
					Matket ="UNY";
				}else if(digit >=78 && digit <= 85) {
					Matket ="NYM";
				}else if(digit >=86 && digit <= 102) {
					Matket ="TRI";
				}else if(digit >=106 && digit <= 117) {
					Matket ="WBV";
				}else if(digit >=120 && digit <= 129) {
					Matket ="HOU TX";
				}else if(digit >=131 && digit <= 140) {
					Matket ="CTX";
				}else if(digit >=180 && digit <= 186) {
					Matket ="CGC";
				}else if(digit >=229 && digit <= 255) {
					Matket ="OPW";
				}else if(digit >=407 && digit <= 417) {
					Matket ="WBV";
				} 
				automated.setMarket(Matket);
				resultMap.put("siteDetails", automated);
				resultMap.put("isPartialReport", isPartialReport);
				resultMap.put("postAuditIssues", audit4GSummaryModelList);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				}else if(programName.equals("VZN-4G-FSU")) {
					List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
					List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuSummaryReportModelList(audit4GFsuSummaryEntityList);
					List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, neId, enbName, dbcollectionFileName, "FSUCIQ", "");
					String Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("NE_Version").getHeaderValue().trim();
					SiteModel automated=(SiteModel) SiteDataDto.getSiteDataDetailsModeFSU(listCIQDetailsModelDay01);
					String firstTwoDigit="";
					String Matket="";
					int digit = 0;
					if(neId.length()==6 ||neId.length()==11 ||neId.length()==9)
					{
						firstTwoDigit =neId.substring(0,3);
						digit =Integer.parseInt(firstTwoDigit);
					}
					else {
						firstTwoDigit =neId.substring(0,2);
						digit =Integer.parseInt(firstTwoDigit);
					}
					if(digit ==31 || digit ==36) {
						Matket ="SAC";
					}else if(digit >=56 && digit <= 68) {
						Matket ="NE";
					}else if(digit >=70 && digit <= 74) {
						Matket ="UNY";
					}else if(digit >=78 && digit <= 85) {
						Matket ="NYM";
					}else if(digit >=86 && digit <= 102) {
						Matket ="TRI";
					}else if(digit >=106 && digit <= 117) {
						Matket ="WBV";
					}else if(digit >=120 && digit <= 129) {
						Matket ="HOU TX";
					}else if(digit >=131 && digit <= 140) {
						Matket ="CTX";
					}else if(digit >=180 && digit <= 186) {
						Matket ="CGC";
					}else if(digit >=229 && digit <= 255) {
						Matket ="OPW";
					}else if(digit >=407 && digit <= 417) {
						Matket ="WBV";
					} 
					automated.setMarket(Matket);
					resultMap.put("siteDetails", automated);
					resultMap.put("isPartialReport", isPartialReport);
					resultMap.put("postAuditIssues", audit4GSummaryModelList);
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("sessionId", sessionId);
					resultMap.put("serviceToken", serviceToken);
					}
			else if(programName.equals("VZN-5G-MM")) {
				List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqName, neId, enbName, dbcollectionFileName, "5GNRCIQAU", "");
				 String Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("ne_version").getHeaderValue().trim();
				SiteModel automated=(SiteModel) SiteDataDto.getSiteDataDetailsModel5GMM(listCIQDetailsModelDay01);
				//List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
				//List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GSummaryDto.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
				
				//resultMap.put("postAuditIssues", audit4GSummaryModelList);
				String firstTwoDigit="";
				String Matket="";
				int digit = 0;
				if(neId.length()==6 ||neId.length()==11)
				{
					firstTwoDigit =neId.substring(0,3);
					digit =Integer.parseInt(firstTwoDigit);
				}
				else {
					firstTwoDigit =neId.substring(0,2);
					digit =Integer.parseInt(firstTwoDigit);
				}
				if(digit ==31 || digit ==36) {
					Matket ="SAC";
				}else if(digit >=56 && digit <= 68) {
					Matket ="NE";
				}else if(digit >=70 && digit <= 74) {
					Matket ="UNY";
				}else if(digit >=78 && digit <= 85) {
					Matket ="NYM";
				}else if(digit >=86 && digit <= 102) {
					Matket ="TRI";
				}else if(digit >=106 && digit <= 117) {
					Matket ="WBV";
				}else if(digit >=120 && digit <= 129) {
					Matket ="HOU TX";
				}else if(digit >=131 && digit <= 140) {
					Matket ="CTX";
				}else if(digit >=180 && digit <= 186) {
					Matket ="CGC";
				}else if(digit >=229 && digit <= 255) {
					Matket ="OPW";
				}else if(digit >=407 && digit <= 417) {
					Matket ="WBV";
				} 
				automated.setMarket(Matket);
				String Site_Name=listCIQDetailsModelDay01.get(0).getCiqMap().get("VZW Site Name").getHeaderValue().trim();
				List<CIQDetailsModel> listCIQDetailsModelDay02 = fileUploadRepository.getEnbTableDetailsRanConfigBySiteName(ciqName, Site_Name, enbName, dbcollectionFileName, "5GNRCIQAU", "");
				ArrayList<String> ne=new ArrayList<String>();
				ArrayList<String> neid=new ArrayList<String>();
				if(!ObjectUtils.isEmpty(listCIQDetailsModelDay02)) {
					for(CIQDetailsModel ciqDetails : listCIQDetailsModelDay02) {
						
						String n=ciqDetails.getCiqMap().get("GNODEB_AU_ID").getHeaderValue().trim();
						neid.add(n);
						
					}
					
				}
				
				String neIDs = String.join("|", neid);
				resultMap.put("neIDs",neIDs);
				resultMap.put("isPartialReport", isPartialReport);
				resultMap.put("siteDetails", automated);
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("sessionId", sessionId);
				resultMap.put("serviceToken", serviceToken);
				}
			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_READ_CONFIGURATION_DETAILS));
			return resultMap;
		}

		return resultMap;
	}
	
	/**
	 * This method will get the getSiteReportInputDetails
	 * 
	 * @param params
	 * @return JSONObject
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/getSiteDetailsById", method = RequestMethod.POST)

	public JSONObject getSiteDetailsById(@RequestBody JSONObject params) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		String programName = null;
		Integer siteDataId = null;

		try {
			sessionId = params.get("sessionId").toString();
			serviceToken = params.get("serviceToken").toString();
			programId = (Integer) params.get("programId");
			programName = params.get("programName").toString();
			siteDataId = (Integer) params.get("siteDataId");
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			if(ObjectUtils.isEmpty(siteDataId))
			{
			 resultMap=getSiteReportInputDetails(params);
			}else {
			resultMap=siteDetailsReportService.getSiteDetailsById(siteDataId);
			
			
			
			ProgramTemplateEntity programTemplateEntity = fileUploadService.getProgramTemplate(programId,
					Constants.SITE_REPORT_INPUTS_TEMPLATE);
			if (!ObjectUtils.isEmpty(programTemplateEntity)) {
				resultMap.put("siteInputs", programTemplateEntity.getValue());
			} else {
				resultMap.put("siteinputs", null);
			}
			
			if(params.containsKey("neDetails") && !ObjectUtils.isEmpty(params.get("neDetails")))
			{
			List<Map<String, String>> enbList =(List<Map<String, String>>)params.get("neDetails");
			String neId = enbList.get(0).get("enbId");
			if(programName.equals("VZN-5G-DSS")) {
				List<Audit5GDSSSummaryEntity> audit5GDSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
				List<Audit4GSummaryModel> audit5GSummaryModelList = audit5GDSSSummaryDto.getAudit5GDSSSummaryReportModelList(audit5GDSummaryEntityList);
				resultMap.put("postAuditIssues", audit5GSummaryModelList);
				}
				else if(programName.equals("VZN-5G-CBAND")) {
					List<Audit5GCBandSummaryEntity> audit5GCBANDSummaryEntityList = audit5GCBANDSummaryService.getAudit5GCBandSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
					List<Audit4GSummaryModel> audit5GCBANDSummaryModelList = audit5GCBandSummaryDto.getAudit5GCBandSummaryReportModelList(audit5GCBANDSummaryEntityList);
					resultMap.put("postAuditIssues", audit5GCBANDSummaryModelList);
					}
				else if(programName.equals("VZN-4G-USM-LIVE")) {
					List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
					List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GSummaryDto.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
					
					resultMap.put("postAuditIssues", audit4GSummaryModelList);
					}
				else if(programName.equals("VZN-4G-FSU")) {
					List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = audit4GFsuSummaryService.getAudit4GFsuSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
					List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GFsuSummaryDto.getAudit4GFsuSummaryReportModelList(audit4GFsuSummaryEntityList);
	
					resultMap.put("postAuditIssues", audit4GSummaryModelList);
					}
			
			//List<Audit5GDSSSummaryEntity> audit5GDSummaryEntityList = audit5GDSSSummaryService.getAudit5GDSSSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
			//List<Audit4GSummaryModel> audit5GSummaryModelList = audit5GDSSSummaryDto.getAudit5GDSSSummaryReportModelList(audit5GDSummaryEntityList);
			
			//List<Audit4GSummaryEntity> audit4GSummaryEntityList = audit4GSummaryService.getAudit4GSummaryEntityListByNeId(neId.replaceAll("^0+(?!$)", ""));			
			//List<Audit4GSummaryModel> audit4GSummaryModelList = audit4GSummaryDto.getAudit4GSummaryReportModelList(audit4GSummaryEntityList);
			
			//resultMap.put("postAuditIssues", audit5GSummaryModelList);
			}else {
				resultMap.put("postAuditIssues", null);
			}
			
			
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_READ_CONFIGURATION_DETAILS));
			return resultMap;
		}

		return resultMap;
	}
	
	
	
	/**
	 * This method will get the getSiteReportInputDetails
	 * 
	 * @param params
	 * @return JSONObject
	 */
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/getHistorySiteDetails", method = RequestMethod.POST)

	public JSONObject getHistorySiteDetails(@RequestBody JSONObject params) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		String programName = null;
		String neId = null;

		try {
			sessionId = params.get("sessionId").toString();
			serviceToken = params.get("serviceToken").toString();
			programId = (Integer) params.get("programId");
			programName = params.get("programName").toString();
			neId = params.get("neId").toString();;
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			
			List<SiteDataModel> siteDataModels =siteDetailsReportService.getHistorySiteDetails(neId);
			
			resultMap.put("historyDetails", siteDataModels);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_READ_CONFIGURATION_DETAILS));
			return resultMap;
		}

		return resultMap;
	}
	//*******************************************************************************************************************************************//
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@RequestMapping(value = "/downloadBulkSiteReport", method = RequestMethod.POST)

	public void getHistorySiteDetailsby(@RequestBody JSONObject params, HttpServletResponse response) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		Integer programId = null;
		String programName = null;
		String neId = null;
	

		try {
			sessionId = params.get("sessionId").toString();
			serviceToken = params.get("serviceToken").toString();
			programId = (Integer) params.get("programId");
			programName = params.get("programName").toString();
			
		     Date FromDate=DateUtil.stringToDate((String) (params.get("fromDate")), Constants.MM_DD_YYYY);
		     Date ToDate=DateUtil.stringToDate((String) (params.get("toDate")), Constants.MM_DD_YYYY);
		     ToDate.setTime(ToDate.getTime() + TimeUnit.HOURS.toMillis(23)); 
		     ToDate.setTime(ToDate.getTime() + TimeUnit.MINUTES.toMillis(59));
		     ToDate.setTime(ToDate.getTime() + TimeUnit.SECONDS.toMillis(59));
			//neId = params.get("neId").toString();
			// check if session expired
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				//return expiryDetails;fromDate toDate
			}
			String Name = "";
			int sourceProgramId =programId;
			List<SiteDataModel> siteDataModels =siteDetailsReportService.getDonldSiteDetails( sourceProgramId,FromDate,ToDate );
			List<Map<String, String>> listDetails=new ArrayList<>();
			Map<String, String> mapDetails=new HashMap<>();
			ArrayList<String> ar = new ArrayList<String>();
			for(SiteDataModel SiteDataEntity:siteDataModels)
			{
				
				mapDetails.put("fileName", SiteDataEntity.getFileName());
				mapDetails.put("filePath", SiteDataEntity.getFilePath());
				String NE =mapDetails.put("fileName", SiteDataEntity.getFileName());
			    Name=mapDetails.put("filePath", SiteDataEntity.getFilePath());
				System.out.println(Name);
				listDetails.add(mapDetails);
				ar.add(NE);
			}
			
			String result =String.join(",", ar);
			resultMap.put("historyDetails", siteDataModels);
			resultMap.put("status", Constants.SUCCESS);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			ArrayList<String> zipFileName=new ArrayList<String>();
			ArrayList<String> fPath=new ArrayList<String>();
			StringBuilder uploadPath = new StringBuilder();

				String fileName = null;
				String filePath = null;
				;
				try {
					
					sessionId = resultMap.get("sessionId").toString();
					serviceToken = resultMap.get("serviceToken").toString();
					fileName = result;
					filePath = Name;
					filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath;
					FileInputStream inputStream = null;
					OutputStream outStream = null;
					File downloadFile = null;
					String fileAbsolutepath = "";
					String zipFileFolderpath = "";
					String zipFilepath = "";
					File zipFileDir = null;
					File zipFile = null;
					try {
						if (fileName.contains(",")) {
							if (filePath.endsWith(Constants.SEPARATOR)) {
								Date now = new Date();
								SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMDD_hh_mm_ss");
								String time = dateFormat.format(now);
								zipFileFolderpath = filePath + programName+"_"+sessionId+"_"+time;
							} else {
								zipFileFolderpath = filePath + Constants.SEPARATOR + "download";
							}
							String filenames[] = fileName.split(",");
							zipFileDir = new File(zipFileFolderpath);
							if (!zipFileDir.exists()) {
								FileUtils.forceMkdir(zipFileDir);
							}
							for (String name : filenames) {
								if (CommonUtil.isValidObject(name) && name.trim().length() > 0) {
									if (filePath.endsWith(Constants.SEPARATOR)) {
										File file = new File(filePath + name);
										FileUtils.copyFileToDirectory(file, zipFileDir);
									} else {
										File file = new File(filePath + Constants.SEPARATOR + name);
										FileUtils.copyFileToDirectory(file, zipFileDir);
									}
								}
							}
							zipFilepath = zipFileFolderpath + ".zip";
							boolean status = CommonUtil.createZipFileOfDirectory(zipFilepath.toString(), zipFileFolderpath);
							if (!status) {
								logger.info("downloadFile() file not found:" + fileAbsolutepath);
								response.setContentType("application/json");
								PrintWriter out = response.getWriter();
								out.println("{\"status\": \"File Not Found\"}");
								out.close();
								
							}
							downloadFile = new File(zipFilepath);
							fileAbsolutepath = zipFilepath;
						} else {
							if (filePath.endsWith(Constants.SEPARATOR)) {
								fileAbsolutepath = filePath + fileName;
								downloadFile = new File(fileAbsolutepath);
							} else {
								fileAbsolutepath = filePath + Constants.SEPARATOR + fileName;
								downloadFile = new File(fileAbsolutepath);
							}
						}
						logger.info("downloadFile() file path:" + fileAbsolutepath);

						if (!downloadFile.exists()) {
							logger.info("downloadFile() file not found:" + fileAbsolutepath);
							response.setContentType("application/json");
							PrintWriter out = response.getWriter();
							out.println("{\"status\": \"File Not Found\"}");
							out.close();
							
						}
						inputStream = new FileInputStream(downloadFile);
						// MIME type of the file
						String mimeType = "application/octet-stream";
						// set content attributes for the response
						response.setContentType(mimeType);
						response.setContentLength((int) downloadFile.length());
						// set headers for the response
						String headerKey = "Content-Disposition";
						String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
						response.setHeader(headerKey, headerValue);
						// get output stream of the response
						outStream = response.getOutputStream();
						byte[] buffer = new byte[4096];
						int bytesRead = -1;
						// write bytes read from the input stream into the output stream
						while ((bytesRead = inputStream.read(buffer)) != -1) {
							outStream.write(buffer, 0, bytesRead);
						}
						outStream.flush();
						inputStream.close();
						outStream.close();
						/*if (CommonUtil.isValidObject(zipFileFolderpath) && CommonUtil.isValidObject(zipFilepath)) {
							FileUtil.deleteFileOrFolder(zipFileFolderpath);
							FileUtil.deleteFileOrFolder(zipFilepath);
						}*/
					} catch (FileNotFoundException fne) {
						logger.error("Exception in NetworkConfigController.downloadFile(): "
								+ ExceptionUtils.getFullStackTrace(fne));
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						out.println("{\"status\": \"File Not Found\"}");
						out.close();
					} catch (Exception e) {
						logger.error(
								"Exception in NetworkConfigController.downloadFile(): " + ExceptionUtils.getFullStackTrace(e));
						response.setContentType("application/json");
						PrintWriter out = response.getWriter();
						out.println("{\"status\": \"Error While Downloading\"}");
						out.close();
					} finally {
						try {
							if (inputStream != null) {
								inputStream.close();
							}
						} catch (IOException e) {
							logger.error("Exception in NetworkConfigController.downloadFile(): "
									+ ExceptionUtils.getFullStackTrace(e));
						}
					}
				} catch (Exception e) {
					logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(e));
					response.setContentType("application/json");
					PrintWriter out = response.getWriter();
					out.println("{\"status\": \"Error While Downloading\"}");
					out.close();
				}
			
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_READ_CONFIGURATION_DETAILS));
		
		}

		
	}
}