package com.smart.rct.common.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.repository.SiteDetailsReportRepository;
import com.smart.rct.common.service.SiteDetailsReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.serviceImpl.RunTestServiceImpl;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;


@Component
public class SiteReportToOV {
	@Autowired
	FileUploadRepository fileUploadRepository;
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	GenerateCsvService objGenerateCsvService;
	@Autowired
	RunTestRepository runTestRepository;
	@Autowired
	SiteDetailsReportRepository siteDetailsReportRepository;
	@Autowired
	SiteDetailsReportService siteDetailsReportService;
	
	final static Logger logger = LoggerFactory.getLogger(RunTestServiceImpl.class);
	@SuppressWarnings("unchecked")
	public JSONObject getTrakerIdList(JSONObject ovUpdateJson, SiteDataEntity statusSiteDataEntity) {
		// TODO Auto-generated method stub
		JSONObject objTrackerIdDetails = new JSONObject();
		String URL = null;
		try {
			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(objOvGeneralEntity.getValue());
			String authStr = getAuthencationString();
			String enbName=ovUpdateJson.get("enbName").toString();
			String programName =ovUpdateJson.get("programName").toString();
			String programId =ovUpdateJson.get("programId").toString();
			String ciqFileName =ovUpdateJson.get("ciqFileName").toString();
			String neId = ovUpdateJson.get("enbId").toString().replaceAll("^0+(?!$)", "");
			
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId),ciqFileName);
			
			String  FuzeProjectID="";//=listCIQDetailsModel.get(0).getCiqMap().get("EquipmentLocation").getHeaderValue();
			System.out.println(FuzeProjectID);
				if (programName.contains("MM")) {
					String[] arrOfstring=neId.split("\\|");
					 neId=arrOfstring[0];
					List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, neId, enbName, dbcollectionFileName, "5GNRCIQAU", "");

