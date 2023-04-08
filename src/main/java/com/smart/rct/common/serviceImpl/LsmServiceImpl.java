package com.smart.rct.common.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import com.smart.rct.common.dto.LsmDetailsDto;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.LsmModel;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.common.repository.LsmRepository;
import com.smart.rct.common.service.LsmService;
import com.smart.rct.common.service.NetworkTypeDetailsService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class LsmServiceImpl implements LsmService {

	final static Logger logger = LoggerFactory.getLogger(LsmServiceImpl.class);

	@Autowired
	NetworkTypeDetailsService networkTypeDetailsService;

	@Autowired
	LsmRepository objLsmRepository;

	@Autowired
	LsmService objLsmService;

	@Autowired
	LsmDetailsDto objLsmDetailsDto;

	/**
	 * This api getLsmDetails
	 * 
	 * @param objLsmModel,page,count
	 * @return Map
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getLsmDetails(LsmModel objLsmModel, int page, int count) {
		Map<String, Object> objMap = null;
		try {
			getLsmDetailsForCreateExcel(objLsmModel);
			objMap = objLsmRepository.getLsmDetails(objLsmModel, page, count);

			if (objMap != null && objMap.size() > 0) {
				if (objMap.containsKey("totList")) {
					List<LsmModel> objTotList = (List<LsmModel>) objMap.get("totList");

					Set<String> nwTypeNamesList = objTotList.stream().map(X -> X.getNetworkType()).sorted()
							.collect(Collectors.toSet());
					Set<String> nwversionList = objTotList.stream().map(X -> X.getLsmVersion()).sorted()
							.collect(Collectors.toSet());
					Set<String> programNamesList = objTotList.stream().map(X -> X.getProgramName()).sorted()
							.collect(Collectors.toSet());
					Set<String> neTypeList = objTotList.stream().map(X -> X.getNeType()).sorted()
							.collect(Collectors.toSet());
					Set<String> neNameList = objTotList.stream().map(X -> X.getLsmName()).sorted()
							.collect(Collectors.toSet());
					Set<String> buketsList = objTotList.stream().map(X -> X.getBucket()).sorted()
							.collect(Collectors.toSet());

					objMap.put("searchNwTypeList", nwTypeNamesList);
					objMap.put("searchnwversionList", nwversionList);
					objMap.put("searchprogramNamesList", programNamesList);
					objMap.put("searchneTypeList", neTypeList);
					objMap.put("searchneNameList", neNameList);
					objMap.put("searchbuketsList", buketsList);

				}
			}

		} catch (Exception e) {
			logger.error("Exception in LsmServiceImpl.getLsmDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This api createLsm
	 * 
	 * @param LsmEntity
	 * 
	 * @return boolean
	 */
	@Override
	public boolean createLsm(LsmEntity lsmEntity) {
		boolean status = false;
		try {
			status = objLsmRepository.createLsm(lsmEntity);
		} catch (Exception e) {
			status = false;

			logger.error("Exception in LsmServiceImpl.createLsm(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api updateLsm
	 * 
	 * @param updateLsmEntity
	 * 
	 * @return boolean
	 */
	@Override
	public boolean updateLsm(LsmEntity updateLsmEntity) {
		boolean status = false;
		try {
			status = objLsmRepository.updateLsm(updateLsmEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in LsmServiceImpl.updateLsm(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api deleteLsmDetails
	 * 
	 * @param lsmId
	 * 
	 * @return boolean
	 */
	@Override
	public boolean deleteLsmDetails(int lsmId) {
		boolean status = false;
		try {
			status = objLsmRepository.deleteLsmDetails(lsmId);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in LsmServiceImpl.deleteLsmDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * 
	 * this method will check duplicateLsm
	 * 
	 * @param objLsmModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateLsm(LsmModel objLsmModel) {
		boolean status = false;
		try {
			status = objLsmRepository.duplicateLsm(objLsmModel);
		} catch (Exception e) {
			logger.error("Exception in LsmServiceImpl.duplicateLsm(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * 
	 * this method will getLsmVersionsByNetworkType
	 * 
	 * @param networkTypeId
	 * @return List<String>
	 */
	@Override
	public List<String> getLsmVersionsByNetworkType(Integer networkTypeId) {
		List<String> lsmVersionList = null;

		try {
			lsmVersionList = objLsmRepository.getLsmVersionsByNetworkType(networkTypeId);
		} catch (Exception e) {
			logger.error("Exception in LsmServiceImpl.getLsmVersionsByNetworkType(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return lsmVersionList;
	}

	/**
	 * 
	 * this method will getNetWorksBasedOnCustomer
	 * 
	 * @param customerId
	 * @return List<NetworkTypeDetailsModel>
	 */
	@Override
	public List<NetworkTypeDetailsModel> getNetWorksBasedOnCustomer(int customerId) {
		List<NetworkTypeDetailsModel> networkTypeDetailsModelList = null;
		try {
			networkTypeDetailsModelList = objLsmRepository.getNetWorksBasedOnCustomer(customerId);
		} catch (Exception e) {
			logger.error(
					"Exception in LsmServiceImpl.getNetWorksBasedOnCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		}

		return networkTypeDetailsModelList;
	}

	/**
	 * 
	 * this method will createLsmFromInputFile
	 * 
	 * @param file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject createLsmFromInputFile(MultipartFile file, String sessionId) {

		JSONObject resultMap = new JSONObject();
		LsmModel objLsmModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("LSMDETAILS"));
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
				int iRowCount = 0;
				while (rowIt.hasNext()) {
					iRowCount = iRowCount + 1;
					Row row = rowIt.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					int j = 9;
					objLsmModel = new LsmModel();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (i) {
						case 0:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("lsm_name")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 1:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("lsm_ip")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 2:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("created_by")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 3:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("lsm_username")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 4:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("lsm_pwd")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 5:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("status")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 6:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("remarks")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 7:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("lsm_version")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						case 8:
							if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("nw_type_id")) {
								// do nothing
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason",
										GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
								return resultMap;
							}
							break;
						}

						if (i >= 9) {
							switch (j) {
							case 9:
								objLsmModel.setLsmName(cell.toString());
								break;
							case 10:
								objLsmModel.setLsmIp(cell.toString());
								break;
							case 11:
								objLsmModel.setCreatedBy(cell.toString());
								break;
							case 12:
								objLsmModel.setLsmUserName(cell.toString());
								break;
							case 13:
								objLsmModel.setLsmPassword(cell.toString());
								break;
							case 14:
								objLsmModel.setStatus(cell.toString());
								break;
							case 15:
								objLsmModel.setRemarks(cell.toString());
								break;
							case 16:
								objLsmModel.setLsmVersion(cell.toString());
								break;
							case 17:
								NetworkTypeDetailsEntity netWorkEntity = networkTypeDetailsService
										.getNetworkTypeByName(cell.toString());
								objLsmModel.setNetworkTypeId(netWorkEntity.getId());
								break;
							}
							// objLsmModel.setCreationDate(new Date());
						}
						i++;
						j++;
					}

					// check duplicate lsm updation
					if (objLsmService.duplicateLsm(objLsmModel)) {
						resultMap.put("status", Constants.FAIL);
						// resultMap.put("reason", "LSM Name,LSM Version And
						// NETWORK TYPE already exists in row "+ (iRowCount-1));
						resultMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.NWCONFIG_DETAILS_ALREADY_EXIST));
						return resultMap;
					}

					LsmEntity objLsmEntity = objLsmDetailsDto.getLsmEntity(objLsmModel, sessionId);

					if (i > 9) {
						if (objLsmEntity != null) {
							if (objLsmRepository.createLsm(objLsmEntity)) {
								resultMap.put("status", Constants.SUCCESS);
								resultMap.put("reason", GlobalInitializerListener.faultCodeMap
										.get(FaultCodes.NWCONFIG_DETAILS_UPLOADED_SUCCESSFULLY));
							} else {
								resultMap.put("status", Constants.FAIL);
								resultMap.put("reason", GlobalInitializerListener.faultCodeMap
										.get(FaultCodes.FAILED_TO_UPLOAD_NWCONFIG_DETAILS));
							}
						}
					}
				}
				if (i == 9) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", "No records to upload");
				}
				if (i < 9) {
					resultMap.put("status", Constants.FAIL);
					resultMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.REQ_INFO_NOT_FOUND));
				}
				workbook.close();
				fis.close();
			}
		} catch (Exception e) {
			logger.error(
					"Exception in LsmServiceImpl.createLsmFromInputFile(): " + ExceptionUtils.getFullStackTrace(e));
			resultMap.put("status", Constants.FAIL);
			resultMap.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_NWCONFIG_DETAILS));
		}
		return resultMap;
	}

	/**
	 * 
	 * this method will getLsmEntityDetails
	 * 
	 * @return List<LsmEntity>
	 */
	@Override
	public List<LsmEntity> getLsmEntityDetails() {
		// TODO Auto-generated method stub
		List<LsmEntity> objList = null;
		try {
			objList = objLsmRepository.getLsmEntityDetails();
		} catch (Exception e) {
			logger.error("Exception in LsmServiceImpl.getLsmEntityDetails(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return objList;
	}

	/**
	 * 
	 * this method will getLsmDetailsForCreateExcel
	 * 
	 * @param objLsmModel
	 * @return boolean
	 */
	@Override
	public boolean getLsmDetailsForCreateExcel(LsmModel objLsmModel) {
		boolean status = false;
		List<LsmModel> objListLsmModel = null;
		String[] columns = Constants.NETCONfig_PARENT_COLUMNS;
		try {
			objListLsmModel = objLsmRepository.getLsmDetailsForCreateExcel(objLsmModel);
			if (objListLsmModel != null && objListLsmModel.size() > 0) {
				// Create a Workbook
				Workbook workbook = new XSSFWorkbook();
				// CreationHelper createHelper = workbook.getCreationHelper();
				// Create a Sheet
				Sheet sheet = workbook.createSheet("NetWork Config");
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
				int rowNum = 1;
				for (LsmModel objLsmModelLoc : objListLsmModel) {
					Row row = sheet.createRow(rowNum++);

					row.createCell(0).setCellValue(objLsmModelLoc.getNetworkType());
					row.createCell(1).setCellValue(objLsmModelLoc.getProgramName());
					row.createCell(2).setCellValue(objLsmModelLoc.getNeType());
					row.createCell(3).setCellValue(objLsmModelLoc.getLsmVersion());
					row.createCell(4).setCellValue(objLsmModelLoc.getLsmName());
					row.createCell(5).setCellValue(objLsmModelLoc.getBucket());
					row.createCell(6).setCellValue(objLsmModelLoc.getLsmIp());
					row.createCell(7).setCellValue(objLsmModelLoc.getLsmUserName());
					row.createCell(8).setCellValue(objLsmModelLoc.getLsmPassword());
					row.createCell(9).setCellValue(objLsmModelLoc.getStatus());
					row.createCell(10).setCellValue(objLsmModelLoc.getRemarks());
				}
				// Resize all columns to fit the content size
				for (int i = 0; i < columns.length; i++) {
					sheet.autoSizeColumn(i);
				}
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append("/home/user/networkconfig.xlsx");
				// Write the output to a file
				FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
				workbook.write(fileOut);
				fileOut.close();
				// Closing the workbook
				workbook.close();
				// Zip Creation
				StringBuilder zipFilePathBuilder = new StringBuilder();
				zipFilePathBuilder.append("/home/user/networkconfig.zip");
				status = createZipFile(zipFilePathBuilder.toString(), fileNameBuilder.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in LsmServiceImpl.getLsmDetailsForCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * 
	 * this method will createZipFile
	 * 
	 * @param zipFilePathBuilder,filePath
	 * @return boolean
	 */
	public boolean createZipFile(String zipFilePathBuilder, String filePath) {
		boolean status = false;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		FileInputStream fis = null;
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];
			fos = new FileOutputStream(zipFilePathBuilder.toString());
			zos = new ZipOutputStream(fos);
			File dataFile = new File(filePath.toString());
			fis = new FileInputStream(dataFile);
			zos.putNextEntry(new ZipEntry(dataFile.getName()));
			int length;
			while ((length = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, length);
			}
			if (dataFile.exists()) {
				dataFile.delete();
			}
			zos.closeEntry();
			status = true;
		} catch (IOException e) {
			logger.error("Failed to create zip file", e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (Exception e) {
				logger.error("Failed to Finally block to zip file", e);
			}
		}
		return status;
	}

}
