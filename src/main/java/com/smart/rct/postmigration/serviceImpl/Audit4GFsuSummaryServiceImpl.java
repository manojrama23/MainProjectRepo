package com.smart.rct.postmigration.serviceImpl;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.smart.rct.postmigration.entity.Audit4GFsuIssueEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuPassFailSummaryEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuRulesEntity;
import com.smart.rct.postmigration.entity.Audit4GFsuSummaryEntity;
import com.smart.rct.postmigration.entity.Audit5GDSSPassFailSummaryEntity;
import com.smart.rct.postmigration.models.Audit4GSummaryModel;
import com.smart.rct.postmigration.models.AuditPassFailSummaryModel;
import com.smart.rct.postmigration.models.AuditRunModel;
import com.smart.rct.postmigration.repository.Audit4GFsuIssueRepository;
import com.smart.rct.postmigration.repository.Audit4GFsuSummaryRepository;
import com.smart.rct.postmigration.service.Audit4GFsuRulesService;
import com.smart.rct.postmigration.service.Audit4GFsuSummaryService;
import com.smart.rct.usermanagement.models.User;

@Service
public class Audit4GFsuSummaryServiceImpl implements Audit4GFsuSummaryService {
	final static Logger logger = LoggerFactory.getLogger(Audit4GSummaryServiceImpl.class);
	
	@Autowired
	Audit4GFsuSummaryRepository audit4GFsuSummaryRepository;
	
	@Autowired
	Audit4GFsuIssueRepository audit4GFsuIssueRepository;
	
	@Autowired
	RunTestRepository runTestRepository;
	
	@Autowired
	Audit4GFsuRulesService audit4GFsuRulesService;
	
