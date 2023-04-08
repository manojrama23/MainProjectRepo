package com.smart.rct.common.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.dto.NetworkConfigDto;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LoginTypeEntity;
import com.smart.rct.common.entity.NeTypeEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ServerTypeEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.LoginTypeModel;
import com.smart.rct.common.models.NeTypeModel;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.common.models.NetworkConfigDetailsModel;
import com.smart.rct.common.models.NetworkConfigModel;
import com.smart.rct.common.models.ServerTypeModel;
import com.smart.rct.common.repository.NetworkConfigRepository;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.common.service.NeVersionService;
import com.smart.rct.common.service.NetworkConfigService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class NetworkConfigServiceImpl implements NetworkConfigService {

	final static Logger logger = LoggerFactory.getLogger(NetworkConfigServiceImpl.class);

	@Autowired
	NetworkConfigRepository networkConfigRepository;

	@Autowired
	NetworkConfigDto networkConfigDto;

	@Autowired
	CustomerService customerService;

	@Autowired
	NeVersionService neVersionService;

	/**
	 * This api will duplicateNetworkConfig
	 * 
	 * @param networkConfigModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateNetworkConfig(NetworkConfigModel networkConfigModel) {
		boolean status = false;
		try {
			status = networkConfigRepository.duplicateNetworkConfig(networkConfigModel);
		} catch (Exception e) {
			logger.error("Exception in NetworkConfigServiceImpl.duplicateNetworkConfig(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will createNetworkConfig
	 * 
	 * @param networkConfigEntity
	 * @return boolean
	 */
	public boolean createNetworkConfig(NetworkConfigEntity networkConfigEntity) {
		boolean status = false;
		try {
			status = networkConfigRepository.createNetworkConfig(networkConfigEntity);
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception NetworkConfigServiceImpl.createNetworkConfig(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getNetworkConfigDetails
	 * 
	 * @param networkConfigModel,page,count,programNamesList
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getNetworkConfigDetails(NetworkConfigModel networkConfigModel, int page, int count,
			List<CustomerDetailsEntity> programNamesList) {
		Map<String, Object> objMap = null;
		try {
			objMap = networkConfigRepository.getNetworkConfigDetails(networkConfigModel, page, count, programNamesList);
			objMap.put("neTypeList", networkConfigRepository.getNeTypeList(null));
			objMap.put("loginTypeList", networkConfigRepository.getLoginTypeList(null));
			objMap.put("serverTypeList", networkConfigRepository.getServerTypeList(null));
		} catch (Exception e) {
			logger.error("Exception NetworkConfigServiceImpl.getNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This api will deleteNetworkConfigDetails
	 * 
	 * @param networkConfigId
	 * @return boolean
	 */
	@Override
	public boolean deleteNetworkConfigDetails(Integer networkConfigId) {
		boolean status = false;
		try {
			status = networkConfigRepository.deleteNetworkConfigDetails(networkConfigId);
		} catch (Exception e) {
			status = false;
			logger.error("Exception NetworkConfigServiceImpl.deleteNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will deleteNetworkConfigServerDetails
	 * 
	 * @param networkConfigDetailId
	 * @return boolean
	 */
	@Override
	public boolean deleteNetworkConfigServerDetails(int networkConfigDetailId) {
		boolean status = false;
		try {
			status = networkConfigRepository.deleteNetworkConfigServerDetails(networkConfigDetailId);
		} catch (Exception e) {
			status = false;
			logger.error("Exception NetworkConfigServiceImpl.deleteNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will importNetworkConfigDetails
	 * 
	 * @param programNamesList,file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public JSONObject importNetworkConfigDetails(MultipartFile file, List<String> programNamesList, String sessionId) {
		JSONObject resultMap = new JSONObject();
		NetworkConfigModel networkConfigModel = null;
		NetworkConfigDetailsModel networkConfigDetailsModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		List<NetworkConfigEntity> listNetworkConfigEntity = new ArrayList<NetworkConfigEntity>();
		List<NetworkConfigDetailsEntity> listNetworkConfigDetailsEntity = new ArrayList<NetworkConfigDetailsEntity>();
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("NETWORK_CONFIG_DETAILS"));
				String sPath = uploadPath.toString();
				File dir = new File(sPath);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// Write XLSX
				String sFileName = file.getOriginalFilename();
				String sSplit[] = sFileName.split("\\.");

				InputStream in = file.getInputStream();
				fileLocation = sPath + sSplit[0] + "_" + dateFormat.format(date) + "." + sSplit[1];
				FileOutputStream f = new FileOutputStream(fileLocation);
				int ch = 0;
				while ((ch = in.read()) != -1) {
					f.write(ch);
				}
				f.flush();
				f.close();

				// Read XLSX
				File excelFile = new File(fileLocation);
				FileInputStream fis = new FileInputStream(excelFile);
				if (sSplit[1].equalsIgnoreCase("xlsx")) {
					workbook = new XSSFWorkbook(fis);
				} else if (sSplit[1].equalsIgnoreCase("xls")) {
					workbook = new HSSFWorkbook(fis);
				}

				Sheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIt = sheet.iterator();
				int i = 0;
				int rowCount = 0;

				while (rowIt.hasNext()) {
					rowCount = rowCount + 1;
					Row row = rowIt.next();
					i = 0;
					networkConfigModel = new NetworkConfigModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": ID");
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("PROGRAME_NAME")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": PROGRAME_NAME");
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_MARKET")) {
									// do nothing
								} else {

								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_NAME")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_NAME");
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_VERSION")) {
									// do nothing
								} else {

								}
								break;
							case 5:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_IP")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_IP");
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_RS_IP")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_RS_IP");
									return resultMap;
								}
								break;
							case 7:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_TYPE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_TYPE");
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("LOGIN_TYPE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": LOGIN_TYPE");
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_USERNAME")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_USERNAME");
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_PWD")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_PWD");
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_PROMPT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_PROMPT");
									return resultMap;
								}
								break;
							case 12:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_SU_PROMPT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE_SU_PROMPT");
									return resultMap;
								}
								break;
							case 13:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("REMARKS")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": REMARKS");
									return resultMap;
								}
								break;
							case 14:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("STATUS")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": STATUS");
									return resultMap;
								}
								break;
							}
						} else {

							switch (i) {
							case 0:
								Double dId = Double.parseDouble(cell.toString());
								networkConfigModel.setId(dId.intValue());
								break;
							case 1:
								if (!(CommonUtil.isValidObject(programNamesList) && programNamesList.size() > 0
										&& programNamesList.contains(cell.toString()))) {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap
													.get(FaultCodes.PROGRAM_NAME_NOT_ASSOCIATED_WITH_USER) + ": "
													+ cell.toString());
									return resultMap;
								}
								CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
								customerDetailsModel.setProgramName(cell.toString());
								List<CustomerDetailsEntity> detailsEntities = customerService
										.getCustomerDetailsList(customerDetailsModel);
								if (CommonUtil.isValidObject(detailsEntities) && detailsEntities.size() > 0) {
									networkConfigModel.setProgramDetailsEntity(detailsEntities.get(0));
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": PROGRAM");
									return resultMap;
								}
								break;
							case 2:
								networkConfigModel.setNeMarket(cell.toString());
								break;
							case 3:
								networkConfigModel.setNeName(cell.toString());
								break;
							case 4:
								if (cell.toString() != null && !cell.toString().trim().equalsIgnoreCase("")) {
									NeVersionModel neVersionModel = new NeVersionModel();
									neVersionModel.setNeVersion(cell.toString());
									List<NeVersionEntity> neVersionEntities = neVersionService
											.getNeVersionList(neVersionModel);
									if (CommonUtil.isValidObject(neVersionEntities) && neVersionEntities.size() > 0) {
										networkConfigModel.setNeVersionEntity(neVersionEntities.get(0));
									} else {
										resultMap.put("status", Constants.FAIL);
										resultMap.put("reason", GlobalInitializerListener.faultCodeMap
												.get(FaultCodes.REQ_INFO_NOT_FOUND) + ": NE VERSION");
										return resultMap;
									}
								} else {
									networkConfigModel.setNeVersionEntity(null);
								}
								break;
							case 5:
								networkConfigModel.setNeIp(cell.toString());
								break;
							case 6:
								networkConfigModel.setNeRsIp(cell.toString());
								break;
							case 7:
								NeTypeModel neTypeModel = new NeTypeModel();
								neTypeModel.setNeType(cell.toString());
								List<NeTypeEntity> neTypeEntities = networkConfigRepository.getNeTypeList(neTypeModel);
								if (CommonUtil.isValidObject(neTypeEntities) && neTypeEntities.size() > 0) {
									networkConfigModel.setNeTypeEntity(neTypeEntities.get(0));
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": NE TYPE");
									return resultMap;
								}
								break;
							case 8:
								LoginTypeModel loginTypeModel = new LoginTypeModel();
								loginTypeModel.setLoginType(cell.toString());
								List<LoginTypeEntity> loginTypeEntities = networkConfigRepository
										.getLoginTypeList(loginTypeModel);
								if (CommonUtil.isValidObject(loginTypeEntities) && loginTypeEntities.size() > 0) {
									networkConfigModel.setLoginTypeEntity(loginTypeEntities.get(0));
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
													+ ": LOGIN TYPE");
									return resultMap;
								}
								break;
							case 9:
								networkConfigModel.setNeUserName(cell.toString());
								break;
							case 10:
								networkConfigModel.setNePassword(cell.toString());
								break;
							case 11:
								networkConfigModel.setNeUserPrompt(cell.toString());
								break;
							case 12:
								networkConfigModel.setNeSuperUserPrompt(cell.toString());
								break;
							case 13:
								networkConfigModel.setRemarks(cell.toString());
								break;
							case 14:
								networkConfigModel.setStatus(cell.toString());
								break;
							}
						}
						i++;
					}

					if (rowCount > 1 && CommonUtil.isValidObject(networkConfigModel.getId())) {
						if (!CommonUtil.isValidObject(networkConfigModel.getNeVersionEntity())
								&& (networkConfigModel.getNeTypeEntity().getId() == 4
										|| networkConfigModel.getNeTypeEntity().getId() == 5)) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
											+ ": NE VERSION");
							return resultMap;
						}
						if (!CommonUtil.isValidObject(networkConfigModel.getNeRsIp())
								&& (networkConfigModel.getNeTypeEntity().getId() == 4
										|| networkConfigModel.getNeTypeEntity().getId() == 5)) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
											+ ":NE_RS_IP");
							return resultMap;
						}
						if (!CommonUtil.isValidObject(networkConfigModel.getNeMarket())
								&& (networkConfigModel.getNeTypeEntity().getId() == 4
										|| networkConfigModel.getNeTypeEntity().getId() == 5)) {
							resultMap.put("status", Constants.FAIL);
							resultMap.put("reason",
									GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND)
											+ ":NE_MARKET");
							return resultMap;
						}
						NetworkConfigEntity networkConfigEntity = networkConfigDto
								.getNetworkConfigEntity(networkConfigModel, sessionId);
						if (networkConfigEntity != null) {
							listNetworkConfigEntity.add(networkConfigEntity);
						}
					}
				}

				if (rowCount == 1) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_RECORDS_NOT_FOUND));
				}

				// Second sheet
				sheet = workbook.getSheetAt(1);
				rowIt = sheet.iterator();
				i = 0;
				rowCount = 0;

				while (rowIt.hasNext()) {
					rowCount = rowCount + 1;
					Row row = rowIt.next();
					i = 0;
					networkConfigDetailsModel = new NetworkConfigDetailsModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NE_ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("STEP")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("SERVER_NAME")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("SERVER_IP")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("LOGIN_TYPE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 5:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("SERVER_TYPE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("SERVER_USERNAME")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 7:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("SERVER_PWD")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("PATH")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("PROMPT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("SU_PROMPT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("CREATED_BY")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							}
							} else {
							switch (i) {
							case 0:
								NetworkConfigEntity networkConfigEntity = new NetworkConfigEntity();
								Double dId = Double.parseDouble(cell.toString());
								networkConfigEntity.setId(dId.intValue());
								networkConfigDetailsModel.setNetworkConfigEntity(networkConfigEntity);
								break;
							case 1:
								Double dStep = Double.parseDouble(cell.toString());
								networkConfigDetailsModel.setStep(dStep.intValue());
								break;
							case 2:
								networkConfigDetailsModel.setServerName(cell.toString());
								break;
							case 3:
								networkConfigDetailsModel.setServerIp(cell.toString());
								break;
							case 4:
								LoginTypeModel loginTypeModel = new LoginTypeModel();
								loginTypeModel.setLoginType(cell.toString());
								List<LoginTypeEntity> loginTypeEntities = networkConfigRepository
										.getLoginTypeList(loginTypeModel);
								if (CommonUtil.isValidObject(loginTypeEntities) && loginTypeEntities.size() > 0) {
									networkConfigDetailsModel.setLoginTypeEntity(loginTypeEntities.get(0));
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 5:
								ServerTypeModel serverTypeModel = new ServerTypeModel();
								serverTypeModel.setServerType(cell.toString());
								List<ServerTypeEntity> serverTypeEntities = networkConfigRepository
										.getServerTypeList(serverTypeModel);
								if (CommonUtil.isValidObject(serverTypeEntities) && serverTypeEntities.size() > 0) {
									networkConfigDetailsModel.setServerTypeEntity(serverTypeEntities.get(0));
								} else {
									resultMap.put("status", Constants.FAIL);
									resultMap.put("reason",
											GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
									return resultMap;
								}
								break;
							case 6:
								networkConfigDetailsModel.setServerUserName(cell.toString());
								break;
							case 7:
								networkConfigDetailsModel.setServerPassword(cell.toString());
								break;
							case 8:
								networkConfigDetailsModel.setPath(cell.toString());
								break;
							case 9:
								networkConfigDetailsModel.setUserPrompt(cell.toString());
								break;
							case 10:
								networkConfigDetailsModel.setSuperUserPrompt(cell.toString());
								break;
							case 11:
								networkConfigDetailsModel.setCreatedBy(cell.toString());
								break;
							
							}
						}
						i++;
					}

					if (rowCount > 1) {
						NetworkConfigDetailsEntity networkConfigDetailsEntity = networkConfigDto
								.getNetworkConfigDetailEntity(networkConfigDetailsModel);
						if (networkConfigDetailsEntity != null) {
							List<NetworkConfigDetailsEntity> duplicateStepRows = listNetworkConfigDetailsEntity.stream()
									.filter(x -> x.getStep().equals(networkConfigDetailsEntity.getStep())
											&& x.getNetworkConfigEntity().getId().equals(
													networkConfigDetailsEntity.getNetworkConfigEntity().getId()))
									.collect(Collectors.toList());
							if (CommonUtil.isValidObject(duplicateStepRows) && duplicateStepRows.size() > 0) {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.DUPLICATE_STEPS_FOUND));
								return resultMap;
							} else {
								listNetworkConfigDetailsEntity.add(networkConfigDetailsEntity);
							}
						}
					}
				}
				workbook.close();
				fis.close();
			}

			for (NetworkConfigEntity networkConfigEntity : listNetworkConfigEntity) {
				List<NetworkConfigDetailsEntity> chlidNetworkConfigDetailsEntity = listNetworkConfigDetailsEntity
						.stream()
						.filter(n -> String.valueOf(n.getNetworkConfigEntity().getId())
								.equalsIgnoreCase(String.valueOf(networkConfigEntity.getId())))
						.collect(Collectors.toList());
				networkConfigEntity.setId(null);
				// check duplicate lsm updation
				networkConfigModel = networkConfigDto.getNetworkConfigModel(networkConfigEntity);
				if (networkConfigRepository.duplicateNetworkConfig(networkConfigModel)) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ALREADY_EXIST));
					return resultMap;
				}

				if (chlidNetworkConfigDetailsEntity != null && chlidNetworkConfigDetailsEntity.size() > 0) {
					chlidNetworkConfigDetailsEntity.forEach((c) -> c.setId(null));
					chlidNetworkConfigDetailsEntity.forEach((c) -> c.setNetworkConfigEntity(networkConfigEntity));
				}
				networkConfigEntity.setNeDetails(chlidNetworkConfigDetailsEntity);
				boolean bStatus = networkConfigRepository.createNetworkConfig(networkConfigEntity);
				if (bStatus) {
					resultMap.put("status", Constants.SUCCESS);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap
							.get(FaultCodes.NWCONFIG_DETAILS_UPLOADED_SUCCESSFULLY));
				}
			}

		} catch (Exception e) {
			logger.error("Excpetion in NetworkConfigServiceImpl.importNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_NWCONFIG_DETAILS));
		}
		return resultMap;
	}

	/**
	 * This api will getNetWorkDetailsForCreateExcel
	 * 
	 * @param programNamesList,networkConfigModel,addToZip
	 * @return boolean
	 */
	@Override
	public boolean getNetWorkDetailsForCreateExcel(NetworkConfigModel networkConfigModel, List<String> programNamesList,
			boolean addToZip) {
		boolean status = false;
		List<NetworkConfigEntity> objNetworkConfigModel = null;
		String[] columns = Constants.NETCONfig_PARENT_COLUMNS;
		String[] childColumns = Constants.NETCONfig_CHILD_COLUMNS;
		Workbook workbook = new XSSFWorkbook();
		try {
			// Create a Sheet
			Sheet sheet = workbook.createSheet(Constants.NE_LIST);
			Sheet sheet2 = workbook.createSheet(Constants.NE_DETAILS);
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
			Row headerRowChild = sheet2.createRow(0);
			// Create cells
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			for (int i = 0; i < childColumns.length; i++) {
				Cell cell = headerRowChild.createCell(i);
				cell.setCellValue(childColumns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			List<NetworkConfigDetailsEntity> objChildList = new ArrayList<>();

			objNetworkConfigModel = networkConfigRepository.getNetworkConfigDetailsForExPort(networkConfigModel);
			if (objNetworkConfigModel != null && objNetworkConfigModel.size() > 0) {
				int rowNum = 1;
				for (NetworkConfigEntity objNetworkConfigEntity : objNetworkConfigModel) {
					if (CommonUtil.isValidObject(programNamesList) && programNamesList.size() > 0 && programNamesList
							.contains(objNetworkConfigEntity.getProgramDetailsEntity().getProgramName())) {
						Row row = sheet.createRow(rowNum++);

						row.createCell(0).setCellValue(objNetworkConfigEntity.getId());
						if (CommonUtil.isValidObject(objNetworkConfigEntity.getProgramDetailsEntity())) {
							row.createCell(1)
									.setCellValue(objNetworkConfigEntity.getProgramDetailsEntity().getProgramName());
						} else {
							row.createCell(1).setCellValue("");
						}
						row.createCell(2).setCellValue(objNetworkConfigEntity.getNeMarket());
						row.createCell(3).setCellValue(objNetworkConfigEntity.getNeName());
						if (CommonUtil.isValidObject(objNetworkConfigEntity.getNeVersionEntity())) {
							row.createCell(4).setCellValue(objNetworkConfigEntity.getNeVersionEntity().getNeVersion());
						} else {
							row.createCell(4).setCellValue("");
						}
						row.createCell(5).setCellValue(objNetworkConfigEntity.getNeIp());
						row.createCell(6).setCellValue(objNetworkConfigEntity.getNeRsIp());
						if (CommonUtil.isValidObject(objNetworkConfigEntity.getNeTypeEntity())) {
							row.createCell(7).setCellValue(objNetworkConfigEntity.getNeTypeEntity().getNeType());
						} else {
							row.createCell(7).setCellValue("");
						}
						if (CommonUtil.isValidObject(objNetworkConfigEntity.getLoginTypeEntity())) {
							row.createCell(8).setCellValue(objNetworkConfigEntity.getLoginTypeEntity().getLoginType());
						} else {
							row.createCell(8).setCellValue("");
						}
						row.createCell(9).setCellValue(objNetworkConfigEntity.getNeUserName());
						row.createCell(10).setCellValue(objNetworkConfigEntity.getNePassword());
						row.createCell(11).setCellValue(objNetworkConfigEntity.getNeUserPrompt());
						row.createCell(12).setCellValue(objNetworkConfigEntity.getNeSuperUserPrompt());
						row.createCell(13).setCellValue(objNetworkConfigEntity.getRemarks());
						row.createCell(14).setCellValue(objNetworkConfigEntity.getStatus());
						if (objNetworkConfigEntity.getNeDetails() != null
								&& objNetworkConfigEntity.getNeDetails().size() > 0) {
							objChildList.addAll(objNetworkConfigEntity.getNeDetails());
						}
					}
					// Resize all columns to fit the content size
					for (int i = 0; i < columns.length; i++) {
						sheet.autoSizeColumn(i);
					}

					if (objChildList.size() > 0) {
						int rowNum2 = 1;
						for (NetworkConfigDetailsEntity objNetworkConfigDetailsEntity : objChildList) {
							Row row = sheet2.createRow(rowNum2++);
							if (CommonUtil.isValidObject(objNetworkConfigDetailsEntity.getNetworkConfigEntity())) {
								row.createCell(0)
										.setCellValue(objNetworkConfigDetailsEntity.getNetworkConfigEntity().getId());
							} else {
								row.createCell(0).setCellValue("");
							}
							row.createCell(1).setCellValue(objNetworkConfigDetailsEntity.getStep());
							row.createCell(2).setCellValue(objNetworkConfigDetailsEntity.getServerName());
							row.createCell(3).setCellValue(objNetworkConfigDetailsEntity.getServerIp());
							if (CommonUtil.isValidObject(objNetworkConfigDetailsEntity.getLoginTypeEntity())) {
								row.createCell(4).setCellValue(
										objNetworkConfigDetailsEntity.getLoginTypeEntity().getLoginType());
							} else {
								row.createCell(4).setCellValue("");
							}
							if (CommonUtil.isValidObject(objNetworkConfigDetailsEntity.getServerTypeEntity())) {
								row.createCell(5).setCellValue(
										objNetworkConfigDetailsEntity.getServerTypeEntity().getServerType());
							} else {
								row.createCell(5).setCellValue("");
							}
							row.createCell(6).setCellValue(objNetworkConfigDetailsEntity.getServerUserName());
							row.createCell(7).setCellValue(objNetworkConfigDetailsEntity.getServerPassword());
							row.createCell(8).setCellValue(objNetworkConfigDetailsEntity.getPath());
							row.createCell(9).setCellValue(objNetworkConfigDetailsEntity.getUserPrompt());
							row.createCell(10).setCellValue(objNetworkConfigDetailsEntity.getSuperUserPrompt());
							row.createCell(11).setCellValue(objNetworkConfigDetailsEntity.getCreatedBy());
						}

						// Resize all columns to fit the content size
						for (int i = 0; i < childColumns.length; i++) {
							sheet2.autoSizeColumn(i);
						}
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
			// Closing the workbook
			workbook.close();

			if (addToZip) {
				// Zip Creation
				StringBuilder zipFilePathBuilder = new StringBuilder();
				zipFilePathBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
						.append(Constants.NETWORKCONFIG_ZIP);

				status = CommonUtil.createZipFile(zipFilePathBuilder.toString(), fileNameBuilder.toString());

			} else {
				status = true;
			}

		} catch (Exception e) {
			logger.error("Excpetion in NetworkConfigServiceImpl.getLsmDetailsForCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This api will getNetworkConfigDetails
	 * 
	 * @param networkConfigModel
	 * @return List<NetworkConfigEntity>
	 */
	@Override
	public List<NetworkConfigEntity> getNetworkConfigDetails(NetworkConfigModel networkConfigModel) {
		List<NetworkConfigEntity> entities = null;
		try {
			entities = networkConfigRepository.getNetworkConfigDetails(networkConfigModel);
		} catch (Exception e) {
			logger.error("Excpetion in NetworkConfigServiceImpl.getNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return entities;
	}

	/**
	 * This api will getNetworkConfigServerDetailsById
	 * 
	 * @param networkConfigDetailId
	 * @return NetworkConfigDetailsEntity
	 */
	@Override
	public NetworkConfigDetailsEntity getNetworkConfigServerDetailsById(int networkConfigDetailId) {
		NetworkConfigDetailsEntity networkConfigDetailsEntity = null;
		try {
			networkConfigDetailsEntity = networkConfigRepository
					.getNetworkConfigServerDetailsById(networkConfigDetailId);
		} catch (Exception e) {
			logger.error("Excpetion in NetworkConfigServiceImpl.getNetworkConfigServerDetailsById(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return networkConfigDetailsEntity;
	}

	@Override
	public List<NetworkConfigEntity> getNetworkConfigList(int programId) {
		List<NetworkConfigEntity> networkConfigEntityList = null;
		try {
			networkConfigEntityList = networkConfigRepository.getNetworkConfigList(programId);
		} catch (Exception e) {
			logger.info("Exception in getNetworkConfigList() in NetworkConfigServiceImpl"+ ExceptionUtils.getFullStackTrace(e));
		}
		return networkConfigEntityList;
	}
	
	@Override
	public Map<String, Object> getNetworkConfigDetailsByPage(NetworkConfigModel networkConfigModel, List<CustomerDetailsEntity> programNamesList) {
		Map<String, Object> objMap = null;
		try {
			objMap = networkConfigRepository.getNetworkConfigDetailsPage(networkConfigModel, programNamesList);
			objMap.put("neTypeList", networkConfigRepository.getNeTypeList(null));
			objMap.put("loginTypeList", networkConfigRepository.getLoginTypeList(null));
			objMap.put("serverTypeList", networkConfigRepository.getServerTypeList(null));
		} catch (Exception e) {
			logger.error("Exception NetworkConfigServiceImpl.getNetworkConfigDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}
}
