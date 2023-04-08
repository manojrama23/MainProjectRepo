package com.smart.rct.postmigration.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.dto.SchedulingSRDto;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;
import com.smart.rct.postmigration.repository.SchedulingSRRepository;
import com.smart.rct.postmigration.service.SchedulingSRService;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class SchedulingSRServiceImpl implements SchedulingSRService {

	final static Logger logger = LoggerFactory.getLogger(SchedulingSRServiceImpl.class);

	@Autowired
	SchedulingSRRepository schedulingRepository;

	DataFormatter dataFormatter = new DataFormatter();

	@Autowired
	SchedulingSRDto schedulingSRDto;

	/**
	 * This method will saveVerizonSchedulingDetails
	 * 
	 * @param schedulingVerizonEntity
	 * @return boolean
	 */
	@Override
	public boolean saveVerizonSchedulingDetails(SchedulingVerizonEntity schedulingVerizonEntity) {
		boolean status = false;
		try {
			status = schedulingRepository.saveVerizonSchedulingDetails(schedulingVerizonEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception SchedulingServiceImpl.saveVerizonSchedulingDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will saveSprintSchedulingDetails
	 * 
	 * @param schedulingSprintEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSprintSchedulingDetails(SchedulingSprintEntity schedulingSprintEntity) {
		boolean status = false;
		try {
			status = schedulingRepository.saveSprintSchedulingDetails(schedulingSprintEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception SchedulingServiceImpl.saveSprintSchedulingDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will deleteVerizonDetails
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteVerizonDetails(int id) {
		boolean status = false;
		try {
			status = schedulingRepository.deleteVerizonDetails(id);
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception SchedulingServiceImpl.deleteVerizonDetails() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will deleteSprintDetails
	 * 
	 * @param id
	 * @return boolean
	 */
	@Override
	public boolean deleteSprintDetails(int id) {
		boolean status = false;
		try {
			status = schedulingRepository.deleteSprintDetails(id);
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception SchedulingServiceImpl.deleteVerizonDetails() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will getVerizonSchedulingDetails
	 * 
	 * @param idschedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getVerizonSchedulingDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId) {
		Map<String, Object> schedulingObj = null;
		try {
			schedulingObj = schedulingRepository.getVerizonSchedulingDetails(schedulingVerizonModel, page, count,
					customerId);
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getVerizonSchedulingDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return schedulingObj;
	}

	/**
	 * This method will getSprintSchedulingDetails
	 * 
	 * @param idschedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSprintSchedulingDetails(SchedulingSprintModel schedulingSprintModel, int page,
			int count, int customerId) {
		Map<String, Object> schedulingObj = null;
		try {
			schedulingObj = schedulingRepository.getSprintSchedulingDetails(schedulingSprintModel, page, count,
					customerId);
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getSprintSchedulingDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return schedulingObj;
	}

	/**
	 * This method will getSchedulingDetailsToCreateExcel
	 * 
	 * @param idschedulingVerizonModel
	 * @return boolean
	 */
	@Override
	public boolean getSchedulingDetailsToCreateExcel(SchedulingVerizonModel schedulingVerizonModel) {
		boolean status = false;
		List<SchedulingVerizonEntity> objSchedulingVerizonEntity = null;
		List<SchedulingVerizonModel> objSchedulingVerizonModel = null;
		String[] columns = Constants.SCHEDULING_VERIZON_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet(Constants.SCHEDULING_VERIZON);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingVerizonEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingVerizonModel);
			objSchedulingVerizonModel = schedulingSRDto.getVerizonSchedulingModel(objSchedulingVerizonEntity);
			if (objSchedulingVerizonModel != null && objSchedulingVerizonModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingVerizonModel objEntity : objSchedulingVerizonModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getId());
					row.createCell(1).setCellValue(objEntity.getForecastStartDate());
					row.createCell(2).setCellValue(objEntity.getCompDate());
					row.createCell(3).setCellValue(objEntity.getMarket());
					row.createCell(4).setCellValue(objEntity.getEnbId());
					row.createCell(5).setCellValue(objEntity.getEnbName());
					row.createCell(6).setCellValue(objEntity.getGrowRequest());
					row.createCell(7).setCellValue(objEntity.getGrowCompleted());
					row.createCell(8).setCellValue(objEntity.getCiqPresent());
					row.createCell(9).setCellValue(objEntity.getEnvCompleted());
					row.createCell(10).setCellValue(objEntity.getStandardNonStandard());
					row.createCell(11).setCellValue(objEntity.getCarriers());
					row.createCell(12).setCellValue(objEntity.getUda());
					row.createCell(13).setCellValue(objEntity.getSoftwareLevels());
					row.createCell(14).setCellValue(objEntity.getFeArrivalTime());
					row.createCell(15).setCellValue(objEntity.getCiStartTime());
					row.createCell(16).setCellValue(objEntity.getDtHandoff());
					row.createCell(17).setCellValue(objEntity.getCiEndTime());
					row.createCell(18).setCellValue(objEntity.getCanRollComp());
					row.createCell(19).setCellValue(objEntity.getTraffic());
					row.createCell(20).setCellValue(objEntity.getAlarmPresent());
					row.createCell(21).setCellValue(objEntity.getCiEngineer());
					row.createCell(22).setCellValue(objEntity.getFt());
					row.createCell(23).setCellValue(objEntity.getDt());
					row.createCell(24).setCellValue(objEntity.getNotes());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.SCHEDULING_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdirs();
			}
			fileNameBuilder.append(Constants.SCHEDULING_VERIZON_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion SchedulingServiceImpl.getSchedulingDetailsToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will getSchedulingSprintToCreateExcel
	 * 
	 * @param schedulingSprintModel
	 * @return boolean
	 */
	@Override
	public boolean getSchedulingSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel) {
		boolean status = false;
		List<SchedulingSprintEntity> objSchedulingSprintEntity = null;
		List<SchedulingSprintModel> objSchedulingSprintModel = null;
		String[] columns = Constants.SCHEDULING_SPRINT_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			Sheet sheet = workbook.createSheet(Constants.SCHEDULING_SPRINT);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingSprintEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingSprintModel);
			objSchedulingSprintModel = schedulingSRDto.getSprintSchedulingModel(objSchedulingSprintEntity);
			if (objSchedulingSprintModel != null && objSchedulingSprintModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingSprintModel objEntity : objSchedulingSprintModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getId());
					row.createCell(1).setCellValue(objEntity.getStartDate());
					row.createCell(2).setCellValue(objEntity.getRegion());
					row.createCell(3).setCellValue(objEntity.getMarket());
					row.createCell(4).setCellValue(objEntity.getCascade());
					row.createCell(5).setCellValue(objEntity.getCiEngineerNight());
					row.createCell(6).setCellValue(objEntity.getBridgeOne());
					row.createCell(7).setCellValue(objEntity.getFeRegion());
					row.createCell(8).setCellValue(objEntity.getFeNight());
					row.createCell(9).setCellValue(objEntity.getCiEngineerDay());
					row.createCell(10).setCellValue(objEntity.getBridge());
					row.createCell(11).setCellValue(objEntity.getFeDay());
					row.createCell(12).setCellValue(objEntity.getNotes());
					row.createCell(13).setCellValue(objEntity.getStatus());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.SCHEDULING_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.SCHEDULING_SPRINT_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion SchedulingServiceImpl.getSchedulingDetailsToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will importVerizonSchedulingDetails
	 * 
	 * @param file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public JSONObject importVerizonSchedulingDetails(MultipartFile file, String sessionId) {
		JSONObject resultMap = new JSONObject();
		SchedulingVerizonModel schedulingVerizonModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		List<SchedulingVerizonEntity> listSchedulingVerizonEntity = new ArrayList<SchedulingVerizonEntity>();
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("SCHEDULING_DETAILS"));
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
					schedulingVerizonModel = new SchedulingVerizonModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Forecast Start Date")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Comp Date")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Market")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("eNB ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 5:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("eNB Name")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Grow Requested")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 7:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Grow Completed")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("CIQ Present")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("ENV completed")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Standard vs Non Standard")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Carriers")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 12:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("UDA")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 13:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Software levels")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 14:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE arrival time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 15:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I start time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 16:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("DT hand off")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 17:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I end time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 18:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Canc/Roll/Comp")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 19:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Traffic")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 20:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Alarm Present (Y/N)")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 21:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I Engineer")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 22:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 23:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("DT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 24:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Notes")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;

							}
						} else {

							switch (i) {
							case 0:
								Double dId = Double.parseDouble(cell.toString());
								schedulingVerizonModel.setId(dId.intValue());
								break;
							case 1:
								schedulingVerizonModel.setForecastStartDate(((String) getCellValue(cell)).trim());
								break;
							case 2:
								schedulingVerizonModel.setCompDate(((String) getCellValue(cell)).trim());
								break;
							case 3:
								schedulingVerizonModel.setMarket(((String) getCellValue(cell)).trim());
								break;
							case 4:
								schedulingVerizonModel.setEnbId(cell.toString());
								break;
							case 5:
								schedulingVerizonModel.setEnbName(cell.toString());
								break;
							case 6:
								schedulingVerizonModel.setGrowRequest(((String) getCellValue(cell)).trim());
								break;
							case 7:
								schedulingVerizonModel.setGrowCompleted(((String) getCellValue(cell)).trim());
								break;
							case 8:
								schedulingVerizonModel.setCiqPresent(((String) getCellValue(cell)).trim());
								break;
							case 9:
								schedulingVerizonModel.setEnvCompleted(((String) getCellValue(cell)).trim());
								break;
							case 10:
								schedulingVerizonModel.setStandardNonStandard(((String) getCellValue(cell)).trim());
								break;
							case 11:
								schedulingVerizonModel.setCarriers(((String) getCellValue(cell)).trim());
								break;
							case 12:
								schedulingVerizonModel.setUda(((String) getCellValue(cell)).trim());
								break;
							case 13:
								schedulingVerizonModel.setSoftwareLevels(((String) getCellValue(cell)).trim());
								break;
							case 14:
								schedulingVerizonModel.setFeArrivalTime(((String) getCellValue(cell)).trim());
								break;
							case 15:
								schedulingVerizonModel.setCiStartTime(((String) getCellValue(cell)).trim());
								break;
							case 16:
								schedulingVerizonModel.setDtHandoff(((String) getCellValue(cell)).trim());
								break;
							case 17:
								schedulingVerizonModel.setCiEndTime(((String) getCellValue(cell)).trim());
								break;
							case 18:
								schedulingVerizonModel.setCanRollComp(((String) getCellValue(cell)).trim());
								break;
							case 19:
								schedulingVerizonModel.setTraffic(((String) getCellValue(cell)).trim());
								break;
							case 20:
								schedulingVerizonModel.setAlarmPresent(((String) getCellValue(cell)).trim());
								break;
							case 21:
								schedulingVerizonModel.setCiEngineer(((String) getCellValue(cell)).trim());
								break;
							case 22:
								schedulingVerizonModel.setFt(((String) getCellValue(cell)).trim());
							case 23:
								schedulingVerizonModel.setDt(((String) getCellValue(cell)).trim());
								break;
							case 24:
								schedulingVerizonModel.setNotes(((String) getCellValue(cell)).trim());
								break;

							}
						}
						i++;
					}

					if (rowCount > 1) {
						SchedulingVerizonEntity schedulingVerizonEntity = schedulingSRDto
								.getVerizonSchedulingEntity(schedulingVerizonModel);
						if (schedulingVerizonEntity != null) {
							listSchedulingVerizonEntity.add(schedulingVerizonEntity);
						}
					}
				}
				workbook.close();
				fis.close();
			}
			for (SchedulingVerizonEntity schedulingVerizonEntities : listSchedulingVerizonEntity) {
				schedulingVerizonEntities.setId(null);
				boolean bStatus = schedulingRepository.saveVerizonSchedulingDetails(schedulingVerizonEntities);
				if (bStatus) {
					resultMap.put("status", Constants.SUCCESS);

				}
			}
		} catch (Exception e) {
			logger.error("Excpetion SchedulingServiceImpl.importVerizonSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * This method will importSprintSchedulingDetails
	 * 
	 * @param file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public JSONObject importSprintSchedulingDetails(MultipartFile file, String sessionId) {
		JSONObject resultMap = new JSONObject();
		SchedulingSprintModel schedulingSprintModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		List<SchedulingSprintEntity> listSchedulingSprintEntity = new ArrayList<SchedulingSprintEntity>();
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("SCHEDULING_DETAILS"));
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
					schedulingSprintModel = new SchedulingSprintModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("START_DATE")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("REGION")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("MARKET")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("CASCADE_ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 5:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("CI_ENGINEER_NIGHT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("BRIDGE_ONE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 7:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE_REGION")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE_NIGHT")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("CI_ENGINEER_DAY")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("BRIDGE")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE_DAY")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 12:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NOTES")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 13:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("STATUS")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							}
						} else {

							switch (i) {
							case 0:
								Double dId = Double.parseDouble(cell.toString());
								schedulingSprintModel.setId(dId.intValue());
								break;
							case 1:
								schedulingSprintModel.setStartDate(((String) getCellValue(cell)).trim());
							case 2:
								schedulingSprintModel.setRegion(((String) getCellValue(cell)).trim());
								break;
							case 3:
								schedulingSprintModel.setMarket(((String) getCellValue(cell)).trim());
								break;
							case 4:
								schedulingSprintModel.setCascade(((String) getCellValue(cell)).trim());

								break;
							case 5:
								schedulingSprintModel.setCiEngineerNight(((String) getCellValue(cell)).trim());
								break;
							case 6:
								schedulingSprintModel.setBridgeOne(((String) getCellValue(cell)).trim());
								break;
							case 7:
								schedulingSprintModel.setFeRegion(((String) getCellValue(cell)).trim());
								break;
							case 8:
								schedulingSprintModel.setFeNight(((String) getCellValue(cell)).trim());
								break;
							case 9:
								schedulingSprintModel.setCiEngineerDay(((String) getCellValue(cell)).trim());
								break;
							case 10:
								schedulingSprintModel.setBridge(((String) getCellValue(cell)).trim());
								break;
							case 11:
								schedulingSprintModel.setFeDay(((String) getCellValue(cell)).trim());
								break;
							case 12:
								schedulingSprintModel.setNotes(((String) getCellValue(cell)).trim());
								break;
							case 13:
								schedulingSprintModel.setStatus(((String) getCellValue(cell)).trim());
								break;

							}
						}
						i++;
					}

					if (rowCount > 1) {
						SchedulingSprintEntity schedulingSprintEntity = schedulingSRDto
								.getSprintSchedulingEntity(schedulingSprintModel);
						if (schedulingSprintEntity != null) {
							listSchedulingSprintEntity.add(schedulingSprintEntity);
						}
					}
				}
				workbook.close();
				fis.close();
			}
			for (SchedulingSprintEntity schedulingSprintEntities : listSchedulingSprintEntity) {
				schedulingSprintEntities.setId(null);
				boolean bStatus = schedulingRepository.saveSprintSchedulingDetails(schedulingSprintEntities);
				if (bStatus) {
					resultMap.put("status", Constants.SUCCESS);

				}
			}
		} catch (Exception e) {
			logger.error("Excpetion SchedulingServiceImpl.importSprintSchedulingDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * This method will getVerizonOverallReportsDetails
	 * 
	 * @param schedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getVerizonOverallReportsDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId) {
		Map<String, Object> overallReportsObj = null;
		try {
			overallReportsObj = schedulingRepository.getVerizonOverallReportsDetails(schedulingVerizonModel, page,
					count, customerId);
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getVerizonOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return overallReportsObj;
	}

	/**
	 * This method will getSprintOverallReportsDetails
	 * 
	 * @param schedulingSprintModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSprintOverallReportsDetails(SchedulingSprintModel schedulingSprintModel, int page,
			int count, int customerId) {
		Map<String, Object> overallReportsObj = null;
		try {
			overallReportsObj = schedulingRepository.getSprintOverallReportsDetails(schedulingSprintModel, page, count,
					customerId);
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getSprintOverallReportsDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return overallReportsObj;
	}

	/**
	 * This method will getOverallDetailsToCreateExcel
	 * 
	 * @param schedulingVerizonModel
	 * @return boolean
	 */
	@Override
	public boolean getOverallDetailsToCreateExcel(SchedulingVerizonModel schedulingVerizonModel) {
		boolean status = false;
		List<SchedulingVerizonEntity> objSchedulingVerizonEntity = null;
		List<SchedulingVerizonModel> objSchedulingVerizonModel = null;
		String[] columns = Constants.OVERALL_REPORTS_VERIZON_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet(Constants.OVERALL_REPORTS_VERIZON);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingVerizonEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingVerizonModel);
			objSchedulingVerizonModel = schedulingSRDto.getVerizonOverallModel(objSchedulingVerizonEntity);
			if (objSchedulingVerizonModel != null && objSchedulingVerizonModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingVerizonModel objEntity : objSchedulingVerizonModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getId());
					row.createCell(1).setCellValue(objEntity.getTotalLookup());
					row.createCell(2).setCellValue(objEntity.getForecastStartDate());
					row.createCell(3).setCellValue(objEntity.getMarket());
					row.createCell(4).setCellValue(objEntity.getEnbName());
					row.createCell(5).setCellValue(objEntity.getEnbId());
					row.createCell(6).setCellValue(objEntity.getRanEngineer());
					row.createCell(7).setCellValue(objEntity.getGrowRequest());
					row.createCell(8).setCellValue(objEntity.getGrowCompleted());
					row.createCell(9).setCellValue(objEntity.getCiqPresent());
					row.createCell(10).setCellValue(objEntity.getStatus());
					row.createCell(11).setCellValue(objEntity.getRevisit());
					row.createCell(12).setCellValue(objEntity.getVlsm());
					row.createCell(13).setCellValue(objEntity.getCiStartTime());
					row.createCell(14).setCellValue(objEntity.getCiEndTime());
					row.createCell(15).setCellValue(objEntity.getComments());
					row.createCell(16).setCellValue(objEntity.getIssue());
					row.createCell(17).setCellValue(objEntity.getCi());
					row.createCell(18).setCellValue(objEntity.getNonCi());
					row.createCell(19).setCellValue(objEntity.getAld());
					row.createCell(20).setCellValue(objEntity.getWeek());
					row.createCell(21).setCellValue(objEntity.getMonth());
					row.createCell(22).setCellValue(objEntity.getStatus2());
					row.createCell(23).setCellValue(objEntity.getQuarter());
					row.createCell(24).setCellValue(objEntity.getYear());
					row.createCell(24).setCellValue(objEntity.getDay());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.OVERALL_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.OVERALL_REPORT_VERIZON_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.getOverallDetailsToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will getOverallSprintToCreateExcel
	 * 
	 * @param schedulingSprintModel
	 * @return boolean
	 */
	@Override
	public boolean getOverallSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel) {
		boolean status = false;
		List<SchedulingSprintEntity> objSchedulingSprintEntity = null;
		List<SchedulingSprintModel> objSchedulingSprintModel = null;
		String[] columns = Constants.OVERALL_REPORTS_SPRINT_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet(Constants.OVERALL_REPORTS_SPRINT);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingSprintEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingSprintModel);
			objSchedulingSprintModel = schedulingSRDto.getSprintSchedulingModel(objSchedulingSprintEntity);
			if (objSchedulingSprintModel != null && objSchedulingSprintModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingSprintModel objEntity : objSchedulingSprintModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getId());
					row.createCell(1).setCellValue(objEntity.getScheduleDate());
					row.createCell(2).setCellValue(objEntity.getStartDate());
					row.createCell(3).setCellValue(objEntity.getCompDate());
					row.createCell(4).setCellValue(objEntity.getRegion());
					row.createCell(5).setCellValue(objEntity.getMarket());
					row.createCell(6).setCellValue(objEntity.getCascade());
					row.createCell(7).setCellValue(objEntity.getEnbId());
					row.createCell(8).setCellValue(objEntity.getType());
					row.createCell(9).setCellValue(objEntity.getDtOrMw());
					row.createCell(10).setCellValue(objEntity.getPutTool());
					row.createCell(11).setCellValue(objEntity.getCurrentSoftware());
					row.createCell(12).setCellValue(objEntity.getScriptsRan());
					row.createCell(13).setCellValue(objEntity.getScriptErrors());
					
					row.createCell(14).setCellValue(objEntity.getDspImplemented());
					row.createCell(15).setCellValue(objEntity.getCiEngineerOne());
					row.createCell(16).setCellValue(objEntity.getCiStartTimeOne());
					row.createCell(17).setCellValue(objEntity.getCiEndTimeOne());
					row.createCell(18).setCellValue(objEntity.getFeRegion());
					row.createCell(19).setCellValue(objEntity.getFeOne());
					row.createCell(20).setCellValue(objEntity.getFeContactInfoOne());
					row.createCell(21).setCellValue(objEntity.getFeArrivalTimeOne());
					
					row.createCell(22).setCellValue(objEntity.getCiEngineerTwo());
					row.createCell(23).setCellValue(objEntity.getCiStartTimeTwo());
					row.createCell(24).setCellValue(objEntity.getCiEndTimeTwo());
					row.createCell(25).setCellValue(objEntity.getFeTwo());
					row.createCell(26).setCellValue(objEntity.getFeContactInfoTwo());
					row.createCell(27).setCellValue(objEntity.getFeArrivalTimeTwo());
					
					row.createCell(28).setCellValue(objEntity.getCiEngineerThree());
					row.createCell(29).setCellValue(objEntity.getCiStartTimeThree());
					row.createCell(30).setCellValue(objEntity.getCiEndTimeThree());
					row.createCell(31).setCellValue(objEntity.getFeThree());
					row.createCell(32).setCellValue(objEntity.getFeContactInfoThree());
					row.createCell(33).setCellValue(objEntity.getFeArrivalTimeThree());
					
					row.createCell(34).setCellValue(objEntity.getGc());
					row.createCell(35).setCellValue(objEntity.getTcName());
					row.createCell(36).setCellValue(objEntity.getTcContactInfo());
					row.createCell(37).setCellValue(objEntity.getGcArrivalTime());
					
					row.createCell(38).setCellValue(objEntity.getCircuitbreakerStart());
					row.createCell(39).setCellValue(objEntity.getCircuitbreakerEnd());
					
					row.createCell(40).setCellValue(objEntity.getAlphaEndTime());
					row.createCell(41).setCellValue(objEntity.getAlphaEndTime());
					row.createCell(42).setCellValue(objEntity.getBetaStartTime());
					row.createCell(43).setCellValue(objEntity.getBetaEndTime());
					row.createCell(44).setCellValue(objEntity.getGammaStartTime());
					row.createCell(45).setCellValue(objEntity.getGammaEndTime());
					
					row.createCell(46).setCellValue(objEntity.getReasonCode());
					row.createCell(47).setCellValue(objEntity.getCiIssue());
					row.createCell(48).setCellValue(objEntity.getNonCiIssue());
					row.createCell(49).setCellValue(objEntity.getResolution());
					row.createCell(50).setCellValue(objEntity.getStatus());
					row.createCell(51).setCellValue(objEntity.getNvtfNoHarm());
					row.createCell(52).setCellValue(objEntity.getEngineerOneNotes());
					row.createCell(53).setCellValue(objEntity.getEngineerTwoNotes());
					row.createCell(54).setCellValue(objEntity.getEngineerThreeNotes());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.OVERALL_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.OVERALL_REPORT_SPRINT_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.getOverallSprintToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will importVerizonOverallDetails
	 * 
	 * @param file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public JSONObject importVerizonOverallDetails(MultipartFile file, String sessionId) {
		JSONObject resultMap = new JSONObject();
		SchedulingVerizonModel schedulingVerizonModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		List<SchedulingVerizonEntity> listSchedulingVerizonEntity = new ArrayList<SchedulingVerizonEntity>();
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("OVERALL_DETAILS"));
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
					schedulingVerizonModel = new SchedulingVerizonModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Total_lookup")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Date")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Market")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("eNB_Name")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 5:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("eNB_ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("RAN_Engineer")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 7:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Grow_Req")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Grow_Comp")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("CIQ_Present")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Status")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Revisit")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 12:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("VLSM")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 13:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Start_Time")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 14:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("End_Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 15:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Comments")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 16:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ISSUE_")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 17:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("C&I")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 18:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Non_C&I")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 19:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ALD")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 20:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Week")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 21:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Month")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 22:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Status2")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 23:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Quarter")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 24:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Year")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 25:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Rules_1")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 26:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Rule_2")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 27:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Day")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							}
						} else {

							switch (i) {
							case 0:
								Double dId = Double.parseDouble(cell.toString());
								schedulingVerizonModel.setId(dId.intValue());
								break;
							case 1:
								schedulingVerizonModel.setTotalLookup(((String) getCellValue(cell)).trim());
								break;
							case 2:
								schedulingVerizonModel.setForecastStartDate(((String) getCellValue(cell)).trim());
								break;
							case 3:
								schedulingVerizonModel.setMarket(((String) getCellValue(cell)).trim());
								break;
							case 4:
								schedulingVerizonModel.setEnbName(((String) getCellValue(cell)).trim());
								break;
							case 5:
								schedulingVerizonModel.setEnbId(((String) getCellValue(cell)).trim());
								break;
							case 6:
								schedulingVerizonModel.setRanEngineer(((String) getCellValue(cell)).trim());
								break;
							case 7:
								schedulingVerizonModel.setGrowRequest(((String) getCellValue(cell)).trim());
								break;
							case 8:
								schedulingVerizonModel.setGrowCompleted(((String) getCellValue(cell)).trim());
								break;
							case 9:
								schedulingVerizonModel.setCiqPresent(((String) getCellValue(cell)).trim());
								break;
							case 10:
								schedulingVerizonModel.setStatus(((String) getCellValue(cell)).trim());
								break;
							case 11:
								schedulingVerizonModel.setRevisit(((String) getCellValue(cell)).trim());
								break;
							case 12:
								schedulingVerizonModel.setVlsm(((String) getCellValue(cell)).trim());
								break;
							case 13:
								schedulingVerizonModel.setStartTime(((String) getCellValue(cell)).trim());
								break;
							case 14:
								schedulingVerizonModel.setEndTime(((String) getCellValue(cell)).trim());
								break;
							case 15:
								schedulingVerizonModel.setComments(((String) getCellValue(cell)).trim());
								break;
							case 16:
								schedulingVerizonModel.setIssue(((String) getCellValue(cell)).trim());
								break;
							case 17:
								schedulingVerizonModel.setCi(((String) getCellValue(cell)).trim());
								break;
							case 18:
								schedulingVerizonModel.setNonCi(((String) getCellValue(cell)).trim());
								break;
							case 19:
								schedulingVerizonModel.setAld(((String) getCellValue(cell)).trim());
								break;
							case 20:
								schedulingVerizonModel.setWeek(((String) getCellValue(cell)).trim());
								break;
							case 21:
								schedulingVerizonModel.setMonth(((String) getCellValue(cell)).trim());
								break;
							case 22:
								schedulingVerizonModel.setStatus2(((String) getCellValue(cell)).trim());
								break;
							case 23:
								schedulingVerizonModel.setQuarter(((String) getCellValue(cell)).trim());
								break;
							case 24:
								schedulingVerizonModel.setYear(((String) getCellValue(cell)).trim());
								break;
							case 25:
								schedulingVerizonModel.setRule1(((String) getCellValue(cell)).trim());
								;
								break;
							case 26:
								schedulingVerizonModel.setRule2(((String) getCellValue(cell)).trim());
								break;
							case 27:
								schedulingVerizonModel.setDay(((String) getCellValue(cell)).trim());
								break;

							}
						}
						i++;
					}

					if (rowCount > 1) {
						SchedulingVerizonEntity schedulingVerizonEntity = schedulingSRDto
								.getVerizonOverallReportsEntity(schedulingVerizonModel);
						if (schedulingVerizonEntity != null) {
							listSchedulingVerizonEntity.add(schedulingVerizonEntity);
						}
					}
				}
				workbook.close();
				fis.close();
			}
			for (SchedulingVerizonEntity schedulingVerizonEntities : listSchedulingVerizonEntity) {
				schedulingVerizonEntities.setId(null);
				boolean bStatus = schedulingRepository.saveVerizonSchedulingDetails(schedulingVerizonEntities);
				if (bStatus) {
					resultMap.put("status", Constants.SUCCESS);

				}
			}
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.importVerizonOverallDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * This method will getVerizonEodDetails
	 * 
	 * @param schedulingVerizonModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getVerizonEodDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId) {
		Map<String, Object> schedulingObj = null;
		try {
			schedulingObj = schedulingRepository.getVerizonEodDetails(schedulingVerizonModel, page, count, customerId);
		} catch (Exception e) {
			logger.error("Exception in SchedulingServiceImpl.getVerizonEodDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return schedulingObj;
	}

	/**
	 * This method will getSprintEodDetails
	 * 
	 * @param schedulingSprintModel,page,count,customerId
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSprintEodDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId) {
		Map<String, Object> schedulingObj = null;
		try {
			schedulingObj = schedulingRepository.getSprintEodDetails(schedulingSprintModel, page, count, customerId);
		} catch (Exception e) {
			logger.error(
					"Exception in SchedulingServiceImpl.getSprintEodDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return schedulingObj;
	}

	/**
	 * This method will getEodVerizonToCreateExcel
	 * 
	 * @param schedulingVerizonModel
	 * @return boolean
	 */
	@Override
	public boolean getEodVerizonToCreateExcel(SchedulingVerizonModel schedulingVerizonModel) {
		boolean status = false;
		List<SchedulingVerizonEntity> objSchedulingVerizonEntity = null;
		List<SchedulingVerizonModel> objSchedulingVerizonModel = null;
		String[] columns = Constants.EOD_REPORTS_VERIZON_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet(Constants.EOD_REPORTS_VERIZON);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingVerizonEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingVerizonModel);
			objSchedulingVerizonModel = schedulingSRDto.getVerizonEodModel(objSchedulingVerizonEntity);
			if (objSchedulingVerizonModel != null && objSchedulingVerizonModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingVerizonModel objEntity : objSchedulingVerizonModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getTotalLookup());
					row.createCell(1).setCellValue(objEntity.getForecastStartDate());
					row.createCell(2).setCellValue(objEntity.getMarket());
					row.createCell(3).setCellValue(objEntity.getEnbName());
					row.createCell(4).setCellValue(objEntity.getEnbId());
					row.createCell(5).setCellValue(objEntity.getRanEngineer());
					row.createCell(6).setCellValue(objEntity.getGrowRequest());
					row.createCell(7).setCellValue(objEntity.getGrowCompleted());
					row.createCell(8).setCellValue(objEntity.getCiqPresent());
					row.createCell(9).setCellValue(objEntity.getStatus());
					row.createCell(10).setCellValue(objEntity.getRevisit());
					row.createCell(11).setCellValue(objEntity.getVlsm());
					row.createCell(12).setCellValue(objEntity.getCiStartTime());
					row.createCell(13).setCellValue(objEntity.getCiEndTime());
					row.createCell(14).setCellValue(objEntity.getComments());
					row.createCell(15).setCellValue(objEntity.getIssue());
					row.createCell(16).setCellValue(objEntity.getCi());
					row.createCell(17).setCellValue(objEntity.getNonCi());
					row.createCell(18).setCellValue(objEntity.getAld());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.EOD_REPORTS_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.EOD_REPORTS_VERIZON_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.getEodVerizonToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will getEodSprintToCreateExcel
	 * 
	 * @param schedulingSprintModel
	 * @return boolean
	 */
	@Override
	public boolean getEodSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel) {
		boolean status = false;
		List<SchedulingSprintEntity> objSchedulingSprintEntity = null;
		List<SchedulingSprintModel> objSchedulingSprintModel = null;
		String[] columns = Constants.EOD_REPORTS_SPRINT_COLUMNS;
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet(Constants.SCHEDULING_SPRINT);
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);
			headerFont.setColor(IndexedColors.BLACK.getIndex());
			XSSFCellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
			headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}
			objSchedulingSprintEntity = schedulingRepository.getSchedulingDetailsToExPort(schedulingSprintModel);
			objSchedulingSprintModel = schedulingSRDto.getSprintEodModel(objSchedulingSprintEntity);
			if (objSchedulingSprintModel != null && objSchedulingSprintModel.size() > 0) {
				int rowNum = 1;
				for (SchedulingSprintModel objEntity : objSchedulingSprintModel) {
					Row row = sheet.createRow(rowNum++);
					row.createCell(0).setCellValue(objEntity.getActualMigrationStartDate());
					row.createCell(1).setCellValue(objEntity.getCompDate());
					row.createCell(2).setCellValue(objEntity.getRegion());
					row.createCell(3).setCellValue(objEntity.getMarket());
					row.createCell(4).setCellValue(objEntity.getEnbId());
					row.createCell(5).setCellValue(objEntity.getCascade());
					row.createCell(6).setCellValue(objEntity.getTypeOne());
					row.createCell(7).setCellValue(objEntity.getCurrentSoftware());
					row.createCell(8).setCellValue(objEntity.getScriptsRan());
					row.createCell(9).setCellValue(objEntity.getDspImplemented());
					row.createCell(10).setCellValue(objEntity.getPutTool());
					row.createCell(11).setCellValue(objEntity.getScriptErrors());
				}
			}
			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			StringBuilder fileNameBuilder = new StringBuilder();
			fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(LoadPropertyFiles.getInstance().getProperty(Constants.EOD_REPORTS_DETAILS));
			File schedulingDirectory = new File(fileNameBuilder.toString());
			if (!schedulingDirectory.exists()) {
				schedulingDirectory.mkdir();
			}
			fileNameBuilder.append(Constants.EOD_REPORTS_SPRINT_XLSX);
			FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
			workbook.write(fileOut);
			fileOut.close();
			workbook.close();
			status = true;
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.getEodSprintToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	/**
	 * This method will importSprintOverallDetails
	 * 
	 * @param file,sessionId
	 * @return JSONObject
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public JSONObject importSprintOverallDetails(MultipartFile file, String sessionId) {
		JSONObject resultMap = new JSONObject();
		SchedulingSprintModel schedulingSprintModel = null;
		String fileLocation = "";
		Workbook workbook = null;
		List<SchedulingSprintEntity> listSchedulingSprintEntity = new ArrayList<SchedulingSprintEntity>();
		try {
			if (file != null) {
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
				Date date = new Date();
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(LoadPropertyFiles.getInstance().getProperty("OVERALL_DETAILS"));
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
					schedulingSprintModel = new SchedulingSprintModel();
					for (int cn = 0; cn < row.getLastCellNum(); cn++) {
						Cell cell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
						if (rowCount == 1) {
							switch (i) {
							case 0:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 1:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Schedule Date")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 2:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Start Date")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 3:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Comp Date")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 4:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Region")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 5:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Market")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 6:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Cascade")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							
							case 7:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("eNB ID")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 8:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Type")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 9:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("DT or MW")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 10:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("PUT Tool")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 11:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Current Software")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 12:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Scripts Ran")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 13:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("CIQ/Script Error")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							
							case 14:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("DSP Implemented")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 15:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("C&I Eng 1")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 16:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I start time 1")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 17:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I end time 1")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 18:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE Region")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 19:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE 1")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 20:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 1 Contact Info")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 21:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 1 Arrival time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 22:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("C&I Eng 2")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 23:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I start time 2")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 24:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I end time 2")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 25:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE 2")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 26:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 2 Contact Info")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 27:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 2 Arrival time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 28:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("C&I Eng 3")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 29:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I start time 3")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 30:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("C&I end time 3")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 31:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("FE 3")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 32:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 3 Contact Info")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 33:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("FE 3 Arrival time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 34:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("GC")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 35:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("TC Name")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
								
							case 36:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("TC Contact Info")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 37:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("GC Arrival Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 38:
								if (cell.toString() != null && cell.toString().trim()
										.equalsIgnoreCase("Circuit Breaker/ Power Work. START")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 39:
								if (cell.toString() != null && cell.toString().trim()
										.equalsIgnoreCase("Circuit Breaker/ Power Work. END")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 40:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Alpha Start Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 41:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Alpha End Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 42:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Beta Start Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 43:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Beta End Time")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 44:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Gamma Start Time")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 45:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Gamma End Time")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 46:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Issue Reason Code")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 47:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("C & I Issue")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 48:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Non C & I Issue")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 49:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Resolution")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 50:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("Status")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 51:
								if (cell.toString() != null && cell.toString().trim().equalsIgnoreCase("NVTF/SAMS no harm #ticket")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 52:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Engineer 1 Notes")) {
									// do nothing

								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 53:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Engineer 2 Notes")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							case 54:
								if (cell.toString() != null
										&& cell.toString().trim().equalsIgnoreCase("Engineer 3 Notes")) {
									// do nothing
								} else {
									resultMap.put("status", Constants.FAIL);
									return resultMap;
								}
								break;
							}
						} else {

							switch (i) {
							case 0:
								Double dId = Double.parseDouble(cell.toString());
								schedulingSprintModel.setId(dId.intValue());
								break;
							case 1:
								schedulingSprintModel.setScheduleDate(((String) getCellValue(cell)).trim());
							case 2:
								schedulingSprintModel.setStartDate(((String) getCellValue(cell)).trim());
								break;
							case 3:
								schedulingSprintModel.setCompDate(((String) getCellValue(cell)).trim());
								break;
							case 4:
								schedulingSprintModel.setRegion(((String) getCellValue(cell)).trim());
								break;
							case 5:
								schedulingSprintModel.setMarket(((String) getCellValue(cell)).trim());
								break;
							case 6:
								schedulingSprintModel.setCascade(((String) getCellValue(cell)).trim());
								break;
							case 7:
								schedulingSprintModel.setEnbId(((String) getCellValue(cell)).trim());
								break;
							case 8:
								schedulingSprintModel.setType(((String) getCellValue(cell)).trim());
								break;
							case 9:
								schedulingSprintModel.setDtOrMw(((String) getCellValue(cell)).trim());
								break;
							case 10:
								schedulingSprintModel.setPutTool(((String) getCellValue(cell)).trim());
								break;
							case 11:
								schedulingSprintModel.setCurrentSoftware(((String) getCellValue(cell)).trim());
								break;
							case 12:
								schedulingSprintModel.setScriptsRan(((String) getCellValue(cell)).trim());
								break;
							case 13:
								schedulingSprintModel.setScriptErrors(((String) getCellValue(cell)).trim());
								break;
							case 14:
								schedulingSprintModel.setDspImplemented(((String) getCellValue(cell)).trim());
								break;
							case 15:
								schedulingSprintModel.setCiEngineerOne(((String) getCellValue(cell)).trim());
								break;
							case 16:
								schedulingSprintModel.setCiStartTimeOne(((String) getCellValue(cell)).trim());
								break;
							case 17:
								schedulingSprintModel.setCiEndTimeOne(((String) getCellValue(cell)).trim());
								break;
							case 18:
								schedulingSprintModel.setFeRegion(((String) getCellValue(cell)).trim());
								break;
							case 19:
								schedulingSprintModel.setFeOne(((String) getCellValue(cell)).trim());
								break;
							case 20:
								schedulingSprintModel.setFeContactInfoOne(((String) getCellValue(cell)).trim());
								break;
							case 21:
								schedulingSprintModel.setFeArrivalTimeOne(((String) getCellValue(cell)).trim());
								break;
							case 22:
								schedulingSprintModel.setCiEngineerTwo(((String) getCellValue(cell)).trim());
								break;
							case 23:
								schedulingSprintModel.setCiStartTimeTwo(((String) getCellValue(cell)).trim());
								break;
							case 24:
								schedulingSprintModel.setCiEndTimeTwo(((String) getCellValue(cell)).trim());
								break;
							case 25:
								schedulingSprintModel.setFeTwo(((String) getCellValue(cell)).trim());
								break;
							case 26:
								schedulingSprintModel.setFeContactInfoTwo(((String) getCellValue(cell)).trim());
								break;
							case 27:
								schedulingSprintModel.setFeArrivalTimeTwo(((String) getCellValue(cell)).trim());
								break;
							case 28:
								schedulingSprintModel.setCiEngineerThree(((String) getCellValue(cell)).trim());
								break;
							case 29:
								schedulingSprintModel.setCiStartTimeThree(((String) getCellValue(cell)).trim());
								break;
							case 30:
								schedulingSprintModel.setCiEndTimeThree(((String) getCellValue(cell)).trim());
								break;
							case 31:
								schedulingSprintModel.setFeThree(((String) getCellValue(cell)).trim());
								break;
							case 32:
								schedulingSprintModel.setFeContactInfoThree(((String) getCellValue(cell)).trim());
								break;
							case 33:
								schedulingSprintModel.setFeArrivalTimeThree(((String) getCellValue(cell)).trim());
								break;
							case 34:
								schedulingSprintModel.setGc(((String) getCellValue(cell)).trim());
								break;
							case 35:
								schedulingSprintModel.setTcName(((String) getCellValue(cell)).trim());
								break;
							case 36:
								schedulingSprintModel.setTcContactInfo(((String) getCellValue(cell)).trim());
								break;
							case 37:
								schedulingSprintModel.setGcArrivalTime(((String) getCellValue(cell)).trim());
								break;
							case 38:
								schedulingSprintModel.setCircuitbreakerStart(((String) getCellValue(cell)).trim());
								break;
							case 39:
								schedulingSprintModel.setCircuitbreakerEnd(((String) getCellValue(cell)).trim());
								break;
							case 40:
								schedulingSprintModel.setAlphaStartTime(((String) getCellValue(cell)).trim());
								break;
							case 41:
								schedulingSprintModel.setAlphaEndTime(((String) getCellValue(cell)).trim());
								break;
							case 42:
								schedulingSprintModel.setBetaStartTime(((String) getCellValue(cell)).trim());
								break;
							case 43:
								schedulingSprintModel.setBetaEndTime(((String) getCellValue(cell)).trim());
								break;
							case 44:
								schedulingSprintModel.setGammaStartTime(((String) getCellValue(cell)).trim());
								break;
							case 45:
								schedulingSprintModel.setGammaEndTime(((String) getCellValue(cell)).trim());
								break;
							case 46:
								schedulingSprintModel.setReasonCode(((String) getCellValue(cell)).trim());
								break;
							case 47:
								schedulingSprintModel.setCiIssue(((String) getCellValue(cell)).trim());
								break;
							case 48:
								schedulingSprintModel.setNonCiIssue(((String) getCellValue(cell)).trim());
								break;
							case 49:
								schedulingSprintModel.setResolution(((String) getCellValue(cell)).trim());
								break;
							case 50:
								schedulingSprintModel.setStatus(((String) getCellValue(cell)).trim());
								break;
							case 51:
								schedulingSprintModel.setNvtfNoHarm(((String) getCellValue(cell)).trim());
								break;
							case 52:
								schedulingSprintModel.setEngineerOneNotes(((String) getCellValue(cell)).trim());
								break;
							case 53:
								schedulingSprintModel.setEngineerTwoNotes(((String) getCellValue(cell)).trim());
								break;
							case 54:
								schedulingSprintModel.setEngineerThreeNotes(((String) getCellValue(cell)).trim());
								break;

							}
						}
						i++;
					}

					if (rowCount > 1) {
						SchedulingSprintEntity schedulingSprintEntity = schedulingSRDto
								.getSprintOverallReportsEntity(schedulingSprintModel);
						if (schedulingSprintEntity != null) {
							listSchedulingSprintEntity.add(schedulingSprintEntity);
						}
					}
				}
				workbook.close();
				fis.close();
			}
			for (SchedulingSprintEntity schedulingSprintEntities : listSchedulingSprintEntity) {
				schedulingSprintEntities.setId(null);
				boolean bStatus = schedulingRepository.saveSprintSchedulingDetails(schedulingSprintEntities);
				if (bStatus) {
					resultMap.put("status", Constants.SUCCESS);

				}
			}
		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.importSprintOverallDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * This method will getCustomerIdList
	 * 
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getCustomerIdList() {
		Map<String, Object> schedulingObj = null;
		try {
			schedulingObj = schedulingRepository.getCustomerIdList();
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getVerizonSchedulingDetails() : "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return schedulingObj;
	}

	@SuppressWarnings("deprecation")
	private Object getCellValue(Cell cell) {

		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_STRING:
			return dataFormatter.formatCellValue(cell);

		case Cell.CELL_TYPE_NUMERIC:
			return dataFormatter.formatCellValue(cell);
		/* return String.valueOf(cell.getNumericCellValue()); */
		case Cell.CELL_TYPE_BOOLEAN:
			return dataFormatter.formatCellValue(cell);
		case Cell.CELL_TYPE_BLANK:
			return dataFormatter.formatCellValue(cell);
		case Cell.CELL_TYPE_FORMULA:
			switch (cell.getCachedFormulaResultType()) {
			case Cell.CELL_TYPE_NUMERIC:
				Double e1Val = cell.getNumericCellValue();
				BigDecimal bd = new BigDecimal(e1Val.toString());
				long lonVal = bd.longValue();
				return String.valueOf(lonVal);
			case Cell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue().toString();
			}
		}

		return "";
	}
}