	@Override
	public Audit4GFsuSummaryEntity getaudit4GFsuSummaryEntityById(int auditSummaryId) {
		Audit4GFsuSummaryEntity audit4GFsuSummaryEntityResult = null;
		try {
			audit4GFsuSummaryEntityResult = audit4GFsuSummaryRepository.getaudit4GFsuSummaryEntityById(auditSummaryId);
		} catch (Exception e) {
			logger.error("Exception in getaudit4GSummaryEntityById()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuSummaryEntityResult;
	}
	
	@Override
	public Audit4GFsuSummaryEntity createAudit4GFsuSummaryEntity(int auditRuleId, int runTestId, String neId, String auditIssue) {
		Audit4GFsuSummaryEntity audit4GFsuSummaryEntityResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit4GFsuRulesEntity audit4GFsuRulesEntity = audit4GFsuRulesService.getAudit4GFsuRulesEntityById(auditRuleId);
			
			Audit4GFsuSummaryEntity audit4GFsuSummaryEntity = new Audit4GFsuSummaryEntity();
			audit4GFsuSummaryEntity.setAudit4gFsuRulesEntity(audit4GFsuRulesEntity);
			audit4GFsuSummaryEntity.setNeId(neId);
			audit4GFsuSummaryEntity.setRunTestEntity(runTestEntity);
			audit4GFsuSummaryEntity.setAuditIssue(auditIssue);
			
			
			
			audit4GFsuSummaryEntityResult = audit4GFsuSummaryRepository.createAudit4GFsuSummaryEntity(audit4GFsuSummaryEntity);
		} catch (Exception e) {
			logger.error("Exception in createAudit4GFsuSummaryEntity()   Audit4GFsuSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuSummaryEntityResult;
	}
	
	@Override
	public Audit4GFsuPassFailSummaryEntity createAudit4GFsuPassFailSummaryEntity(int auditRuleId, int runTestId,
			String neId, String auditPassFail) {
		Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailSummaryResult = null;
		try {
			RunTestEntity runTestEntity = runTestRepository.getRunTestEntity(runTestId);
			Audit4GFsuRulesEntity audit4GFsuRulesEntity = audit4GFsuRulesService.getAudit4GFsuRulesEntityById(auditRuleId);

			Audit4GFsuPassFailSummaryEntity auditPassFailEntity = new Audit4GFsuPassFailSummaryEntity();
			auditPassFailEntity.setAudit4gFsuRulesEntity(audit4GFsuRulesEntity);
			auditPassFailEntity.setNeId(neId);
			auditPassFailEntity.setRunTestEntity(runTestEntity);
			auditPassFailEntity.setAuditPassFail(auditPassFail);
			auditPassFailEntity.setCreationDate(runTestEntity.getCreationDate());
			
			audit4GFsuPassFailSummaryResult=audit4GFsuSummaryRepository.createAudit4GFsuPassFailEntity(auditPassFailEntity);
			
		} catch (Exception e) {
			logger.error("Exception in createAudit4GFsuPassFailEntity()   Audit4GFsuSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuPassFailSummaryResult;
	}
	
	
	
	@Override
	public List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityListByRunTestId(int runTestId) {
		List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = new ArrayList<>();
		try {
			audit4GFsuSummaryEntityList = audit4GFsuSummaryRepository.getAudit4GFsuSummaryEntityList(runTestId);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GFsuSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuSummaryEntityList;
	}
	
	@Override
	public List<Audit4GFsuPassFailSummaryEntity> getAudit4GFsuPassFailSummaryEntityEachRunId(int runId) {
		
		List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = new ArrayList<Audit4GFsuPassFailSummaryEntity>();
		try {
			audit4GFsuPassFailSummaryEntityList = audit4GFsuSummaryRepository.createAudit4GFsuPassFailEachId(runId);
			
			
		} catch (Exception e) {
			logger.error("Exception in getAudit4GFsuSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuPassFailSummaryEntityList;
	}
	
	@Override
	public List<Audit4GFsuPassFailSummaryEntity> getAudit4GFsuPassFailSummaryEntityListByRunTestId(Set<Integer> set1) {
		List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailSummaryEntityList = new ArrayList<>();
		try {
			audit4GFsuPassFailSummaryEntityList = audit4GFsuSummaryRepository.createAudit4GFsuPassFailEntityList(set1);
		} catch (Exception e) {
			logger.error("Exception in getAudit4GFsuSummaryEntityListByRunTestId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuPassFailSummaryEntityList;
	}
	
	
	@Override
	public List<Audit4GFsuSummaryEntity> getAudit4GFsuSummaryEntityListByNeId(String neId) {
		List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = new ArrayList<>();
		try {
			List<Audit4GFsuIssueEntity> audit4GFsuIssueEntityList = audit4GFsuIssueRepository.getAudit4GFsuIssueEntityList(neId);
			if(audit4GFsuIssueEntityList != null && !audit4GFsuIssueEntityList.isEmpty()) {
				Audit4GFsuIssueEntity audit4GFsuIssueEntity = audit4GFsuIssueEntityList.get(0);
				int runTestId = audit4GFsuIssueEntity.getRunTestEntity().getId();
				audit4GFsuSummaryEntityList = audit4GFsuSummaryRepository.getAudit4GFsuSummaryEntityList(runTestId);
			}			
		} catch (Exception e) {
			logger.error("Exception in getAudit4GSummaryEntityListByNeId()   Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return audit4GFsuSummaryEntityList;
	}
	
	
	@Override
	public boolean deleteAuditSummaryReport(int runTestId) {
		boolean status = false;
		try {
			List<Audit4GFsuSummaryEntity> audit4GFsuSummaryEntityList = getAudit4GFsuSummaryEntityListByRunTestId(runTestId);
			for(Audit4GFsuSummaryEntity audit4GFsuSummaryEntity :audit4GFsuSummaryEntityList) {
				if(!audit4GFsuSummaryRepository.deleteaudit4GFsuSummaryEntityById(audit4GFsuSummaryEntity.getId())) {
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
	@Override
	public boolean deleteAuditPassFailReport(int runTestId) {
		boolean status = false;
		try {

			List<Audit4GFsuPassFailSummaryEntity> audit4GFsuPassFailEntityList = getAudit4GFsuPassFailSummaryEntityEachRunId(
					runTestId);
			for (Audit4GFsuPassFailSummaryEntity audit4GFsuPassFailEntity : audit4GFsuPassFailEntityList) {
				if (!audit4GFsuSummaryRepository.deleteaudit4GFsuPassFailEntityById(audit4GFsuPassFailEntity.getId())) {
					return false;
				}
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error("Exception in deleteAuditSummaryReport() in  Audit4GSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean createAudit4GFsuSummaryReportExcel(JSONObject audit4GFsuSummaryReportDetails, String filePath, String neName) {
		boolean status = false;
		try {
			List<LinkedHashMap<String, String>> audit4GFsuSummaryModelList = (List<LinkedHashMap<String, String>>)audit4GFsuSummaryReportDetails.get("postAuditIssues");
			if(audit4GFsuSummaryModelList!=null && !audit4GFsuSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_4G_FSU_SUMMARY_REPORT);
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
				
				String[] columnHeaderNames = Constants.AUDIT_4G_FSU_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				int rowCount = 2;
				for(LinkedHashMap<String, String> audit4GFsuSummaryModel : audit4GFsuSummaryModelList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;
										
					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("testName"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("test"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("yangCommand"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("auditIssue"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("expectedResult"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("actionItem"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("errorCode"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("referenceMOP"));

					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("remarks"));
				}
				String fileName = "AUDIT_4G_FSU_SUMMARY_REPORT_" + neName + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit4GSFsuummaryReportExcel()   Audit4GFsuSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	

	@Override
	public boolean createBulkAudit4GFsuSummaryReportExcel(JSONObject downloadBulkReport, String filePath) {

		boolean status = false;
		try {
			/*Map<String, Map<Integer, Map<String, String>>> bulkaudit4GFsuSummaryModelList = (Map<String, Map<Integer, Map<String, String>>>) downloadBulkReport
					.get("passfailStatus");*/
			
			List<AuditPassFailSummaryModel>  bulkaudit4GFsuSummaryModelList = (List<AuditPassFailSummaryModel>) downloadBulkReport.get("passfailStatus");
			List<Audit4GSummaryModel> audit4GFsuSummaryModelList = (List<Audit4GSummaryModel>)downloadBulkReport.get("postAuditIssues");
			//Map<String, Object> postAuditIssueList = (Map<String, Object>)downloadBulkReport.get("postAuditIssues");
			
		//	List<Audit4GSummaryModel>  bulkauditSummaryModelList = (List<Audit4GSummaryModel>) downloadBulkReport.get("postAuditIssues");
			if (bulkaudit4GFsuSummaryModelList != null && !bulkaudit4GFsuSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_PASSFAIL_SUMMARY_REPORT);
				
				//sSheet sheet1 = workbook.createSheet(Constants.AUDIT_4G_FSU_SUMMARY_REPORT);

				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 10);
				headerFont.setColor(IndexedColors.BLACK.getIndex());
				XSSFCellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setFont(headerFont);
				Row headerRow = sheet.createRow(0);
				
				
				
				String[] columnHeaderNames = Constants.AUDIT_4G_FSU_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				XSSFCellStyle cellStyle = workbook.createCellStyle();
				Set<String> set=(Set<String>) downloadBulkReport.get("neheaders");
				List<String> headers=new ArrayList<>();
				headers.add("Date");
				headers.add("Ne-Id");
				headers.add("Tech");
				headers.add("runId");
				headers.add("userName");
				headers.addAll(set);
				
				
				cellStyle.setWrapText(true);
				
//				String[] columnHeaderNames = Constants.AUDIT_4G_FSU_BULK_PASSFAIL_SUMMARY_REPORT_COLUMNS;
				for (int i = 0; i < headers.size(); i++) {
					sheet.setColumnWidth(i, 1000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(headers.get(i));
					cell.setCellStyle(headerCellStyle);
				}
				
				int len = headers.size();
				int rowCount = 1;
				
				for(AuditPassFailSummaryModel entry:bulkaudit4GFsuSummaryModelList)
				{
					String neId = entry.getNeId();
					
					String programme = entry.getTech();
					
			
					String userName = entry.getUserName();
					
					List<AuditRunModel> auditNeRunSummary = entry.getAuditNeRunSummary();
					
					for (AuditRunModel runEntry : auditNeRunSummary) {
						Row row = sheet.createRow(rowCount++);
						String runId = runEntry.getRunId();
						Map<String, String> value = runEntry.getRunTestParams();
						
						Date creationDate = runEntry.getCreationDate();
						System.out.println(creationDate);
						System.out.println(value);
						int cellCount = 0;
						
						
						
						while (len > cellCount) {
								if (cellCount == 0) {
									Cell cell = row.createCell(cellCount);
									cell.setCellStyle(cellStyle);
							
									cell.setCellValue(creationDate.toString());
									
									
								} else if (cellCount == 1) {
									Cell cell = row.createCell(cellCount);
									cell.setCellStyle(cellStyle);
							
									cell.setCellValue(neId);
								}else if (cellCount == 2) {
									Cell cell = row.createCell(cellCount);
									cell.setCellStyle(cellStyle);
							
									cell.setCellValue(programme);
								}else if (cellCount == 3) {
									Cell cell = row.createCell(cellCount);
									cell.setCellStyle(cellStyle);
							
									cell.setCellValue(runId);
								} else if (cellCount == 4) {
									Cell cell = row.createCell(cellCount);
									cell.setCellStyle(cellStyle);
							
									cell.setCellValue(userName);
							
								} else {
							
							Cell cell2 = headerRow.getCell(cellCount);
							System.out.println("cell2:" + cell2);
							if (null != cell2 && null != cell2.getStringCellValue()) {
								Cell cell = row.createCell(cellCount);
								cell.setCellStyle(cellStyle);
								String string = value.get(cell2.getStringCellValue());
								string = null != string ? string : "NA";
								cell.setCellValue(string);
							}
							
								}
								cellCount++;
						}
					}

				}
				
				if (audit4GFsuSummaryModelList != null && !audit4GFsuSummaryModelList.isEmpty()) {
				
					logger.error("AuditSummaryList {}", audit4GFsuSummaryModelList.size());
					Sheet sheet1 = workbook.createSheet(Constants.AUDIT_SUMMARY_REPORT);

					Font headerFont1 = workbook.createFont();
					headerFont1.setBold(true);
					headerFont1.setFontHeightInPoints((short) 10);
					headerFont1.setColor(IndexedColors.BLACK.getIndex());
					XSSFCellStyle headerCellStyle1 = workbook.createCellStyle();
					headerCellStyle1.setFillForegroundColor(new XSSFColor(new java.awt.Color(164, 211, 238)));
					headerCellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					headerCellStyle1.setFont(headerFont1);
					Row headerRow1 = sheet1.createRow(1);
				
					
					XSSFCellStyle cellStyle1 = workbook.createCellStyle();
					Set<String> set1=(Set<String>) downloadBulkReport.get("neheaders");
					List<String> headers1=new ArrayList<>();
					headers1.add("Date");
					headers1.add("Ne-Id");
					headers1.add("Tech");
					headers1.add("runId");
					headers1.add("userName");
					headers1.addAll(set);
					
					cellStyle.setWrapText(true);
					
					String[] columnHeaderNames1 = Constants.AUDIT_4G_FSU_BULK_SUMMARY_REPORT_COLUMNS;
					for(int i=0; i<columnHeaderNames1.length; i++) {
						sheet1.setColumnWidth(i, 9000);
						Cell cell = headerRow1.createCell(i);
						cell.setCellValue(columnHeaderNames1[i]);
						cell.setCellStyle(headerCellStyle1);
					}
					 rowCount = 2;
			
					for(Audit4GSummaryModel audit4GFsuSummaryModel : audit4GFsuSummaryModelList) {
						//audit4GFsuSummaryModel.getNeId();
						
						System.out.println(audit4GFsuSummaryModelList.toString());
						
						System.out.println(audit4GFsuSummaryModel);
						
						Row row1 = sheet1.createRow(rowCount++);
						int cellCount = 0;
						
						Cell cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getNeId());
						
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getTestName());
								
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getTest());
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getYangCommand());
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getAuditIssue());
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getExpectedResult());
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getActionItem());
						
						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getErrorCode());
						
						/*
						 * cell = row1.createCell(cellCount++); cell.setCellStyle(cellStyle);
						 * cell.setCellValue(audit4GFsuSummaryModel.getReferenceMOP());
						 */

						cell = row1.createCell(cellCount++);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(audit4GFsuSummaryModel.getRemarks());
					}
					
				
				}

				String fileName = "AUDIT_BULK_PASSFAIL_SUMMARY_REPORT_" + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit4GSFsuummaryReportExcel()   Audit4GFsuSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		logger.error("Status {}",status);
		return status;
		
	}


	@Override
	public boolean createAudit4GFsuPassFailSummaryReportExcel(JSONObject audit4gFsuPassFailSummaryReportDetails,
			String filePath, String neName) {
		boolean status = false;
		try {
			List<LinkedHashMap<String, String>> audit4GFsuSummaryModelList = (List<LinkedHashMap<String, String>>)audit4gFsuPassFailSummaryReportDetails.get("postAuditPassFailData");
			if(audit4GFsuSummaryModelList!=null && !audit4GFsuSummaryModelList.isEmpty()) {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet(Constants.AUDIT_4G_FSU_PASSFAIL_SUMMARY_REPORT);
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
				
				String[] columnHeaderNames = Constants.AUDIT_4G_FSU_PASSFAIL_SUMMARY_REPORT_COLUMNS;
				for(int i=0; i<columnHeaderNames.length; i++) {
					sheet.setColumnWidth(i, 9000);
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columnHeaderNames[i]);
					cell.setCellStyle(headerCellStyle);
				}
				
				int rowCount = 2;
				for(LinkedHashMap<String, String> audit4GFsuSummaryModel : audit4GFsuSummaryModelList) {
					Row row = sheet.createRow(rowCount++);
					int cellCount = 0;
					Cell cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("testName"));
					
					cell = row.createCell(cellCount++);
					cell.setCellStyle(cellStyle);
					cell.setCellValue(audit4GFsuSummaryModel.get("auditissues"));
					

				}
				String fileName = "AUDIT_4G_FSU_PASSFAIL_SUMMARY_REPORT_" + neName + ".xlsx";
				try (FileOutputStream fileOut = new FileOutputStream(filePath + Constants.SEPARATOR + fileName)) {
					workbook.write(fileOut);
					workbook.close();
					status = true;
				} catch (Exception e) {
					logger.error(ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) {
			logger.error("Exception in createAudit4GSFsuummaryReportExcel()   Audit4GFsuSummaryServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;

		
	}

}
