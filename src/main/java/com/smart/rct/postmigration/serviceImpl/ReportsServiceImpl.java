package com.smart.rct.postmigration.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.ReportsEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.controller.RunTestController;
import com.smart.rct.migration.repository.RunTestResultRepository;
import com.smart.rct.migration.service.RunTestService;
import com.smart.rct.postmigration.models.ReportsModel;
import com.smart.rct.postmigration.repository.ReportsRepository;
import com.smart.rct.postmigration.service.ReportsService;
import com.smart.rct.premigration.controller.GenerateCsvController;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class ReportsServiceImpl implements ReportsService {

	final static Logger logger = LoggerFactory.getLogger(ReportsServiceImpl.class);

	@Autowired
	ReportsRepository reportsRepository;

	@Autowired
	RunTestResultRepository runTestResultRepository;

	@Autowired
	GenerateCsvController generateCsvController;

	// @Autowired
	// WorkFlowManagementEntity WorkFlowManagementEntityThread;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	RunTestService runTestService;

	@Autowired
	RunTestController runTestController;

	@Autowired
	NeMappingService neMappingService;

	@Autowired
	CommonUtil common;

	@Override
	public Map<String, Object> getReportsDetails(Integer customerId, int page, int count, String programName,
			ReportsModel reportsModel) {
		Map<String, Object> overallReportsObj = null;
		try {
			overallReportsObj = reportsRepository.getReportsDetails(customerId, page, count, programName, reportsModel,"");
		} catch (Exception e) {
			logger.error("Exception SchedulingServiceImpl.getVerizonOverallReportsDetails(): "
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean getDetailsToCreateExcel(Integer customerId, int page, int count, String programName,
			ReportsModel reportsModel, List<String> filter,String type) {
		boolean status = false;
		Map<String, Object> objSchedulingVerizonEntity = null;
		String[] columns = Constants.REPORTS_COLUMNS_ALL;
		String[] column = null;
		Map<String, String> map = new HashMap<>();
		map.put("VZN-5G-MM", "SRCT_Service_Delivery_Report_5G.xlsx");
		map.put("VZN-4G-USM-LIVE", "SRCT_Service_Delivery_Report_4G.xlsx");
		map.put("VZN-5G-DSS", "SRCT_Service_Delivery_Report_DSS.xlsx");
		map.put("VZN-4G-FSU", "SRCT_Service_Delivery_Report_4G_FSU.xlsx");
		try {
			// CreationHelper createHelper = workbook.getCreationHelper();
			objSchedulingVerizonEntity = reportsRepository.getReportsDetails(customerId, page, count, programName,
					reportsModel,"download");

			List<ReportsEntity> reports = (List<ReportsEntity>) objSchedulingVerizonEntity.get("runTestEntity");

			List<List<ReportsEntity>> programbasedReports = groupReportsBasedOnProgram(reports);
			for (List<ReportsEntity> r : programbasedReports) {

				int cnt = 0;
				XSSFWorkbook workbook = new XSSFWorkbook();
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

				if (r.get(0).getProgrameName().contains("MM")) {
					column = Constants.REPORTS_COLUMNS_5G;
				}else if (r.get(0).getProgrameName().contains("DSS")) {
					column = Constants.REPORTS_COLUMNS_DSS;
				}
				else if (r.get(0).getProgrameName().contains("USM-LIVE")) {
					column = Constants.REPORTS_COLUMNS_4G;
				}
				else if (r.get(0).getProgrameName().contains("FSU")) {
					column = Constants.REPORTS_COLUMNS_4G_FSU;
				} else
					column = columns;
				for (int i = 0; i < column.length; i++) {
					if (filter.isEmpty() || filter.contains(column[i])) {
						Cell cell = headerRow.createCell(cnt++);
						cell.setCellValue(column[i]);
						cell.setCellStyle(headerCellStyle);
					}
				}
				if (r != null && r.size() > 0) {
					int rowNum = 1;
					for (ReportsEntity objEntity : r) {
						Row row = sheet.createRow(rowNum++);
						int c = 0;
						
						if (filter.isEmpty() || filter.contains(columns[0])) {
							row.createCell(c++).setCellValue(
									CommonUtil.dateToString(objEntity.getStartDate(), Constants.YYYY_MM_DD_HH_MM_SS));
						}
						if (filter.isEmpty() || filter.contains(columns[1])) {
							row.createCell(c++).setCellValue(objEntity.getMarket());
						}
						if (filter.isEmpty() || filter.contains(columns[2])) {
							row.createCell(c++).setCellValue(objEntity.getCiqName());
						}
						if (filter.isEmpty() || filter.contains(columns[3])) {
							row.createCell(c++).setCellValue(objEntity.getNeName());
						}
						if (filter.isEmpty() || filter.contains(columns[4])) {
							row.createCell(c++).setCellValue(objEntity.getEnbId());
						}
						if (filter.isEmpty() || filter.contains(columns[5])) {
							row.createCell(c++).setCellValue(objEntity.getUserName());
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[6])) {
								row.createCell(c++).setCellValue(objEntity.getPreMigEnvStatus());
							}
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[7])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPreMigEnvGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[8])) {
								row.createCell(c++).setCellValue(objEntity.getPreMigGrowStatus());
							}
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[9])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPreMigGrowGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[10])) {
								row.createCell(c++).setCellValue(objEntity.getPreMigCommStatus());
	
							}
						}
						if (!objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[11])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPreMigCommGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[12])) {
								row.createCell(c++).setCellValue(objEntity.getPreMigEndcStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[13])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPreMigEndcGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[14])) {
								row.createCell(c++).setCellValue(objEntity.getPreMigRfScriptStatus());
							}
						}                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[15])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPreMigRfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (!objEntity.getProgrameName().contains("DSS") && !objEntity.getProgrameName().contains("FSU")) {
							if (filter.isEmpty() || filter.contains(columns[16])) {
								row.createCell(c++).setCellValue(objEntity.getNeGrowPnpStatus());
							}
						}
						if (!objEntity.getProgrameName().contains("DSS") && !objEntity.getProgrameName().contains("FSU")) {
							if (filter.isEmpty() || filter.contains(columns[17])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getNeGrowPnpgenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[18])) {
								row.createCell(c++).setCellValue(objEntity.getNeGrowAuCacellStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[19])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getNeGrowAuCacellGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
								if (filter.isEmpty() || filter.contains(columns[20])) {
									row.createCell(c++).setCellValue(objEntity.getNeGrowAuStatus());
								}
							}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[21])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getNeGrowAuGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
								if (filter.isEmpty() || filter.contains(columns[22])) {
									row.createCell(c++).setCellValue(objEntity.getNeGrowenbStatus());
								}
							}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[23])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getNeGrowenbGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
								if (filter.isEmpty() || filter.contains(columns[24])) {
									row.createCell(c++).setCellValue(objEntity.getNeGrowCellStatus());
								}
							}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[25])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getNeGrowCellGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")
									|| objEntity.getProgrameName().contains("USM-LIVE")) {
								if (filter.isEmpty() || filter.contains(columns[26])) {
									row.createCell(c++).setCellValue(objEntity.getMigCommStatus());
								}
							}
						if (objEntity.getProgrameName().contains("MM")
								|| objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[27])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigCommGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[28])) {
								row.createCell(c++).setCellValue(objEntity.getMigAcpfStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[29])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigAcpfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[30])) {
								row.createCell(c++).setCellValue(objEntity.getMigCslStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[31])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigCslGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[32])) {
								row.createCell(c++).setCellValue(objEntity.getMigEndcStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[33])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigEndcGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[34])) {
								row.createCell(c++).setCellValue(objEntity.getMigAnchorStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[35])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigAnchorGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}						
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[36])) {
								row.createCell(c++).setCellValue(objEntity.getMigNbrStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[37])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigNbrGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("4G")) {
							if (filter.isEmpty() || filter.contains(columns[38])) {
								row.createCell(c++).setCellValue(objEntity.getMigRfStatus());
							}
						}
						if (objEntity.getProgrameName().contains("4G")) {
							if (filter.isEmpty() || filter.contains(columns[39])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigRfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[40])) {
								row.createCell(c++).setCellValue(objEntity.getMigPreCheckStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[41])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigPreCheckGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[42])) {
								row.createCell(c++).setCellValue(objEntity.getMigCutoverStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[43])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigCutoverGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[44])) {
								row.createCell(c++).setCellValue(objEntity.getMigRollbackStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[45])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getMigRollbackGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[46])) {
								row.createCell(c++).setCellValue(objEntity.getPostAuAuditStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[47])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostAuAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[48])) {
								row.createCell(c++).setCellValue(objEntity.getPostEndcAuditStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM")) {
							if (filter.isEmpty() || filter.contains(columns[49])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostEndcAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[50])) {
								row.createCell(c++).setCellValue(objEntity.getPostMigAtpStatus());
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[51])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostMigAtpGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[52])) {
								row.createCell(c++).setCellValue(objEntity.getPostMigAudiStatus());
							}
						}
						if (objEntity.getProgrameName().contains("USM-LIVE")) {
							if (filter.isEmpty() || filter.contains(columns[53])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostMigAudiGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[54])) {
								row.createCell(c++).setCellValue(objEntity.getPostMigVduStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[55])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostMigVduGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[56])) {
								row.createCell(c++).setCellValue(objEntity.getPostMigEnbAudiStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[57])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostMigEnbAudiGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM") || objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[58])) {
								row.createCell(c++).setCellValue(objEntity.getPostAupfStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM") || objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[59])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostAupfGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("MM") || objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[60])) {
								row.createCell(c++).setCellValue(objEntity.getPostAcpfAuditStatus());
							}
						}
						if (objEntity.getProgrameName().contains("MM") || objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[61])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostAcpfAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[62])) {
								row.createCell(c++).setCellValue(objEntity.getPostMigFsuAuditStatus());
							}
						}
						if (objEntity.getProgrameName().contains("DSS")) {
							if (filter.isEmpty() || filter.contains(columns[63])) {
								row.createCell(c++).setCellValue(CommonUtil.dateToString(objEntity.getPostMigFsuAuditGenTime(), Constants.YYYY_MM_DD_HH_MM_SS));
							}
						}
						if (filter.isEmpty() || filter.contains(columns[64])) {
							row.createCell(c++).setCellValue(objEntity.getSiteDataStatus());
						}
						
						if (filter.isEmpty() || filter.contains(columns[65])) {
							row.createCell(c++).setCellValue(objEntity.getRemarks());
						}
						if (filter.isEmpty() || filter.contains(columns[66])) {
							row.createCell(c++).setCellValue(objEntity.getProgrameName());
						}
					}
				}
				for (int i = 0; i < column.length; i++) {
					sheet.autoSizeColumn(i);
				}
				StringBuilder fileNameBuilder = new StringBuilder();
				if(type.equals("download"))
					fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						.append(Constants.OVERALL_DETAILS + "/");
				else
					fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
					.append(Constants.OVERALL_REPORTS_DETAILS + "/");
				
				File schedulingDirectory = new File(fileNameBuilder.toString());
				if (!schedulingDirectory.exists()) {
					schedulingDirectory.mkdir();
				}
				fileNameBuilder.append(map.get(r.get(0).getProgrameName()));
				FileOutputStream fileOut = new FileOutputStream(fileNameBuilder.toString());
				workbook.write(fileOut);
				fileOut.close();
				workbook.close();
				status = true;
			}

		} catch (Exception e) {
			logger.error("Excpetion in SchedulingServiceImpl.getOverallDetailsToCreateExcel(): "
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return status;
	}

	public List<List<ReportsEntity>> groupReportsBasedOnProgram(List<ReportsEntity> reports) {

		List<List<ReportsEntity>> details = new ArrayList<>();

		List<String> str = new ArrayList<>();
		ReportsEntity rep = null;
		ReportsEntity rep1 = null;

		for (int i = 0; i < reports.size(); i++) {
			List<ReportsEntity> data = new ArrayList<>();
			rep = reports.get(i);
			if (str.isEmpty() || !str.contains(rep.getProgrameName())) {
				for (int j = i+1; j < reports.size(); j++) {
					rep1 = reports.get(j);
					if (rep.getProgrameName().equals(rep1.getProgrameName())) {
						data.add(rep1);
				}
			}
			
			data.add(rep);
			details.add(data);
			}
			str.add(rep.getProgrameName());
		}
		return details;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertReportDetails(JSONObject reportsObject, Integer programId, String programName, String user,
			 String ciqFileName, String filetype) {
		try {
			logger.error("Inside insertReportDetails");
			if(programName.contains("5G-CBAND")) {
				return;
			}
			String completed = "Completed";
			String failure = "Failure";
			Integer customerId = 0;
			String enbId;
			String enbName = null;
			String market = null;
			String dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
			boolean envFiletype = filetype.equalsIgnoreCase(Constants.FILE_TYPE_ENV) || filetype.equalsIgnoreCase(Constants.ALL_5g)
					|| "RANConfig".equalsIgnoreCase(filetype);
			boolean commFiletype = filetype.equalsIgnoreCase(Constants.FILE_TYPE_COMM_SCRIPT) || filetype.equalsIgnoreCase(Constants.ALL_5g)
					|| "RANConfig".equalsIgnoreCase(filetype);
			boolean growFiletype = filetype.equalsIgnoreCase(Constants.FILE_TYPE_CSV) || filetype.equalsIgnoreCase(Constants.ALL_5g)
					|| "RANConfig".equalsIgnoreCase(filetype);
			boolean endcFiletype = filetype.equalsIgnoreCase(Constants.FILE_TYPE_ENDC) || filetype.equalsIgnoreCase(Constants.ALL_5g)
					|| "RANConfig".equalsIgnoreCase(filetype);
			Set<String> reportskey = reportsObject.keySet();
			for (String key : reportskey) {
				enbId = key;
				ReportsEntity reportEntity = reportsRepository.getEntityData(programName, enbId);
				if (reportEntity == null) {
					reportEntity = new ReportsEntity();
					logger.error("Report entity is null ");
					List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName,
							enbId, "", dbcollectionFileName);
					if (CommonUtil.isValidObject(listCIQDetailsModel) && !listCIQDetailsModel.isEmpty()) {
						if (programName.contains("5G-MM")) {
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("GNB AU Name")) {
								enbName = listCIQDetailsModel.get(0).getCiqMap().get("GNB AU Name").getHeaderValue();
							}
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("Market")) {
								market = listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue();
							}
						} else if(programName.contains("4G-USM-LIVE")) {
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("eNB_Name")) {
								enbName = listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name").getHeaderValue();
							}
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("Market")) {
								market = listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue();
							}
						} else if(programName.contains("4G-FSU")) {
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NE_Name")) {
								enbName = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
							}
						} else if(programName.contains("5G-DSS")) {
							if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NEName")) {
								enbName = listCIQDetailsModel.get(0).getCiqMap().get("NEName").getHeaderValue();
							}
						}
					}
					CustomerDetailsEntity customerDetailsEntity = getCustomerDetailsEntityById(programId);
					if(customerDetailsEntity != null) {
						customerId = customerDetailsEntity.getCustomerEntity().getId();
					}
					reportEntity.setEnbId(enbId);
					reportEntity.setNeName(enbName);
					reportEntity.setCiqName(ciqFileName);
					reportEntity.setMarket(market);
					reportEntity.setCustomerId(customerId);
					reportEntity.setProgrameName(programName);
				}

				Map<String, JSONObject> reportsMap = (Map<String, JSONObject>) reportsObject.get(key);
				if(reportEntity.getUserName()==null || reportEntity.getUserName().isEmpty()) {
					reportEntity.setUserName(user);
				} else {
					List<String> users = Arrays.asList(reportEntity.getUserName().split("\\s*,\\s*"));
					if(!users.contains(user)) {
						reportEntity.setUserName(reportEntity.getUserName() + "," + user);					
					}				
				}			
				reportEntity.setStartDate(new Date());
				
				if (programName.contains("5G-MM")) {
					if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
							&& reportsMap.get("ENV").get("status").equals(true)) {
						reportEntity.setPreMigEnvStatus(completed);
						reportEntity.setPreMigEnvGenTime(new Date());
					} else if(envFiletype) {
						reportEntity.setPreMigEnvStatus(failure);
						reportEntity.setPreMigEnvGenTime(new Date());
					}
					boolean growStatus = reportsMap.containsKey("CELL") && reportsMap.get("CELL").containsKey("status")
							&& reportsMap.get("CELL").get("status").equals(true) && reportsMap.containsKey("AU")
							&& reportsMap.get("AU").containsKey("status")
							&& reportsMap.get("AU").get("status").equals(true) 
							&& reportsMap.containsKey("pnp_macro")
							&& reportsMap.get("pnp_macro").containsKey("status")
							&& reportsMap.get("pnp_macro").get("status").equals(true);
					if (growStatus) {
						reportEntity.setPreMigGrowStatus(completed);
						reportEntity.setPreMigGrowGenTime(new Date());
					} else if(growFiletype) {
						reportEntity.setPreMigGrowStatus(failure);
						reportEntity.setPreMigGrowGenTime(new Date());
					}
					boolean commStatus = 
							reportsMap.containsKey("ROUTE") && reportsMap.get("ROUTE").containsKey("status")
							&& reportsMap.get("ROUTE").get("status").equals(true)  && reportsMap.containsKey("ANCHOR")
							&& reportsMap.get("ANCHOR").containsKey("status")
							&& reportsMap.get("ANCHOR").get("status").equals(true);
					if (commStatus) {
						reportEntity.setPreMigCommStatus(completed);
						reportEntity.setPreMigCommGenTime(new Date());
					} else if(commFiletype){
						reportEntity.setPreMigCommStatus(failure);
						reportEntity.setPreMigCommGenTime(new Date());
					}

					if (reportsMap.containsKey("ENDC") && reportsMap.get("ENDC").containsKey("status")
							&& reportsMap.get("ENDC").get("status").equals(true)) {
						reportEntity.setPreMigEndcStatus(completed);
						reportEntity.setPreMigEndcGenTime(new Date());
					} else if(endcFiletype){
						reportEntity.setPreMigEndcStatus(failure);
						reportEntity.setPreMigEndcGenTime(new Date());
					}
					reportsRepository.createReports(reportEntity);
				} else if (programName.contains("4G-USM-LIVE")) {
					logger.error("insertReportDetails 4G-USM-LIVE");
					if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
							&& reportsMap.get("ENV").get("status").equals(true)) {
						logger.error("Reports Service IMPL env check in.");
						reportEntity.setPreMigEnvStatus(completed);
						reportEntity.setPreMigEnvGenTime(new Date());
						logger.error("Reports Service IMPL env check out and date set as : "+reportEntity.getPreMigEnvGenTime());
					} else if(envFiletype){
						reportEntity.setPreMigEnvStatus(failure);
						reportEntity.setPreMigEnvGenTime(new Date());
					}
					boolean growStatus = reportsMap.containsKey("enb") && reportsMap.get("enb").containsKey("status")
							&& reportsMap.get("enb").get("status").equals(true);
						//	&& reportsMap.containsKey("cell")
						//	&& reportsMap.get("cell").containsKey("status")
						//	&& reportsMap.get("cell").get("status").equals(true);
					if (growStatus) {
						System.out.println("insertReportDetails growStatus "+growStatus);
						reportEntity.setPreMigGrowStatus(completed);
						reportEntity.setPreMigGrowGenTime(new Date());
					} else if(growFiletype){
						System.out.println("insertReportDetails growStatus "+growStatus);
						reportEntity.setPreMigGrowStatus(failure);
						reportEntity.setPreMigGrowGenTime(new Date());
					}
					if (reportsMap.containsKey("COMM") && reportsMap.get("COMM").containsKey("status")
							&& reportsMap.get("COMM").get("status").equals(true)) {
						System.out.println("insertReportDetails COMM Status "+completed);
						reportEntity.setPreMigCommStatus(completed);
						reportEntity.setPreMigCommGenTime(new Date());
					} else if(commFiletype){
						System.out.println("insertReportDetails COMM Status "+failure);
						reportEntity.setPreMigCommStatus(failure);
						reportEntity.setPreMigCommGenTime(new Date());
					}
					reportsRepository.createReports(reportEntity);
				} else if (programName.contains("4G-FSU")) {
					if (reportsMap.containsKey("ENV") && reportsMap.get("ENV").containsKey("status")
							&& reportsMap.get("ENV").get("status").equals(true)) {
						reportEntity.setPreMigEnvStatus(completed);
						reportEntity.setPreMigEnvGenTime(new Date());
					} else if(envFiletype){
						reportEntity.setPreMigEnvStatus(failure);
						reportEntity.setPreMigEnvGenTime(new Date());
					}
					if (reportsMap.containsKey("CSV") && reportsMap.get("CSV").containsKey("status")
							&& reportsMap.get("CSV").get("status").equals(true)) {
						reportEntity.setPreMigGrowStatus(completed);
						reportEntity.setPreMigGrowGenTime(new Date());
					} else if(growFiletype){
						reportEntity.setPreMigGrowStatus(failure);
						reportEntity.setPreMigGrowGenTime(new Date());
					}
					reportsRepository.createReports(reportEntity);
				} else if (programName.contains("5G-DSS")) {
					if (reportsMap.containsKey("DSS") && reportsMap.get("DSS").containsKey("status")
							&& reportsMap.get("DSS").get("status").equals(Constants.SUCCESS)) {
						reportEntity.setPreMigRfScriptStatus(completed);
						reportEntity.setPreMigRfGenTime(new Date());
					} else {
						reportEntity.setPreMigRfScriptStatus(failure);
						reportEntity.setPreMigRfGenTime(new Date());
					}
					reportsRepository.createReports(reportEntity);
				}

			}
		} catch (Exception e) {
			logger.error("Exception in insertReportDetails() " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void insertRunTestReportDetails(String programName, String enbId, String migType, String migSubType,
			List<LinkedHashMap> useCaseList, String finalStatus, String user) {
		try {
			if(programName.contains("5G-CBAND")) {
				return;
			}
			ReportsEntity reportEntity = reportsRepository.getEntityData(programName, enbId);
			if (reportEntity == null) {
				return;
			}
			if(reportEntity.getUserName().isEmpty()) {
				reportEntity.setUserName(user);
			} else {
				List<String> users = Arrays.asList(reportEntity.getUserName().split("\\s*,\\s*"));
				if(!users.contains(user)) {
					reportEntity.setUserName(reportEntity.getUserName() + "," + user);					
				}				
			}
			for (Map usecase : useCaseList) {
				if (((String) usecase.get("useCaseName")).contains(enbId)) {
					String usecaseName = (String) usecase.get("useCaseName");

					if (programName.contains("5G-MM")) {
						if ("NEGrow".equalsIgnoreCase(migSubType)) {
							if (usecaseName.contains("pnp")) {
								reportEntity.setNeGrowPnpStatus(finalStatus);
								reportEntity.setNeGrowPnpgenTime(new Date());
							} else if (usecaseName.contains("AUCaCell")) {
								reportEntity.setNeGrowAuCacellStatus(finalStatus);
								reportEntity.setNeGrowAuCacellGenTime(new Date());
							} else if (usecaseName.contains("AU")) {
								reportEntity.setNeGrowAuStatus(finalStatus);
								reportEntity.setNeGrowAuGenTime(new Date());
							}
						} else if ("Migration".equalsIgnoreCase(migType)) {
							if (usecaseName.contains("ENDC_X2_UseCase")) {
								reportEntity.setMigEndcStatus(finalStatus);
								reportEntity.setMigEndcGenTime(new Date());
							} else if (usecaseName.contains("Anchor_CSL_UseCase")) {
								reportEntity.setMigAnchorStatus(finalStatus);
								reportEntity.setMigAnchorGenTime(new Date());
							} else if (usecaseName.contains("CSL_Usecase")) {
								reportEntity.setMigCslStatus(finalStatus);
								reportEntity.setMigCslGenTime(new Date());
							} else if (usecaseName.contains("AU_Commision_Usecase")) {
								reportEntity.setMigCommStatus(finalStatus);
								reportEntity.setMigCommGenTime(new Date());
							} else if (usecaseName.contains("ACPF_A1A2_Config_Usecase")) {
								reportEntity.setMigAcpfStatus(finalStatus);
								reportEntity.setMigAcpfGenTime(new Date());
							} else if (usecaseName.contains("RF_Scripts_Usecase")) {
								reportEntity.setMigNbrStatus(finalStatus);
								reportEntity.setMigNbrGenTime(new Date());
							}
						}
					} else if (programName.contains("4G-USM-LIVE")) {
						if ("NEGrow".equalsIgnoreCase(migSubType)) {
							if (usecaseName.contains("pnp")) {
								reportEntity.setNeGrowPnpStatus(finalStatus);
								reportEntity.setNeGrowPnpgenTime(new Date());

							} else if (usecaseName.contains("GrowEnb")) {
								reportEntity.setNeGrowenbStatus(finalStatus);
								reportEntity.setNeGrowenbGenTime(new Date());

							} else if (usecaseName.contains("GrowCell")) {
								reportEntity.setNeGrowCellStatus(finalStatus);
								reportEntity.setNeGrowCellGenTime(new Date());

							}
						} else if ("Migration".equalsIgnoreCase(migType)) {
							if (usecaseName.contains("CommissionScriptUsecase")) {
								reportEntity.setMigCommStatus(finalStatus);
								reportEntity.setMigCommGenTime(new Date());

							} else if (usecaseName.contains("RFUsecase")) {
								reportEntity.setMigRfStatus(finalStatus);
								reportEntity.setMigRfGenTime(new Date());

							} 
						}
					} else if (programName.contains("5G-DSS") && "Migration".equalsIgnoreCase(migType)) {
						if (usecaseName.contains("Pre-Check_RF_Scripts_Usecase")) {
							reportEntity.setMigPreCheckStatus(finalStatus);
							reportEntity.setMigPreCheckGenTime(new Date());

						} else if (usecaseName.contains("Rollback_RF_Scripts_Usecase")) {
							reportEntity.setMigRollbackStatus(finalStatus);
							reportEntity.setMigRollbackGenTime(new Date());

						} else if (usecaseName.contains("Cutover_RF_Scripts_Usecase")) {
							reportEntity.setMigCutoverStatus(finalStatus);
							reportEntity.setMigCutoverGenTime(new Date());

						}
					}
				}
			}

			reportsRepository.createReports(reportEntity);
		} catch (Exception e) {
			logger.error("Exception in insertRunTestReportDetails() " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public void insertPostMigAuditReportDetails(String enbId, Integer programId, String programName, String user,
			String ciqFileName, Map<String, String> scriptDetails, String finalStatus, String migSubtype) {
		Integer customerId = 0;
		try {
			ReportsEntity reportEntity = reportsRepository.getEntityData(programName, enbId);
			String enbName = null;
			String market = null;
			String dbcollectionFileName = null;
			if (reportEntity == null) {
				reportEntity = new ReportsEntity();
				dbcollectionFileName = CommonUtil.createMongoDbFileName(String.valueOf(programId), ciqFileName);
				List<CIQDetailsModel> listCIQDetailsModel = fileUploadRepository.getEnbTableDetails(ciqFileName, enbId,
						"", dbcollectionFileName);
				if (CommonUtil.isValidObject(listCIQDetailsModel) && !listCIQDetailsModel.isEmpty()) {
					if (programName.contains("5G-MM")) {
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("GNB AU Name")) {
							enbName = listCIQDetailsModel.get(0).getCiqMap().get("GNB AU Name").getHeaderValue();
						}
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("Market")) {
							market = listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue();
						}
					} else if(programName.contains("4G-USM-LIVE")) {
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("eNB_Name")) {
							enbName = listCIQDetailsModel.get(0).getCiqMap().get("eNB_Name").getHeaderValue();
						}
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("Market")) {
							market = listCIQDetailsModel.get(0).getCiqMap().get("Market").getHeaderValue();
						}
					} else if(programName.contains("4G-FSU")) {
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NE_Name")) {
							enbName = listCIQDetailsModel.get(0).getCiqMap().get("NE_Name").getHeaderValue();
						}
					} else if(programName.contains("5G-DSS")) {
						if (listCIQDetailsModel.get(0).getCiqMap().containsKey("NEName")) {
							enbName = listCIQDetailsModel.get(0).getCiqMap().get("NEName").getHeaderValue();
						}
					}
				}
				CustomerDetailsEntity customerDetailsEntity = getCustomerDetailsEntityById(programId);
				if(customerDetailsEntity != null) {
					customerId = customerDetailsEntity.getCustomerEntity().getId();
				}
				reportEntity.setEnbId(enbId);
				reportEntity.setNeName(enbName);
				reportEntity.setCiqName(ciqFileName);
				reportEntity.setMarket(market);
				reportEntity.setCustomerId(customerId);
				reportEntity.setProgrameName(programName);
				reportEntity.setStartDate(new Date());
			}
			
			if(reportEntity.getUserName()==null || reportEntity.getUserName().isEmpty()) {
				reportEntity.setUserName(user);
			} else {
				List<String> users = Arrays.asList(reportEntity.getUserName().split("\\s*,\\s*"));
				if(!users.contains(user)) {
					reportEntity.setUserName(reportEntity.getUserName() + "," + user);					
				}				
			}
			
			if(programName.contains("4G-USM-LIVE")) {
				if(migSubtype.equalsIgnoreCase("AUDIT")) {
					reportEntity.setPostMigAudiStatus(finalStatus);
					reportEntity.setPostMigAudiGenTime(new Date());;

				} else if(migSubtype.equalsIgnoreCase("RANATP")) {
					reportEntity.setPostMigAtpStatus(finalStatus);
					reportEntity.setPostMigAtpGenTime(new Date());;

				}
			} else {
				Set<String> keySet = scriptDetails.keySet();
				for (String scriptPath : keySet) {
					String scriptName = FilenameUtils.getBaseName(scriptPath);
					if (programName.contains("5G-MM")) {
						if (scriptName.contains("AUPF")) {
							reportEntity.setPostAupfStatus(finalStatus);
							reportEntity.setPostAupfGenTime(new Date());

						} else if (scriptName.contains("AU")) {
							reportEntity.setPostAuAuditStatus(finalStatus);
							reportEntity.setPostAuAuditGenTime(new Date());

						} else if (scriptName.contains("ACPF")) {
							reportEntity.setPostAcpfAuditStatus(finalStatus);
							reportEntity.setPostAcpfAuditGenTime(new Date());

						} else if (scriptName.contains("eNB")) {
							reportEntity.setPostEndcAuditStatus(finalStatus);
							reportEntity.setPostEndcAuditGenTime(new Date());

						}
					} else if(programName.contains("5G-DSS")) {
						if (scriptName.contains("DSS_AUPF")) {
							reportEntity.setPostAupfStatus(finalStatus);
							reportEntity.setPostAupfGenTime(new Date());
							
						} else if (scriptName.contains("DSS_eNB-endc")) {
							reportEntity.setPostMigEnbAudiStatus(finalStatus);
							reportEntity.setPostMigEnbAudiGenTime(new Date());

						} else if (scriptName.contains("DSS_ACPF")) {
							reportEntity.setPostAcpfAuditStatus(finalStatus);
							reportEntity.setPostAcpfAuditGenTime(new Date());

						} else if (scriptName.contains("DSS_FSU")) {
							reportEntity.setPostMigFsuAuditStatus(finalStatus);
							reportEntity.setPostMigFsuAuditGenTime(new Date());

						} else if (scriptName.contains("DSS_vDU")) {
							reportEntity.setPostMigVduStatus(finalStatus);
							reportEntity.setPostMigVduGenTime(new Date());

						}
					}
				}
			}
			
			
			reportsRepository.createReports(reportEntity);
		} catch (Exception e) {
			logger.error("Exception in insertPostMigAuditReportDetails() " + ExceptionUtils.getFullStackTrace(e));
		}
	}
	
	private CustomerDetailsEntity getCustomerDetailsEntityById(int programId) {
		CustomerDetailsEntity customerDetailsEntity = null;
		try {
			customerDetailsEntity = reportsRepository.getCustomerDetailsEntityById(programId);
		} catch (Exception e) {
			logger.error("Exception in getCustomerDetailsEntityById() " + ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntity;
	}

}
