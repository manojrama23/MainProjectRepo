package com.smart.rct.postmigration.serviceImpl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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

import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandIssueEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandPassFailEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandRulesEntity;
import com.smart.rct.postmigration.entity.Audit5GCBandSummaryEntity;
import com.smart.rct.postmigration.repository.Audit5GCBandIssueRepository;
import com.smart.rct.postmigration.repository.Audit5GCBandSummaryRepository;
import com.smart.rct.postmigration.service.Audit5GCBandRulesService;
import com.smart.rct.postmigration.service.Audit5GCBandSummaryService;

@Service
public class Audit5GCBandSummaryServiceImpl implements Audit5GCBandSummaryService {
	final static Logger logger = LoggerFactory.getLogger(Audit5GCBandSummaryServiceImpl.class);
	@Autowired
	Audit5GCBandSummaryRepository audit5GCBandSummaryRepository;
	
	@Autowired
	Audit5GCBandIssueRepository audit5GCBandIssueRepository;
	
	@Autowired
	RunTestRepository runTestRepository;
	
	@Autowired
	Audit5GCBandRulesService audit5GCBandRulesService;
	
	@Override
	public Audit5GCBandSummaryEntity getaudit5GCBandSummaryEntityById(int auditSummaryId) {
		Audit5GCBandSummaryEntity audit5GCBandSummaryEntityResult = null;
		try {
			audit5GCBandSummaryEntityResult = audit5GCBandSummaryRepository.getaudit5GCBandSummaryEntityById(auditSummaryId);
		} catch (Exception e) {
			logger.error("Exception in getaudit5GCBandSummaryEntityById()   Audit5GCBandSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandSummaryEntityResult;
	}
	
	@Override
	public Audit5GCBandSummaryEntity createAudit5GCBandSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue) {
		Audit5GCBandSummaryEntity audit5GCBandSummaryEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit5GCBandRulesEntity audit5GCBandRulesEntity = audit5GCBandRulesService.getAudit5GCBandRulesEntityById(auditRuleId);
			
			Audit5GCBandSummaryEntity audit5GCBandSummaryEntity = new Audit5GCBandSummaryEntity();
			audit5GCBandSummaryEntity.setAudit5gCbandRulesEntity(audit5GCBandRulesEntity);
			audit5GCBandSummaryEntity.setNeId(neId);
			audit5GCBandSummaryEntity.setRunTestEntity(runTestEntity);
			audit5GCBandSummaryEntity.setAuditIssue(auditIssue);
			
			audit5GCBandSummaryEntityResult = audit5GCBandSummaryRepository.createAudit5GCBandSummaryEntity(audit5GCBandSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in createAudit5GCBandSummaryEntity()   Audit5GCBandSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandSummaryEntityResult;
	}
	
	@Override
	public Audit5GCBandPassFailEntity createAudit5GCBandPassFailEntity(int auditRuleId, int runTestId, String neId,
			String PassFail) {
		Audit5GCBandPassFailEntity audit5GCBandPassFailEntityResult = null;
		 try {
			 RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			 Audit5GCBandRulesEntity audit5GCBandRulesEntity = audit5GCBandRulesService.getAudit5GCBandRulesEntityById(auditRuleId);

			 Audit5GCBandPassFailEntity audit5GCBandPassFailEntity = new Audit5GCBandPassFailEntity();
			 audit5GCBandPassFailEntity.setAudit5GCBandRulesEntity(audit5GCBandRulesEntity);
			 audit5GCBandPassFailEntity.setNeId(neId);
			 audit5GCBandPassFailEntity.setRunTestEntity(runTestEntity);
			 audit5GCBandPassFailEntity.setAuditPassFail(PassFail);
			 audit5GCBandPassFailEntity.setCreationDate(runTestEntity.getCreationDate());
			 
			 audit5GCBandPassFailEntityResult = audit5GCBandSummaryRepository.createAudit5GCBandPassFailEntity(audit5GCBandPassFailEntity);
		 }catch (Exception e) {
				logger.error("Exception in createAudit5GCBandPassFailEntity()   Audit5GCBandSummaryServiceImpl:"
						+ ExceptionUtils.getFullStackTrace(e));
			}
		
		
		return audit5GCBandPassFailEntityResult;
	}
	
	@Override
	public List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityListByRunTestId(int runTestId) {
		List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = new ArrayList<>();
		try {
			audit5GCBandSummaryEntityList = audit5GCBandSummaryRepository.getAudit5GCBandSummaryEntityList(runTestId);
		} catch (Exception e) {
			logger.error("Exception in getAudit5GCBandSummaryEntityListByRunTestId()   Audit5GCBandSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandSummaryEntityList;
	}
	
	@Override
	public List<Audit5GCBandSummaryEntity> getAudit5GCBandSummaryEntityListByNeId(String neId) {
		List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = new ArrayList<>();
		try {
			List<Audit5GCBandIssueEntity> audit5GCBandIssueEntityList = audit5GCBandIssueRepository.getAudit5GCBandIssueEntityList(neId);
			if(audit5GCBandIssueEntityList != null && !audit5GCBandIssueEntityList.isEmpty()) {
				Audit5GCBandIssueEntity audit5GCBandIssueEntity = audit5GCBandIssueEntityList.get(0);
				int runTestId = audit5GCBandIssueEntity.getRunTestEntity().getId();
				audit5GCBandSummaryEntityList = audit5GCBandSummaryRepository.getAudit5GCBandSummaryEntityList(runTestId);
			}			
		} catch (Exception e) {
			logger.error("Exception in getAudit5GCBandSummaryEntityListByNeId()   Audit5GCBandSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandSummaryEntityList;
	}
	
	@Override
	public boolean deleteAuditSummaryReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit5GCBandSummaryEntity> audit5GCBandSummaryEntityList = getAudit5GCBandSummaryEntityListByRunTestId(runTestId);
			for(Audit5GCBandSummaryEntity audit5GCBandSummaryEntity :audit5GCBandSummaryEntityList) {
				if(!audit5GCBandSummaryRepository.deleteaudit5GCBandSummaryEntityById(audit5GCBandSummaryEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditSummaryReport() in  Audit5GCBandSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean createAudit5GCBandSummaryReportExcel(JSONObject audit5GCbandSummaryReportDetails, String filePath, String neName) {
		boolean status = false;
		try {
			List<LinkedHashMap<String, String>> audit5GCBandSummaryModelList = (List<LinkedHashMap<String, String>>)audit5GCbandSummaryReportDetails.get("postAuditIssues");
			if(audit5GCBandSummaryModelList!=null && !audit5GCBandSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_5G_CBAND_SUMMARY_REPORT);
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 10);
				headerFont.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setFont(headerFont);
				Row headerRow = sheet.createRow(1);
				
				XSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setWrapText(true);
				
				String[] columnHeaderNames = Constants.AUDIT_5G_CBAND_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				int rowCount = 2;
				for(LinkedHashMap<String, String> audit5GCBandSummaryModel : audit5GCBandSummaryModelList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;
					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("testName"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("test"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("yangCommand"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("auditIssue"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("expectedResult"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("actionItem"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("errorCode"));

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("referenceMOP"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GCBandSummaryModel.get("remarks"));
				}
				String fileName = "AUDIT_5GCBand_SUMMARY_REPORT_" + neName + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit5GCBandSummaryReportExcel()   Audit5GCBandSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<Audit5GCBandPassFailEntity> getAudit5GCBandPassFailsEntityEachRunId(int runId) {
		List<Audit5GCBandPassFailEntity> audit5GCBandPassFailEntityList = new ArrayList<Audit5GCBandPassFailEntity>();
		try {
			audit5GCBandPassFailEntityList = audit5GCBandSummaryRepository.createAudit5GCBandPassFailEachId(runId );
			
			
		} catch (Exception e) {
			logger.error("Exception in getAudit5GCBandEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandPassFailEntityList;
		
	}

	@Override
	public List<Audit5GCBandPassFailEntity> getAudit5GCBandPassFailEntityListByRunTestId(Set<Integer> set1) {

		List<Audit5GCBandPassFailEntity> audit5GCBandPassFailEntityList = new ArrayList<>();
		try {
			audit5GCBandPassFailEntityList = audit5GCBandSummaryRepository.createAudit5GCBandPassFailEntityList(set1);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GFsuSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GCBandPassFailEntityList;
	
	}

	@Override
	public boolean deleteAuditPassFailReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit5GCBandPassFailEntity> audit5GCBandPassFailEntityList = getAudit5GCBandPassFailsEntityEachRunId(runTestId);
			for(Audit5GCBandPassFailEntity audit5GCBandPassFailEntity :audit5GCBandPassFailEntityList) {
				if(!audit5GCBandSummaryRepository.deleteaudit5GCBandPassFailEntityById(audit5GCBandPassFailEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditSummaryReport() in  Audit5GCBandSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	
}
