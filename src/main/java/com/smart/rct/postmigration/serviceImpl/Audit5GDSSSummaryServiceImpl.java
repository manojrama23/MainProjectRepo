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
import com.smart.rct.postmigration.entity.Audit5GDSSIssueEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSRulesEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSSummaryEntity;
import com.smart.rct.postmigration.repository.Audit5GDSSIssueRepository;
import com.smart.rct.postmigration.repository.Audit5GDSSSummaryRepository;
import com.smart.rct.postmigration.service.Audit5GDSSRulesService;
import com.smart.rct.postmigration.service.Audit5GDSSSummaryService;

@Service
public class Audit5GDSSSummaryServiceImpl implements Audit5GDSSSummaryService {
	final static Logger logger = LoggerFactory.getLogger(Audit5GDSSSummaryServiceImpl.class);
	@Autowired
	Audit5GDSSSummaryRepository audit5GDSSSummaryRepository;
	
	@Autowired
	Audit5GDSSIssueRepository audit5GDSSIssueRepository;
	
	@Autowired
	RunTestRepository runTestRepository;
	
	@Autowired
	Audit5GDSSRulesService audit5GDSSRulesService;
	
	@Override
	public Audit5GDSSSummaryEntity getaudit5GDSSSummaryEntityById(int auditSummaryId) {
		Audit5GDSSSummaryEntity audit5GDSSSummaryEntityResult = null;
		try {
			audit5GDSSSummaryEntityResult = audit5GDSSSummaryRepository.getaudit5GDSSSummaryEntityById(auditSummaryId);
		} catch (Exception e) {
			logger.error("Exception in getaudit5GDSSSummaryEntityById()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSSummaryEntityResult;
	}
	
	@Override
	public Audit5GDSSSummaryEntity createAudit5GDSSSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue) {
		Audit5GDSSSummaryEntity audit5GDSSSummaryEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit5GDSSRulesEntity audit5GDSSRulesEntity = audit5GDSSRulesService.getAudit5GDSSRulesEntityById(auditRuleId);
			
			Audit5GDSSSummaryEntity audit5GDSSSummaryEntity = new Audit5GDSSSummaryEntity();
			audit5GDSSSummaryEntity.setAudit5gDSSRulesEntity(audit5GDSSRulesEntity);
			audit5GDSSSummaryEntity.setNeId(neId);
			audit5GDSSSummaryEntity.setRunTestEntity(runTestEntity);
			audit5GDSSSummaryEntity.setAuditIssue(auditIssue);
			
			audit5GDSSSummaryEntityResult = audit5GDSSSummaryRepository.createAudit5GDSSSummaryEntity(audit5GDSSSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in createAudit5GDSSSummaryEntity()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSSummaryEntityResult;
	}
	
	@Override
	public List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityListByRunTestId(int runTestId) {
		List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = new ArrayList<>();
		try {
			audit5GDSSSummaryEntityList = audit5GDSSSummaryRepository.getAudit5GDSSSummaryEntityList(runTestId);
		} catch (Exception e) {
			logger.error("Exception in getAudit5GDSSSummaryEntityListByRunTestId()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSSummaryEntityList;
	}
	
	@Override
	public List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailsEntityEachRunId(int runId) {
		List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailEntityList = new ArrayList<>();
		try {
			audit5GDSSPassFailEntityList = audit5GDSSSummaryRepository.getAudit5GDSSPassFailEntityList(runId);
		} catch (Exception e) {
			logger.error("Exception in getAudit5GDSSPassFailEntityListByRunTestId()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSPassFailEntityList;
	}

	
	@Override
	public List<Audit5GDSSSummaryEntity> getAudit5GDSSSummaryEntityListByNeId(String neId) {
		List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = new ArrayList<>();
		try {
			List<Audit5GDSSIssueEntity> audit5GDSSIssueEntityList = audit5GDSSIssueRepository.getAudit5GDSSIssueEntityList(neId);
			if(audit5GDSSIssueEntityList != null && !audit5GDSSIssueEntityList.isEmpty()) {
				Audit5GDSSIssueEntity audit5GDSSIssueEntity = audit5GDSSIssueEntityList.get(0);
				int runTestId = audit5GDSSIssueEntity.getRunTestEntity().getId();
				audit5GDSSSummaryEntityList = audit5GDSSSummaryRepository.getAudit5GDSSSummaryEntityList(runTestId);
			}			
		} catch (Exception e) {
			logger.error("Exception in getAudit5GDSSSummaryEntityListByNeId()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSSummaryEntityList;
	}
	
	@Override
	public boolean deleteAuditSummaryReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit5GDSSSummaryEntity> audit5GDSSSummaryEntityList = getAudit5GDSSSummaryEntityListByRunTestId(runTestId);
			for(Audit5GDSSSummaryEntity audit5GDSSSummaryEntity :audit5GDSSSummaryEntityList) {
				if(!audit5GDSSSummaryRepository.deleteaudit5GDSSSummaryEntityById(audit5GDSSSummaryEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditSummaryReport() in  Audit5GDSSSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean createAudit5GDSSSummaryReportExcel(JSONObject audit5GDSSSummaryReportDetails, String filePath, String neName) {
		boolean status = false;
		try {
			List<LinkedHashMap<String, String>> audit5GDSSSummaryModelList = (List<LinkedHashMap<String, String>>)audit5GDSSSummaryReportDetails.get("postAuditIssues");
			if(audit5GDSSSummaryModelList!=null && !audit5GDSSSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_5G_DSS_SUMMARY_REPORT);
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
				
				String[] columnHeaderNames = Constants.AUDIT_5G_DSS_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				int rowCount = 2;
				for(LinkedHashMap<String, String> audit5GDSSSummaryModel : audit5GDSSSummaryModelList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;
					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("testName"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("test"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("yangCommand"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("auditIssue"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("expectedResult"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("actionItem"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("errorCode"));

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("referenceMOP"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit5GDSSSummaryModel.get("remarks"));
				}
				String fileName = "AUDIT_5GDSS_SUMMARY_REPORT_" + neName + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit5GDSSSummaryReportExcel()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public Audit5GDSSPassFailSummaryEntity createAudit5GDSSPassFailEntity(int auditRuleId, int runTestId, String neId,
			String auditPassFail) {
		Audit5GDSSPassFailSummaryEntity audit5GDSSPassFailSummaryEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit5GDSSRulesEntity audit5GDSSRulesEntity = audit5GDSSRulesService.getAudit5GDSSRulesEntityById(auditRuleId);
			
			Audit5GDSSPassFailSummaryEntity audit5GDSSPassFailSummaryEntity = new Audit5GDSSPassFailSummaryEntity();
			audit5GDSSPassFailSummaryEntity.setAudit5GDSSRulesEntity(audit5GDSSRulesEntity);
			audit5GDSSPassFailSummaryEntity.setNeId(neId);
			audit5GDSSPassFailSummaryEntity.setRunTestEntity(runTestEntity);
			audit5GDSSPassFailSummaryEntity.setAuditPassFail(auditPassFail);
			audit5GDSSPassFailSummaryEntity.setCreationDate(runTestEntity.getCreationDate());
			
			audit5GDSSPassFailSummaryEntityResult = audit5GDSSSummaryRepository.createAudit5GDSSPassFailEntity(audit5GDSSPassFailSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in createAudit5GDSSPassFailSummaryEntity()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSPassFailSummaryEntityResult;
	}

	@Override
	public List<Audit5GDSSPassFailSummaryEntity> getAudit5GDSSPassFailSummaryEntityListByRunTestId(
			Set<Integer> set1) {
		
		List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailEntityList = new ArrayList<>();
		try {
			audit5GDSSPassFailEntityList = audit5GDSSSummaryRepository.createAudit5GDSSPassFailEntityList(set1);
		} catch (Exception e) {
			logger.error("Exception in getAudit5GDSSSummaryEntityListByRunTestId()   Audit5GDSSSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit5GDSSPassFailEntityList;
	}

	@Override
	public boolean deleteAuditPassFailReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit5GDSSPassFailSummaryEntity> audit5GDSSPassFailEntityList = getAudit5GDSSPassFailsEntityEachRunId(runTestId);
			for(Audit5GDSSPassFailSummaryEntity audit5GDSSPassFailEntity :audit5GDSSPassFailEntityList) {
				if(!audit5GDSSSummaryRepository.deleteaudit5GDSSPassFailEntityById(audit5GDSSPassFailEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleteAuditPassFailReport() in  Audit5GDSSSummaryServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}


	
}