					FuzeProjectID=listCIQDetailsModelDay01.get(0).getCiqMap().get("FuzeProjId").getHeaderValue();
					URL = Constants.OV_TRACKERID_URL +"P_FUZE_PROJECT_ID_FZ="+FuzeProjectID+"&P_5G_MMW_SCOPE=1";
						
				}
				if (programName.contains("USM-LIVE")) {
					List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, neId, enbName, dbcollectionFileName, "CIQUpstateNY", "");
					FuzeProjectID=listCIQDetailsModelDay01.get(0).getCiqMap().get("FuzeProjId").getHeaderValue();
					//FuzeProjectID="2718124";
					URL = Constants.OV_TRACKERID_URL +"P_SPMS_SITE_ID="+FuzeProjectID+"&P_4G_LTE_SCOPE=1";

				}
				if (programName.contains("DSS")) {
					List<CIQDetailsModel> listCIQDetailsModelDay1 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, neId, enbName, dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "");
					FuzeProjectID=listCIQDetailsModelDay1.get(0).getCiqMap().get("EquipmentLocation").getHeaderValue();
					URL = Constants.OV_TRACKERID_URL  +"P_FUZE_PROJECT_ID_FZ="+FuzeProjectID+"&P_5G_DSS_SCOPE=1";
					//URL = Constants.OV_TRACKERID_URL + "view=L:SRCTDSS&filter=L:SRCTDSS"+"D-0";
				}
				if (programName.contains("CBAND")) {
					List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, neId, enbName, dbcollectionFileName, "Day0_1", "");
					FuzeProjectID=listCIQDetailsModelDay01.get(0).getCiqMap().get("fuzeProjectId").getHeaderValue();
					URL = Constants.OV_TRACKERID_URL + "view=L:SRCT_SN_CBAND_AUDIT&page=1&per_page=1&Project.P_FUZE_PROJECT_ID_FZ="+FuzeProjectID;
					
				}
				if (programName.contains("FSU")) {
				List<CIQDetailsModel> listCIQDetailsModelDay01 = fileUploadRepository.getEnbTableDetailsRanConfig(ciqFileName, neId, enbName, dbcollectionFileName, "FSUCIQ", "");
				FuzeProjectID=listCIQDetailsModelDay01.get(0).getCiqMap().get("FuzeProjId").getHeaderValue();
					// view=L%3ASRCTCBAND&filter=L%3ASRCTCBANDD5'
					URL = Constants.OV_TRACKERID_URL +"P_FUZE_PROJECT_ID_FZ="+FuzeProjectID+"&P_4G_LTE_SCOPE=1";
							//P_FUZE_PROJECT_ID_FZ=16228992&P_4G_LTE_SCOPE=1
				}
				List<TrackerDetailsModel> listTrackerDetailsModel = new ArrayList<>();
				if(!FuzeProjectID.isEmpty()&&FuzeProjectID !=null) {
			urlBuilder.append(URL);
			String trakerIdUrl = urlBuilder.toString();
			logger.error("Tracker URL: "+trakerIdUrl);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			// create headers
			headers.add("Authorization", "Basic " + base64Creds);
			HttpEntity requestEntity = new HttpEntity<>(headers);
			HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
			
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			logger.error("RequestEntity:  from SRCT",requestEntity);
			
			ResponseEntity<String> response = restTemplate.exchange(trakerIdUrl, HttpMethod.GET, requestEntity,
					String.class);
			logger.error("1st Api Response :",response);
			System.out.println(response);
			int statusCode = response.getStatusCodeValue();

			if (200 == statusCode) {
				String trackerIdJson = response.getBody();
				List<HashMap<String, Object>> trakerList = new ObjectMapper().readValue(trackerIdJson,
						new TypeReference<List<HashMap<String, Object>>>() {
						});
				logger.error("OV Responce Json :"+ trackerIdJson);
				
				

				if (!ObjectUtils.isEmpty(trakerList)) {
					for (HashMap<String, Object> entryData : trakerList) {
						if (entryData.containsKey("TRACKOR_ID")
								&& StringUtils.isNotEmpty(entryData.get("TRACKOR_ID").toString())) {
							TrackerDetailsModel trackerDetailsModel = new TrackerDetailsModel();
							trackerDetailsModel.setTrackerId(entryData.get("TRACKOR_ID").toString());
							listTrackerDetailsModel.add(trackerDetailsModel);
						}
					}
				}

				objTrackerIdDetails.put("trakerjson", listTrackerDetailsModel);
				objTrackerIdDetails.put("statusCode", response.getStatusCode());
			}
				}else {
					//objTrackerIdDetails.put("trakerjson", listTrackerDetailsModel);
					objTrackerIdDetails.put("reason", "Fuze ID is Empty for "+neId);
				}
		} catch (HttpClientErrorException e) {
			objTrackerIdDetails.put("sta'tusCode", e.getStatusCode());
			objTrackerIdDetails.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.error("error:"+ e);
			logger.info(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return objTrackerIdDetails;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getWorkPlanIdList(String trackerID) {
		JSONObject objWorkplanIdDetails = new JSONObject();
		String URL = null;
		try {
			
			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
			StringBuilder urlBuilder = new StringBuilder();
			urlBuilder.append(objOvGeneralEntity.getValue());
			urlBuilder.append(Constants.OV_WORKPLANID_URL);
			urlBuilder.append("?");
			urlBuilder.append("wp_template=");
			urlBuilder.append("Project Workplan v2");
			urlBuilder.append("&");
			urlBuilder.append("trackor_id=");
			urlBuilder.append(trackerID);
			urlBuilder.append("&");
			urlBuilder.append("page=");
			urlBuilder.append("1");
			String trakerIdUrl = urlBuilder.toString();
			logger.error("2nd URL: "+trakerIdUrl);
			String authStr = getAuthencationString();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			// create headers
			headers.add("Authorization", "Basic " + base64Creds);
			@SuppressWarnings("rawtypes")
			HttpEntity requestEntity = new HttpEntity(headers);
			HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			ResponseEntity<String> response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET,
					requestEntity, String.class);
			int statusCode = response.getStatusCodeValue();

			
			if (200 == statusCode) {
				String trackerIdJson = response.getBody();
				
				logger.error("2nd Api Responce before ObjectMapper :"+ response);
				System.out.println(response);
				List<HashMap<String, String>> workPlanList = new ObjectMapper().readValue(trackerIdJson,
						new TypeReference<List<HashMap<String, String>>>() {
						});
				TrackerDetailsModel trackerDetailsModel = new TrackerDetailsModel();
				logger.error("OV Responce workplanJson :"+ trackerIdJson);
				if (!ObjectUtils.isEmpty(workPlanList)) {
					for (HashMap<String, String> entryData : workPlanList) {
						if(entryData.containsKey("active")&& StringUtils.isNotEmpty(entryData.get("active")) &&
								entryData.get("active").equalsIgnoreCase("true"))
						{
							if (entryData.containsKey("id") && StringUtils.isNotEmpty(entryData.get("id"))) {
								trackerDetailsModel.setWorkPlanId(entryData.get("id"));
								trackerDetailsModel.setWorkPlanStatus(entryData.get("active"));
								System.out.println("workplan Status : "+ entryData.get("active"));
							}
						}
					}
				}

				objWorkplanIdDetails.put("workPlanjson", trackerDetailsModel);
				objWorkplanIdDetails.put("statusCode", response.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			objWorkplanIdDetails.put("statusCode", e.getStatusCode());
			objWorkplanIdDetails.put("response", e.getResponseBodyAsString());
			logger.error(
					"Exception in connecting  2nd Api " + e);
		} catch (Exception e) {
			logger.info(
					"Exception in getTrakerIdList in FetchProcessServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return objWorkplanIdDetails;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getSiteReportUploadDetails(JSONObject ovUpdateJson, SiteDataEntity statusSiteDataEntity) {
		JSONObject objStatus = new JSONObject();
		StringBuilder envUpdateJson = new StringBuilder();
		try {

			String programName =ovUpdateJson.get("programName").toString();
			String date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
				OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_URL);
				StringBuilder urlBuilder = new StringBuilder();
				urlBuilder.append(objOvGeneralEntity.getValue());
				urlBuilder.append(Constants.ENV_UPLOAD_URL);
				String url = urlBuilder.toString();
				System.out.println("Ov upload url : "+url);
				String authStr = getAuthencationString();
				StringBuilder filePath = new StringBuilder();
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

				headers.add("Authorization", "Basic " + base64Creds);

				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				filePath.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH)).append(ovUpdateJson.get("filePath")).append(ovUpdateJson.get("FileName"));
				FileSystemResource file = new FileSystemResource(filePath.toString());
				body.add("file", file);

				Map<String, String> urlParams = new HashMap<>();
				urlParams.put("trackor_id", ovUpdateJson.get("TrackerID").toString());
				if (programName.contains("MM")){
					urlParams.put("field_name", "P_MMW_5G_CI_REPORT");
				}
				if (programName.contains("USM-LIVE")) {
				
				urlParams.put("field_name", "P_LTE_4G_CI_REPORT");
				}
				if (programName.contains("DSS")) {
					urlParams.put("field_name", "P_DSS_CI_REPORT");
				}
				if (programName.contains("CBAND")) {
					urlParams.put("field_name", "P_CBAND_CI_REPORT");
				}
				if (programName.contains("FSU")) {
					urlParams.put("field_name", "P_FSU_CI_REPORT");
				}
				
				//urlParams.put("field_name",MileStonesModel.getEnvMode().toString());
				
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
				HttpComponentsClientHttpRequestFactory requestFactory = getHttpsConfiguration();
				
				UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
						// add Querry params
						.queryParam("file_name", file.getFilename());

				System.out.println(builder.buildAndExpand(urlParams).toUri());
				envUpdateJson.append("Request:").append(builder.buildAndExpand(urlParams).toUri()).append("\n");
				RestTemplate restTemplate = new RestTemplate(requestFactory);
				String FileName=ovUpdateJson.get("FileName").toString();
			logger.error("request Entity: "+ requestEntity);
				ResponseEntity<String> response = restTemplate.postForEntity(builder.buildAndExpand(urlParams).toUri(),
						requestEntity, String.class);
				logger.error("ENV Upload API Response: "+ response);
				int statusCode = response.getStatusCodeValue();
				logger.error("Status Code for Upload ENV: "+ statusCode);
				if (200 == statusCode) {
					
					SiteReportOVEntity siteReportOVEntity= new SiteReportOVEntity();
					SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(statusSiteDataEntity.getId());
					siteReportOVEntity.setSiteDataEntity(siteDataEntity);
					siteReportOVEntity.setFileName(ovUpdateJson.get("FileName").toString());
					siteReportOVEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					siteReportOVEntity.setCurrentResult("["+date2+"]"+"-"+"Successfully File :"+FileName +" Uploaded To OV");

					siteDetailsReportRepository.updateSiteReportOv(siteReportOVEntity);
					statusSiteDataEntity.setOvUpdateStatus("SUCCESS");
					siteDetailsReportService.updateSiteDataEntity(statusSiteDataEntity);
					objStatus.put("statusCode", response.getStatusCode());
					objStatus.put("response", response.getBody());
				} else {
					SiteReportOVEntity siteReportOVEntity= new SiteReportOVEntity();
					SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(statusSiteDataEntity.getId());
					siteReportOVEntity.setSiteDataEntity(siteDataEntity);
					siteReportOVEntity.setFileName(ovUpdateJson.get("FileName").toString());
					siteReportOVEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					siteReportOVEntity.setCurrentResult("["+date2+"]"+"-"+"Failed to  updated the " +FileName);
					siteDetailsReportRepository.updateSiteReportOv(siteReportOVEntity);
					statusSiteDataEntity.setOvUpdateStatus("Failure");
					siteDetailsReportService.updateSiteDataEntity(statusSiteDataEntity);;
				}
			}//envUpdateJson.append("Response:").append(response.getStatusCode());
			 catch (HttpClientErrorException e) {
			objStatus.put("statusCode", e.getStatusCode());
			objStatus.put("response", e.getResponseBodyAsString());
		} catch (Exception e) {
			logger.error("Exception in deleteCiqDir in UploadCIQController " + ExceptionUtils.getFullStackTrace(e));
		}
		return objStatus;

	}

//RunTestEntity runtestEntity = runTestRepository.getRunTestEntity(runTestId);
	public String getAuthencationString() {
		StringBuilder authString = new StringBuilder();
		try {
			OvGeneralEntity objOvGeneralEntityUserName = customerRepository.getOvlabelTemplate(Constants.OV_USERNAME);
			OvGeneralEntity objOvGeneralEntityPassword = customerRepository.getOvlabelTemplate(Constants.OV_PASSWORD);
			authString.append(objOvGeneralEntityUserName.getValue());
			authString.append(":");
			authString.append(objOvGeneralEntityPassword.getValue());
		} catch (Exception e) {
			logger.info("Exception in getAuthencationString in FetchProcessServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return authString.toString();
	}
	public HttpComponentsClientHttpRequestFactory getHttpsConfiguration()
			throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

		BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
				socketFactoryRegistry);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
				.setConnectionManager(connectionManager).build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setHttpClient(httpClient);
		return requestFactory;
	}



}
