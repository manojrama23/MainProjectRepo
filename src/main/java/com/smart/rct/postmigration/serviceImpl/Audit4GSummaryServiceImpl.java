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
import com.smart.rct.postmigration.entity.Audit4GIssueEntity;
import com.smart.rct.postmigration.entity.Audit4GPassFailEntity;
import com.smart.rct.postmigration.entity.Audit4GRulesEntity;
import com.smart.rct.postmigration.entity.Audit4GSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.repository.Audit4GIssueRepository;
import com.smart.rct.postmigration.repository.Audit4GSummaryRepository;
import com.smart.rct.postmigration.service.Audit4GRulesService;
import com.smart.rct.postmigration.service.Audit4GSummaryService;

@Service
public class Audit4GSummaryServiceImpl implements Audit4GSummaryService {
	final static Logger logger = LoggerFactory.getLogger(Audit4GSummaryServiceImpl.class);
	@Autowired
	Audit4GSummaryRepository audit4GSummaryRepository;
	
	@Autowired
	Audit4GIssueRepository audit4GIssueRepository;
	
	@Autowired
	RunTestRepository runTestRepository;
	
	@Autowired
	Audit4GRulesService audit4GRulesService;
	
	@Override
	public Audit4GSummaryEntity getaudit4GSummaryEntityById(int auditSummaryId) {
		Audit4GSummaryEntity audit4GSummaryEntityResult = null;
		try {
			audit4GSummaryEntityResult = audit4GSummaryRepository.getaudit4GSummaryEntityById(auditSummaryId);
		} catch (Exception e) {
			logger.error("Exception in getaudit4GSummaryEntityById()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GSummaryEntityResult;
	}
	
	@Override
	public Audit4GSummaryEntity createAudit4GSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue) {
		Audit4GSummaryEntity audit4GSummaryEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit4GRulesEntity audit4GRulesEntity = audit4GRulesService.getAudit4GRulesEntityById(auditRuleId);
			
			Audit4GSummaryEntity audit4GSummaryEntity = new Audit4GSummaryEntity();
			audit4GSummaryEntity.setAudit4gRulesEntity(audit4GRulesEntity);
			audit4GSummaryEntity.setNeId(neId);
			audit4GSummaryEntity.setRunTestEntity(runTestEntity);
			audit4GSummaryEntity.setAuditIssue(auditIssue);
			
			audit4GSummaryEntityResult = audit4GSummaryRepository.createAudit4GSummaryEntity(audit4GSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in createAudit4GSummaryEntity()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GSummaryEntityResult;
	}
	
	@Override
	public Audit4GPassFailEntity createAudit4GPassFailEntity(int auditRuleId, int runTestId, String neId,
			String auditPassFail) {
		
		Audit4GPassFailEntity audit4GPassFailEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit4GRulesEntity audit4GRulesEntity = audit4GRulesService.getAudit4GRulesEntityById(auditRuleId);
			
			Audit4GPassFailEntity audit4GPassFailEntity = new Audit4GPassFailEntity();
			audit4GPassFailEntity.setAudit4gRulesEntity(audit4GRulesEntity);
			audit4GPassFailEntity.setNeId(neId);
			audit4GPassFailEntity.setRunTestEntity(runTestEntity);
			audit4GPassFailEntity.setAuditPassFail(auditPassFail);
			audit4GPassFailEntity.setCreationDate(runTestEntity.getCreationDate());
			
			audit4GPassFailEntityResult = audit4GSummaryRepository.createAudit4GPassFailEntity(audit4GPassFailEntity);
		}catch (Exception e) {
			logger.error("Exception in createAudit4GPassFailEntity()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		
		return audit4GPassFailEntityResult;
	}
	
	@Override
	public List<Audit4GSummaryEntity> getAudit4GSummaryEntityListByRunTestId(int runTestId) {
		List<Audit4GSummaryEntity> audit4GSummaryEntityList = new ArrayList<>();
		try {
			audit4GSummaryEntityList = audit4GSummaryRepository.getAudit4GSummaryEntityList(runTestId);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GSummaryEntityList;
	}
	
	@Override
	public List<Audit4GPassFailEntity> getAudit4GPassFailEntityEachRunId(int runId) {
		List<Audit4GPassFailEntity> audit4GPassFailEntityList = new ArrayList<>();
		try {
			audit4GPassFailEntityList = audit4GSummaryRepository.getAudit4GPassFailEntityList(runId);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GPassFailEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		
		return audit4GPassFailEntityList;
	}

	
	@Override
	public List<Audit4GSummaryEntity> getAudit4GSummaryEntityListByNeId(String neId) {
		List<Audit4GSummaryEntity> audit4GSummaryEntityList = new ArrayList<>();
		try {
			List<Audit4GIssueEntity> audit4GIssueEntityList = audit4GIssueRepository.getAudit4GIssueEntityList(neId);
			if(audit4GIssueEntityList != null && !audit4GIssueEntityList.isEmpty()) {
				Audit4GIssueEntity audit4GIssueEntity = audit4GIssueEntityList.get(0);
				int runTestId = audit4GIssueEntity.getRunTestEntity().getId();
				audit4GSummaryEntityList = audit4GSummaryRepository.getAudit4GSummaryEntityList(runTestId);
			}			
		} catch (Exception e) {
			logger.error("Exception in getAudit4GSummaryEntityListByNeId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GSummaryEntityList;
	}
	
	@Override
	public boolean deleteAuditSummaryReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit4GSummaryEntity> audit4GSummaryEntityList = getAudit4GSummaryEntityListByRunTestId(runTestId);
			for(Audit4GSummaryEntity audit4GSummaryEntity :audit4GSummaryEntityList) {
				if(!audit4GSummaryRepository.deleteaudit4GSummaryEntityById(audit4GSummaryEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditSummaryReport() in  Audit4GSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean createAudit4GSummaryReportExcel(JSONObject audit4GSummaryReportDetails, String filePath, String neName) {
		boolean status = false;
		try {
			List<LinkedHashMap<String, String>> audit4GSummaryModelList = (List<LinkedHashMap<String, String>>)audit4GSummaryReportDetails.get("postAuditIssues");
			if(audit4GSummaryModelList!=null && !audit4GSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_4G_SUMMARY_REPORT);
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
				
				String[] columnHeaderNames = Constants.AUDIT_4G_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				int rowCount = 2;
				for(LinkedHashMap<String, String> audit4GSummaryModel : audit4GSummaryModelList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;

					
					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("testName"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("test"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("yangCommand"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("auditIssue"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("expectedResult"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("actionItem"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("errorCode"));

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("referenceMOP"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GSummaryModel.get("remarks"));
				}
				String fileName = "AUDIT_4G_SUMMARY_REPORT_" + neName + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit4GSummaryReportExcel()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<Audit4GPassFailEntity> getAudit4GPassFailEntityListByRunTestId(Set<Integer> set1) {
		List<Audit4GPassFailEntity> audit4GPassFailEntityList = new ArrayList<>();
		try {
			audit4GPassFailEntityList = audit4GSummaryRepository.createAudit4GPassFailEntityList(set1);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GFsuSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GPassFailEntityList;
	}

	@Override
	public boolean deleteAuditPassFailReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit4GPassFailEntity> audit4GSummaryEntityList = getAudit4GPassFailEntityEachRunId(runTestId);
			for(Audit4GPassFailEntity audit4GSummaryEntity :audit4GSummaryEntityList) {
				if(!audit4GSummaryRepository.deleteaudit4GPassFailEntityById(audit4GSummaryEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditSummaryReport() in  Audit4GSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}



}
