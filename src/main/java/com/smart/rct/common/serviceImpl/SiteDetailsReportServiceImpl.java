package com.smart.rct.common.serviceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.SiteReportOVEntity;
import com.smart.rct.common.models.AuditSummaryModel;
import com.smart.rct.common.models.CategoryDetailsModel;
import com.smart.rct.common.models.CriticalCheckDetails;
import com.smart.rct.common.models.SiteCarriers;
import com.smart.rct.common.models.SiteCompletionModel;
import com.smart.rct.common.models.TimeLineDetailsModel;
import com.smart.rct.common.models.TrackerDetailsModel;
import com.smart.rct.common.models.TroubleshootTimelineDetailsModel;
import com.smart.rct.common.repository.SiteDetailsReportRepository;
import com.smart.rct.common.service.SiteDetailsReportService;
import com.smart.rct.constants.Constants;
import com.smart.rct.postmigration.dto.SiteDataDto;
import com.smart.rct.postmigration.entity.PartialSaveSiteReportEntity;
import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.postmigration.service.SiteDataService;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.serviceImpl.PreMigrationToOV;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class SiteDetailsReportServiceImpl implements SiteDetailsReportService {
	final static Logger logger = LoggerFactory.getLogger(SiteDetailsReportServiceImpl.class);

	/**
	 * This api will createSiteReportExcelDetails
	 * 
	 * @return boolean
	 */
	@Autowired
	SiteDataService siteDataService;

	@Autowired
	SiteDetailsReportRepository siteDetailsReportRepository;
	
	@Autowired
	SiteDataDto siteDataDto;
	@Autowired
	SiteReportToOV siteReportToOV;

	@Override
	public boolean createExcelSiteReportDetails(SiteCompletionModel siteCompletionModel, String fileNamePath) {
		// TODO Auto-generated method stub
		boolean status = false;

		if (!ObjectUtils.isEmpty(siteCompletionModel)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				List<Integer> listData=new ArrayList<>();
				Sheet sheet = workbook.createSheet("SiteReport");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
				headerCellStyle.setBorderBottom(BorderStyle.THIN);
				headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderLeft(BorderStyle.THIN);
				headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderRight(BorderStyle.THIN);
				headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderTop(BorderStyle.THIN);
				headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setAlignment((CellStyle.ALIGN_CENTER));

				Font sectorFont = workbook.createFont();
				sectorFont.setBold(true);
				sectorFont.setFontHeightInPoints((short) 12);
				sectorFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle sectorCellStyle = workbook.createCellStyle();
				sectorCellStyle.setFont(sectorFont);
				sectorCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
				sectorCellStyle.setBorderBottom(BorderStyle.THIN);
				sectorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderLeft(BorderStyle.THIN);
				sectorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderRight(BorderStyle.THIN);
				sectorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderTop(BorderStyle.THIN);
				sectorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				sectorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				Font mainHead = workbook.createFont();
				mainHead.setBold(true);
				mainHead.setFontHeightInPoints((short) 14);
				mainHead.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle mainHeaderCellStyle = workbook.createCellStyle();
				mainHeaderCellStyle.setFont(sectorFont);
				mainHeaderCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 208, 142)));
				mainHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
				mainHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
				mainHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderRight(BorderStyle.THIN);
				mainHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderTop(BorderStyle.THIN);
				mainHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				mainHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				
				
				AtomicInteger rowCountCount = new AtomicInteger();
				rowCountCount.getAndSet(1);// 1
				listData.add(rowCountCount.get());
				Row firstRow = sheet.createRow(rowCountCount.get());
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				Cell firstRowcell = firstRow.createCell(0);
				/*if (StringUtils.isNotEmpty(siteCompletionModel.getSiteReportStatus())) {
					siteCompletionModel.setSiteReportStatus(siteCompletionModel.getSiteReportStatus());
				} else {
					siteCompletionModel.setSiteReportStatus("");
				}*/
				/*firstRowcell.setCellValue("SITE " + siteCompletionModel.getSiteReportStatus() + " REPORT - "
						+ siteCompletionModel.getNeName());*/
				firstRowcell.setCellValue(siteCompletionModel.getProject()+ " - "+ siteCompletionModel.getMarket() +" - "
						+ siteCompletionModel.getNeName()+" - "+siteCompletionModel.getFinalIntegStatus());
				firstRowcell.setCellStyle(mainHeaderCellStyle);
				//CellUtil.setAlignment(firstRowcell, workbook, CellStyle.ALIGN_CENTER);
				rowCountCount.getAndIncrement();// 2
				listData.add(rowCountCount.get());
				Row headerRow = sheet.createRow(rowCountCount.get());
				// Create header cells
				String[] siteColumns = Constants.SITE_REPORT_COLUMN;
				for (int i = 0; i < siteColumns.length; i++) {
					Cell cell = headerRow.createCell(i);

					cell.setCellValue(siteColumns[i]);
					cell.setCellStyle(headerCellStyle);
				}
				rowCountCount.getAndIncrement();// 3
				Row generalRow = sheet.createRow(rowCountCount.get());
				listData.add(rowCountCount.get());
				Cell generalRowHeadercell = generalRow.createCell(0);
				generalRowHeadercell.setCellValue("General Information");
				generalRowHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));

				// rowCountCount.getAndIncrement();//4

				siteReportFiveGTemplate(siteCompletionModel, workbook, sheet, rowCountCount, sectorCellStyle);
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Time Line Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				String[] timeLineColumns = Constants.TIME_LINE_COLUMN;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTimeLineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeLineColumns.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeLineColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}

					XSSFCellStyle timeCellStyle = workbook.createCellStyle();
					/*headerCellStyle.setFont(headerFont);
					headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
					headerCellStyle.setBorderBottom(BorderStyle.THIN);
					headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderLeft(BorderStyle.THIN);
					headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderRight(BorderStyle.THIN);
					headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderTop(BorderStyle.THIN);
					headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
					// and solid fill pattern produces solid grey cell fill
					headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);*/
					timeCellStyle.setAlignment((CellStyle.ALIGN_CENTER));
					for (TimeLineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTimeLineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						cell1.setCellStyle(timeCellStyle);
						cell2.setCellStyle(timeCellStyle);
						/*CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);*/
					}
					
				}
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow1 = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell1 = secondHeaderRow1.createCell(0);
				secondHeadercell1.setCellValue("Time Duration");
				secondHeadercell1.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 2));
				String[] timeDuration = Constants.Time_Duration;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTroubleshootTimelineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeDuration.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeDuration[i]);
						cell.setCellStyle(headerCellStyle);
					}

					
					for (TroubleshootTimelineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTroubleshootTimelineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteTimeHr());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTimeMin());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						//cell1.setCellStyle(timeCellStyle);
						//cell2.setCellStyle(timeCellStyle);
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}

				}

				if (!ObjectUtils.isEmpty(siteCompletionModel.getCategoryDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row thirdHeaderRow = sheet.createRow(rowCountCount.get());
					Cell thirdHeadercell = thirdHeaderRow.createCell(0);
					thirdHeadercell.setCellValue("Other Issues Information");
					thirdHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					
						rowCountCount.getAndIncrement();
						listData.add(rowCountCount.get());
						Row categoryRow = sheet.createRow(rowCountCount.get());
						// Create cells
						String[] categoryColumns = Constants.CATEGORY__COLUMN;
						for (int i = 0; i < categoryColumns.length; i++) {
							Cell cell = categoryRow.createCell(i);

							cell.setCellValue(categoryColumns[i]);
							cell.setCellStyle(headerCellStyle);
						}
						//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						for (CategoryDetailsModel locCategoryDetailsModel : siteCompletionModel.getCategoryDetails()) {
							rowCountCount.getAndIncrement();
							Row rowTimeLine = sheet.createRow(rowCountCount.get());
							rowTimeLine.createCell(0).setCellValue(locCategoryDetailsModel.getCategory());
							rowTimeLine.createCell(1).setCellValue(locCategoryDetailsModel.getIssue());
							rowTimeLine.createCell(2).setCellValue(locCategoryDetailsModel.getTechnology());
							rowTimeLine.createCell(3).setCellValue(locCategoryDetailsModel.getAttribute());
							rowTimeLine.createCell(4).setCellValue(locCategoryDetailsModel.getResolved());
							rowTimeLine.createCell(5).setCellValue(locCategoryDetailsModel.getRemarks());
							//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						}

					}
				if (!(siteCompletionModel.getIsCancellationReport()).equalsIgnoreCase("yes")){
				if (!ObjectUtils.isEmpty(siteCompletionModel.getPostAuditIssues())) {

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row fourthHeaderRow = sheet.createRow(rowCountCount.get());
					Cell fourthHeadercell = fourthHeaderRow.createCell(0);
					fourthHeadercell.setCellValue("Post Audit Issues Information");
					fourthHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 4));

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row auditSummaryRow = sheet.createRow(rowCountCount.get());
					// Create cells
					String[] auditColumns = Constants.AUDIT_COLUMNS;
					for (int i = 0; i < auditColumns.length; i++) {
						Cell cell = auditSummaryRow.createCell(i);

						cell.setCellValue(auditColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
					// rowCountCount.get(), 1, 2));
					for (AuditSummaryModel locAuditSummaryModel : siteCompletionModel.getPostAuditIssues()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						//rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTestName());
						rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTest());
						rowTimeLine.createCell(1).setCellValue(locAuditSummaryModel.getYangCommand());
						rowTimeLine.createCell(2).setCellValue(locAuditSummaryModel.getAuditIssue());
						rowTimeLine.createCell(3).setCellValue(locAuditSummaryModel.getExpectedResult());
						rowTimeLine.createCell(4).setCellValue(locAuditSummaryModel.getActionItem());
						//rowTimeLine.createCell(6).setCellValue(locAuditSummaryModel.getRemarks());
						// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
						// rowCountCount.get(), 1, 2));
					}

				}
				}
				
				

				for (int i = 0; i < Constants.AUDIT_COLUMNS.length; i++) {
					sheet.autoSizeColumn(i);
				}
				//String cellAddr="$A$11:$A$17";
				for(int i=1;i<=rowCountCount.get()+1;i++)
				{   if(!listData.contains(i))
                {
					String cellAddr="A"+i+":"+"H"+i;
					setRegionBorderWithMedium(CellRangeAddress.valueOf(cellAddr), sheet);
                  }
				}
				
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
				File siteConfigDirectory = new File(fileNameBuilder.toString());
				if (!siteConfigDirectory.exists()) {
					siteConfigDirectory.mkdir();
				}
				/*
				 * fileNameBuilder.append(File.separator);
				 * fileNameBuilder.append(Constants.SITE_REPORT_XL);
				 */

				// Write the output to a file
				try (FileOutputStream fileOut = new FileOutputStream(fileNamePath)) {
					workbook.write(fileOut);
					status = true;
				} catch (Exception e) {
					status = false;
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}

			} catch (Exception e) {
				status = false;
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;
	}
	
	
	//*********************************Site Reoprt for DSS**********************************************************************//
	@Override
	public boolean createExcelSiteReportDetailsForDSS(SiteCompletionModel siteCompletionModel, String fileNamePath) {
		// TODO Auto-generated method stub
		boolean status = false;

		if (!ObjectUtils.isEmpty(siteCompletionModel)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				List<Integer> listData=new ArrayList<>();
				Sheet sheet = workbook.createSheet("SiteReport");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
				headerCellStyle.setBorderBottom(BorderStyle.THIN);
				headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderLeft(BorderStyle.THIN);
				headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderRight(BorderStyle.THIN);
				headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderTop(BorderStyle.THIN);
				headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setAlignment((CellStyle.ALIGN_CENTER));

				Font sectorFont = workbook.createFont();
				sectorFont.setBold(true);
				sectorFont.setFontHeightInPoints((short) 12);
				sectorFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle sectorCellStyle = workbook.createCellStyle();
				sectorCellStyle.setFont(sectorFont);
				sectorCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
				sectorCellStyle.setBorderBottom(BorderStyle.THIN);
				sectorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderLeft(BorderStyle.THIN);
				sectorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderRight(BorderStyle.THIN);
				sectorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderTop(BorderStyle.THIN);
				sectorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				sectorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				Font mainHead = workbook.createFont();
				mainHead.setBold(true);
				mainHead.setFontHeightInPoints((short) 14);
				mainHead.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle mainHeaderCellStyle = workbook.createCellStyle();
				mainHeaderCellStyle.setFont(sectorFont);
				mainHeaderCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 208, 142)));
				mainHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
				mainHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
				mainHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderRight(BorderStyle.THIN);
				mainHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderTop(BorderStyle.THIN);
				mainHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				mainHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				
				
				AtomicInteger rowCountCount = new AtomicInteger();
				rowCountCount.getAndSet(1);// 1
				listData.add(rowCountCount.get());
				Row firstRow = sheet.createRow(rowCountCount.get());
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				Cell firstRowcell = firstRow.createCell(0);
				/*if (StringUtils.isNotEmpty(siteCompletionModel.getSiteReportStatus())) {
					siteCompletionModel.setSiteReportStatus(siteCompletionModel.getSiteReportStatus());
				} else {
					siteCompletionModel.setSiteReportStatus("");
				}*/
				/*firstRowcell.setCellValue("SITE " + siteCompletionModel.getSiteReportStatus() + " REPORT - "
						+ siteCompletionModel.getNeName());*/
				firstRowcell.setCellValue(siteCompletionModel.getProject()+ " - "+ siteCompletionModel.getMarket() +" - "
						+ siteCompletionModel.getNeName()+" - "+siteCompletionModel.getFinalIntegStatus());
				firstRowcell.setCellStyle(mainHeaderCellStyle);
				//CellUtil.setAlignment(firstRowcell, workbook, CellStyle.ALIGN_CENTER);
				rowCountCount.getAndIncrement();// 2
				listData.add(rowCountCount.get());
				Row headerRow = sheet.createRow(rowCountCount.get());
				// Create header cells
				String[] siteColumns = Constants.SITE_REPORT_COLUMN;
				for (int i = 0; i < siteColumns.length; i++) {
					Cell cell = headerRow.createCell(i);

					cell.setCellValue(siteColumns[i]);
					cell.setCellStyle(headerCellStyle);
				}
				rowCountCount.getAndIncrement();// 3
				Row generalRow = sheet.createRow(rowCountCount.get());
				listData.add(rowCountCount.get());
				Cell generalRowHeadercell = generalRow.createCell(0);
				generalRowHeadercell.setCellValue("General Information");
				generalRowHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));

				// rowCountCount.getAndIncrement();//4

				 siteReportForDSSemplate(siteCompletionModel, workbook, sheet, rowCountCount, sectorCellStyle);
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Time Line Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				String[] timeLineColumns = Constants.TIME_LINE_COLUMN;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTimeLineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeLineColumns.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeLineColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}

					XSSFCellStyle timeCellStyle = workbook.createCellStyle();
					/*headerCellStyle.setFont(headerFont);
					headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
					headerCellStyle.setBorderBottom(BorderStyle.THIN);
					headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderLeft(BorderStyle.THIN);
					headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderRight(BorderStyle.THIN);
					headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderTop(BorderStyle.THIN);
					headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
					// and solid fill pattern produces solid grey cell fill
					headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);*/
					timeCellStyle.setAlignment((CellStyle.ALIGN_CENTER));
					for (TimeLineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTimeLineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						cell1.setCellStyle(timeCellStyle);
						cell2.setCellStyle(timeCellStyle);
						/*CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);*/
					}
					
				}
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow1 = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell1 = secondHeaderRow1.createCell(0);
				secondHeadercell1.setCellValue("Time Duration");
				secondHeadercell1.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 2));
				String[] timeDuration = Constants.Time_Duration;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTroubleshootTimelineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeDuration.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeDuration[i]);
						cell.setCellStyle(headerCellStyle);
					}

					
					for (TroubleshootTimelineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTroubleshootTimelineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteTimeHr());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTimeMin());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						//cell1.setCellStyle(timeCellStyle);
						//cell2.setCellStyle(timeCellStyle);
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}

				}

				if (!ObjectUtils.isEmpty(siteCompletionModel.getCategoryDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row thirdHeaderRow = sheet.createRow(rowCountCount.get());
					Cell thirdHeadercell = thirdHeaderRow.createCell(0);
					thirdHeadercell.setCellValue("Other Issues Information");
					thirdHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					
						rowCountCount.getAndIncrement();
						listData.add(rowCountCount.get());
						Row categoryRow = sheet.createRow(rowCountCount.get());
						// Create cells
						String[] categoryColumns = Constants.CATEGORY__COLUMN;
						for (int i = 0; i < categoryColumns.length; i++) {
							Cell cell = categoryRow.createCell(i);

							cell.setCellValue(categoryColumns[i]);
							cell.setCellStyle(headerCellStyle);
						}
						//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						for (CategoryDetailsModel locCategoryDetailsModel : siteCompletionModel.getCategoryDetails()) {
							rowCountCount.getAndIncrement();
							Row rowTimeLine = sheet.createRow(rowCountCount.get());
							rowTimeLine.createCell(0).setCellValue(locCategoryDetailsModel.getCategory());
							rowTimeLine.createCell(1).setCellValue(locCategoryDetailsModel.getIssue());
							rowTimeLine.createCell(2).setCellValue(locCategoryDetailsModel.getTechnology());
							rowTimeLine.createCell(3).setCellValue(locCategoryDetailsModel.getAttribute());
							rowTimeLine.createCell(4).setCellValue(locCategoryDetailsModel.getResolved());
							rowTimeLine.createCell(5).setCellValue(locCategoryDetailsModel.getRemarks());
							//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						}

					}
				if (!(siteCompletionModel.getIsCancellationReport()).equalsIgnoreCase("yes")){
				if (!ObjectUtils.isEmpty(siteCompletionModel.getPostAuditIssues())) {

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row fourthHeaderRow = sheet.createRow(rowCountCount.get());
					Cell fourthHeadercell = fourthHeaderRow.createCell(0);
					fourthHeadercell.setCellValue("Post Audit Issues Information");
					fourthHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 7));

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row auditSummaryRow = sheet.createRow(rowCountCount.get());
					// Create cells
					String[] auditColumns = Constants.AUDIT_COLUMNS;
					for (int i = 0; i < auditColumns.length; i++) {
						Cell cell = auditSummaryRow.createCell(i);

						cell.setCellValue(auditColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
					// rowCountCount.get(), 1, 2));
					for (AuditSummaryModel locAuditSummaryModel : siteCompletionModel.getPostAuditIssues()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						//rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTestName());
						rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTest());
						rowTimeLine.createCell(1).setCellValue(locAuditSummaryModel.getYangCommand());
						rowTimeLine.createCell(2).setCellValue(locAuditSummaryModel.getAuditIssue());
						System.out.println(locAuditSummaryModel.getAuditIssue());
						rowTimeLine.createCell(3).setCellValue(locAuditSummaryModel.getExpectedResult());
						rowTimeLine.createCell(4).setCellValue(locAuditSummaryModel.getActionItem());
						rowTimeLine.createCell(5).setCellValue(locAuditSummaryModel.getErrorCode());
						//rowTimeLine.createCell(7).setCellValue(locAuditSummaryModel.getRemarks());
						// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
						// rowCountCount.get(), 1, 2));
					}

				}
			}	
				

				for (int i = 0; i < Constants.AUDIT_COLUMNS.length; i++) {
					sheet.autoSizeColumn(i);
				}
				//String cellAddr="$A$11:$A$17";
				for(int i=1;i<=rowCountCount.get()+1;i++)
				{   if(!listData.contains(i))
                {
					String cellAddr="A"+i+":"+"H"+i;
					setRegionBorderWithMedium(CellRangeAddress.valueOf(cellAddr), sheet);
                  }
				}
				
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
				File siteConfigDirectory = new File(fileNameBuilder.toString());
				if (!siteConfigDirectory.exists()) {
					siteConfigDirectory.mkdir();
				}
				/*
				 * fileNameBuilder.append(File.separator);
				 * fileNameBuilder.append(Constants.SITE_REPORT_XL);
				 */

				// Write the output to a file
				try (FileOutputStream fileOut = new FileOutputStream(fileNamePath)) {
					workbook.write(fileOut);
					status = true;
				} catch (Exception e) {
					status = false;
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}

			} catch (Exception e) {
				status = false;
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;
	}
	public void siteReportForDSSemplate(SiteCompletionModel siteCompletionModel, Workbook workbook, Sheet sheet,
			AtomicInteger rowCountCount, CellStyle sectorCellStyle) {
		Font fontDetails = workbook.createFont();
		fontDetails.setBold(true);
		fontDetails.setFontHeightInPoints((short) 12);
		CellStyle columnCellStyle = workbook.createCellStyle();
		columnCellStyle.setFont(fontDetails);
		int remarksStart = 0;
		int remarksEnd = 0;
		for (int i = 4; i <= 23; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);

			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			if (4 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_DATE);
				cell1.setCellValue(siteCompletionModel.getReportDate());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (5 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VDU_NAME);
				cell1.setCellValue(siteCompletionModel.getNeName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (6 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_Name);
				cell1.setCellValue(siteCompletionModel.geteNodeBName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (7 == rowCountCount.get()) {
				cell0.setCellValue(Constants.siteReportStatus);
				cell1.setCellValue(siteCompletionModel.getSiteReportStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (8 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_PROJECT);
				cell1.setCellValue(siteCompletionModel.getProject());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (9 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_SW);
				cell1.setCellValue(siteCompletionModel.geteNodeBSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (10 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FSU_SW);
				cell1.setCellValue(siteCompletionModel.getFsuSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (11 == rowCountCount.get()) {
				cell0.setCellValue(Constants.vDU_SW );
				cell1.setCellValue(siteCompletionModel.getvDUSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (12 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_MARKET);
				cell1.setCellValue(siteCompletionModel.getMarket());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (13 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FUZE_PROJECT_ID);
				cell1.setCellValue(siteCompletionModel.getFuzeProjId());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (14 == rowCountCount.get()) {
				cell0.setCellValue(Constants.User);
				cell1.setCellValue(siteCompletionModel.getUserName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (15 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VENDOR_TYPE);
				cell1.setCellValue(siteCompletionModel.getVendorType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (16 == rowCountCount.get()) {
				cell0.setCellValue("Is all post audit issues resolved?");
				cell1.setCellValue(siteCompletionModel.getResAuditIssueCheck());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (17 == rowCountCount.get()) {
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Site Migration Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
			} else if (18 == rowCountCount.get()) {
				cell0.setCellValue(Constants.DSS_Commissioning_Complete);
				cell1.setCellValue(siteCompletionModel.getDssCommComp());
				cell2.setCellValue("");
				CellStyle wrapStyle = workbook.createCellStyle();
				  wrapStyle.setWrapText(true);
				  cell3.setCellStyle(wrapStyle);
				cell3.setCellValue(siteCompletionModel.getRemarks());
				CellUtil.setVerticalAlignment(cell3, VerticalAlignment.TOP);
				remarksStart = rowCountCount.get();

			} else if (19 == rowCountCount.get()) {
				cell0.setCellValue(Constants.DSS_Ops_ATP_Passing);
				cell1.setCellValue(siteCompletionModel.getDssOpsATP());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (20 == rowCountCount.get()) {
				cell0.setCellValue(Constants.TC_GC_Released);
				cell1.setCellValue(siteCompletionModel.getTcReleased());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (21 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Final_Integration_Status);
				cell1.setCellValue(siteCompletionModel.getFinalIntegStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (22 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Type_Effort);
				cell1.setCellValue(siteCompletionModel.getTypeOfEffort());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (23 == rowCountCount.get()) {
				cell0.setCellValue(Constants.OV_Ticket_Numbers);
				cell1.setCellValue(siteCompletionModel.getOvTicketNum());
				cell2.setCellValue("");
				cell3.setCellValue("");
				remarksEnd = rowCountCount.get();
			}

		}

		sheet.addMergedRegion(new CellRangeAddress(remarksStart, remarksEnd, 3, 3));
		for (CriticalCheckDetails locTimeLineDetailsModel : siteCompletionModel.getCheckDetails()) {
			rowCountCount.getAndIncrement();
			Font fontDetails1 = workbook.createFont();
			fontDetails1.setBold(true);
			fontDetails1.setFontHeightInPoints((short) 12);
			CellStyle columnCellStyle1 = workbook.createCellStyle();
			columnCellStyle1.setFont(fontDetails1);
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle1);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			//Cell cell3 = rowTimeLine.createCell(3);
			cell0.setCellValue(locTimeLineDetailsModel.getTitle());
			cell1.setCellValue(locTimeLineDetailsModel.getCheckPerformed());
			cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			System.out.println(locTimeLineDetailsModel.getRemarks());
			//cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_LEFT);
			CellUtil.setAlignment(cell3, workbook, CellStyle.ALIGN_LEFT);
			
		
			sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
		}

			
	}

	
	//********************************* End Site Reoprt for DSS**********************************************************************//
	@Override
	public boolean createExcelSiteReportDetailsForCBANAD(SiteCompletionModel siteCompletionModel, String fileNamePath) {
		// TODO Auto-generated method stub
		boolean status = false;

		if (!ObjectUtils.isEmpty(siteCompletionModel)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				List<Integer> listData=new ArrayList<>();
				Sheet sheet = workbook.createSheet("SiteReport");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
				headerCellStyle.setBorderBottom(BorderStyle.THIN);
				headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderLeft(BorderStyle.THIN);
				headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderRight(BorderStyle.THIN);
				headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderTop(BorderStyle.THIN);
				headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setAlignment((CellStyle.ALIGN_CENTER));

				Font sectorFont = workbook.createFont();
				sectorFont.setBold(true);
				sectorFont.setFontHeightInPoints((short) 12);
				sectorFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle sectorCellStyle = workbook.createCellStyle();
				sectorCellStyle.setFont(sectorFont);
				sectorCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
				sectorCellStyle.setBorderBottom(BorderStyle.THIN);
				sectorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderLeft(BorderStyle.THIN);
				sectorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderRight(BorderStyle.THIN);
				sectorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderTop(BorderStyle.THIN);
				sectorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				sectorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				Font mainHead = workbook.createFont();
				mainHead.setBold(true);
				mainHead.setFontHeightInPoints((short) 14);
				mainHead.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle mainHeaderCellStyle = workbook.createCellStyle();
				mainHeaderCellStyle.setFont(sectorFont);
				mainHeaderCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 208, 142)));
				mainHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
				mainHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
				mainHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderRight(BorderStyle.THIN);
				mainHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderTop(BorderStyle.THIN);
				mainHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				mainHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				
				
				AtomicInteger rowCountCount = new AtomicInteger();
				rowCountCount.getAndSet(1);// 1
				listData.add(rowCountCount.get());
				Row firstRow = sheet.createRow(rowCountCount.get());
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				Cell firstRowcell = firstRow.createCell(0);
				/*if (StringUtils.isNotEmpty(siteCompletionModel.getSiteReportStatus())) {
					siteCompletionModel.setSiteReportStatus(siteCompletionModel.getSiteReportStatus());
				} else {
					siteCompletionModel.setSiteReportStatus("");
				}*/
				/*firstRowcell.setCellValue("SITE " + siteCompletionModel.getSiteReportStatus() + " REPORT - "
						+ siteCompletionModel.getNeName());*/
				firstRowcell.setCellValue("#" + siteCompletionModel.getProject()+ " - #"+ siteCompletionModel.getMarket() +" - #"
						+ siteCompletionModel.getNeName()+" - "+siteCompletionModel.getFinalCBANDIntegStatus());
				firstRowcell.setCellStyle(mainHeaderCellStyle);
				//CellUtil.setAlignment(firstRowcell, workbook, CellStyle.ALIGN_CENTER);
				rowCountCount.getAndIncrement();// 2
				listData.add(rowCountCount.get());
				Row headerRow = sheet.createRow(rowCountCount.get());
				// Create header cells
				String[] siteColumns = Constants.SITE_REPORT_COLUMN;
				for (int i = 0; i < siteColumns.length; i++) {
					Cell cell = headerRow.createCell(i);

					cell.setCellValue(siteColumns[i]);
					cell.setCellStyle(headerCellStyle);
				}
				rowCountCount.getAndIncrement();// 3
				Row generalRow = sheet.createRow(rowCountCount.get());
				listData.add(rowCountCount.get());
				Cell generalRowHeadercell = generalRow.createCell(0);
				generalRowHeadercell.setCellValue("General Information");
				generalRowHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));

				// rowCountCount.getAndIncrement();//4

				siteReportForCBANDemplate(siteCompletionModel, workbook, sheet, rowCountCount, sectorCellStyle);
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Time Line Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				String[] timeLineColumns = Constants.TIME_LINE_COLUMN;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTimeLineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeLineColumns.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeLineColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}

					XSSFCellStyle timeCellStyle = workbook.createCellStyle();
					/*headerCellStyle.setFont(headerFont);
					headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
					headerCellStyle.setBorderBottom(BorderStyle.THIN);
					headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderLeft(BorderStyle.THIN);
					headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderRight(BorderStyle.THIN);
					headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderTop(BorderStyle.THIN);
					headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
					// and solid fill pattern produces solid grey cell fill
					headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);*/
					timeCellStyle.setAlignment((CellStyle.ALIGN_CENTER));
					for (TimeLineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTimeLineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						cell1.setCellStyle(timeCellStyle);
						cell2.setCellStyle(timeCellStyle);
						/*CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);*/
					}
					for (TroubleshootTimelineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTroubleshootTimelineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						cell1.setCellStyle(timeCellStyle);
						cell2.setCellStyle(timeCellStyle);
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}

				}
				if (!ObjectUtils.isEmpty(siteCompletionModel.getCategoryDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row thirdHeaderRow = sheet.createRow(rowCountCount.get());
					Cell thirdHeadercell = thirdHeaderRow.createCell(0);
					thirdHeadercell.setCellValue("Other Issues Information");
					thirdHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					
						rowCountCount.getAndIncrement();
						listData.add(rowCountCount.get());
						Row categoryRow = sheet.createRow(rowCountCount.get());
						// Create cells
						String[] categoryColumns = Constants.CATEGORY__COLUMN;
						for (int i = 0; i < categoryColumns.length; i++) {
							Cell cell = categoryRow.createCell(i);

							cell.setCellValue(categoryColumns[i]);
							cell.setCellStyle(headerCellStyle);
						}
						//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						for (CategoryDetailsModel locCategoryDetailsModel : siteCompletionModel.getCategoryDetails()) {
							rowCountCount.getAndIncrement();
							Row rowTimeLine = sheet.createRow(rowCountCount.get());
							rowTimeLine.createCell(0).setCellValue(locCategoryDetailsModel.getCategory());
							rowTimeLine.createCell(1).setCellValue(locCategoryDetailsModel.getIssue());
							rowTimeLine.createCell(2).setCellValue(locCategoryDetailsModel.getTechnology());
							rowTimeLine.createCell(3).setCellValue(locCategoryDetailsModel.getAttribute());
							rowTimeLine.createCell(4).setCellValue(locCategoryDetailsModel.getResolved());
							rowTimeLine.createCell(5).setCellValue(locCategoryDetailsModel.getRemarks());
							//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						}

					}
				if (!(siteCompletionModel.getIsCancellationReport()).equalsIgnoreCase("yes")){
				if (!ObjectUtils.isEmpty(siteCompletionModel.getPostAuditIssues())) {

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row fourthHeaderRow = sheet.createRow(rowCountCount.get());
					Cell fourthHeadercell = fourthHeaderRow.createCell(0);
					fourthHeadercell.setCellValue("Post Audit Issues Information");
					fourthHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row auditSummaryRow = sheet.createRow(rowCountCount.get());
					// Create cells
					String[] auditColumns = Constants.AUDIT_COLUMNS;
					for (int i = 0; i < auditColumns.length; i++) {
						Cell cell = auditSummaryRow.createCell(i);

						cell.setCellValue(auditColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
					// rowCountCount.get(), 1, 2));
					for (AuditSummaryModel locAuditSummaryModel : siteCompletionModel.getPostAuditIssues()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						//rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTestName());
						rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTest());
						rowTimeLine.createCell(1).setCellValue(locAuditSummaryModel.getYangCommand());
						rowTimeLine.createCell(2).setCellValue(locAuditSummaryModel.getAuditIssue());
						System.out.println(locAuditSummaryModel.getAuditIssue());
						rowTimeLine.createCell(3).setCellValue(locAuditSummaryModel.getExpectedResult());
						rowTimeLine.createCell(4).setCellValue(locAuditSummaryModel.getActionItem());
						rowTimeLine.createCell(5).setCellValue(locAuditSummaryModel.getErrorCode());
						//rowTimeLine.createCell(7).setCellValue(locAuditSummaryModel.getRemarks());
						
						// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
						// rowCountCount.get(), 1, 2));
					}

				}
				
				}

				for (int i = 0; i < Constants.AUDIT_COLUMNS.length; i++) {
					sheet.autoSizeColumn(i);
				}
				//String cellAddr="$A$11:$A$17";
				for(int i=1;i<=rowCountCount.get()+1;i++)
				{   if(!listData.contains(i))
                {
					String cellAddr="A"+i+":"+"H"+i;
					setRegionBorderWithMedium(CellRangeAddress.valueOf(cellAddr), sheet);
                  }
				}
				
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
				File siteConfigDirectory = new File(fileNameBuilder.toString());
				if (!siteConfigDirectory.exists()) {
					siteConfigDirectory.mkdir();
				}
				/*
				 * fileNameBuilder.append(File.separator);
				 * fileNameBuilder.append(Constants.SITE_REPORT_XL);
				 */

				// Write the output to a file
				try (FileOutputStream fileOut = new FileOutputStream(fileNamePath)) {
					workbook.write(fileOut);
					status = true;
				} catch (Exception e) {
					status = false;
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}

			} catch (Exception e) {
				status = false;
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;
	}
	public void siteReportForCBANDemplate(SiteCompletionModel siteCompletionModel, Workbook workbook, Sheet sheet,
			AtomicInteger rowCountCount, CellStyle sectorCellStyle) {
		Font fontDetails = workbook.createFont();
		fontDetails.setBold(true);
		fontDetails.setFontHeightInPoints((short) 12);
		CellStyle columnCellStyle = workbook.createCellStyle();
		columnCellStyle.setFont(fontDetails);
		int remarksStart = 0;
		int remarksEnd = 0;
		for (int i = 4; i <= 20; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);

			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			if (4 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_DATE);
				cell1.setCellValue(siteCompletionModel.getReportDate());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (5 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VDU_NAME);
				cell1.setCellValue(siteCompletionModel.getNeName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (6 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_Name);
				cell1.setCellValue(siteCompletionModel.geteNodeBName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} 
			else if (7 == rowCountCount.get()) {
				cell0.setCellValue(Constants.siteReportStatus);
				cell1.setCellValue(siteCompletionModel.getSiteReportStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (8 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_PROJECT);
				cell1.setCellValue(siteCompletionModel.getProject());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (9 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_SW);
				cell1.setCellValue(siteCompletionModel.geteNodeBSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (10 == rowCountCount.get()) {
				cell0.setCellValue(Constants.vDU_SW );
				cell1.setCellValue(siteCompletionModel.getvDUSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (11 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_MARKET);
				cell1.setCellValue(siteCompletionModel.getMarket());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (12 == rowCountCount.get()) {
				cell0.setCellValue(Constants.User);
				cell1.setCellValue(siteCompletionModel.getUserName());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (13 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VENDOR_TYPE);
				cell1.setCellValue(siteCompletionModel.getVendorType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (14 == rowCountCount.get()) {
				cell0.setCellValue("Is all post audit issues resolved?");
				cell1.setCellValue(siteCompletionModel.getResAuditIssueCheck());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (15 == rowCountCount.get()) {
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Site Migration Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
			} else if (16 == rowCountCount.get()) {
				cell0.setCellValue(Constants.C_Band_Integration_Status);
				cell1.setCellValue(siteCompletionModel.getCurrCBANDIntegStatus());
				cell2.setCellValue("");
				CellStyle wrapStyle = workbook.createCellStyle();
				  wrapStyle.setWrapText(true);
				  cell3.setCellStyle(wrapStyle);
				cell3.setCellValue(siteCompletionModel.getRemarks());
				CellUtil.setVerticalAlignment(cell3, VerticalAlignment.TOP);
				remarksStart = rowCountCount.get();

			}else if (17 == rowCountCount.get()) {
				cell0.setCellValue(Constants.TC_GC_Released);
				cell1.setCellValue(siteCompletionModel.getTcReleased());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} 
			else if (18 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FINAL_C_BAND_INTEGRATION_STATUS);
				cell1.setCellValue(siteCompletionModel.getFinalCBANDIntegStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (19 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Type_Effort);
				cell1.setCellValue(siteCompletionModel.getTypeOfEffort());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (20 == rowCountCount.get()) {
				cell0.setCellValue(Constants.OV_Ticket_Numbers);
				cell1.setCellValue(siteCompletionModel.getOvTicketNum());
				cell2.setCellValue("");
				cell3.setCellValue("");
				remarksEnd = rowCountCount.get();
			}
		}

		sheet.addMergedRegion(new CellRangeAddress(remarksStart, remarksEnd, 3, 3));
		for (CriticalCheckDetails locTimeLineDetailsModel : siteCompletionModel.getCheckDetails()) {
			rowCountCount.getAndIncrement();
			Font fontDetails1 = workbook.createFont();
			fontDetails1.setBold(true);
			fontDetails1.setFontHeightInPoints((short) 12);
			CellStyle columnCellStyle1 = workbook.createCellStyle();
			columnCellStyle1.setFont(fontDetails1);
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle1);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			//Cell cell3 = rowTimeLine.createCell(3);
			cell0.setCellValue(locTimeLineDetailsModel.getTitle());
			cell1.setCellValue(locTimeLineDetailsModel.getCheckPerformed());
			cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			System.out.println(locTimeLineDetailsModel.getRemarks());
			//cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_LEFT);
			CellUtil.setAlignment(cell3, workbook, CellStyle.ALIGN_LEFT);
			
		
			sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
		}

			
	}

	//*********************************End Site Report for CBAand*******************************************************************//
	
	public boolean createExcelSiteReportDetailsForFSU(SiteCompletionModel siteCompletionModel, String fileNamePath) {
		// TODO Auto-generated method stub
		boolean status = false;

		if (!ObjectUtils.isEmpty(siteCompletionModel)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				List<Integer> listData=new ArrayList<>();
				Sheet sheet = workbook.createSheet("SiteReport");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
				headerCellStyle.setBorderBottom(BorderStyle.THIN);
				headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderLeft(BorderStyle.THIN);
				headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderRight(BorderStyle.THIN);
				headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderTop(BorderStyle.THIN);
				headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setAlignment((CellStyle.ALIGN_CENTER));

				Font sectorFont = workbook.createFont();
				sectorFont.setBold(true);
				sectorFont.setFontHeightInPoints((short) 12);
				sectorFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle sectorCellStyle = workbook.createCellStyle();
				sectorCellStyle.setFont(sectorFont);
				sectorCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
				sectorCellStyle.setBorderBottom(BorderStyle.THIN);
				sectorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderLeft(BorderStyle.THIN);
				sectorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderRight(BorderStyle.THIN);
				sectorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderTop(BorderStyle.THIN);
				sectorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				sectorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				Font mainHead = workbook.createFont();
				mainHead.setBold(true);
				mainHead.setFontHeightInPoints((short) 14);
				mainHead.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle mainHeaderCellStyle = workbook.createCellStyle();
				mainHeaderCellStyle.setFont(sectorFont);
				mainHeaderCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 208, 142)));
				mainHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
				mainHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
				mainHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderRight(BorderStyle.THIN);
				mainHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderTop(BorderStyle.THIN);
				mainHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				mainHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				
				
				AtomicInteger rowCountCount = new AtomicInteger();
				rowCountCount.getAndSet(1);// 1
				listData.add(rowCountCount.get());
				Row firstRow = sheet.createRow(rowCountCount.get());
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				Cell firstRowcell = firstRow.createCell(0);
				/*if (StringUtils.isNotEmpty(siteCompletionModel.getSiteReportStatus())) {
					siteCompletionModel.setSiteReportStatus(siteCompletionModel.getSiteReportStatus());
				} else {
					siteCompletionModel.setSiteReportStatus("");
				}*/
				/*firstRowcell.setCellValue("SITE " + siteCompletionModel.getSiteReportStatus() + " REPORT - "
						+ siteCompletionModel.getNeName());*/
				firstRowcell.setCellValue(siteCompletionModel.getProject()+ " - "+ siteCompletionModel.getMarket() +" - "
						+ siteCompletionModel.getNeName()+" - "+siteCompletionModel.getFinalIntegStatus());
				firstRowcell.setCellStyle(mainHeaderCellStyle);
				//CellUtil.setAlignment(firstRowcell, workbook, CellStyle.ALIGN_CENTER);
				rowCountCount.getAndIncrement();// 2
				listData.add(rowCountCount.get());
				Row headerRow = sheet.createRow(rowCountCount.get());
				// Create header cells
				String[] siteColumns = Constants.SITE_REPORT_COLUMN;
				for (int i = 0; i < siteColumns.length; i++) {
					Cell cell = headerRow.createCell(i);

					cell.setCellValue(siteColumns[i]);
					cell.setCellStyle(headerCellStyle);
				}
				rowCountCount.getAndIncrement();// 3
				Row generalRow = sheet.createRow(rowCountCount.get());
				listData.add(rowCountCount.get());
				Cell generalRowHeadercell = generalRow.createCell(0);
				generalRowHeadercell.setCellValue("General Information");
				generalRowHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));

				// rowCountCount.getAndIncrement();//4

				siteReportForFSUtemplate(siteCompletionModel, workbook, sheet, rowCountCount, sectorCellStyle);
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Time Line Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				String[] timeLineColumns = Constants.TIME_LINE_COLUMN;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTimeLineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeLineColumns.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeLineColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}

					XSSFCellStyle timeCellStyle = workbook.createCellStyle();
					/*headerCellStyle.setFont(headerFont);
					headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
					headerCellStyle.setBorderBottom(BorderStyle.THIN);
					headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderLeft(BorderStyle.THIN);
					headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderRight(BorderStyle.THIN);
					headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
					headerCellStyle.setBorderTop(BorderStyle.THIN);
					headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
					// and solid fill pattern produces solid grey cell fill
					headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);*/
					timeCellStyle.setAlignment((CellStyle.ALIGN_CENTER));
					for (TimeLineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTimeLineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						cell1.setCellStyle(timeCellStyle);
						cell2.setCellStyle(timeCellStyle);
						/*CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);*/
					}
					
				}
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow1 = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell1 = secondHeaderRow1.createCell(0);
				secondHeadercell1.setCellValue("Time Duration");
				secondHeadercell1.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 2));
				String[] timeDuration = Constants.Time_Duration;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTroubleshootTimelineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeDuration.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeDuration[i]);
						cell.setCellStyle(headerCellStyle);
					}

					
					for (TroubleshootTimelineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTroubleshootTimelineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteTimeHr());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTimeMin());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						//cell1.setCellStyle(timeCellStyle);
						//cell2.setCellStyle(timeCellStyle);
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}

				}

				if (!ObjectUtils.isEmpty(siteCompletionModel.getCategoryDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row thirdHeaderRow = sheet.createRow(rowCountCount.get());
					Cell thirdHeadercell = thirdHeaderRow.createCell(0);
					thirdHeadercell.setCellValue("Other Issues Information");
					thirdHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					
						rowCountCount.getAndIncrement();
						listData.add(rowCountCount.get());
						Row categoryRow = sheet.createRow(rowCountCount.get());
						// Create cells
						String[] categoryColumns = Constants.CATEGORY__COLUMN;
						for (int i = 0; i < categoryColumns.length; i++) {
							Cell cell = categoryRow.createCell(i);

							cell.setCellValue(categoryColumns[i]);
							cell.setCellStyle(headerCellStyle);
						}
						//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						for (CategoryDetailsModel locCategoryDetailsModel : siteCompletionModel.getCategoryDetails()) {
							rowCountCount.getAndIncrement();
							Row rowTimeLine = sheet.createRow(rowCountCount.get());
							rowTimeLine.createCell(0).setCellValue(locCategoryDetailsModel.getCategory());
							rowTimeLine.createCell(1).setCellValue(locCategoryDetailsModel.getIssue());
							rowTimeLine.createCell(2).setCellValue(locCategoryDetailsModel.getTechnology());
							rowTimeLine.createCell(3).setCellValue(locCategoryDetailsModel.getAttribute());
							rowTimeLine.createCell(4).setCellValue(locCategoryDetailsModel.getResolved());
							rowTimeLine.createCell(5).setCellValue(locCategoryDetailsModel.getRemarks());
							//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						}

					}
				if (!(siteCompletionModel.getIsCancellationReport()).equalsIgnoreCase("yes")){
				if (!ObjectUtils.isEmpty(siteCompletionModel.getPostAuditIssues())) {

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row fourthHeaderRow = sheet.createRow(rowCountCount.get());
					Cell fourthHeadercell = fourthHeaderRow.createCell(0);
					fourthHeadercell.setCellValue("Post Audit Issues Information");
					fourthHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row auditSummaryRow = sheet.createRow(rowCountCount.get());
					// Create cells
					String[] auditColumns = Constants.AUDIT_COLUMNS;
					for (int i = 0; i < auditColumns.length; i++) {
						Cell cell = auditSummaryRow.createCell(i);

						cell.setCellValue(auditColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
					// rowCountCount.get(), 1, 2));
					for (AuditSummaryModel locAuditSummaryModel : siteCompletionModel.getPostAuditIssues()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						//rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTestName());
						rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTest());
						rowTimeLine.createCell(1).setCellValue(locAuditSummaryModel.getYangCommand());
						rowTimeLine.createCell(2).setCellValue(locAuditSummaryModel.getAuditIssue());
						System.out.println(locAuditSummaryModel.getAuditIssue());
						rowTimeLine.createCell(3).setCellValue(locAuditSummaryModel.getExpectedResult());
						rowTimeLine.createCell(4).setCellValue(locAuditSummaryModel.getActionItem());
						rowTimeLine.createCell(5).setCellValue(locAuditSummaryModel.getErrorCode());
					//	rowTimeLine.createCell(7).setCellValue(locAuditSummaryModel.getRemarks());
						
						// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
						// rowCountCount.get(), 1, 2));
					}

				}
				}
				

				for (int i = 0; i < Constants.AUDIT_COLUMNS.length; i++) {
					sheet.autoSizeColumn(i);
				}
				//String cellAddr="$A$11:$A$17";
				for(int i=1;i<=rowCountCount.get()+1;i++)
				{   if(!listData.contains(i))
                {
					String cellAddr="A"+i+":"+"H"+i;
					setRegionBorderWithMedium(CellRangeAddress.valueOf(cellAddr), sheet);
                  }
				}
				
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
				File siteConfigDirectory = new File(fileNameBuilder.toString());
				if (!siteConfigDirectory.exists()) {
					siteConfigDirectory.mkdir();
				}
				/*
				 * fileNameBuilder.append(File.separator);
				 * fileNameBuilder.append(Constants.SITE_REPORT_XL);
				 */

				// Write the output to a file
				try (FileOutputStream fileOut = new FileOutputStream(fileNamePath)) {
					workbook.write(fileOut);
					status = true;
				} catch (Exception e) {
					status = false;
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}

			} catch (Exception e) {
				status = false;
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;
	}

	public void siteReportForFSUtemplate(SiteCompletionModel siteCompletionModel, Workbook workbook, Sheet sheet,
			AtomicInteger rowCountCount, CellStyle sectorCellStyle) {
		Font fontDetails = workbook.createFont();
		fontDetails.setBold(true);
		fontDetails.setFontHeightInPoints((short) 12);
		CellStyle columnCellStyle = workbook.createCellStyle();
		columnCellStyle.setFont(fontDetails);
		int remarksStart = 0;
		int remarksEnd = 0;
		for (int i = 4; i <= 20; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);

			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			if (4 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_DATE);
				cell1.setCellValue(siteCompletionModel.getReportDate());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (5 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VDU_NAME);
				cell1.setCellValue(siteCompletionModel.getNeName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (6 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_Name);
				cell1.setCellValue(siteCompletionModel.geteNodeBName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (7 == rowCountCount.get()) {
				cell0.setCellValue(Constants.siteReportStatus);
				cell1.setCellValue(siteCompletionModel.getSiteReportStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} 
			else if (8 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_PROJECT);
				cell1.setCellValue(siteCompletionModel.getProject());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (9 == rowCountCount.get()) {
				cell0.setCellValue(Constants.eNodeB_SW);
				cell1.setCellValue(siteCompletionModel.geteNodeBSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (10 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FSU_SW);
				cell1.setCellValue(siteCompletionModel.geteNodeBSW());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (11 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_MARKET);
				cell1.setCellValue(siteCompletionModel.getMarket());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (12 == rowCountCount.get()) {
				cell0.setCellValue(Constants.User);
				cell1.setCellValue(siteCompletionModel.getUserName());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (13 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VENDOR_TYPE);
				cell1.setCellValue(siteCompletionModel.getVendorType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (14 == rowCountCount.get()) {
				cell0.setCellValue("Is all post audit issues resolved?");
				cell1.setCellValue(siteCompletionModel.getResAuditIssueCheck());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (15 == rowCountCount.get()) {
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Site Migration Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
			} else if (16 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FSU_Integrated_in_Bypass_Mode);
				cell1.setCellValue(siteCompletionModel.getFsuIntegBypass());
				cell2.setCellValue("");
				CellStyle wrapStyle = workbook.createCellStyle();
				  wrapStyle.setWrapText(true);
				  cell3.setCellStyle(wrapStyle);
				cell3.setCellValue(siteCompletionModel.getRemarks());
				CellUtil.setVerticalAlignment(cell3, VerticalAlignment.TOP);
				remarksStart = rowCountCount.get();

			} /*else if (15 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FSU_Integrated_in_Multiplex_Mode);
				cell1.setCellValue(siteCompletionModel.getFsuIntegMultiplex());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}*/
			else if (17 == rowCountCount.get()) {
				cell0.setCellValue(Constants.TC_GC_Released);
				cell1.setCellValue(siteCompletionModel.getTcReleased());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (18 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Final_Integration_Status);
				cell1.setCellValue(siteCompletionModel.getFinalIntegStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (19 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Type_Effort);
				cell1.setCellValue(siteCompletionModel.getTypeOfEffort());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (20 == rowCountCount.get()) {
				cell0.setCellValue(Constants.OV_Ticket_Numbers);
				cell1.setCellValue(siteCompletionModel.getOvTicketNum());
				cell2.setCellValue("");
				cell3.setCellValue("");
				remarksEnd = rowCountCount.get();
			}

		}

		sheet.addMergedRegion(new CellRangeAddress(remarksStart, remarksEnd, 3, 3));
		for (CriticalCheckDetails locTimeLineDetailsModel : siteCompletionModel.getCheckDetails()) {
			rowCountCount.getAndIncrement();
			Font fontDetails1 = workbook.createFont();
			fontDetails1.setBold(true);
			fontDetails1.setFontHeightInPoints((short) 12);
			CellStyle columnCellStyle1 = workbook.createCellStyle();
			columnCellStyle1.setFont(fontDetails1);
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle1);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			//Cell cell3 = rowTimeLine.createCell(3);
			cell0.setCellValue(locTimeLineDetailsModel.getTitle());
			cell1.setCellValue(locTimeLineDetailsModel.getCheckPerformed());
			cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			System.out.println(locTimeLineDetailsModel.getRemarks());
			//cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_LEFT);
			CellUtil.setAlignment(cell3, workbook, CellStyle.ALIGN_LEFT);
			
		
			sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
		}

			
	}


	
	private void setRegionBorderWithMedium(CellRangeAddress region, Sheet sheet) {
        Workbook wb = sheet.getWorkbook();
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region, sheet, wb);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region, sheet, wb);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region, sheet, wb);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region, sheet, wb);
    }
	public void siteReportFiveGTemplate(SiteCompletionModel siteCompletionModel, Workbook workbook, Sheet sheet,
			AtomicInteger rowCountCount, CellStyle sectorCellStyle) {
		Font fontDetails = workbook.createFont();
		fontDetails.setBold(true);
		fontDetails.setFontHeightInPoints((short) 12);
		CellStyle columnCellStyle = workbook.createCellStyle();
		columnCellStyle.setFont(fontDetails);
		int remarksStart = 0;
		int remarksEnd = 0;
		for (int i = 4; i <= 22; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);

			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			if (4 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_DATE);
				cell1.setCellValue(siteCompletionModel.getReportDate());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (5 == rowCountCount.get()) {
				cell0.setCellValue(Constants.SITE_NAME);
				cell1.setCellValue(siteCompletionModel.getNeName());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (6 == rowCountCount.get()) {
				cell0.setCellValue(Constants.AU_ID);
				cell1.setCellValue(siteCompletionModel.getNeId());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (7 == rowCountCount.get()) {
				cell0.setCellValue(Constants.siteReportStatus);
				cell1.setCellValue(siteCompletionModel.getSiteReportStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (8 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_PROJECT);
				cell1.setCellValue(siteCompletionModel.getProject());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (9 == rowCountCount.get()) {
				cell0.setCellValue(Constants.SOFTWARE_RELEASE);
				cell1.setCellValue(siteCompletionModel.getSoftWareRelease());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (10 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_MARKET);
				cell1.setCellValue(siteCompletionModel.getMarket());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (11 == rowCountCount.get()) {
				cell0.setCellValue(Constants.INTEGRATION_TYPE);
				cell1.setCellValue(siteCompletionModel.getIntegrationType());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (12 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FUZE_PROJECT_ID);
				cell1.setCellValue(siteCompletionModel.getFuzeProjId());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (13 == rowCountCount.get()) {
				cell0.setCellValue(Constants.User);
				cell1.setCellValue(siteCompletionModel.getUserName());
				cell2.setCellValue("");
				cell3.setCellValue("");
				//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (14 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VENDOR_TYPE);
				cell1.setCellValue(siteCompletionModel.getVendorType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (15 == rowCountCount.get()) {
				cell0.setCellValue("Is all post audit issues resolved?");
				cell1.setCellValue(siteCompletionModel.getResAuditIssueCheck());
				cell2.setCellValue("");
				cell3.setCellValue("");
			} else if (16 == rowCountCount.get()) {
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Site Migration Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
			} else if (17 == rowCountCount.get()) {
				cell0.setCellValue(Constants.mmW_Commissioning_Complete);
				cell1.setCellValue(siteCompletionModel.getMmCommComp());
				cell2.setCellValue("");
				CellStyle wrapStyle = workbook.createCellStyle();
				  wrapStyle.setWrapText(true);
				  cell3.setCellStyle(wrapStyle);
				cell3.setCellValue(siteCompletionModel.getRemarks());
				CellUtil.setVerticalAlignment(cell3, VerticalAlignment.TOP);
				remarksStart = rowCountCount.get();

			} else if (18 == rowCountCount.get()) {
				cell0.setCellValue(Constants.mmW_Ops_ATP_Passing);
				cell1.setCellValue(siteCompletionModel.getMmOpsATP());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}
			else if (19 == rowCountCount.get()) {
				cell0.setCellValue(Constants.TC_GC_Released);
				cell1.setCellValue(siteCompletionModel.getTcReleased());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (20 == rowCountCount.get()) {
				cell0.setCellValue(Constants.Final_Integration_Status);
				cell1.setCellValue(siteCompletionModel.getFinalIntegStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
			}else if (21== i) {
				cell0.setCellValue(Constants.TYPE_OF_EFFORT);
				cell1.setCellValue(siteCompletionModel.getTypeOfEffort());
				cell2.setCellValue("");
				cell3.setCellValue("");
				//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (22 == rowCountCount.get()) {
				cell0.setCellValue(Constants.OV_Ticket_Numbers);
				cell1.setCellValue(siteCompletionModel.getOvTicketNum());
				cell2.setCellValue("");
				cell3.setCellValue("");
				remarksEnd = rowCountCount.get();
			}

		}

		sheet.addMergedRegion(new CellRangeAddress(remarksStart, remarksEnd, 3, 3));
		for (CriticalCheckDetails locTimeLineDetailsModel : siteCompletionModel.getCheckDetails()) {
			rowCountCount.getAndIncrement();
			Font fontDetails1 = workbook.createFont();
			fontDetails1.setBold(true);
			fontDetails1.setFontHeightInPoints((short) 12);
			CellStyle columnCellStyle1 = workbook.createCellStyle();
			columnCellStyle1.setFont(fontDetails1);
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle1);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			//Cell cell3 = rowTimeLine.createCell(3);
			cell0.setCellValue(locTimeLineDetailsModel.getTitle());
			cell1.setCellValue(locTimeLineDetailsModel.getCheckPerformed());
			cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			System.out.println(locTimeLineDetailsModel.getRemarks());
			//cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_LEFT);
			CellUtil.setAlignment(cell3, workbook, CellStyle.ALIGN_LEFT);
			
		
			sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
		}
			
	}

	
	
	
	/**
	 * This api will createSiteReportExcelDetails
	 * 
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean createExcelSiteReportUsmLiveDetails(SiteCompletionModel siteCompletionModel, String fileNamePath) {
		// TODO Auto-generated method stub
		boolean status = false;

		if (!ObjectUtils.isEmpty(siteCompletionModel)) {
			try (XSSFWorkbook workbook = new XSSFWorkbook()) {
				List<Integer> listData=new ArrayList<>();
				Sheet sheet = workbook.createSheet("SiteReport");
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.WHITE.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(91, 155, 214)));
				headerCellStyle.setBorderBottom(BorderStyle.THIN);
				headerCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderLeft(BorderStyle.THIN);
				headerCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderRight(BorderStyle.THIN);
				headerCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				headerCellStyle.setBorderTop(BorderStyle.THIN);
				headerCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setAlignment((CellStyle.ALIGN_CENTER));

				Font sectorFont = workbook.createFont();
				sectorFont.setBold(true);
				sectorFont.setFontHeightInPoints((short) 12);
				sectorFont.setColor(IndexedColors.BLACK.getIndex());
				CellStyle sectorCellStyle = workbook.createCellStyle();
				sectorCellStyle.setFont(sectorFont);
				sectorCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
				sectorCellStyle.setBorderBottom(BorderStyle.THIN);
				sectorCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderLeft(BorderStyle.THIN);
				sectorCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderRight(BorderStyle.THIN);
				sectorCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				sectorCellStyle.setBorderTop(BorderStyle.THIN);
				sectorCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				sectorCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				
				XSSFCellStyle  contentCellStyle = workbook.createCellStyle();
				contentCellStyle.setWrapText(true);
				contentCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
				
				
				Font mainHead = workbook.createFont();
				mainHead.setBold(true);
				mainHead.setFontHeightInPoints((short) 14);
				mainHead.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle mainHeaderCellStyle = workbook.createCellStyle();
				mainHeaderCellStyle.setFont(sectorFont);
				mainHeaderCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(169, 208, 142)));
				mainHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
				mainHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
				mainHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderRight(BorderStyle.THIN);
				mainHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
				mainHeaderCellStyle.setBorderTop(BorderStyle.THIN);
				mainHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
				// and solid fill pattern produces solid grey cell fill
				mainHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				

				AtomicInteger rowCountCount = new AtomicInteger();
				rowCountCount.getAndSet(1);// 1
				listData.add(rowCountCount.get());
				Row firstRow = sheet.createRow(rowCountCount.get());
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				Cell firstRowcell = firstRow.createCell(0);
				/*if (StringUtils.isNotEmpty(siteCompletionModel.getSiteReportStatus())) {
					siteCompletionModel.setSiteReportStatus(siteCompletionModel.getSiteReportStatus().toUpperCase());
				} else {
					siteCompletionModel.setSiteReportStatus("");
				}*/
				/*firstRowcell.setCellValue("SITE " + siteCompletionModel.getSiteReportStatus() + " REPORT - "
						+ siteCompletionModel.getNeName());*/
				firstRowcell.setCellValue(siteCompletionModel.getProject()+ " - "+ siteCompletionModel.getMarket() +" - "
						+ siteCompletionModel.getNeName()+" - "+siteCompletionModel.getFinalIntegStatus());
				firstRowcell.setCellStyle(mainHeaderCellStyle);
				//CellUtil.setAlignment(firstRowcell, workbook, CellStyle.ALIGN_CENTER);
				rowCountCount.getAndIncrement();// 2
				listData.add(rowCountCount.get());
				Row headerRow = sheet.createRow(rowCountCount.get());
				// Create header cells
				String[] siteColumns = Constants.SITE_REPORT_COLUMN_USM;
				for (int i = 0; i < siteColumns.length; i++) {
					Cell cell = headerRow.createCell(i);

					cell.setCellValue(siteColumns[i]);
					cell.setCellStyle(headerCellStyle);
				}
				rowCountCount.getAndIncrement();// 3
				listData.add(rowCountCount.get());
				Row generalRow = sheet.createRow(rowCountCount.get());
				Cell generalRowHeadercell = generalRow.createCell(0);
				generalRowHeadercell.setCellValue("General Information");
				generalRowHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));

				// rowCountCount.getAndIncrement();//4

				siteReportUsmLiveFourGTemplate(siteCompletionModel, workbook, sheet, rowCountCount, headerCellStyle,
						sectorCellStyle,listData);

				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Time Line Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
				String[] timeLineColumns = Constants.TIME_LINE_COLUMN;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTimeLineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeLineColumns.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeLineColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}

					for (TimeLineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTimeLineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteDate());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTime());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}
					
				}
				rowCountCount.getAndIncrement();
				listData.add(rowCountCount.get());
				Row secondHeaderRow1 = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell1 = secondHeaderRow1.createCell(0);
				secondHeadercell1.setCellValue("Time Duration");
				secondHeadercell1.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 2));
				String[] timeDuration = Constants.Time_Duration;
				if (!ObjectUtils.isEmpty(siteCompletionModel.getTroubleshootTimelineDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row timeLineRow = sheet.createRow(rowCountCount.get());
					// Create cells

					for (int i = 0; i < timeDuration.length; i++) {
						Cell cell = timeLineRow.createCell(i);

						cell.setCellValue(timeDuration[i]);
						cell.setCellStyle(headerCellStyle);
					}

					
					for (TroubleshootTimelineDetailsModel locTimeLineDetailsModel : siteCompletionModel.getTroubleshootTimelineDetails()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						cell0.setCellValue(locTimeLineDetailsModel.getTimeLine());
						cell1.setCellValue(locTimeLineDetailsModel.getSiteTimeHr());
						cell2.setCellValue(locTimeLineDetailsModel.getSiteTimeMin());
						cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
						//cell1.setCellStyle(timeCellStyle);
						//cell2.setCellStyle(timeCellStyle);
						CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);
						CellUtil.setAlignment(cell2, workbook, CellStyle.ALIGN_CENTER);
					}

				}

				if (!ObjectUtils.isEmpty(siteCompletionModel.getCategoryDetails())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row thirdHeaderRow = sheet.createRow(rowCountCount.get());
					Cell thirdHeadercell = thirdHeaderRow.createCell(0);
					thirdHeadercell.setCellValue("Other Issues Information");
					thirdHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));
					
						rowCountCount.getAndIncrement();
						listData.add(rowCountCount.get());
						Row categoryRow = sheet.createRow(rowCountCount.get());
						// Create cells
						String[] categoryColumns = Constants.CATEGORY__COLUMN;
						for (int i = 0; i < categoryColumns.length; i++) {
							Cell cell = categoryRow.createCell(i);

							cell.setCellValue(categoryColumns[i]);
							cell.setCellStyle(headerCellStyle);
						}
						//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						for (CategoryDetailsModel locCategoryDetailsModel : siteCompletionModel.getCategoryDetails()) {
							rowCountCount.getAndIncrement();
							Row rowTimeLine = sheet.createRow(rowCountCount.get());
							rowTimeLine.createCell(0).setCellValue(locCategoryDetailsModel.getCategory());
							rowTimeLine.createCell(1).setCellValue(locCategoryDetailsModel.getIssue());
							rowTimeLine.createCell(2).setCellValue(locCategoryDetailsModel.getTechnology());
							rowTimeLine.createCell(3).setCellValue(locCategoryDetailsModel.getAttribute());
							rowTimeLine.createCell(4).setCellValue(locCategoryDetailsModel.getResolved());
							rowTimeLine.createCell(5).setCellValue(locCategoryDetailsModel.getRemarks());
							//sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
						}

					}

				if (!(siteCompletionModel.getIsCancellationReport()).equalsIgnoreCase("yes")){
				if (!ObjectUtils.isEmpty(siteCompletionModel.getPostAuditIssues())) {
					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row fourthHeaderRow = sheet.createRow(rowCountCount.get());
					Cell fourthHeadercell = fourthHeaderRow.createCell(0);
					fourthHeadercell.setCellValue("Post Audit Issues Information");
					fourthHeadercell.setCellStyle(sectorCellStyle);
					sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 5));

					rowCountCount.getAndIncrement();
					listData.add(rowCountCount.get());
					Row auditSummaryRow = sheet.createRow(rowCountCount.get());
					// Create cells
					String[] auditColumns = Constants.AUDIT_COLUMNS;
					for (int i = 0; i < auditColumns.length; i++) {
						Cell cell = auditSummaryRow.createCell(i);

						cell.setCellValue(auditColumns[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
					// rowCountCount.get(), 1, 2));
					for (AuditSummaryModel locAuditSummaryModel : siteCompletionModel.getPostAuditIssues()) {
						rowCountCount.getAndIncrement();
						Row rowTimeLine = sheet.createRow(rowCountCount.get());
						Cell cell0 = rowTimeLine.createCell(0);
						Cell cell1 = rowTimeLine.createCell(1);
						Cell cell2 = rowTimeLine.createCell(2);
						Cell cell3 = rowTimeLine.createCell(3);
						Cell cell4 = rowTimeLine.createCell(4);
						Cell cell5 = rowTimeLine.createCell(5);
						//Cell cell6 = rowTimeLine.createCell(6);
						//Cell cell7 = rowTimeLine.createCell(7);
						
						
						//cell0.setCellValue(locAuditSummaryModel.getTestName());
						cell0.setCellValue(locAuditSummaryModel.getTest());
						cell1.setCellValue(locAuditSummaryModel.getYangCommand());
						cell2.setCellValue(locAuditSummaryModel.getAuditIssue());
						cell3.setCellValue(locAuditSummaryModel.getExpectedResult());
						cell4.setCellValue(locAuditSummaryModel.getActionItem());
						cell5.setCellValue(locAuditSummaryModel.getErrorCode());
						//cell7.setCellValue(locAuditSummaryModel.getRemarks());
						
                         cell0.setCellStyle(contentCellStyle);
						cell1.setCellStyle(contentCellStyle);
						cell2.setCellStyle(contentCellStyle);
						cell3.setCellStyle(contentCellStyle);
						cell4.setCellStyle(contentCellStyle);
						cell5.setCellStyle(contentCellStyle);
						//cell6.setCellStyle(contentCellStyle);
						//cell7.setCellStyle(contentCellStyle);
						/*rowTimeLine.createCell(0).setCellValue(locAuditSummaryModel.getTestName());
						rowTimeLine.createCell(1).setCellValue(locAuditSummaryModel.getTest());
						rowTimeLine.createCell(2).setCellValue(locAuditSummaryModel.getYangCommand());
						rowTimeLine.createCell(3).setCellValue(locAuditSummaryModel.getAuditIssue());
						rowTimeLine.createCell(4).setCellValue(locAuditSummaryModel.getExpectedResult());
						rowTimeLine.createCell(5).setCellValue(locAuditSummaryModel.getActionItem());
						rowTimeLine.createCell(6).setCellValue(locAuditSummaryModel.getRemarks());*/
						// sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(),
						// rowCountCount.get(), 1, 2));
					}

				}
								
				}
				for (int i = 0; i < Constants.AUDIT_COLUMNS.length; i++) {
					sheet.autoSizeColumn(i);
				}
				for(int i=1;i<=rowCountCount.get()+1;i++)
				{   if(!listData.contains(i))
                {
					String cellAddr="A"+i+":"+"H"+i;
					setRegionBorderWithMedium(CellRangeAddress.valueOf(cellAddr), sheet);
                  }
				}
				StringBuilder fileNameBuilder = new StringBuilder();
				fileNameBuilder.append(LoadPropertyFiles.getInstance().getProperty(Constants.BASE_PATH))
						// .append(LoadPropertyFiles.getInstance().getProperty(Constants.SMART))
						.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS));
				File siteConfigDirectory = new File(fileNameBuilder.toString());
				if (!siteConfigDirectory.exists()) {
					siteConfigDirectory.mkdir();
				}
				/*
				 * fileNameBuilder.append(File.separator);
				 * fileNameBuilder.append(Constants.SITE_REPORT_XL);
				 */

				// Write the output to a file
				try (FileOutputStream fileOut = new FileOutputStream(fileNamePath)) {
					workbook.write(fileOut);
					status = true;
				} catch (Exception e) {
					status = false;
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}

			} catch (Exception e) {
				status = false;
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		return status;
	}

	public void siteReportUsmLiveFourGTemplate(SiteCompletionModel siteCompletionModel, Workbook workbook, Sheet sheet,
			AtomicInteger rowCountCount, CellStyle headerCellStyle, CellStyle sectorCellStyle,List<Integer> listData) {
		Font fontDetails = workbook.createFont();
		fontDetails.setBold(true);
		fontDetails.setFontHeightInPoints((short) 12);
		CellStyle columnCellStyle = workbook.createCellStyle();
		columnCellStyle.setFont(fontDetails);
		int remarksStart = 0;
		for (int i = 4; i <= 17; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			if (4 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_DATE);
				cell1.setCellValue(siteCompletionModel.getReportDate());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (5 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_NE_NAME);
				cell1.setCellValue(siteCompletionModel.getNeName());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (6 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_NEID);
				cell1.setCellValue(siteCompletionModel.getNeId());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (7 == rowCountCount.get()) {
				cell0.setCellValue(Constants.siteReportStatus);
				cell1.setCellValue(siteCompletionModel.getSiteReportStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (8 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_PROJECT);
				cell1.setCellValue(siteCompletionModel.getProject());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (9 == rowCountCount.get()) {
				cell0.setCellValue(Constants.SOFTWARE_RELEASE);
				cell1.setCellValue(siteCompletionModel.getSoftWareRelease());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (10 == rowCountCount.get()) {
				cell0.setCellValue(Constants.REPORT_MARKET);
				cell1.setCellValue(siteCompletionModel.getMarket());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (11 == rowCountCount.get()) {
				cell0.setCellValue(Constants.INTEGRATION_TYPE);
				cell1.setCellValue(siteCompletionModel.getIntegrationType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (12 == rowCountCount.get()) {
				cell0.setCellValue(Constants.FUZE_PROJECT_ID);
				cell1.setCellValue(siteCompletionModel.getFuzeProjId());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (13 == rowCountCount.get()) {
				cell0.setCellValue(Constants.User);
				cell1.setCellValue(siteCompletionModel.getUserName());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (14 == rowCountCount.get()) {
				cell0.setCellValue(Constants.VENDOR_TYPE);
				cell1.setCellValue(siteCompletionModel.getVendorType());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (15 == rowCountCount.get()) {
				cell0.setCellValue("Is all post audit issues resolved?");
				cell1.setCellValue(siteCompletionModel.getResAuditIssueCheck());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			} else if (16 == rowCountCount.get()) {
				Row secondHeaderRow = sheet.createRow(rowCountCount.get());
				Cell secondHeadercell = secondHeaderRow.createCell(0);
				secondHeadercell.setCellValue("Site Migration Information");
				secondHeadercell.setCellStyle(sectorCellStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 0, 3));
			} else if (17 == rowCountCount.get()) {
				listData.add(rowCountCount.get());
				cell0.setCellValue(Constants.CARRIERS);
				cell1.setCellValue(Constants.LIKE_FOR_LIKE);
				cell2.setCellValue(Constants.INCREMENTAL);
				cell3.setCellValue(Constants.SITE_REMARKS);
				cell1.setCellStyle(headerCellStyle);
				cell2.setCellStyle(headerCellStyle);
				cell3.setCellStyle(headerCellStyle);
				CellStyle wrapStyle = workbook.createCellStyle();
				  wrapStyle.setWrapText(true);
				  cell3.setCellStyle(wrapStyle);
				CellUtil.setVerticalAlignment(cell0, VerticalAlignment.CENTER);
				char tickMark = '\u2713';
				remarksStart = rowCountCount.get() + 1;
				boolean status=false;
				for (SiteCarriers siteCarriers : siteCompletionModel.getCarriers()) {
					rowCountCount.getAndIncrement();
					Row rowTimeLineCarriers = sheet.createRow(rowCountCount.get());
					Cell cellCarrier0 = rowTimeLineCarriers.createCell(0);
					cellCarrier0.setCellStyle(columnCellStyle);
					Cell cellCarrier1 = rowTimeLineCarriers.createCell(1);
					Cell cellCarrier2 = rowTimeLineCarriers.createCell(2);
					Cell cellCarrier3 = rowTimeLineCarriers.createCell(3);
					  cellCarrier3.setCellStyle(wrapStyle);
					CellUtil.setAlignment(cellCarrier0, workbook, CellStyle.ALIGN_RIGHT);
					CellUtil.setAlignment(cellCarrier1, workbook, CellStyle.ALIGN_CENTER);
					CellUtil.setAlignment(cellCarrier2, workbook, CellStyle.ALIGN_CENTER);
					//CellUtil.setAlignment(cellCarrier3, workbook, CellStyle.ALIGN_CENTER);

					cellCarrier0.setCellValue("");
					if ("yes".equalsIgnoreCase(siteCarriers.getLikeforlikeCheckBox())) {
						cellCarrier1.setCellValue(
								siteCarriers.getLikeforlike() + "-" + siteCarriers.getLikeforlikeCheckBox());
					} else {
						cellCarrier1.setCellValue(
								siteCarriers.getLikeforlike() + "-" + siteCarriers.getLikeforlikeCheckBox());
					}

					if ("yes".equalsIgnoreCase(siteCarriers.getIncrementalCheckBox())) {
						cellCarrier2.setCellValue(
								siteCarriers.getIncremental() + "-" + siteCarriers.getIncrementalCheckBox());
					} else {
						cellCarrier2.setCellValue(
								siteCarriers.getIncremental() + "-" + siteCarriers.getIncrementalCheckBox());
					}
					// cellCarrier2.setCellValue(siteCarriers.getIncremental());
					if(!status) {
					cellCarrier3.setCellValue(siteCompletionModel.getRemarks());
					CellUtil.setVerticalAlignment(cellCarrier3, VerticalAlignment.TOP);
					status=true;
					}
				}

				sheet.addMergedRegion(new CellRangeAddress(17, rowCountCount.get(), 0, 0));

			}

		}

		int remarksEnd = 0;
		for (int i = 1; i <= 11; i++) {
			rowCountCount.getAndIncrement();
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);

			/*CellUtil.setAlignment(cell0, workbook, CellStyle.ALIGN_RIGHT);
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_CENTER);*/

			 if (1 == i) {
				cell0.setCellValue(Constants.LTE_Commissioning_Complete);
				cell1.setCellValue(siteCompletionModel.getLteCommComp());
				cell2.setCellValue("");
				cell3.setCellValue("");
				CellUtil.setVerticalAlignment(cell3, VerticalAlignment.TOP);
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (2 == i) {
				cell0.setCellValue(Constants.LTE_Ops_ATP_Passing);
				cell1.setCellValue(siteCompletionModel.getLteOpsATP());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (3 == i) {
				cell0.setCellValue(Constants.CBRS_Commissioning_Complete);
				cell1.setCellValue(siteCompletionModel.getLteCBRSCommComp());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (4 == i) {
				cell0.setCellValue(Constants.CBRS_Ops_ATP_Passing);
				cell1.setCellValue(siteCompletionModel.getLteCBRSOpsAtp());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}
			else if (5 == i) {
				cell0.setCellValue(Constants.LAA_Commissioning_Complete);
				cell1.setCellValue(siteCompletionModel.getLteLAACommComp());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (6 == i) {
				cell0.setCellValue(Constants.LAA_Ops_ATP_Passing);
				cell1.setCellValue(siteCompletionModel.getLteLAAOpsATP());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (7 == i) {
				cell0.setCellValue(Constants.FSU_Integrated_in_Bypass_Mode);
				cell1.setCellValue(siteCompletionModel.getFsuIntegBypass());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (8 == i) {
				cell0.setCellValue(Constants.TC_GC_Released);
				cell1.setCellValue(siteCompletionModel.getTcReleased());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (9 == i) {
				cell0.setCellValue(Constants.Final_Integration_Status);
				cell1.setCellValue(siteCompletionModel.getFinalIntegStatus());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (10 == i) {
				cell0.setCellValue(Constants.TYPE_OF_EFFORT);
				cell1.setCellValue(siteCompletionModel.getTypeOfEffort());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
			}else if (11== i) {
				cell0.setCellValue(Constants.OV_Ticket_Numbers);
				cell1.setCellValue(siteCompletionModel.getOvTicketNum());
				cell2.setCellValue("");
				cell3.setCellValue("");
				sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
				remarksEnd = rowCountCount.get();
			}

		}
		sheet.addMergedRegion(new CellRangeAddress(remarksStart, remarksEnd, 3, 3));
		
		for (CriticalCheckDetails locTimeLineDetailsModel : siteCompletionModel.getCheckDetails()) {
			rowCountCount.getAndIncrement();
			Font fontDetails1 = workbook.createFont();
			fontDetails1.setBold(true);
			fontDetails1.setFontHeightInPoints((short) 12);
			CellStyle columnCellStyle1 = workbook.createCellStyle();
			columnCellStyle1.setFont(fontDetails1);
			Row rowTimeLine = sheet.createRow(rowCountCount.get());
			Cell cell0 = rowTimeLine.createCell(0);
			cell0.setCellStyle(columnCellStyle1);
			Cell cell1 = rowTimeLine.createCell(1);
			Cell cell2 = rowTimeLine.createCell(2);
			Cell cell3 = rowTimeLine.createCell(3);
			CellStyle wrapStyle = workbook.createCellStyle();
			  wrapStyle.setWrapText(true);
			  cell3.setCellStyle(wrapStyle);
			//Cell cell3 = rowTimeLine.createCell(3);
			cell0.setCellValue(locTimeLineDetailsModel.getTitle());
			cell1.setCellValue(locTimeLineDetailsModel.getCheckPerformed());
			cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			System.out.println(locTimeLineDetailsModel.getRemarks());
			//cell3.setCellValue(locTimeLineDetailsModel.getRemarks());
			CellUtil.setAlignment(cell1, workbook, CellStyle.ALIGN_LEFT);
			CellUtil.setAlignment(cell3, workbook, CellStyle.ALIGN_LEFT);
			
		
			sheet.addMergedRegion(new CellRangeAddress(rowCountCount.get(), rowCountCount.get(), 1, 2));
		}

		
		/*
		 * sheet.addMergedRegion(new CellRangeAddress(mergeCountFirst,
		 * rowCountCount.get(), 1, 2));
		 */
	}

	@Override
	public SiteDataEntity saveSiteDetails(SiteCompletionModel siteCompletionUsmLiveModel, JSONObject siteDetails,
			String excelpath, String fileName) {
		boolean status = false;
		SiteDataEntity statusEntity =null;
		try {
			String jsonText = siteDetails.toJSONString();
			String sessionId = siteDetails.get("sessionId").toString();
			/*if (siteDetails.containsKey("siteId") && !ObjectUtils.isEmpty(siteDetails.get("siteId"))) {
				Integer siteDataId = (Integer) siteDetails.get("siteDataId");
				SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDetailsById(siteDataId);
				siteDataEntity.setFilePath(excelpath);
				siteDataEntity.setNeName(siteCompletionUsmLiveModel.getNeName());
				siteDataEntity.setRemarks("");
				siteDataEntity.setFileName(fileName);
				siteDataEntity.setPackedDate(new Date());
				siteDataEntity.setPackedBy("");
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(Integer.valueOf(siteDetails.get("programId").toString()));
				siteDataEntity.setProgramDetailsEntity(programDetailsEntity);
				siteDataEntity.setSiteReportJson(jsonText);
				siteDataEntity.setSiteReportStatus(siteCompletionUsmLiveModel.getSiteReportStatus());
				siteDataEntity.setNeId(siteCompletionUsmLiveModel.getNeId());
				siteDataEntity.setReportType(Constants.REPORT_TYPE_SITE);
				String ciqName = siteDetails.get("ciqName").toString();
				siteDataEntity.setCiqFileName(ciqName);
				status = siteDataService.saveSiteDataAudit(siteDataEntity);
			} else {

				SiteDataEntity siteDataEntity = new SiteDataEntity();
				siteDataEntity.setFilePath(excelpath);
				siteDataEntity.setNeName(siteCompletionUsmLiveModel.getNeName());
				siteDataEntity.setRemarks("");
				siteDataEntity.setFileName(fileName);
				siteDataEntity.setPackedDate(new Date());
				siteDataEntity.setPackedBy("");
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(Integer.valueOf(siteDetails.get("programId").toString()));
				siteDataEntity.setProgramDetailsEntity(programDetailsEntity);
				siteDataEntity.setSiteReportJson(jsonText);
				siteDataEntity.setSiteReportStatus(siteCompletionUsmLiveModel.getSiteReportStatus());
				siteDataEntity.setNeId(siteCompletionUsmLiveModel.getNeId());
				siteDataEntity.setReportType(Constants.REPORT_TYPE_SITE);
				String ciqName = siteDetails.get("ciqName").toString();
				siteDataEntity.setCiqFileName(ciqName);
				status = siteDataService.saveSiteDataAudit(siteDataEntity);
			}*/
			StringBuilder filePath=new StringBuilder();
			filePath.append(File.separator)
			.append(LoadPropertyFiles.getInstance().getProperty(Constants.NETWORK_CONFIG_DETAILS))
			.append(File.separator);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			SiteDataEntity siteDataEntity = new SiteDataEntity();
			siteDataEntity.setFilePath(filePath.toString());
			siteDataEntity.setNeName(siteCompletionUsmLiveModel.getNeName());
			siteDataEntity.setRemarks("");
			siteDataEntity.setFileName(fileName);
			siteDataEntity.setPackedDate(new Date());
			siteDataEntity.setPackedBy(user.getUserName());
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(Integer.valueOf(siteDetails.get("programId").toString()));
			siteDataEntity.setProgramDetailsEntity(programDetailsEntity);
			siteDataEntity.setSiteReportJson(jsonText);
			siteDataEntity.setSiteReportStatus(siteCompletionUsmLiveModel.getSiteReportStatus());
			siteDataEntity.setNeId(siteCompletionUsmLiveModel.getNeId());
			siteDataEntity.setReportType(Constants.REPORT_TYPE_SITE);
			String ciqName ="";
			if(siteDetails.containsKey("ciqName") && !ObjectUtils.isEmpty(siteDetails.get("ciqName")))
			{
				 ciqName = siteDetails.get("ciqName").toString();
			}
			//ciqName="CTX-VZ_CIQ_Ver_0.0.07_20201214 (1).xlsx";
			siteDataEntity.setCiqFileName(ciqName);
			 statusEntity = siteDetailsReportRepository.saveSiteDataEntity(siteDataEntity);
		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return statusEntity;
	}

	
	
	
	@Override
	public PartialSaveSiteReportEntity savePartialSiteDetails(SiteCompletionModel siteCompletionUsmLiveModel, JSONObject siteDetails,String neidfiveG) {
		boolean status = false;
		PartialSaveSiteReportEntity statusEntity =null;
		try {
			String jsonText = siteDetails.toJSONString();
			String sessionId = siteDetails.get("sessionId").toString();
					
		
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			PartialSaveSiteReportEntity partialSaveSiteReportEntity = new PartialSaveSiteReportEntity();
			partialSaveSiteReportEntity.setNeName(siteCompletionUsmLiveModel.getNeName());
			partialSaveSiteReportEntity.setRemarks("");
			partialSaveSiteReportEntity.setPackedDate(new Date());
			partialSaveSiteReportEntity.setPackedBy(user.getUserName());
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(Integer.valueOf(siteDetails.get("programId").toString()));
			partialSaveSiteReportEntity.setProgramDetailsEntity(programDetailsEntity);
			partialSaveSiteReportEntity.setSiteReportJson(jsonText);
			partialSaveSiteReportEntity.setSiteReportStatus(siteCompletionUsmLiveModel.getSiteReportStatus());
			if(!neidfiveG.isEmpty()) {
				partialSaveSiteReportEntity.setNeId(neidfiveG);
			}else {
			partialSaveSiteReportEntity.setNeId(siteCompletionUsmLiveModel.getNeId());
			}
			partialSaveSiteReportEntity.setReportType(Constants.REPORT_TYPE_SITE);
			String ciqName ="";
			if(siteDetails.containsKey("ciqName") && !ObjectUtils.isEmpty(siteDetails.get("ciqName")))
			{
				 ciqName = siteDetails.get("ciqName").toString();
			}
			//ciqName="CTX-VZ_CIQ_Ver_0.0.07_20201214 (1).xlsx";
			partialSaveSiteReportEntity.setCiqFileName(ciqName);
			 statusEntity = siteDetailsReportRepository.savePartialSiteDataEntity(partialSaveSiteReportEntity);
		} catch (Exception e) {
			status = false;
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return statusEntity;
	}

	public JSONObject getSiteDetailsById(int siteDataId) {
		JSONObject newJObject = null;
		try {
			SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDetailsById(siteDataId);
			if (!ObjectUtils.isEmpty(siteDataEntity) && StringUtils.isNotEmpty(siteDataEntity.getSiteReportJson())) {
				JSONParser parser = new JSONParser();
				newJObject = (JSONObject) parser.parse(siteDataEntity.getSiteReportJson());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return newJObject;
	}
	public JSONObject getSiteDetailsForSavefile(String neId) {
		JSONObject newJObject = null;
		try {
			PartialSaveSiteReportEntity siteDataEntity = siteDetailsReportRepository.getSiteDetailsForSavefile(neId);
			//for(SiteDataEntity acg:siteDataEntity)
			if (!ObjectUtils.isEmpty(siteDataEntity) && StringUtils.isNotEmpty(siteDataEntity.getSiteReportJson())) {
				JSONParser parser = new JSONParser();
				newJObject = (JSONObject) parser.parse(siteDataEntity.getSiteReportJson());
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return newJObject;
	}
	@Override
	public List<SiteDataModel> getHistorySiteDetails(String neId) {
		List<SiteDataModel> siteDataModels = null;
		try {
			List<SiteDataEntity> siteDataEntities = siteDetailsReportRepository.getHistorySiteDetails(neId);
			if (!ObjectUtils.isEmpty(siteDataEntities)) {
				siteDataModels = new ArrayList<SiteDataModel>();
				for (SiteDataEntity siteDataEntity : siteDataEntities) {
					siteDataModels.add(siteDataDto.getSiteDataDetailsModel(siteDataEntity));
					System.out.println(siteDataModels);
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return siteDataModels;
	}
	//////////*******************************************************************************************************/////////////////////
	@Override
	public List<SiteDataModel> getDonldSiteDetails(int sourceProgramId,Date FromDate,Date ToDate ) {
		List<SiteDataModel> siteDataModels = null;
		try {
			List<SiteDataEntity> siteDataEntities = siteDetailsReportRepository.getDonldSiteDetails(sourceProgramId,FromDate,ToDate);
			if (!ObjectUtils.isEmpty(siteDataEntities)) {
				siteDataModels = new ArrayList<SiteDataModel>();
				for (SiteDataEntity siteDataEntity : siteDataEntities) {
					siteDataModels.add(siteDataDto.getSiteDataDetailsModel(siteDataEntity));
					System.out.println(siteDataModels.toString());
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}
		return siteDataModels;
	}

	@SuppressWarnings("unchecked")
	public JSONObject SiteReportUploadeToOV(JSONObject siteDetails, SiteDataEntity statusSiteDataEntity)
	 {
		JSONObject result = new JSONObject();
		try {
			JSONObject ovUpdateJson=new JSONObject();
			
			ovUpdateJson.put("FileName",statusSiteDataEntity.getFileName());
			ovUpdateJson.put("filePath",statusSiteDataEntity.getFilePath());
			ovUpdateJson.put("programName",siteDetails.get("programName"));
			ovUpdateJson.put("programId",siteDetails.get("programId"));
			ovUpdateJson.put("ciqFileName",statusSiteDataEntity.getCiqFileName());
			ovUpdateJson.put("enbName",statusSiteDataEntity.getNeName());
			ovUpdateJson.put("enbId",statusSiteDataEntity.getNeId());
			String neId=statusSiteDataEntity.getNeId().toString();
			JSONObject trakerIdDetails  = siteReportToOV.getTrakerIdList(ovUpdateJson, statusSiteDataEntity);
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
									JSONObject updateJsonAPI = siteReportToOV.getSiteReportUploadDetails(ovUpdateJson, statusSiteDataEntity);
								
				}else
				{	
					
				
					SiteReportOVEntity siteReportOVEntity= new SiteReportOVEntity();
					siteReportOVEntity.setCurrentResult("["+date2+"]"+"-"+"failed to fetch the Tracker ID/ No Tracker ID on OV for: "+neId);
					SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(statusSiteDataEntity.getId());					
					siteReportOVEntity.setSiteDataEntity(siteDataEntity);
					siteReportOVEntity.setFileName(ovUpdateJson.get("FileName").toString());
					siteReportOVEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					siteDetailsReportRepository.updateSiteReportOv(siteReportOVEntity);
					statusSiteDataEntity.setOvUpdateStatus("Failure");
					updateSiteDataEntity(statusSiteDataEntity);
					//runTestRepository.updateGeneratedFileDetails(runTestEntityMap.get(neId));
					
					
					logger.error("fail in 1nd Api" );
				}
				}else
				{
					 date2=DateUtil.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
					 System.out.println(date2);
						SiteReportOVEntity siteReportOVEntity= new SiteReportOVEntity();
					if(trakerIdDetails.containsKey("reason")) {
					String reason=trakerIdDetails.get("reason").toString();
					siteReportOVEntity.setCurrentResult("["+date2+"]"+"-"+reason);
					}else {
						siteReportOVEntity.setCurrentResult("["+date2+"]"+"-"+"failed to fetch the Tracker ID/ No Tracker ID on OV for: "+neId);
					}
				
					//ovTestResultEntity.setCurrentResult("failed to fetch the Tracker ID");
					SiteDataEntity siteDataEntity = siteDetailsReportRepository.getSiteDataEntity(statusSiteDataEntity.getId());
					siteReportOVEntity.setSiteDataEntity(siteDataEntity);
					siteReportOVEntity.setFileName(ovUpdateJson.get("FileName").toString());
					siteReportOVEntity.setFilePath(ovUpdateJson.get("filePath").toString());
					siteDetailsReportRepository.updateSiteReportOv(siteReportOVEntity);
					statusSiteDataEntity.setOvUpdateStatus("Failure");
					
					//runTestEntityMap.get(neId).setOvUpdateReason("failed to fetch the Tracker ID " );
					
					updateSiteDataEntity(statusSiteDataEntity);
					
					logger.error("fail in 1nd Api" );
				}
			
		} catch(Exception e) {
			result.put("status", Constants.FAIL);
			result.put("reason", e.getMessage());
			logger.info("Exception in runReTest() in RunTestController UpdateENV from PreMigration API" + ExceptionUtils.getFullStackTrace(e));
		}
		return result;
	}
	@Override
	public boolean updateSiteDataEntity(SiteDataEntity siteDataEntity) {
		boolean status = false;
		try {
			status = siteDetailsReportRepository.updateSiteDataEntity(siteDataEntity);
		} catch (Exception e) {
			logger.error("Exception updateGeneratedFileDetails() in GenerateCsvServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	@Override
	public List<SiteReportOVEntity> getSiteReportOVEntity(Integer runTestId) {
		List<SiteReportOVEntity> runTestResultEntityList = null;
		try {

			runTestResultEntityList = siteDetailsReportRepository.getSiteReportOVEntity(runTestId);

		} catch (Exception e) {
			logger.error("Exception RunTestServiceImpl in getRunTestResult() " + ExceptionUtils.getFullStackTrace(e));
		}
		return runTestResultEntityList;
	}
}
