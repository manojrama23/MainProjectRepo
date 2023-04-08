package com.smart.rct.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.smart.rct.common.dto.NetworkConfigDto;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NeVersionService;
import com.smart.rct.common.service.NetworkConfigService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@RestController
public class NetworkConfigController {

	final static Logger logger = LoggerFactory.getLogger(NetworkConfigController.class);

	@Autowired
	NetworkConfigService networkConfigService;

	@Autowired
	NetworkConfigDto networkConfigDto;

	@Autowired
	CommonUtil commonUtil;

	@Autowired
	CustomerService customerService;

	@Autowired
	NeVersionService neVersionService;

	/**
	 * This method will Save/Update Network Config Details to DB
	 * 
	 * @param networkConfigDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = Constants.SAVE_NETWORK_CONFIG, method = RequestMethod.POST)
	public JSONObject saveNetworkConfigDetails(@RequestBody JSONObject networkConfigDetails) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		String sessionId = null;
		String serviceToken = null;
		String updateIP ;
		String currentIp=null;
		String ip=null;;
		try {
			sessionId = networkConfigDetails.get("sessionId").toString();
			serviceToken = networkConfigDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			updateIP=networkConfigDetails.get("updateIp").toString();
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			NetworkConfigModel networkConfigModel = new Gson().fromJson(
					JSONObject.toJSONString((Map) networkConfigDetails.get("networkConfigDetails")),
					NetworkConfigModel.class);
			if (networkConfigService.duplicateNetworkConfig(networkConfigModel)) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ALREADY_EXIST));
				return resultMap;
			}
			NetworkConfigEntity networkConfigEntity = networkConfigDto.getNetworkConfigEntity(networkConfigModel,
					sessionId);
			if(updateIP.equals("true")) {
				
				int programeID=networkConfigEntity.getProgramDetailsEntity().getId();
				int id1= networkConfigEntity.getId();
				
				//String currentIp=networkConfigEntity.getNeDetails().get(0).getServerIp();
				
				List<NetworkConfigDetailsEntity> list=networkConfigEntity.getNeDetails();
				for(int i=0;i< list.size();i++) {
					if(list.get(i).getServerTypeEntity().getServerType().equals("SANE"))
					{		currentIp=list.get(i).getServerIp();
								System.out.println(currentIp);
								}
				}
				
				List<NetworkConfigEntity> n =networkConfigService.getNetworkConfigList(programeID);
				for(NetworkConfigEntity entry:n) {
					
					int id =entry.getId();
					if(id==id1)
					{
						List<NetworkConfigDetailsEntity> n1=entry.getNeDetails();
						for(int i=0;i< n1.size();i++) {
							if(n1.get(i).getServerTypeEntity().getServerType().equals("SANE"))
							{		ip=n1.get(i).getServerIp();
										System.out.println(ip);
										}
						}
					}
					
					
				}
				if(ip!=null) {
				
				for(NetworkConfigEntity entry:n) {
					
						List<NetworkConfigDetailsEntity> n1=entry.getNeDetails();
						for(int i=0;i< n1.size();i++) {
							if(n1.get(i).getServerTypeEntity().getServerType().equals("SANE"))
							{		String ip2=n1.get(i).getServerIp();
									if(ip2.equals(ip)) {
										n1.get(i).setServerIp(currentIp);
										networkConfigService.createNetworkConfig(entry);
										}
						
							}
					
					
				}
				}
				
			}
			}
			if (networkConfigEntity != null) {
				if (networkConfigService.createNetworkConfig(networkConfigEntity)) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.NWCONFIG_DETAILS_CREATED_SUCCESSFULLY));
					if (networkConfigModel.getId() != null) {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG, Constants.ACTION_UPDATE,
								"Network Config Details Updated Successfully For: " + networkConfigModel.getNeName(),
								sessionId);
					} else {
						commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS,
								Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG, Constants.ACTION_SAVE,
								"Network Config Details Created Successfully For: " + networkConfigModel.getNeName(),
								sessionId);
					}
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_NWCONFIG_DETAILS));
				}
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.saveNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_ADD_NWCONFIG_DETAILS));
			return resultMap;
		}

	}

	/**
	 * This api will Get NetworkConfigDetails
	 * 
	 * @param networkConfigDetails
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@RequestMapping(value = Constants.GET_NETWORK_CONFIG_LIST, method = RequestMethod.POST)
	public JSONObject getNetworkConfigDetails(@RequestBody JSONObject networkConfigDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		Integer customerId = null;
		String searchStatus = null;
		NetworkConfigModel networkConfigModel = null;
		List<CustomerDetailsEntity> programNamesList = null;
		try {
			sessionId = networkConfigDetails.get("sessionId").toString();
			serviceToken = networkConfigDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			searchStatus = networkConfigDetails.get("searchStatus").toString();
			if (StringUtils.isNotEmpty(networkConfigDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(networkConfigDetails.get("customerId").toString());
			}
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			Map<String, Integer> paginationData = (Map<String, Integer>) networkConfigDetails.get("pagination");
			int page = paginationData.get("page");
			int count = paginationData.get("count");
			if ("search".equals(searchStatus)) {
				networkConfigModel = new Gson().fromJson(
						networkConfigDetails.toJSONString((Map) networkConfigDetails.get("searchDetails")),
						NetworkConfigModel.class);
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(customerId);
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("programNamesList", customerService.getCustomerDetailsList(customerDetailsModel));
				programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
			} else {
				resultMap.put("programNamesList", customerService.getProgramDetailsList(user));
				programNamesList = customerService.getProgramDetailsList(user);
			}
			Map<String, Object> objNetworkConfigMap = networkConfigService.getNetworkConfigDetails(networkConfigModel,
					page, count, programNamesList);
			resultMap.put("neTypeList", objNetworkConfigMap.get("neTypeList"));
			resultMap.put("loginTypeList", objNetworkConfigMap.get("loginTypeList"));
			resultMap.put("serverTypeList", objNetworkConfigMap.get("serverTypeList"));
			NeVersionModel neVersionModel = new NeVersionModel();
			neVersionModel.setStatus(Constants.ACTIVE);
			List<NeVersionEntity> neVersionEntities = neVersionService.getNeVersionList(neVersionModel);
			resultMap.put("neversionList", neVersionEntities);

			if (objNetworkConfigMap != null && objNetworkConfigMap.size() > 0) {
				List<NetworkConfigModel> networkConfigModelList = new ArrayList<NetworkConfigModel>();
				resultMap.put("neNameList", objNetworkConfigMap.get("neNameList"));
				resultMap.put("neMarketList", objNetworkConfigMap.get("neMarketList"));
				List<NetworkConfigEntity> networkConfigList = (List<NetworkConfigEntity>) objNetworkConfigMap
						.get("networkConfigList");
				if (networkConfigList != null && networkConfigList.size() > 0) {
					for (NetworkConfigEntity networkConfigEntity : networkConfigList) {
						List<String> programs = programNamesList.stream().map(x -> x.getProgramName())
								.collect(Collectors.toList());
						if (CommonUtil.isValidObject(programs) && programs.size() > 0 && programs
								.contains(networkConfigEntity.getProgramDetailsEntity().getProgramName().trim())) {
							networkConfigModelList.add(networkConfigDto.getNetworkConfigModel(networkConfigEntity));
						}
					}
				}
				resultMap.put("networkConfigList", networkConfigModelList);
				resultMap.put("pageCount", objNetworkConfigMap.get("pageCount"));
			}
			resultMap.put("status", Constants.SUCCESS);
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in NetworkConfigController.getNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_GET_NWCONFIG_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This api will delete NetworkConfigDetails
	 * 
	 * @param networkConfigDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_NETWORK_CONFIG, method = RequestMethod.POST)
	public JSONObject deleteNetworkConfigDetails(@RequestBody JSONObject networkConfigDetails) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String networkConfigId = null;
		try {
			sessionId = networkConfigDetails.get("sessionId").toString();
			serviceToken = networkConfigDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);

			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			networkConfigId = networkConfigDetails.get("id").toString();
			NetworkConfigModel networkConfigModel = new NetworkConfigModel();
			networkConfigModel.setId((Integer.valueOf(networkConfigId)));
			List<NetworkConfigEntity> list = networkConfigService.getNetworkConfigDetails(networkConfigModel);
			if (CommonUtil.isValidObject(list) && CommonUtil.isValidObject(networkConfigId)) {
				if (networkConfigService.deleteNetworkConfigDetails((Integer.valueOf(networkConfigId)))) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.NWCONFIG_DETAILS_DELETED_SUCCESSFULLY));
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG,
							Constants.ACTION_DELETE,
							"Network Config Details Deleted Successfully For: " + list.get(0).getNeName(), sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NWCONFIG_DETAILS));
				}
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in NetworkConfigController.deleteNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NWCONFIG_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This method will Upload Network Config Details to DB
	 * 
	 * @param file
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.IMPORT_NETWORK_CONFIG, method = RequestMethod.POST)
	public JSONObject importNetworkConfigDetails(@RequestParam("networkConfigFile") MultipartFile file,
			String sessionId, String serviceToken) {
		JSONObject resultMap = new JSONObject();
		JSONObject expiryDetails = null;
		Integer customerId = null;
		List<CustomerDetailsEntity> programNamesList = null;
		try {
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			customerId = user.getCustomerId();
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(customerId);
				customerDetailsModel.setCustomerEntity(customerEntity);
				resultMap.put("programNamesList", customerService.getCustomerDetailsList(customerDetailsModel));
				programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
			} else {
				resultMap.put("programNamesList", customerService.getProgramDetailsList(user));
				programNamesList = customerService.getProgramDetailsList(user);
			}
			List<String> programs = programNamesList.stream().map(x -> x.getProgramName()).collect(Collectors.toList());
			resultMap = networkConfigService.importNetworkConfigDetails(file, programs, sessionId);
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG,
					Constants.ACTION_UPLOAD,
					"Network Details File Uploaded Successfully From File: " + file.getOriginalFilename(), sessionId);
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.importNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_NWCONFIG_DETAILS));
			return resultMap;
		}
		return resultMap;
	}

	/**
	 * This api deletes the network config server details
	 * 
	 * @param networkConfigFile
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.DELETE_NETWORK_CONFIG_SERVER_DETAILS, method = RequestMethod.POST)
	public JSONObject deleteNetworkConfigServerDetails(@RequestBody JSONObject networkConfigFile) {
		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		String networkConfigDetailId = null;
		try {
			sessionId = networkConfigFile.get("sessionId").toString();
			serviceToken = networkConfigFile.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			networkConfigDetailId = networkConfigFile.get("id").toString();
			NetworkConfigDetailsEntity networkConfigDetailsEntity = networkConfigService
					.getNetworkConfigServerDetailsById(Integer.parseInt(networkConfigDetailId));
			if (CommonUtil.isValidObject(networkConfigDetailsEntity) && CommonUtil.isValidObject(networkConfigDetailId)
					&& networkConfigService.deleteNetworkConfigServerDetails(Integer.parseInt(networkConfigDetailId))) {
				resultMap.put("status", Constants.SUCCESS);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.DELETED_NWCONFIG_DETAILS_SUCCESSFULLY));
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_GENERAL_CONFIG,
						Constants.ACTION_DELETE,
						"Network Config Details Deleted Successfully: " + networkConfigDetailsEntity.getServerName()
								+ " From: " + networkConfigDetailsEntity.getNetworkConfigEntity().getNeName(),
						sessionId);
			} else {
				resultMap.put("status", Constants.FAIL);
			}
		} catch (Exception e) {
			resultMap.put("status", Constants.FAIL);
			logger.error("Exception in NetworkConfigController.deleteNetworkConfigServerDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_DELETE_NWCONFIG_SERVER_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This api exports the network config details to excel
	 * 
	 * @param networkconfigDownloadDetails
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = Constants.EXPORT_NETWORK_CONFIG, method = RequestMethod.POST)
	public void exportNetworkConfigDetails(@RequestBody JSONObject networkconfigDownloadDetails,
			HttpServletResponse response) throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		String sessionId = null;
		String serviceToken = null;
		Integer customerId = null;
		List<CustomerDetailsEntity> programNamesList = null;
		NetworkConfigModel networkConfigModel = null;
		String searchStatus = null;
		try {
			sessionId = networkconfigDownloadDetails.get("sessionId").toString();
			serviceToken = networkconfigDownloadDetails.get("serviceToken").toString();

			if (StringUtils.isNotEmpty(networkconfigDownloadDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(networkconfigDownloadDetails.get("customerId").toString());
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(customerId);
				customerDetailsModel.setCustomerEntity(customerEntity);
				programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
			} else {
				programNamesList = customerService.getProgramDetailsList(user);
			}
			List<String> programs = programNamesList.stream().map(x -> x.getProgramName()).collect(Collectors.toList());
			if (networkconfigDownloadDetails.get("searchStatus")!=null && !"".equals(networkconfigDownloadDetails.get("searchStatus"))) {
				searchStatus = networkconfigDownloadDetails.get("searchStatus").toString();
				if("search".equals(searchStatus)) {
					networkConfigModel = new Gson().fromJson(
							networkconfigDownloadDetails.toJSONString((Map) networkconfigDownloadDetails.get("searchDetails")),
							NetworkConfigModel.class);
				}else if("load".equals(searchStatus)){
					networkConfigModel = new NetworkConfigModel();
				}
			}else {
				networkConfigModel = new NetworkConfigModel();
			}
			
			//if (networkConfigService.getNetWorkDetailsForCreateExcel(new NetworkConfigModel(), programs, false)) {
			if (networkConfigService.getNetWorkDetailsForCreateExcel(networkConfigModel, programs, false)) {
				filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
						.append(Constants.NETWORKCONFIG_XLSX);
				downloadFile = new File(filePath.toString());
				if (!downloadFile.exists()) {
					logger.info("downloadFile() file not found:" + filePath.toString());
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
				response.setHeader("sessionId", networkconfigDownloadDetails.get("sessionId").toString());
				response.setHeader("serviceToken", networkconfigDownloadDetails.get("serviceToken").toString());
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
				commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG,
						Constants.ACTION_EXPORT, "Network Config Details File Exported Successfully", sessionId);
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
			logger.error("Exception in NetworkConfigController.exportNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \""
					+ GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_EXPORT_NETWORK_CONFIG) + "\"}");
			out.close();
			return;
		}
		return;
	}

	/**
	 * This method will check whether nw config zip available or not
	 * 
	 * @param networkconfigDownloadDetails
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = Constants.ZIP_AVAILABLE, method = RequestMethod.POST)
	public JSONObject isZipAvilableForNwConfig(@RequestBody JSONObject networkconfigDownloadDetails) {

		String sessionId = null;
		String serviceToken = null;
		JSONObject expiryDetails = null;
		JSONObject resultMap = new JSONObject();
		NetworkConfigModel objNetworkConfigModel = null;
		Integer customerId = null;
		List<CustomerDetailsEntity> programNamesList = null;
		try {
			sessionId = networkconfigDownloadDetails.get("sessionId").toString();
			serviceToken = networkconfigDownloadDetails.get("serviceToken").toString();
			resultMap.put("sessionId", sessionId);
			resultMap.put("serviceToken", serviceToken);
			if (StringUtils.isNotEmpty(networkconfigDownloadDetails.get("customerId").toString())) {
				customerId = Integer.valueOf(networkconfigDownloadDetails.get("customerId").toString());
			}
			// check session TimeOut
			expiryDetails = CommonUtil.getSessionExpirationDetails(sessionId);
			if (expiryDetails != null) {
				return expiryDetails;
			}
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			if (user.getRoleId() <= 3) {
				CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
				CustomerEntity customerEntity = new CustomerEntity();
				customerEntity.setId(customerId);
				customerDetailsModel.setCustomerEntity(customerEntity);
				programNamesList = customerService.getCustomerDetailsList(customerDetailsModel);
			} else {
				programNamesList = customerService.getProgramDetailsList(user);
			}
			List<String> programs = programNamesList.stream().map(x -> x.getProgramName()).collect(Collectors.toList());
			try {
				if (networkConfigService.getNetWorkDetailsForCreateExcel(objNetworkConfigModel, programs, true)) {
					resultMap.put("status", Constants.SUCCESS);
					commonUtil.saveAudit(Constants.EVENT_CONFIGURATIONS, Constants.EVENT_CONFIGURATIONS_NETWORK_CONFIG,
							Constants.ACTION_EXPORT, "Network Config Details File Exported Successfully", sessionId);
				} else {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ZIP_NOT_AVAILABLE));
				}
			} catch (Exception e) {
				resultMap.put("status", Constants.FAIL);
				resultMap.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ZIP_NOT_AVAILABLE));
				logger.error("Exception while checking for PI .zip file:" + ExceptionUtils.getFullStackTrace(e));
			}
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.isZipAvilableForNwConfig(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ZIP_NOT_AVAILABLE));
			return resultMap;
		}
		return resultMap;
	}

	/**
	 * This method will download n/w config zip
	 * 
	 * @param networkconfigDownloadDetails
	 * @return
	 */
	@RequestMapping(value = Constants.DOWNLOAD_NETWORK_CONFIG, method = RequestMethod.POST)
	public void downloadNwConfigZip(@RequestBody JSONObject networkconfigDownloadDetails, HttpServletResponse response)
			throws IOException {
		FileInputStream inputStream = null;
		OutputStream outStream = null;
		StringBuilder filePath = new StringBuilder();
		File downloadFile = null;
		try {
			filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
					.append(Constants.NETWORKCONFIG_ZIP);
			downloadFile = new File(filePath.toString());
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
			response.setHeader("sessionId", networkconfigDownloadDetails.get("sessionId").toString());
			response.setHeader("serviceToken", networkconfigDownloadDetails.get("serviceToken").toString());
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
		} catch (FileNotFoundException fne) {
			logger.error("Exception in download EXcel :" + ExceptionUtils.getFullStackTrace(fne));
			response.setContentType("application/json");
			response.setHeader("sessionId", networkconfigDownloadDetails.get("sessionId").toString());
			response.setHeader("serviceToken", networkconfigDownloadDetails.get("serviceToken").toString());
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"File Not Found\"}");
			out.close();
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.downloadNwConfigZip(): "
					+ ExceptionUtils.getFullStackTrace(e));
			response.setContentType("application/json");
			response.setHeader("sessionId", networkconfigDownloadDetails.get("sessionId").toString());
			response.setHeader("serviceToken", networkconfigDownloadDetails.get("serviceToken").toString());
			PrintWriter out = response.getWriter();
			out.println("{\"status\": \"Error While Downloading\"}");
			out.close();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error("Exception in NetworkConfigController.downloadNwConfigZip(): "
						+ ExceptionUtils.getFullStackTrace(e));
			}

		}
	}

	/**
	 * This method will add file to zip
	 * 
	 * @param fileName
	 * @return
	 */
	public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {
		try {
			logger.info("Adding to zip file: " + fileName);
			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(fileName);
			zos.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
			fis.close();
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigController.addToZipFile(): " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * This method will download the file
	 * 
	 * @param fileDetails
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = Constants.DOWNLOAD_FILE, method = RequestMethod.POST)
	public void downloadFile(@RequestBody JSONObject fileDetails, HttpServletResponse response) throws IOException {
		String fileName = null;
		String filePath = null;
		String sessionId = null;
		String serviceToken = null;
		try {
			sessionId = fileDetails.get("sessionId").toString();
			serviceToken = fileDetails.get("serviceToken").toString();
			fileName = fileDetails.get("fileName").toString();
			filePath = fileDetails.get("filePath").toString();
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
						zipFileFolderpath = filePath + "download";
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
						return;
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
				if (CommonUtil.isValidObject(zipFileFolderpath) && CommonUtil.isValidObject(zipFilepath)) {
					FileUtil.deleteFileOrFolder(zipFileFolderpath);
					FileUtil.deleteFileOrFolder(zipFilepath);
				}
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
	}
}
