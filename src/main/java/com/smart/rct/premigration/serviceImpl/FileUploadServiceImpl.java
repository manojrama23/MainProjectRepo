package com.smart.rct.premigration.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.CIQTemplateModel;
import com.smart.rct.common.models.CheckListTemplateColumnsModel;
import com.smart.rct.common.models.CheckListTemplateModel;
import com.smart.rct.common.models.CiqMapValuesModel;
import com.smart.rct.common.models.EnbTemplateModel;
import com.smart.rct.common.models.ErrorDisplayModel;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.common.models.ProgramTemplateColumnsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.models.ValidationDependColumnModel;
import com.smart.rct.common.models.ValidationTemplateColumnModel;
import com.smart.rct.common.models.ValidationTemplateModel;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.CheckListModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.CIQUploadRepository;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.FileUploadService;
import com.smart.rct.premigration.service.GenerateCsvService;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.CommonValidator;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Service
public class FileUploadServiceImpl extends TimerTask implements FileUploadService {

	final static Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);
	@Autowired
	FileUploadRepository objFileUploadRepository;
	DataFormatter dataFormatter = new DataFormatter();

	@Autowired
	CIQUploadRepository ciqUploadRepository;

	@Autowired
	CounterServiceImpl objCounterServiceImpl;

	@Autowired
	GenerateCsvService objGenerateCsvService;

	@Autowired
	CustomerService customerService;
	
	@Autowired
	CommonValidator commonValidator;

	int iMethodCall;
	String time;
	JSONObject CSVObject;

	public FileUploadServiceImpl() {

	}

	public FileUploadServiceImpl(String time, int iMethodCall, JSONObject CSVObject,
			GenerateCsvService objGenerateCsvService) {
		this.time = time;
		this.iMethodCall = iMethodCall;
		this.CSVObject = CSVObject;
		this.objGenerateCsvService = objGenerateCsvService;
	}

	/**
	 * This api ugetEnBDataByPath
	 * 
	 * @param collectionName,
	 *            sheetName, enbId, path
	 * @return String
	 */
	@Override
	public String getEnBDataByPath(String collectionName, String sheetName, String enbId, String path) {
		String enbData = "";
		try {
			enbData = objFileUploadRepository.getEnBDataByPath(collectionName, sheetName, enbId, path);
		} catch (Exception e) {
			logger.error(
					"Exception  getEnBDataByPath() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return enbData;
	}

	/**
	 * This api uploads CIQ
	 * 
	 * @param file,
	 *            uploadPath
	 * @return boolean
	 */
	@Override
	public boolean uploadMultipartFile(MultipartFile file, String uploadPath) throws RctException {
		boolean uploadStatus = false;
		try {
			FileUtil.createDirectory(uploadPath);
			try {
				FileUtil.uploadMultipartFile(file, uploadPath);
				uploadStatus = true;
			} catch (Exception e) {
				uploadStatus = false;
				logger.error("uploadExcel() FileUploadServiceImpl" + ExceptionUtils.getFullStackTrace(e));
				FileUtil.deleteFileOrFolder(uploadPath);
			}

		} catch (Exception e) {
			uploadStatus = false;
			logger.error("Exception  uploadExcel() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return uploadStatus;
	}

	/**
	 * This api deletes CIQ file
	 * 
	 * @param CiqUploadAuditTrailDetModel,
	 * @return boolean
	 */
	public boolean deleteCheckList(CiqUploadAuditTrailDetModel ciqAuditdetails) {
		boolean status = false;
		try {
			status = objFileUploadRepository.deleteCiqDetailsByFilename(CommonUtil.createMongoDbFileNameCheckList(
					String.valueOf(ciqAuditdetails.getProgramDetailsEntity().getId()),
					ciqAuditdetails.getChecklistFileName(), ciqAuditdetails.getCiqFileName()));
		} catch (Exception e) {
			logger.error("Exception  getCiqAuditDetails() in  deleteCheckList:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api deletes CIQ file
	 * 
	 * @param CiqUploadAuditTrailDetModel,
	 * @return boolean
	 */
	public boolean deleteCiq(CiqUploadAuditTrailDetModel ciqAuditdetails) {
		boolean status = false;
		try {
			status = objFileUploadRepository.deleteCiqDetailsByFilename(
					CommonUtil.createMongoDbFileName(String.valueOf(ciqAuditdetails.getProgramDetailsEntity().getId()),
							ciqAuditdetails.getCiqFileName()));
			if (status) {
				status = objFileUploadRepository.deleteCiq(ciqAuditdetails.getId());
			}
		} catch (Exception e) {
			logger.error(
					"Exception  getCiqAuditDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will create CIQ audit
	 * 
	 * @param objCiqUploadAuditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean createCiqAudit(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity) {
		boolean status = false;
		try {

			status = objFileUploadRepository.createCiqAudit(objCiqUploadAuditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception createCiqAudit() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will update CIQ audit
	 * 
	 * @param objCiqUploadAuditTrailEntity
	 * @return boolean
	 */

	@Override
	public boolean updateCiqAuditDetails(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity) {
		boolean status = false;
		try {

			status = objFileUploadRepository.updateCiqAuditDetails(objCiqUploadAuditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception updateCiqAuditDetails() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will return CiqDetails Records Based on count objBodyProcessMap
	 * 
	 * @param fileName,
	 *            page, count
	 * @return Map
	 */
	@Override
	public Map<String, Object> retriveCiqDetails(String fileName, int page, int count) {

		Map<String, Object> objMap = new LinkedHashMap<String, Object>();
		Map<String, List<CIQDetailsModel>> objEnbMap = new LinkedHashMap<>();
		Map<String, List<CIQDetailsModel>> objCountEnbMap = new LinkedHashMap<>();
		List<CIQDetailsModel> ciqDetailsList = null;
		int pageNationCount = 0;
		// int pageNo = (page - 1);
		// long pageNationCount = 0;
		try {
			// for pagination
			// Pageable pageableRequest = PageRequest.of(pageNo, count, new
			// Sort(Sort.Direction.ASC, "id"));
			// Page<CIQDetailsModel> result =
			// ciqUploadRepository.findByFileName(fileName,
			// pageableRequest);
			ciqDetailsList = ciqUploadRepository.findAll(fileName);
			if (ciqDetailsList != null) {
				// ciqList = result.getContent();
				// pageNationCount = result.getTotalPages();
				// Java 8
				LinkedHashSet<String> objendSet = ciqDetailsList.stream().map(X -> X.geteNBName())
						.collect(Collectors.toCollection(LinkedHashSet::new));
				if (objendSet != null && objendSet.size() > 0) {
					int i = 1;
					for (String enbName : objendSet) {
						List<CIQDetailsModel> list = new ArrayList<>();
						for (CIQDetailsModel ciqDetailsModel : ciqDetailsList) {
							if (enbName.equalsIgnoreCase(ciqDetailsModel.geteNBName())) {
								list.add(ciqDetailsModel);
							} else {
								continue;
							}
						}
						objEnbMap.put(enbName, list);
						if (((page - 1) * count) + 1 <= i && i <= ((page) * count)) {
							objCountEnbMap.put(enbName, list);
						}

						i++;

					}

				}

			}

			if (objEnbMap != null && objEnbMap.size() > 0 && count > 0) {
				double totSize = objEnbMap.size();
				double countDe = count;
				pageNationCount = (int) Math.ceil((totSize / countDe));
			}

			objMap.put("ciqDetailsList", objCountEnbMap);
			objMap.put("pageCount", pageNationCount);
		} catch (Exception e) {
			logger.error(
					"Exception retriveCiqDetails() in FileUploadServiceImpl :" + ExceptionUtils.getFullStackTrace(e));
		}

		return objMap;
	}

	/**
	 * This method will return Ciq files list
	 * 
	 * 
	 * @return Set
	 */
	@Override
	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate) {
		List<CiqUploadAuditTrailDetEntity> ciqList = null;
		try {
			ciqList = objFileUploadRepository.getCiqList(user, programId, fromDate, toDate);
		} catch (Exception e) {
			logger.error("Exception getCiqList() in UserDetailsRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return ciqList;
	}

	/**
	 * This method will update CIQ details
	 * 
	 * @param objCIQDetailsModel
	 * @return Map
	 */
	@Override
	public boolean updateCIQDetailsBasedOnId(CIQDetailsModel objCIQDetailsModel) {
		boolean status = false;

		try {

			CIQDetailsModel objnew = ciqUploadRepository.save(objCIQDetailsModel, null);
			if (objnew != null) {
				status = true;
			}

		} catch (Exception e) {
			status = false;
			logger.error("Exception updateCIQDetails() in UserDetailsRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	private String getStringValueOfCell(Cell cell) {
		return dataFormatter.formatCellValue(cell);
	}

	/**
	 * This method will update CIQ File details
	 * 
	 * @param uploadedCiqEntity
	 * @return boolean
	 */
	@Override
	public boolean updateCiqFileDetaiils(CIQDetailsModel upDateCiqEntity,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user) {
		boolean status = false;
		StringBuilder fileName = new StringBuilder();
		try {
			boolean statusOfSave = false;
			boolean statusOfExcel = false;

			List<CIQDetailsModel> objList = ciqUploadRepository.getCiqFileDetails(CommonUtil.createMongoDbFileName(
					String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
					upDateCiqEntity.getFileName()));

			if (objList != null && objList.size() > 0) {

				if (upDateCiqEntity.getId() != null && upDateCiqEntity.getId() > 0) {
					statusOfSave = saveNewCiqDetails(objList, upDateCiqEntity, uploadedCiqAuditEntity);
					if (statusOfSave) {
						statusOfExcel = createTheNewCiqFile(objList, upDateCiqEntity, uploadedCiqAuditEntity);
					}

				} else {
					// objList.add(upDateCiqEntity);
					// for sheet id maintains
					if (StringUtils.isNotEmpty(upDateCiqEntity.getSubSheetName())) {
						List<Integer> sheetIds = objList.stream()
								.filter(x -> upDateCiqEntity.getSheetName().equals(x.getSheetName()))
								.map(x -> x.getSheetId()).collect(Collectors.toList());

						if (sheetIds != null && sheetIds.size() > 0) {
							Integer maxSheetId = Collections.max(sheetIds);
							upDateCiqEntity.setSheetId(maxSheetId + 1);
							objList.add(upDateCiqEntity);
						}
					} else {
						List<Integer> sheetIds = objList.stream()
								.filter(x -> upDateCiqEntity.getSheetName().equals(x.getSheetName()))
								.map(x -> x.getSheetId()).collect(Collectors.toList());

						if (sheetIds != null && sheetIds.size() > 0) {
							Integer maxSheetId = Collections.max(sheetIds);
							upDateCiqEntity.setSheetId(maxSheetId + 1);
							objList.add(upDateCiqEntity);
						}
					}
					statusOfSave = saveNewCiqDetails(objList, upDateCiqEntity, uploadedCiqAuditEntity);
					if (statusOfSave) {
						statusOfExcel = createTheNewCiqFile(objList, upDateCiqEntity, uploadedCiqAuditEntity);
					}
				}

			}

			if (statusOfExcel) {

				if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {
					fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
							Constants.CIQ_FILE_MODIFIED));
				} else {
					fileName.append(uploadedCiqAuditEntity.getCiqFileName());
				}

				CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailDetEntity = objFileUploadRepository
						.getCiqAuditBasedONFileNameAndProgram(fileName.toString(),
								uploadedCiqAuditEntity.getProgramDetailsEntity().getId());
				if (objCiqUploadAuditTrailDetEntity == null) {
					objCiqUploadAuditTrailDetEntity = uploadedCiqAuditEntity;
					objCiqUploadAuditTrailDetEntity.setId(null);
				}
				if (Constants.CIQ_VERSION_ORIGINAL.equals(objCiqUploadAuditTrailDetEntity.getCiqVersion())) {
					objCiqUploadAuditTrailDetEntity.setId(null);
				}
				objCiqUploadAuditTrailDetEntity.setCiqVersion(Constants.CIQ_VERSION_MODIFIED);
				objCiqUploadAuditTrailDetEntity.setCiqFileName(fileName.toString());
				objCiqUploadAuditTrailDetEntity.setCreationDate(new Date());
				objCiqUploadAuditTrailDetEntity.setUploadBy(user.getUserName());
				status = objFileUploadRepository.updateCiqAuditDetails(objCiqUploadAuditTrailDetEntity);
			}
			// status = ciqUploadRepository.updateCiqFileDetails(uploadedCiqEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception updateCiqFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private boolean saveNewCiqDetails(List<CIQDetailsModel> objList, CIQDetailsModel upDateCiqEntity,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity) {
		// TODO Auto-generated method stub

		boolean status = false;
		StringBuilder fileName = new StringBuilder();

		try {
			if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {

				fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
						Constants.CIQ_FILE_MODIFIED));

			} else {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName());
			}
			// delete if already Exist
			objFileUploadRepository.deleteCiqDetailsByFilename(CommonUtil.createMongoDbFileName(
					String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()), fileName.toString()));
			for (CIQDetailsModel objCIQDetailsModelLoc : objList) {
				if (upDateCiqEntity.getId() != null && upDateCiqEntity.getId() > 0) {
					if (upDateCiqEntity.getId().intValue() == objCIQDetailsModelLoc.getId().intValue())
						objCIQDetailsModelLoc = upDateCiqEntity;
				}
				// nee to change extraproperty
				objCIQDetailsModelLoc.setId(objCounterServiceImpl.getNextSequence(CommonUtil.createMongoDbFileName(
						String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
						fileName.toString())));
				objCIQDetailsModelLoc.setFileName(fileName.toString());

				CIQDetailsModel objnew = ciqUploadRepository.save(objCIQDetailsModelLoc,
						CommonUtil.createMongoDbFileName(
								String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
								fileName.toString()));
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception saveNewCiqDetails() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private boolean createTheNewCiqFile(List<CIQDetailsModel> uploadedCiqEntity, CIQDetailsModel upDateCiqEntity,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		Workbook workbook = new XSSFWorkbook();
		StringBuilder fileName = new StringBuilder();
		StringBuilder filePath = new StringBuilder();

		try {
			if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
						Constants.CIQ_FILE_MODIFIED));
			} else {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName());
			}
			filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"));
			filePath.append(uploadedCiqAuditEntity.getCiqFilePath());
			filePath.append(File.separator);
			filePath.append(fileName.toString());
			LinkedHashSet<String> objSheets = uploadedCiqEntity.stream().map(X -> X.getSheetName())
					.collect(Collectors.toCollection(LinkedHashSet::new));
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BROWN.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			// for clone
			if (upDateCiqEntity.getId() == null || upDateCiqEntity.getId() == 0) {
				uploadedCiqEntity.add(upDateCiqEntity);
			}
			for (String SheetName : objSheets) {

				Sheet sheet = workbook.createSheet(SheetName);

				Row headerRow = sheet.createRow(0);

				List<CIQDetailsModel> objListSheetData = uploadedCiqEntity.stream()
						.filter(p -> SheetName.equals(p.getSheetName()))
						.collect(Collectors.toCollection(() -> new ArrayList<CIQDetailsModel>()));

				LinkedHashMap<String, CiqMapValuesModel> objMapSheet = objListSheetData.get(0).getCiqMap();
				String[] columns = objMapSheet.keySet().toArray(new String[objMapSheet.size()]);

				// Create cells
				for (int i = 0; i < columns.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns[i]);
					cell.setCellStyle(headerCellStyle);
				}

				int rowNum = 1;
				for (CIQDetailsModel objCIQDetailsModel : objListSheetData) {

					// for update;

					if (upDateCiqEntity.getId() != null && upDateCiqEntity.getId() > 0
							&& objCIQDetailsModel.getId().intValue() == upDateCiqEntity.getId().intValue()) {
						objCIQDetailsModel = upDateCiqEntity;
					}

					LinkedHashMap<String, CiqMapValuesModel> objCiqsheetLocal = objCIQDetailsModel.getCiqMap();
					Row row = sheet.createRow(rowNum);
					int column = 0;

					for (Map.Entry<String, CiqMapValuesModel> objMaploc : objCiqsheetLocal.entrySet()) {

						row.createCell(column).setCellValue(objMaploc.getValue().getHeaderValue());
						column++;

					}

					rowNum++;

				}

			}

			FileOutputStream fileOut = new FileOutputStream(filePath.toString());
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			status = true;

		} catch (Exception e) {
			logger.error(
					"Exception createTheNewCiqFile() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	/**
	 * This method will create CIQ File details
	 * 
	 * @param createCiqEntity
	 * @return boolean
	 */
	@Override
	public boolean createCiqFileDetaiils(CIQDetailsModel createCiqEntity) {
		boolean status = false;
		try {
			createCiqEntity.setId(objCounterServiceImpl.getNextSequence(createCiqEntity.getFileName()));
			status = ciqUploadRepository.createCiqFileDetaiils(createCiqEntity);
		} catch (Exception e) {
			logger.error("Exception createCiqFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will delete the deleteCiqRowDetails.
	 * 
	 * @param id,fileName
	 * @return boolean
	 */
	@Override
	public boolean deleteCiqRowDetails(Integer id, CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user) {
		boolean status = false;
		StringBuilder fileName = new StringBuilder();
		try {
			boolean statusOfSave = false;
			boolean statusOfExcel = false;

			List<CIQDetailsModel> objList = ciqUploadRepository.getCiqFileDetails(CommonUtil.createMongoDbFileName(
					String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
					uploadedCiqAuditEntity.getCiqFileName()));

			if (objList != null && objList.size() > 0) {

				if (id != null && id > 0) {
					statusOfSave = saveNewDeleteCiqDetails(objList, id, uploadedCiqAuditEntity);
					if (statusOfSave) {
						statusOfExcel = createTheNewDeleteCiqFile(objList, id, uploadedCiqAuditEntity);
					}

				}

			}

			if (statusOfExcel) {

				if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {
					fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
							Constants.CIQ_FILE_MODIFIED));
				} else {
					fileName.append(uploadedCiqAuditEntity.getCiqFileName());
				}

				CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailDetEntity = objFileUploadRepository
						.getCiqAuditBasedONFileNameAndProgram(fileName.toString(),
								uploadedCiqAuditEntity.getProgramDetailsEntity().getId());
				if (objCiqUploadAuditTrailDetEntity == null) {
					objCiqUploadAuditTrailDetEntity = uploadedCiqAuditEntity;
					objCiqUploadAuditTrailDetEntity.setId(null);
				}
				if (Constants.CIQ_VERSION_ORIGINAL.equals(objCiqUploadAuditTrailDetEntity.getCiqVersion())) {
					objCiqUploadAuditTrailDetEntity.setId(null);
				}
				objCiqUploadAuditTrailDetEntity.setCiqVersion(Constants.CIQ_VERSION_MODIFIED);
				objCiqUploadAuditTrailDetEntity.setCiqFileName(fileName.toString());
				objCiqUploadAuditTrailDetEntity.setCreationDate(new Date());
				objCiqUploadAuditTrailDetEntity.setUploadBy(user.getUserName());
				status = objFileUploadRepository.updateCiqAuditDetails(objCiqUploadAuditTrailDetEntity);
			}
			// status = ciqUploadRepository.updateCiqFileDetails(uploadedCiqEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception updateCiqFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private boolean saveNewDeleteCiqDetails(List<CIQDetailsModel> objList, Integer id,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity) {
		// TODO Auto-generated method stub

		boolean status = false;
		StringBuilder fileName = new StringBuilder();

		try {
			if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
						Constants.CIQ_FILE_MODIFIED));
			} else {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName());
			}
			// delete if already Exist
			objFileUploadRepository.deleteCiqDetailsByFilename(CommonUtil.createMongoDbFileName(
					String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()), fileName.toString()));

			for (CIQDetailsModel objCIQDetailsModelLoc : objList) {
				if (objCIQDetailsModelLoc.getId() != null && id > 0) {
					if (objCIQDetailsModelLoc.getId().intValue() == id.intValue())
						continue;
				}
				objCIQDetailsModelLoc.setId(objCounterServiceImpl.getNextSequence(CommonUtil.createMongoDbFileName(
						String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
						fileName.toString())));
				objCIQDetailsModelLoc.setFileName(fileName.toString());
				CIQDetailsModel objnew = ciqUploadRepository.save(objCIQDetailsModelLoc,
						CommonUtil.createMongoDbFileName(
								String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
								fileName.toString()));
			}
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception saveNewCiqDetails() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	private boolean createTheNewDeleteCiqFile(List<CIQDetailsModel> uploadedCiqEntity, Integer id,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		Workbook workbook = new XSSFWorkbook();
		StringBuilder fileName = new StringBuilder();
		StringBuilder filePath = new StringBuilder();

		try {
			if (Constants.CIQ_VERSION_ORIGINAL.equals(uploadedCiqAuditEntity.getCiqVersion())) {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName().replaceAll(Constants.XLXSEXTENTION,
						Constants.CIQ_FILE_MODIFIED));
			} else {
				fileName.append(uploadedCiqAuditEntity.getCiqFileName());
			}
			filePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"));
			filePath.append(uploadedCiqAuditEntity.getCiqFilePath());
			filePath.append(File.separator);
			filePath.append(fileName.toString());
			LinkedHashSet<String> objSheets = uploadedCiqEntity.stream().map(X -> X.getSheetName())
					.collect(Collectors.toCollection(LinkedHashSet::new));
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.BROWN.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			for (String SheetName : objSheets) {

				Sheet sheet = workbook.createSheet(SheetName);

				Row headerRow = sheet.createRow(0);

				List<CIQDetailsModel> objListSheetData = uploadedCiqEntity.stream()
						.filter(p -> SheetName.equals(p.getSheetName()))
						.collect(Collectors.toCollection(() -> new ArrayList<CIQDetailsModel>()));

				LinkedHashMap<String, CiqMapValuesModel> objMapSheet = objListSheetData.get(0).getCiqMap();
				String[] columns = objMapSheet.keySet().toArray(new String[objMapSheet.size()]);

				// Create cells
				for (int i = 0; i < columns.length; i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns[i]);
					cell.setCellStyle(headerCellStyle);
				}

				int rowNum = 1;
				for (CIQDetailsModel objCIQDetailsModel : objListSheetData) {

					if (objCIQDetailsModel.getId() != null && id != null
							&& objCIQDetailsModel.getId().intValue() == id.intValue()) {
						continue;
					}

					LinkedHashMap<String, CiqMapValuesModel> objCiqsheetLocal = objCIQDetailsModel.getCiqMap();
					Row row = sheet.createRow(rowNum);
					int column = 0;

					for (Map.Entry<String, CiqMapValuesModel> objMaploc : objCiqsheetLocal.entrySet()) {

						row.createCell(column).setCellValue(objMaploc.getValue().getHeaderValue());
						column++;

					}

					rowNum++;

				}

			}

			FileOutputStream fileOut = new FileOutputStream(filePath.toString());
			workbook.write(fileOut);
			fileOut.close();
			// Closing the workbook
			workbook.close();
			status = true;

		} catch (Exception e) {
			logger.error(
					"Exception createTheNewCiqFile() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	/**
	 * This api returns enode details
	 * 
	 * @param id,name
	 * @return Map
	 */
	public List<Map<String, String>> getEnbDetails(String id, String fileName, String dbcollectionFileName) {
		List<Map<String, String>> objCiqenodeBEntity = null;

		try {
			objCiqenodeBEntity = objFileUploadRepository.getEnbDetails(id, fileName, dbcollectionFileName);
		} catch (Exception e) {
			logger.error(
					"Exception  getenodebDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqenodeBEntity;

	}

	public List<Map<String, String>> getEnbDetails5G(String id, String fileName, String dbcollectionFileName) {
		List<Map<String, String>> objCiqenodeBEntity = null;

		try {
			objCiqenodeBEntity = objFileUploadRepository.getEnbDetails5G(id, fileName, dbcollectionFileName);
		} catch (Exception e) {
			logger.error(
					"Exception  getenodebDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqenodeBEntity;

	}

	/**
	 * This api returns enodeblist
	 * 
	 * @param enbId,filename
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Object> getEnbTableDetails(String programId, String fileName, String enbId, String enbName,
			String menuName, int page, int count, String dbcollectionFileName) {
		// TODO Auto-generated method stub
		Map<String, Object> objMap = new LinkedHashMap<String, Object>();

		int pageNationCount = 0;
		try {
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(Integer.valueOf(programId));
			programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			programTemplateModel.setLabel(Constants.ENB_MENU_TEMPLATE);
			List<ProgramTemplateEntity> entities = customerService.getProgTemplateDetails(programTemplateModel);
			Map<String, Set<String>> sheetSet = getCiqSheetNamesBasedOnEnb(fileName,
					CommonUtil.createMongoDbFileName(String.valueOf(programId), fileName), enbName, enbId);

			if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
				JSONObject obj = CommonUtil.parseDataToJSON(entities.get(0).getValue());

				ObjectMapper mapper = new ObjectMapper();
				JsonObject objData = CommonUtil.parseRequestDataToJson(entities.get(0).getValue());

				List<EnbTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("ciqMenu").toString(),
						new TypeReference<List<EnbTemplateModel>>() {
						});

				if (myCIQTemplateModel != null && myCIQTemplateModel.size() > 0) {

					EnbTemplateModel objEnbTemplateModel = myCIQTemplateModel.stream().filter(
							X -> menuName.equals(X.getMenuName()) && sheetSet.containsKey(X.getSheetAliasName()))
							.findAny().orElse(null);

					if (objEnbTemplateModel != null) {
						List<CIQDetailsModel> objList = objFileUploadRepository.getEnbTableDetailsRanConfig(fileName,
								enbId, enbName, dbcollectionFileName, objEnbTemplateModel.getSheetAliasName(),
								objEnbTemplateModel.getSubSheetAliasName());

						List<CIQDetailsModel> objListCiq = new ArrayList<>();
						List<CIQDetailsModel> objCountListCiq = new ArrayList<>();

						if (objList != null && objList.size() > 0) {

							for (CIQDetailsModel objCIQDetailsModel : objList) {
								CIQDetailsModel objlatestCIQDetailsModel = new CIQDetailsModel();
								LinkedHashMap<String, CiqMapValuesModel> objDetailsMap = new LinkedHashMap<>();
								LinkedHashMap<String, CiqMapValuesModel> objhashMap = objCIQDetailsModel.getCiqMap();

								List<String> menuList = objEnbTemplateModel.getSubMenu();

								for (String name : menuList) {
									if (objhashMap.get(name) != null)
										objDetailsMap.put(name, objhashMap.get(name));
								}

								if (objDetailsMap != null && objDetailsMap.size() > 0) {
									objlatestCIQDetailsModel = objCIQDetailsModel;
									objlatestCIQDetailsModel.setCiqMap(objDetailsMap);
									objListCiq.add(objlatestCIQDetailsModel);
								}
							}

							if (objListCiq != null && objListCiq.size() > 0) {

								int i = 1;
								for (CIQDetailsModel objlinkedList : objListCiq) {

									if (((page - 1) * count) + 1 <= i && i <= ((page) * count)) {
										objCountListCiq.add(objlinkedList);
									}

									i++;

								}

								if (objListCiq != null && objListCiq.size() > 0 && count > 0) {
									double totSize = objListCiq.size();
									double countDe = count;
									pageNationCount = (int) Math.ceil((totSize / countDe));
								}

							}

						}

						objMap.put("eNodeMapDetails", objCountListCiq);
						objMap.put("pageCount", pageNationCount);
					} else {
						objMap.put("status", Constants.FAIL);
						objMap.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
										+ ": " + Constants.ENB_MENU_TEMPLATE);
					}
				} else {
					objMap.put("status", Constants.FAIL);
					objMap.put("reason",
							GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND) + ": "
									+ Constants.ENB_MENU_TEMPLATE);
				}
			} else {
				objMap.put("status", Constants.FAIL);
				objMap.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FETCH_CIQ_TEMPLATE_NOT_FOUND)
						+ ": " + Constants.ENB_MENU_TEMPLATE);
			}
		} catch (Exception e) {
			logger.error(
					"Exception  getEnbTableDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/**
	 * This api returns getEnbDetailsFilename
	 * 
	 * @param enbId,filename
	 * @return List
	 */
	@Override
	public ArrayList<String> getEnbDetailsFilename(String fileName, String enbId) {

		ArrayList<String> objenbEntity = null;
		try {
			objenbEntity = objFileUploadRepository.getEnbDetailsFilename(fileName, enbId);
		} catch (Exception e) {
			logger.error("Exception  getEnbDetailsByFilename() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objenbEntity;
	}

	/**
	 * This api returns updateCiqFileDetaiilsEndBased
	 * 
	 * @param uploadedCiqEntity,menuName
	 * @return boolean
	 */
	@Override
	public boolean updateCiqFileDetaiilsEndBased(CIQDetailsModel uploadedCiqEntity, String menuName,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user) {
		boolean status = false;
		try {
			String dbCollcetionDbName = CommonUtil.createMongoDbFileName(
					String.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()),
					uploadedCiqEntity.getFileName());
			CIQDetailsModel objCIQDetailsModel = ciqUploadRepository.getEnbTableDetailsById(
					uploadedCiqEntity.getFileName(), uploadedCiqEntity.getId(), dbCollcetionDbName);

			if (objCIQDetailsModel != null) {

				ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
				CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
				programDetailsEntity.setId(Integer.valueOf(uploadedCiqAuditEntity.getProgramDetailsEntity().getId()));
				programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
				programTemplateModel.setLabel(Constants.ENB_MENU_TEMPLATE);
				List<ProgramTemplateEntity> entities = customerService.getProgTemplateDetails(programTemplateModel);

				if (CommonUtil.isValidObject(entities) && entities.size() > 0) {
					JSONObject obj = CommonUtil.parseDataToJSON(entities.get(0).getValue());
					JSONArray menuarray = (JSONArray) obj.get("ciqMenu");
					for (int i = 0; i < menuarray.size(); i++) {
						obj = (JSONObject) menuarray.get(i);
						if (obj.get("menuName").toString().equalsIgnoreCase(menuName)) {
							JSONArray subMenuarray = (JSONArray) obj.get("subMenu");
							for (int j = 0; j < subMenuarray.size(); j++) {
								objCIQDetailsModel.getCiqMap().put(subMenuarray.get(j).toString(),
										uploadedCiqEntity.getCiqMap().get(subMenuarray.get(j).toString()));

							}
						}
					}

					status = updateCiqFileDetaiils(objCIQDetailsModel, uploadedCiqAuditEntity, user);

				} else {
					status = false;
				}

			}

		} catch (Exception e) {
			logger.error("Exception updateCiqFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public void scheduleTime(String time, int iMethodCall, JSONObject CSVObject) {
		try {
			// the Date and time at which you want to execute
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null;
			// date = dateFormatter .parse("2019-01-04 14:38:00");
			date = dateFormatter.parse(time);

			// Now create the time and schedule it
			Timer timer = new Timer();

			// Use this if you want to execute it once
			System.out.println("Above schedule");
			timer.schedule(new FileUploadServiceImpl(time, iMethodCall, CSVObject, objGenerateCsvService), date);
			// timer.schedule(new FileUploadServiceImpl(), date);
			System.out.println("After schedule");

			// Use this if you want to execute it repeatedly
			// int period = 10000;//10secs
			// timer.schedule(new MyTimeTask(), date, period );
		} catch (Exception e) {
			logger.error("Exception scheduleTime() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public void run() {
		try {
			if (iMethodCall == 1) {
				StringBuilder uploadPath = new StringBuilder();
				uploadPath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
						// .append(LoadPropertyFiles.getInstance().getProperty("SMART"))
						.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(CSVObject.get("customerName"))
						.append(File.separator).append(CSVObject.get("networkType")).append(File.separator)
						.append(CSVObject.get("lsmVersion")).append(Constants.PRE_MIGRATION_CSV);

				if (CommonUtil.isValidObject(uploadPath.toString())) {
					File fileExist = new File(uploadPath.toString());
					if (!fileExist.exists()) {
						FileUtil.createDirectory(uploadPath.toString());
					}
					boolean status = objGenerateCsvService.csvFileGeneration(CSVObject.get("fileName").toString(),
							uploadPath.toString(), Integer.parseInt(CSVObject.get("customerId").toString()),
							Integer.parseInt(CSVObject.get("networkTypeId").toString()),
							CSVObject.get("lsmVersion").toString(), CSVObject.get("sessionId").toString());
				}
			} else {
				ObjectMapper objMapper = new ObjectMapper();
				String data = CommonUtil.convertObjectToJson(CSVObject);
				JsonObject objData = CommonUtil.parseRequestDataToJson(data);
				List<GenerateInfoAuditModel> objHbSenderConfigEntityList = objMapper.readValue(
						objData.get("csvAuditDetails").toString(), new TypeReference<List<GenerateInfoAuditModel>>() {
						});
				objGenerateCsvService.transferCiqFile(objHbSenderConfigEntityList,
						Integer.parseInt(CSVObject.get("lsmId").toString()), new JSONObject());
			}
		} catch (Exception ex) {
			logger.error("Exception run() in FileUploadServiceImpl " + ExceptionUtils.getFullStackTrace(ex));
		}
	}

	/**
	 * This api process CIQ
	 * 
	 * @param file,
	 *            uploadPath, isAllowDuplicate
	 * @return Map<String, Object>
	 */

	@Override
	@SuppressWarnings("unused")
	public synchronized Map<String, Object> process5GCiq(MultipartFile file, String uploadPath,
			boolean isAllowDuplicate, Integer programId, String programName) {
		boolean status = false;
		boolean enodeIdStatus = false;
		boolean falsestatus = true;
		Workbook workbook = null;
		Map<String, Object> objResult = new LinkedHashMap<>();
		FileInputStream excelFileInputStream = null;

		LinkedHashMap<String, CiqMapValuesModel> objBodyProcessMap = null;
		ObjectMapper mapper = new ObjectMapper();
		List<CIQDetailsModel> objCIQDetailsModelList = new ArrayList<>();
		String programDetailId = String.valueOf(programId);
		boolean columnsMatchstatus = false;

		long startTime = System.currentTimeMillis();
		long workbookReadTime = System.currentTimeMillis();
		long ciqTemplateValidateTime = System.currentTimeMillis();
		long saveToMongoDBTime = System.currentTimeMillis();

		try {
			ProgramTemplateEntity objProgramTemplateEntity ;
			if(LoadPropertyFiles.getInstance().getProperty("ciqType").equals("OLD") && programName.contains("VZN-4G-USM-LIVE")) {
			 objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.CIQ_VALIDATE_TEMPLATE_OLD);
			}else {
			 objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.CIQ_VALIDATE_TEMPLATE);
			}
			if (objProgramTemplateEntity != null && StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {

				// bala performance
				String dbCollectionName = CommonUtil.createMongoDbFileName(programDetailId, file.getOriginalFilename());
				AtomicBoolean existCollectionDelted = new AtomicBoolean();
				AtomicInteger incrementSequence = new AtomicInteger(1);

				logger.error("FileUploadServiceImpl.processCiq() processing file: " + uploadPath);
				excelFileInputStream = new FileInputStream(uploadPath.toString());
				JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());

				List<CIQTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("sheets").toString(),
						new TypeReference<List<CIQTemplateModel>>() {
						});

				startTime = System.currentTimeMillis();
				workbookReadTime = System.currentTimeMillis();

				if (FilenameUtils.getExtension(uploadPath).equals("xlsx")) {
					workbook = new XSSFWorkbook(excelFileInputStream);
					workbookReadTime = System.currentTimeMillis();
				} else {
					workbook = new HSSFWorkbook(excelFileInputStream);
					workbookReadTime = System.currentTimeMillis();
				}
				logger.info("FileUploadServiceImpl.processCiq() time taken to read Ciq workbook: "
						+ (workbookReadTime - startTime) + "ms");
				LinkedHashMap<String, String> objHeaderProcessMap = null;
				List<LinkedHashMap<String, CiqMapValuesModel>> objProcessList = null;
				if (myCIQTemplateModel != null && myCIQTemplateModel.size() > 0) {

					// AtomicInteger counts = new AtomicInteger();

					sheetWiseDisplayDataLoop:

					for (CIQTemplateModel objLocCIQTemplateModel : myCIQTemplateModel) {
						String enbIdHeaderName = null;
						String enbNameHeaderName = null;
						objHeaderProcessMap = new LinkedHashMap<>();
						objProcessList = new ArrayList<>();
						if (StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetName())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getHeaderRow())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetType())) {

							int headerRowNo = 0;
							int dataRowNo = 0;
							if(programName.contains("5G-CBAND")) {
								dataRowNo = Integer.parseInt(objData.get("dataRow").toString());
							}
							Sheet datatypeSheet;
							if(objLocCIQTemplateModel.getSheetName().equals("Upstate NY CIQ"))
								datatypeSheet = workbook.getSheet(workbook.getSheetName(0));
							else
								datatypeSheet = workbook.getSheet(objLocCIQTemplateModel.getSheetName());
							AtomicBoolean fsuCiqStatus = new AtomicBoolean();
							if (datatypeSheet != null) {
								if (StringUtils.isNumeric(objLocCIQTemplateModel.getHeaderRow())) {
									headerRowNo = Integer.parseInt(objLocCIQTemplateModel.getHeaderRow());
								} else {
									Iterator<Row> rowIterator = datatypeSheet.iterator();
									while (rowIterator.hasNext()) {
										Row row = rowIterator.next();
										Cell headercell = row.getCell(0);
										if (headercell != null && (String) getCellValue(headercell) != null && objLocCIQTemplateModel.getHeaderRow()
												.replaceAll("_", "").replaceAll("\\s", "")
												.equalsIgnoreCase((((String) getCellValue(headercell))).trim()
														.replaceAll("_", "").replaceAll("\\s", ""))) {
											headerRowNo = headercell.getRowIndex() + 1;
											objLocCIQTemplateModel.setHeaderRow(Integer.toString(headerRowNo));
											break;
										}

									}
								}

								if ("normal".equals(objLocCIQTemplateModel.getSheetType())) {

									boolean enbidboo = false;
									boolean enbnameboo = false;
									Row headerRow = datatypeSheet.getRow(headerRowNo - 1);
									for (Cell headercell : headerRow) {
										for (ProgramTemplateColumnsModel li : objLocCIQTemplateModel.getColumns()) {
											if ((String) getCellValue(headercell) != null && li.getColumnName().replaceAll("_", "").replaceAll("\\s", "")
													.equalsIgnoreCase((((String) getCellValue(headercell))).trim()
															.replaceAll("_", "").replaceAll("\\s", ""))
													&& li.getColumnHeaderName().equals("")) {
												// System.out.println("Matching");
												li.setColumnHeaderName(
														headercell.getAddress().toString().replaceAll("[0-9]", ""));
												break;
											}
										}

										if ((String) getCellValue(headercell) != null && objLocCIQTemplateModel.getEnbIdColumnHeaderName()!=null &&(((String) getCellValue(headercell))).trim().contains(
												objLocCIQTemplateModel.getEnbIdColumnHeaderName()) && !enbidboo) {
											System.out.println("EnbId : " + (String) getCellValue(headercell) + " "
													+ objLocCIQTemplateModel.getEnbIdColumnHeaderName());
											objLocCIQTemplateModel.setEnbIdColumnHeaderName(
													headercell.getAddress().toString().replaceAll("[0-9]", ""));
											enbidboo = true;
										}
										if ((String) getCellValue(headercell) != null && objLocCIQTemplateModel.getEnbNameColumnHeaderName() != null
												&& (((String) getCellValue(headercell))).trim()
														.contains(objLocCIQTemplateModel.getEnbNameColumnHeaderName())
												&& !enbnameboo) {
											System.out.println("EnbName: " + (String) getCellValue(headercell) + "  "
													+ objLocCIQTemplateModel.getEnbNameColumnHeaderName());
											objLocCIQTemplateModel.setEnbNameColumnHeaderName(
													headercell.getAddress().toString().replaceAll("[0-9]", ""));
											enbnameboo = true;
										}

									}
									// ArrayList<ProgramTemplateColumnsModel> notThere = new ArrayList<>();
									for (int i = objLocCIQTemplateModel.getColumns().size() - 1; i >= 0; i--) {
										System.out.println(
												objLocCIQTemplateModel.getColumns().get(i).getColumnHeaderName() + " "
														+ objLocCIQTemplateModel.getColumns().get(i).getColumnName());
										if (objLocCIQTemplateModel.getColumns().get(i).getColumnHeaderName()
												.equals("")) {
											System.out.println("Not There" + i);
											objLocCIQTemplateModel.getColumns().remove(i);
										}

									}

									System.out.println(objLocCIQTemplateModel.getEnbIdColumnHeaderName() + " "
											+ objLocCIQTemplateModel.getEnbNameColumnHeaderName());
									List<ProgramTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										/*
										 * Map<String, String> mapColumns = objListColumnsModel.stream()
										 * .collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
										 * ProgramTemplateColumnsModel::getColumnHeaderName));
										 */

										Map<String, String> mapColumns2 = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnName));

										Map<String, String> mapColumnsHeaderAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										if (mapColumns2 != null && mapColumns2.size() > 0) {

											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();

												// Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();
												for (int cn = 0; cn < row.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
													/* while (cellIterator.hasNext()) { */
													if ((String) getCellValue(currentCell) != null && StringUtils.isNotEmpty(
															objLocCIQTemplateModel.getEnbIdColumnHeaderName())
															&& currentCell.getAddress().toString()
																	.replaceAll("[0-9]", "")
																	.equals(objLocCIQTemplateModel
																			.getEnbIdColumnHeaderName())) {
														if (currentCell.getCellType() == Cell.CELL_TYPE_BLANK
																|| ((((String) getCellValue(currentCell)).trim())
																		.equalsIgnoreCase("Site Specific"))
																|| ((((String) getCellValue(currentCell)).trim())
																		.equalsIgnoreCase("RF"))
																|| ((((String) getCellValue(currentCell)).trim())
																		.equalsIgnoreCase("0"))) {
															continue stopReadingIfRowIsBlank;
														}
													}
													// Cell currentCell = cellIterator.next();
													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils.isNotEmpty(
																((String) getCellValue(currentCell)).trim())) {

															if (mapColumns2.containsKey(data)) {

																if (((String) getCellValue(currentCell)).trim()
																		.replaceAll("_", "").replaceAll("\\s", "")
																		.equalsIgnoreCase(mapColumns2.get(data)
																				.replaceAll("_", "")
																				.replaceAll("\\s", ""))) {

																	// enb Id

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())) {

																		/*
																		 * enbIdHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */

																		enbIdHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	// enb Name

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())) {
																		/*
																		 * enbNameHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */

																		enbNameHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	objHeaderProcessMap.put(colIndex,
																			((String) getCellValue(currentCell)).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																} else if ("FSUCIQ".equalsIgnoreCase(
																		objLocCIQTemplateModel.getSheetAliasName())
																		&& !fsuCiqStatus.get()) {
																	headerRowNo = headerRowNo + 1;
																	objLocCIQTemplateModel
																			.setHeaderRow(String.valueOf(headerRowNo));
																	fsuCiqStatus.getAndSet(true);
																	headerRowStatus = false;
																	continue stopReadingIfRowIsBlank;

																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason", ((String) getCellValue(
																			currentCell)).trim()
																			+ " Column Not Matched With Program Template");

																	// break sheetWiseDisplayDataLoop;
																	return objResult;

																}
															}

														}
													} else if (headerRowStatus) {
														if(programName.contains("5G-CBAND") && dataRowNo - 1 > row.getRowNum()) {
															continue stopReadingIfRowIsBlank;
														}

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {

															CiqMapValuesModel objNewCiqMapValuesModel = new CiqMapValuesModel();

															objNewCiqMapValuesModel.setHeaderValue(
																	((String) getCellValue(currentCell)).trim());
															objNewCiqMapValuesModel
																	.setHeaderName(objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()));

															objBodyProcessMap.put(
																	mapColumnsHeaderAliasName.get(colIndexBody),
																	objNewCiqMapValuesModel);

															if (StringUtils.isNotEmpty(
																	((String) getCellValue(currentCell)).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														break stopReadingIfRowIsBlank;
													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													int sheetId = 1;
													for (LinkedHashMap<String, CiqMapValuesModel> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CIQDetailsModel ciqDetailsModel = new CIQDetailsModel();
															if (ciqRecord.containsKey(enbIdHeaderName)) {

																String tempEnbid = ciqRecord.get(enbIdHeaderName)
																		.getHeaderValue().trim();

																if (programName.contains("VZN-5G-MM") || programName.contains("VZN-5G-DSS")
																		|| programName.contains("5G-CBAND")) {
																	if (StringUtils.isNotEmpty(tempEnbid)
																			&& tempEnbid.length() > 0)
																		tempEnbid = tempEnbid.replaceAll("^0+(?!$)", "");
																}

																ciqDetailsModel.seteNBId(tempEnbid);

															}
															if (ciqRecord.containsKey(enbNameHeaderName)) {
																String tempEnbname = ciqRecord.get(enbNameHeaderName)
																		.getHeaderValue().trim();
																if (programName.contains("VZN-5G-MM") || programName.contains("VZN-5G-DSS")
																		|| programName.contains("5G-CBAND")) {
																	if (StringUtils.isNotEmpty(tempEnbname)
																			&& tempEnbname.length() > 0)
																		tempEnbname = tempEnbname.replaceAll("^0+(?!$)", "");
																}
																ciqDetailsModel.seteNBName(tempEnbname);
															}
															if (ciqRecord.containsKey("VZW Site Name")) {
																ciqDetailsModel.setSiteName(ciqRecord
																		.get("VZW Site Name").getHeaderValue());
															}
															ciqDetailsModel.setCiqMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setSubSheetAliasName(
																	objLocCIQTemplateModel.getSubSheetAliasName());
															ciqDetailsModel.setSheetAliasName(
																	objLocCIQTemplateModel.getSheetAliasName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSheetId(sheetId);
															objCIQDetailsModelList.add(ciqDetailsModel);

															if (!columnsMatchstatus) {

																if (isAllowDuplicate && !existCollectionDelted.get()) {
																	// objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename());
																	objFileUploadRepository.deleteCiqDetailsByFilename(
																			dbCollectionName);
																	existCollectionDelted.getAndSet(true);
																}

																ciqDetailsModel.setId(incrementSequence.get());
																CIQDetailsModel objnew = ciqUploadRepository
																		.save(ciqDetailsModel, dbCollectionName);
																incrementSequence.getAndIncrement();
																if (objnew != null) {
																	status = true;
																} else {
																	falsestatus = false;
																	objResult.put("status", falsestatus);
																	objResult.put("reason",
																			GlobalInitializerListener.faultCodeMap.get(
																					FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
																	return objResult;

																}

															}
															sheetId++;

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));
												return objResult;

											}
											// counts.getAndIncrement();

										}

									}

								} else if ("multiple".equals(objLocCIQTemplateModel.getSheetType())
										&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getReadingRange())) {

									String[] readingRange = objLocCIQTemplateModel.getReadingRange().split("-");
									int from = Integer.parseInt(readingRange[0]);
									int limit = Integer.parseInt(readingRange[1]);

									List<Integer> rowsConsiderList = inclusiveRange(from - 1, limit - 1);

									List<ProgramTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										/*
										 * Map<String, String> mapColumns = objListColumnsModel.stream()
										 * .collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
										 * ProgramTemplateColumnsModel::getColumnHeaderName));
										 */
										Map<String, String> mapColumns2 = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnName));
										Map<String, String> mapColumnsAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										Map<String, String> mapColumnsHeaderAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										if (mapColumns2 != null && mapColumns2.size() > 0) {

											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();
												// Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();
												for (int cn = 0; cn < row.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
													/*
													 * while (cellIterator.hasNext()) {
													 * 
													 * Cell currentCell = cellIterator.next();
													 */
													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils
																.isNotEmpty(getStringValueOfCell(currentCell).trim())) {

															if (mapColumns2.containsKey(data)) {

																if (getStringValueOfCell(currentCell).trim()
																		.equals(mapColumns2.get(data))) {

																	// enb Id

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())) {
																		/*
																		 * enbIdHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */
																		enbIdHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	// enb Name

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())) {
																		/*
																		 * enbNameHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */
																		enbNameHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}
																	objHeaderProcessMap.put(colIndex,
																			getStringValueOfCell(currentCell).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																} else if ("FSUCIQ".equalsIgnoreCase(
																		objLocCIQTemplateModel.getSheetAliasName())
																		&& !fsuCiqStatus.get()) {

																	headerRowNo = headerRowNo + 1;
																	objLocCIQTemplateModel
																			.setHeaderRow(String.valueOf(headerRowNo));
																	fsuCiqStatus.getAndSet(true);
																	headerRowStatus = false;
																	continue stopReadingIfRowIsBlank;

																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason", getStringValueOfCell(
																			currentCell).trim()
																			+ " Columns Not Matched With Program Template");

																	// break sheetWiseDisplayDataLoop;
																	return objResult;

																}
															}

														}
													} else if (headerRowStatus
															&& rowsConsiderList.contains(row.getRowNum())) {

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {

															CiqMapValuesModel objNewCiqMapValuesModel = new CiqMapValuesModel();

															objNewCiqMapValuesModel.setHeaderValue(
																	getStringValueOfCell(currentCell).trim());
															objNewCiqMapValuesModel
																	.setHeaderName(objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()));

															objBodyProcessMap.put(
																	mapColumnsHeaderAliasName.get(colIndexBody),
																	objNewCiqMapValuesModel);

															if (StringUtils.isNotEmpty(
																	getStringValueOfCell(currentCell).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}

														/*
														 * String colIndexBody = currentCell.getAddress().toString()
														 * .replaceAll("[0-9]", ""); if (objHeaderProcessMap.size() > 0
														 * && objHeaderProcessMap.containsKey(colIndexBody +
														 * objLocCIQTemplateModel.getHeaderRow())) {
														 * objBodyProcessMap.put( objHeaderProcessMap.get(colIndexBody +
														 * objLocCIQTemplateModel.getHeaderRow()),
														 * getStringValueOfCell(currentCell).trim());
														 * 
														 * if (StringUtils.isNotEmpty(
														 * getStringValueOfCell(currentCell).trim())) {
														 * objAtomicBoolean.getAndSet(true); } }
														 */
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														break stopReadingIfRowIsBlank;
													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													int sheetId = 1;
													for (LinkedHashMap<String, CiqMapValuesModel> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CIQDetailsModel ciqDetailsModel = new CIQDetailsModel();

															if (ciqRecord.containsKey(enbIdHeaderName)
																	&& ciqRecord.containsKey(enbNameHeaderName)) {

																ciqRecord.get(enbIdHeaderName).getHeaderValue();
																ciqDetailsModel.seteNBId(ciqRecord.get(enbIdHeaderName)
																		.getHeaderValue());
																ciqDetailsModel.seteNBName(ciqRecord
																		.get(enbNameHeaderName).getHeaderValue());
															}

															ciqDetailsModel.setCiqMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setSubSheetAliasName(
																	objLocCIQTemplateModel.getSubSheetAliasName());
															ciqDetailsModel.setSheetAliasName(
																	objLocCIQTemplateModel.getSheetAliasName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSheetId(sheetId);
															objCIQDetailsModelList.add(ciqDetailsModel);

															if (!columnsMatchstatus) {

																/*
																 * String dbCollectionName =
																 * CommonUtil.createMongoDbFileName(programDetailId,
																 * file.getOriginalFilename());
																 */
																if (isAllowDuplicate && !existCollectionDelted.get()) {
																	// objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename());
																	objFileUploadRepository.deleteCiqDetailsByFilename(
																			dbCollectionName);
																	existCollectionDelted.getAndSet(true);
																}

																ciqDetailsModel.setId(incrementSequence.get());
																CIQDetailsModel objnew = ciqUploadRepository
																		.save(ciqDetailsModel, dbCollectionName);
																incrementSequence.getAndIncrement();
																if (objnew != null) {
																	status = true;
																} else {
																	falsestatus = false;
																	objResult.put("status", falsestatus);
																	objResult.put("reason",
																			GlobalInitializerListener.faultCodeMap.get(
																					FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
																	return objResult;

																}

															}

															sheetId++;

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));

											}
											// counts.getAndIncrement();

										}

									}
								}

							} else {
								if(status) {
									continue;
								}
								objResult.put("status", status);
								objResult.put("reason", "Sheet Names Not Matched With Program Template");

								return objResult;
							}
						}

					}
					ciqTemplateValidateTime = System.currentTimeMillis();
					logger.info("FileUploadServiceImpl.processCiq() time taken to Ciq with Template: "
							+ (ciqTemplateValidateTime - workbookReadTime) + "ms");
					/*
					 * if (objCIQDetailsModelList.size() > 0 && !columnsMatchstatus) {
					 * 
					 * String dbCollectionName = CommonUtil.createMongoDbFileName(programDetailId,
					 * file.getOriginalFilename()); if (isAllowDuplicate) { //
					 * objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename()
					 * ); objFileUploadRepository.deleteCiqDetailsByFilename(dbCollectionName); }
					 * for (CIQDetailsModel objCIQDetailsModelLoc : objCIQDetailsModelList) {
					 * objCIQDetailsModelLoc.setId(objCounterServiceImpl.getNextSequence(
					 * dbCollectionName)); CIQDetailsModel objnew =
					 * ciqUploadRepository.save(objCIQDetailsModelLoc, dbCollectionName); if (objnew
					 * != null) { status = true; } else { falsestatus = false;
					 * 
					 * } } saveToMongoDBTime = System.currentTimeMillis(); logger.
					 * info("FileUploadServiceImpl.processCiq() time taken to dump Ciq Data into MongoDB: "
					 * + (saveToMongoDBTime-ciqTemplateValidateTime)+"ms");
					 * 
					 * }
					 */

					if (status && falsestatus) {
						status = true;
						objResult.put("status", status);
					} else {
						objResult.put("status", status);
						objResult.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
					}

				}

			} else {
				objResult.put("status", status);
				objResult.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROVIDE_PROGRAM_TEMPLATE_DETAILS));
				return objResult;
			}

		}
		catch (NotOfficeXmlFileException e) {
			status=false;
			objResult.put("status", status);
			objResult.put("reason", "CIQ is encrypted we can not process");
			logger.error(
			"Exception  processExcelData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
			}
		catch (Exception e) {
			objResult.put("status", status);
			objResult.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
			logger.error(
					"Exception  processExcelData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return objResult;
	}

	@Override
	@SuppressWarnings("unused")
	public synchronized Map<String, Object> processCiq(MultipartFile file, String uploadPath, boolean isAllowDuplicate,
			Integer programId) {
		boolean status = false;
		boolean enodeIdStatus = false;
		boolean falsestatus = true;
		Workbook workbook = null;
		Map<String, Object> objResult = new LinkedHashMap<>();
		FileInputStream excelFileInputStream = null;

		LinkedHashMap<String, CiqMapValuesModel> objBodyProcessMap = null;
		ObjectMapper mapper = new ObjectMapper();
		List<CIQDetailsModel> objCIQDetailsModelList = new ArrayList<>();
		String programDetailId = String.valueOf(programId);
		boolean columnsMatchstatus = false;

		long startTime = System.currentTimeMillis();
		long workbookReadTime = System.currentTimeMillis();
		long ciqTemplateValidateTime = System.currentTimeMillis();
		long saveToMongoDBTime = System.currentTimeMillis();

		try {

			String sheetWorkbook = "";
			ProgramTemplateEntity objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.CIQ_VALIDATE_TEMPLATE);
			if (objProgramTemplateEntity != null && StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {

				// bala performance
				String dbCollectionName = CommonUtil.createMongoDbFileName(programDetailId, file.getOriginalFilename());
				AtomicBoolean existCollectionDelted = new AtomicBoolean();
				AtomicInteger incrementSequence = new AtomicInteger(1);

				logger.info("FileUploadServiceImpl.processCiq() processing file: " + uploadPath);
				excelFileInputStream = new FileInputStream(uploadPath.toString());
				JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());

				List<CIQTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("sheets").toString(),
						new TypeReference<List<CIQTemplateModel>>() {
						});

				startTime = System.currentTimeMillis();
				workbookReadTime = System.currentTimeMillis();

				if (FilenameUtils.getExtension(uploadPath).equals("xlsx")) {
					workbook = new XSSFWorkbook(excelFileInputStream);
					sheetWorkbook = workbook.getSheetName(0);
					workbookReadTime = System.currentTimeMillis();
				} else {
					workbook = new HSSFWorkbook(excelFileInputStream);
					workbookReadTime = System.currentTimeMillis();
				}
				for (CIQTemplateModel ciqtemplate : myCIQTemplateModel) {
					if (ciqtemplate.getSheetName().equals("Upstate NY CIQ")) {
						if (!sheetWorkbook.equals("Upstate NY CIQ")) {
							ciqtemplate.setSheetName(sheetWorkbook);
						}
					}
				}
				logger.info("FileUploadServiceImpl.processCiq() time taken to read Ciq workbook: "
						+ (workbookReadTime - startTime) + "ms");
				LinkedHashMap<String, String> objHeaderProcessMap = null;
				List<LinkedHashMap<String, CiqMapValuesModel>> objProcessList = null;
				if (myCIQTemplateModel != null && myCIQTemplateModel.size() > 0) {

					// AtomicInteger counts = new AtomicInteger();

					sheetWiseDisplayDataLoop:

					for (CIQTemplateModel objLocCIQTemplateModel : myCIQTemplateModel) {
						String enbIdHeaderName = null;
						String enbNameHeaderName = null;
						objHeaderProcessMap = new LinkedHashMap<>();
						objProcessList = new ArrayList<>();
						if (StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetName())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getHeaderRow())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetType())) {

							int headerRowNo = Integer.parseInt(objLocCIQTemplateModel.getHeaderRow());

							Sheet datatypeSheet = workbook.getSheet(objLocCIQTemplateModel.getSheetName());
							AtomicBoolean fsuCiqStatus = new AtomicBoolean();
							if (datatypeSheet != null) {

								if ("normal".equals(objLocCIQTemplateModel.getSheetType())) {

									List<ProgramTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										/*
										 * Map<String, String> mapColumns = objListColumnsModel.stream()
										 * .collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
										 * ProgramTemplateColumnsModel::getColumnHeaderName));
										 */

										Map<String, String> mapColumns2 = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnName));

										Map<String, String> mapColumnsAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										Map<String, String> mapColumnsHeaderAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										if (mapColumns2 != null && mapColumns2.size() > 0) {

											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();
												// Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();
												for (int cn = 0; cn < row.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
													/* while (cellIterator.hasNext()) { */

													// Cell currentCell = cellIterator.next();
													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils.isNotEmpty(
																((String) getCellValue(currentCell)).trim())) {

															if (mapColumns2.containsKey(data)) {

																if (((String) getCellValue(currentCell)).trim()
																		.equals(mapColumns2.get(data))) {

																	// enb Id

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())) {

																		/*
																		 * enbIdHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */

																		enbIdHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	// enb Name

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())) {
																		/*
																		 * enbNameHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */

																		enbNameHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	objHeaderProcessMap.put(colIndex,
																			((String) getCellValue(currentCell)).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																} else if ("FSUCIQ".equalsIgnoreCase(
																		objLocCIQTemplateModel.getSheetAliasName())
																		&& !fsuCiqStatus.get()) {
																	headerRowNo = headerRowNo + 1;
																	objLocCIQTemplateModel
																			.setHeaderRow(String.valueOf(headerRowNo));
																	fsuCiqStatus.getAndSet(true);
																	headerRowStatus = false;
																	continue stopReadingIfRowIsBlank;

																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason", ((String) getCellValue(
																			currentCell)).trim()
																			+ " Column Not Matched With Program Template");

																	// break sheetWiseDisplayDataLoop;
																	return objResult;

																}
															}

														}
													} else if (headerRowStatus) {

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {

															CiqMapValuesModel objNewCiqMapValuesModel = new CiqMapValuesModel();

															objNewCiqMapValuesModel.setHeaderValue(
																	((String) getCellValue(currentCell)).trim());
															objNewCiqMapValuesModel
																	.setHeaderName(objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()));

															objBodyProcessMap.put(
																	mapColumnsHeaderAliasName.get(colIndexBody),
																	objNewCiqMapValuesModel);

															if (StringUtils.isNotEmpty(
																	((String) getCellValue(currentCell)).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														break stopReadingIfRowIsBlank;
													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													int sheetId = 1;
													for (LinkedHashMap<String, CiqMapValuesModel> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CIQDetailsModel ciqDetailsModel = new CIQDetailsModel();
															if (ciqRecord.containsKey(enbIdHeaderName)
																	&& ciqRecord.containsKey(enbNameHeaderName)) {

																ciqRecord.get(enbIdHeaderName).getHeaderValue();
																ciqDetailsModel.seteNBId(ciqRecord.get(enbIdHeaderName)
																		.getHeaderValue());
																ciqDetailsModel.seteNBName(ciqRecord
																		.get(enbNameHeaderName).getHeaderValue());
															}

															ciqDetailsModel.setCiqMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setSubSheetAliasName(
																	objLocCIQTemplateModel.getSubSheetAliasName());
															ciqDetailsModel.setSheetAliasName(
																	objLocCIQTemplateModel.getSheetAliasName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSheetId(sheetId);
															objCIQDetailsModelList.add(ciqDetailsModel);

															if (!columnsMatchstatus) {

																if (isAllowDuplicate && !existCollectionDelted.get()) {
																	// objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename());
																	objFileUploadRepository.deleteCiqDetailsByFilename(
																			dbCollectionName);
																	existCollectionDelted.getAndSet(true);
																}

																ciqDetailsModel.setId(incrementSequence.get());
																CIQDetailsModel objnew = ciqUploadRepository
																		.save(ciqDetailsModel, dbCollectionName);
																incrementSequence.getAndIncrement();
																if (objnew != null) {
																	status = true;
																} else {
																	falsestatus = false;
																	objResult.put("status", falsestatus);
																	objResult.put("reason",
																			GlobalInitializerListener.faultCodeMap.get(
																					FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
																	return objResult;

																}

															}
															sheetId++;

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));
												return objResult;

											}
											// counts.getAndIncrement();

										}

									}

								} else if ("multiple".equals(objLocCIQTemplateModel.getSheetType())
										&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getReadingRange())) {

									String[] readingRange = objLocCIQTemplateModel.getReadingRange().split("-");
									int from = Integer.parseInt(readingRange[0]);
									int limit = Integer.parseInt(readingRange[1]);

									List<Integer> rowsConsiderList = inclusiveRange(from - 1, limit - 1);

									List<ProgramTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										/*
										 * Map<String, String> mapColumns = objListColumnsModel.stream()
										 * .collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
										 * ProgramTemplateColumnsModel::getColumnHeaderName));
										 */
										Map<String, String> mapColumns2 = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnName));
										Map<String, String> mapColumnsAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(ProgramTemplateColumnsModel::getColumnName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										Map<String, String> mapColumnsHeaderAliasName = objListColumnsModel.stream()
												.collect(Collectors.toMap(
														ProgramTemplateColumnsModel::getColumnHeaderName,
														ProgramTemplateColumnsModel::getColumnAliasName));

										if (mapColumns2 != null && mapColumns2.size() > 0) {

											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();
												// Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();
												for (int cn = 0; cn < row.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);
													/*
													 * while (cellIterator.hasNext()) {
													 * 
													 * Cell currentCell = cellIterator.next();
													 */
													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils
																.isNotEmpty(getStringValueOfCell(currentCell).trim())) {

															if (mapColumns2.containsKey(data)) {

																if (getStringValueOfCell(currentCell).trim()
																		.equals(mapColumns2.get(data))) {

																	// enb Id

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbIdColumnHeaderName())) {
																		/*
																		 * enbIdHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */
																		enbIdHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}

																	// enb Name

																	if (StringUtils
																			.isNotEmpty(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())
																			&& data.equals(objLocCIQTemplateModel
																					.getEnbNameColumnHeaderName())) {
																		/*
																		 * enbNameHeaderName = getStringValueOfCell(
																		 * currentCell).trim().replace(".", "*")
																		 * .replace(" ", "_");
																		 */
																		enbNameHeaderName = mapColumnsHeaderAliasName
																				.get(data);

																	}
																	objHeaderProcessMap.put(colIndex,
																			getStringValueOfCell(currentCell).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																} else if ("FSUCIQ".equalsIgnoreCase(
																		objLocCIQTemplateModel.getSheetAliasName())
																		&& !fsuCiqStatus.get()) {

																	headerRowNo = headerRowNo + 1;
																	objLocCIQTemplateModel
																			.setHeaderRow(String.valueOf(headerRowNo));
																	fsuCiqStatus.getAndSet(true);
																	headerRowStatus = false;
																	continue stopReadingIfRowIsBlank;

																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason", getStringValueOfCell(
																			currentCell).trim()
																			+ " Columns Not Matched With Program Template");

																	// break sheetWiseDisplayDataLoop;
																	return objResult;

																}
															}

														}
													} else if (headerRowStatus
															&& rowsConsiderList.contains(row.getRowNum())) {

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {

															CiqMapValuesModel objNewCiqMapValuesModel = new CiqMapValuesModel();

															objNewCiqMapValuesModel.setHeaderValue(
																	getStringValueOfCell(currentCell).trim());
															objNewCiqMapValuesModel
																	.setHeaderName(objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()));

															objBodyProcessMap.put(
																	mapColumnsHeaderAliasName.get(colIndexBody),
																	objNewCiqMapValuesModel);

															if (StringUtils.isNotEmpty(
																	getStringValueOfCell(currentCell).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}

														/*
														 * String colIndexBody = currentCell.getAddress().toString()
														 * .replaceAll("[0-9]", ""); if (objHeaderProcessMap.size() > 0
														 * && objHeaderProcessMap.containsKey(colIndexBody +
														 * objLocCIQTemplateModel.getHeaderRow())) {
														 * objBodyProcessMap.put( objHeaderProcessMap.get(colIndexBody +
														 * objLocCIQTemplateModel.getHeaderRow()),
														 * getStringValueOfCell(currentCell).trim());
														 * 
														 * if (StringUtils.isNotEmpty(
														 * getStringValueOfCell(currentCell).trim())) {
														 * objAtomicBoolean.getAndSet(true); } }
														 */
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														break stopReadingIfRowIsBlank;
													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													int sheetId = 1;
													for (LinkedHashMap<String, CiqMapValuesModel> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CIQDetailsModel ciqDetailsModel = new CIQDetailsModel();

															if (ciqRecord.containsKey(enbIdHeaderName)
																	&& ciqRecord.containsKey(enbNameHeaderName)) {

																ciqRecord.get(enbIdHeaderName).getHeaderValue();
																ciqDetailsModel.seteNBId(ciqRecord.get(enbIdHeaderName)
																		.getHeaderValue());
																ciqDetailsModel.seteNBName(ciqRecord
																		.get(enbNameHeaderName).getHeaderValue());
															}

															ciqDetailsModel.setCiqMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setSubSheetAliasName(
																	objLocCIQTemplateModel.getSubSheetAliasName());
															ciqDetailsModel.setSheetAliasName(
																	objLocCIQTemplateModel.getSheetAliasName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSheetId(sheetId);
															objCIQDetailsModelList.add(ciqDetailsModel);

															if (!columnsMatchstatus) {

																/*
																 * String dbCollectionName =
																 * CommonUtil.createMongoDbFileName(programDetailId,
																 * file.getOriginalFilename());
																 */
																if (isAllowDuplicate && !existCollectionDelted.get()) {
																	// objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename());
																	objFileUploadRepository.deleteCiqDetailsByFilename(
																			dbCollectionName);
																	existCollectionDelted.getAndSet(true);
																}

																ciqDetailsModel.setId(incrementSequence.get());
																CIQDetailsModel objnew = ciqUploadRepository
																		.save(ciqDetailsModel, dbCollectionName);
																incrementSequence.getAndIncrement();
																if (objnew != null) {
																	status = true;
																} else {
																	falsestatus = false;
																	objResult.put("status", falsestatus);
																	objResult.put("reason",
																			GlobalInitializerListener.faultCodeMap.get(
																					FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
																	return objResult;

																}

															}

															sheetId++;

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));

											}
											// counts.getAndIncrement();

										}

									}
								}

							} /*
								 * else { objResult.put("status", status); objResult.put("reason",
								 * "Sheet Names Not Matched With Program Template");
								 * 
								 * return objResult; }
								 */
						}

					}
					ciqTemplateValidateTime = System.currentTimeMillis();
					logger.info("FileUploadServiceImpl.processCiq() time taken to Ciq with Template: "
							+ (ciqTemplateValidateTime - workbookReadTime) + "ms");
					/*
					 * if (objCIQDetailsModelList.size() > 0 && !columnsMatchstatus) {
					 * 
					 * String dbCollectionName = CommonUtil.createMongoDbFileName(programDetailId,
					 * file.getOriginalFilename()); if (isAllowDuplicate) { //
					 * objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename()
					 * ); objFileUploadRepository.deleteCiqDetailsByFilename(dbCollectionName); }
					 * for (CIQDetailsModel objCIQDetailsModelLoc : objCIQDetailsModelList) {
					 * objCIQDetailsModelLoc.setId(objCounterServiceImpl.getNextSequence(
					 * dbCollectionName)); CIQDetailsModel objnew =
					 * ciqUploadRepository.save(objCIQDetailsModelLoc, dbCollectionName); if (objnew
					 * != null) { status = true; } else { falsestatus = false;
					 * 
					 * } } saveToMongoDBTime = System.currentTimeMillis(); logger.
					 * info("FileUploadServiceImpl.processCiq() time taken to dump Ciq Data into MongoDB: "
					 * + (saveToMongoDBTime-ciqTemplateValidateTime)+"ms");
					 * 
					 * }
					 */

					if (status && falsestatus) {
						status = true;
						objResult.put("status", status);
					} else {
						objResult.put("status", status);
						objResult.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
					}

				}

			} else {
				objResult.put("status", status);
				objResult.put("reason",
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROVIDE_PROGRAM_TEMPLATE_DETAILS));
				return objResult;
			}

		}
		catch (NotOfficeXmlFileException e) {
			status=false;
			objResult.put("status", status);
			objResult.put("reason", "CIQ is encrypted we can not process");
			logger.error(
			"Exception  processExcelData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
			}
		catch (Exception e) {
			objResult.put("status", status);
			objResult.put("reason", GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CIQ_FILE));
			logger.error(
					"Exception  processExcelData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return objResult;
	}

	@Override
	public Map<String, Set<String>> getCiqSheetNames(String fileName, String dbCollectionName) {
		// TODO Auto-generated method stub
		List<CIQDetailsModel> getCiqSheetNames = null;
		Set<String> objSheetList = null;

		Map<String, Set<String>> objLinkedHashMap = new LinkedHashMap<>();

		try {
			getCiqSheetNames = ciqUploadRepository.getCiqSheetNames(fileName, dbCollectionName);

			if (getCiqSheetNames != null && getCiqSheetNames.size() > 0) {
				objSheetList = getCiqSheetNames.stream().filter(X -> StringUtils.isNotEmpty(X.getSheetName()))
						.map(X -> X.getSheetName()).collect(Collectors.toSet());

				if (objSheetList != null && objSheetList.size() > 0) {

					for (String sheetName : objSheetList) {
						objSheetList = getCiqSheetNames.stream()
								.filter(X -> (sheetName.equals(X.getSheetName())
										&& StringUtils.isNotEmpty(X.getSubSheetName())))
								.map(X -> X.getSubSheetName()).collect(Collectors.toSet());
						objLinkedHashMap.put(sheetName, objSheetList);

					}

				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception  getCiqSheetNames() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objLinkedHashMap;
	}

	@Override
	public Map<String, Set<String>> getCiqSheetNamesBasedOnEnb(String fileName, String dbCollectionName, String enbName,
			String enbId) {
		// TODO Auto-generated method stub
		List<CIQDetailsModel> getCiqSheetNames = null;
		Set<String> objSheetList = null;

		Map<String, Set<String>> objLinkedHashMap = new LinkedHashMap<>();

		try {
			getCiqSheetNames = ciqUploadRepository.getCiqSheetNamesBasedOnEnb(fileName, dbCollectionName, enbName,
					enbId);

			if (getCiqSheetNames != null && getCiqSheetNames.size() > 0) {
				objSheetList = getCiqSheetNames.stream().filter(X -> StringUtils.isNotEmpty(X.getSheetAliasName()))
						.map(X -> X.getSheetAliasName()).collect(Collectors.toSet());

				if (objSheetList != null && objSheetList.size() > 0) {

					for (String sheetName : objSheetList) {
						objSheetList = getCiqSheetNames.stream()
								.filter(X -> (sheetName.equals(X.getSubSheetAliasName())
										&& StringUtils.isNotEmpty(X.getSubSheetAliasName())))
								.map(X -> X.getSubSheetName()).collect(Collectors.toSet());
						objLinkedHashMap.put(sheetName, objSheetList);

					}

				}
			}
		} catch (Exception e) {
			logger.error("Exception  getCiqSheetNamesBasedOnEnb() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objLinkedHashMap;
	}

	@Override
	public Map<String, Object> getCiqSheetDisply(String fileName, String sheetName, String subSheetName,
			Map<String, String> ciqSearchMap, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> CiqSheetDetails = null;
		Set<String> objSheetList = null;

		try {
			CiqSheetDetails = ciqUploadRepository.getCiqSheetDisply(fileName, sheetName, subSheetName, ciqSearchMap,
					page, count);

		} catch (Exception e) {
			logger.error(
					"Exception  getCiqSheetDisply() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return CiqSheetDetails;
	}

	/**
	 * This api gives the list of getCiqAuditDetailsonSearch
	 * 
	 * @param page,count
	 * @return ciqTrailEntity
	 */
	@Override
	public Map<String, Object> getCiqAuditDetails(CiqUploadAuditTrailDetModel ciqAuditTrailModel, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> ciqTrailEntity = null;
		try {
			ciqTrailEntity = objFileUploadRepository.getCiqAuditDetails(ciqAuditTrailModel, page, count);

		} catch (Exception e) {
			logger.error("Exception getCiqAuditDetailsonSearch: " + ExceptionUtils.getFullStackTrace(e));
		}
		return ciqTrailEntity;
	}

	@Override
	public boolean unzipFile(String sourcePath, String destinationPath) throws RctException {
		boolean unzipStatus = false;
		try {
			String fileExtension = FilenameUtils.getExtension(sourcePath);
			if (fileExtension.equalsIgnoreCase("7z")) {
				FileUtil.untar7zipFile(sourcePath, destinationPath, false);
				unzipStatus = true;
			} else if (fileExtension.equalsIgnoreCase("zip")) {
				FileUtil.unzipFile(sourcePath, destinationPath, false);
				unzipStatus = true;
			} else if (fileExtension.equalsIgnoreCase("gz") || fileExtension.equalsIgnoreCase("tar.gz")
					|| fileExtension.equalsIgnoreCase("tgz")) {
				FileUtil.unzipTarFile(sourcePath, destinationPath, false);
				unzipStatus = true;
			}

		} catch (Exception e) {
			unzipStatus = false;
			logger.error("Exception  uploadExcel() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return unzipStatus;
	}

	@Override
	public Map<String, Object> fetchFileFromServer(NetworkConfigEntity networkConfigEntity,
			Map<String, Object> fileInfo, String marketName, FetchDetailsModel fetchDetailsModel, String fileType)
			throws RctException {
		Map<String, Object> objMap = new HashMap<String, Object>();
		try {
			objMap = FileUtil.fetchFileFromServer(networkConfigEntity, fileInfo, marketName, fetchDetailsModel,
					fileType);
		} catch (Exception e) {
			logger.error("Exception  fetchFileFromServer() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objMap;
	}

	/*
	 * @Override public Map<String, Object> processChecklist(MultipartFile file,
	 * String uploadPath, boolean isAllowDuplicate) { Map<String, Object> objResult
	 * = new LinkedHashMap<>(); objResult.put("status", true); return objResult; }
	 */

	private List<Integer> inclusiveRange(int from, int limit) {
		List<Integer> listnumbers = new ArrayList<>();
		try {
			for (int i = from; i <= limit; i++) {
				listnumbers.add(i);
			}
		} catch (Exception e) {
			logger.error(
					"Exception  inclusiveRange() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return listnumbers;
	}

	/**
	 * This api process CIQ
	 * 
	 * @param file,
	 *            uploadPath, isAllowDuplicate
	 * @return Map<String, Object>
	 */
	@Override
	@SuppressWarnings("unused")
	public Map<String, Object> processChecklist(MultipartFile file, String uploadPath, boolean isAllowDuplicate,
			Integer programId, String ciqFileName) {
		boolean status = false;
		boolean enodeIdStatus = false;
		boolean falsestatus = true;
		Workbook workbook = null;
		Map<String, Object> objResult = new LinkedHashMap<>();
		FileInputStream excelFileInputStream = null;

		LinkedHashMap<String, String> objBodyProcessMap = null;
		ObjectMapper mapper = new ObjectMapper();
		List<CheckListDetailsModel> objCIQDetailsModelList = new ArrayList<>();
		String programDetailId = String.valueOf(programId);
		boolean columnsMatchstatus = false;
		try {

			/*
			 * uploadPath="/home/user/Documents/Verizon_vLSM_Migration Checklist_1.0.3.xlsx"
			 * ; File files=new
			 * File("/home/user/Documents/Verizon_vLSM_Migration Checklist_1.0.3.xlsx");
			 * 
			 * FileInputStream input = new FileInputStream(files); file = new
			 * MockMultipartFile("/home/user/Documents/Verizon_vLSM_Migration Checklist_1.0.3.xlsx"
			 * , "Verizon_vLSM_Migration Checklist_1.0.3.xlsx","text/plain",IOUtils.
			 * toByteArray(input) );
			 */

			// ProgramTemplateEntity
			// objProgramTemplateEntity=objFileUploadRepository.getProgramTemplate(programId,
			// "temlplateJson");
			ProgramTemplateEntity objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.CHECK_LIST_VALIDATE_TEMPLATE);
			if (objProgramTemplateEntity != null && StringUtils.isNotEmpty(objProgramTemplateEntity.getValue())) {
				logger.info("FileUploadServiceImpl.processChecklist() processing file: " + uploadPath);
				;
				excelFileInputStream = new FileInputStream(uploadPath.toString());
				JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());

				List<CheckListTemplateModel> myCIQTemplateModel = mapper.readValue(objData.get("sheets").toString(),
						new TypeReference<List<CheckListTemplateModel>>() {
						});

				if (FilenameUtils.getExtension(uploadPath).equals("xlsx")) {
					workbook = new XSSFWorkbook(excelFileInputStream);
				} else {
					workbook = new HSSFWorkbook(excelFileInputStream);
				}
				LinkedHashMap<String, String> objHeaderProcessMap = null;
				List<LinkedHashMap<String, String>> objProcessList = null;
				if (myCIQTemplateModel != null && myCIQTemplateModel.size() > 0) {

					// AtomicInteger counts = new AtomicInteger();

					sheetWiseDisplayDataLoop:

					for (CheckListTemplateModel objLocCIQTemplateModel : myCIQTemplateModel) {
						String enbIdHeaderName = null;
						String enbNameHeaderName = null;
						objHeaderProcessMap = new LinkedHashMap<>();
						objProcessList = new ArrayList<>();
						if (StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetName())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getHeaderRow())
								&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getSheetType())) {

							int headerRowNo = Integer.parseInt(objLocCIQTemplateModel.getHeaderRow());

							Sheet datatypeSheet = workbook.getSheet(objLocCIQTemplateModel.getSheetName());

							if (datatypeSheet != null) {

								if ("normal".equals(objLocCIQTemplateModel.getSheetType())) {

									List<CheckListTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										Map<String, String> mapColumns = objListColumnsModel.stream()
												.collect(Collectors.toMap(CheckListTemplateColumnsModel::getColumnName,
														CheckListTemplateColumnsModel::getColumnHeaderName));

										if (mapColumns != null && mapColumns.size() > 0) {
											// int cellCount=0;

											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();
												// Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();
												stopReadingCellIteration: for (int cn = 0; cn < row
														.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

													// Cell currentCell = cellIterator.next();
													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils
																.isNotEmpty(getStringValueOfCell(currentCell).trim())) {

															if (mapColumns.containsKey(
																	getStringValueOfCell(currentCell).trim())) {

																if (mapColumns
																		.get(getStringValueOfCell(currentCell).trim())
																		.equals(data)) {

																	objHeaderProcessMap.put(colIndex,
																			getStringValueOfCell(currentCell).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																	// cellCount++;

																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason",
																			"Columns Not Matched With Check List Program Template");

																	// break sheetWiseDisplayDataLoop;

																	return objResult;

																}
															}

														}
													} else if (headerRowStatus) {

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {
															objBodyProcessMap.put(
																	objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()),
																	((String) getCellValue(currentCell)).trim());

															if (StringUtils.isNotEmpty(
																	((String) getCellValue(currentCell)).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														break stopReadingIfRowIsBlank;
													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													for (LinkedHashMap<String, String> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CheckListDetailsModel ciqDetailsModel = new CheckListDetailsModel();

															ciqDetailsModel.setCheckListMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setConfigType(
																	objLocCIQTemplateModel.getConfigType());
															objCIQDetailsModelList.add(ciqDetailsModel);

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));

											}

										}

									}

								} else if ("multiple".equals(objLocCIQTemplateModel.getSheetType())
										&& StringUtils.isNotEmpty(objLocCIQTemplateModel.getReadingRange())) {

									String[] readingRange = objLocCIQTemplateModel.getReadingRange().split("-");
									int from = Integer.parseInt(readingRange[0]);
									int limit = Integer.parseInt(readingRange[1]);

									List<Integer> rowsConsiderList = inclusiveRange(from - 1, limit - 1);

									List<CheckListTemplateColumnsModel> objListColumnsModel = objLocCIQTemplateModel
											.getColumns();

									if (objListColumnsModel != null && objListColumnsModel.size() > 0) {

										Map<String, String> mapColumns = objListColumnsModel.stream()
												.collect(Collectors.toMap(CheckListTemplateColumnsModel::getColumnName,
														CheckListTemplateColumnsModel::getColumnHeaderName));

										if (mapColumns != null && mapColumns.size() > 0) {
											// int cellCount=0;
											Iterator<Row> rowIterator = datatypeSheet.iterator();
											boolean headerRowStatus = false;
											stopReadingIfRowIsBlank: while (rowIterator.hasNext()) {
												objBodyProcessMap = new LinkedHashMap<>();
												Row row = rowIterator.next();
												Iterator<Cell> cellIterator = row.cellIterator();
												AtomicBoolean objAtomicBoolean = new AtomicBoolean();

												stopReadingCellIteration: for (int cn = 0; cn < row
														.getLastCellNum(); cn++) {
													Cell currentCell = row.getCell(cn, Row.CREATE_NULL_AS_BLANK);

													String data = currentCell.getAddress().toString()
															.replaceAll("[0-9]", "");
													// column Name
													String colIndex = currentCell.getAddress().toString(); // for
																											// geting
													// columnIndex
													if (row.getRowNum() == headerRowNo - 1) {
														headerRowStatus = true;

														if (StringUtils
																.isNotEmpty(getStringValueOfCell(currentCell).trim())) {

															if (mapColumns.containsKey(
																	getStringValueOfCell(currentCell).trim())) {

																if (mapColumns
																		.get(getStringValueOfCell(currentCell).trim())
																		.equals(data)) {

																	objHeaderProcessMap.put(colIndex,
																			getStringValueOfCell(currentCell).trim()
																					.replace(".", "*")
																					.replace(" ", "_"));
																	// cellCount++;
																} else {
																	columnsMatchstatus = true;
																	status = false;
																	objResult.put("status", status);
																	objResult.put("reason",
																			"Columns Not Matched With Program Template");

																	return objResult;

																}
															}

														}
													} else if (headerRowStatus
															&& rowsConsiderList.contains(row.getRowNum())) {

														String colIndexBody = currentCell.getAddress().toString()
																.replaceAll("[0-9]", "");
														if (objHeaderProcessMap.size() > 0
																&& objHeaderProcessMap.containsKey(colIndexBody
																		+ objLocCIQTemplateModel.getHeaderRow())) {
															objBodyProcessMap.put(
																	objHeaderProcessMap.get(colIndexBody
																			+ objLocCIQTemplateModel.getHeaderRow()),
																	((String) getCellValue(currentCell)).trim());

															if (StringUtils.isNotEmpty(
																	((String) getCellValue(currentCell)).trim())) {
																objAtomicBoolean.getAndSet(true);
															}
														}
													} else {
														continue stopReadingIfRowIsBlank;
													}

												}

												if (objBodyProcessMap != null && objBodyProcessMap.size() > 0) {
													if (!objAtomicBoolean.get()) {
														if (rowsConsiderList.contains(row.getRowNum())) {

															continue stopReadingIfRowIsBlank;
														} else {

															break stopReadingIfRowIsBlank;
														}

													}

													objProcessList.add(objBodyProcessMap);
												}

											}

											if (objProcessList.size() > 0) {

												if (objProcessList != null && objProcessList.size() > 0) {
													for (LinkedHashMap<String, String> ciqRecord : objProcessList) {
														if ((ciqRecord != null && ciqRecord.size() > 0)) {
															CheckListDetailsModel ciqDetailsModel = new CheckListDetailsModel();

															ciqDetailsModel.setCheckListMap(ciqRecord);
															ciqDetailsModel.setFileName(file.getOriginalFilename());
															ciqDetailsModel.setSheetName(
																	objLocCIQTemplateModel.getSheetName());
															ciqDetailsModel
																	.setSeqOrder(objLocCIQTemplateModel.getSeqOrder());
															ciqDetailsModel.setSubSheetName(
																	objLocCIQTemplateModel.getSubSheetName());
															ciqDetailsModel.setConfigType(
																	objLocCIQTemplateModel.getConfigType());
															objCIQDetailsModelList.add(ciqDetailsModel);

														}
													}

												}
											} else {
												objResult.put("status", status);
												objResult.put("reason", GlobalInitializerListener.faultCodeMap
														.get(FaultCodes.FAILED_TO_UPLOAD_NO_CONTENT));

											}
											// counts.getAndIncrement();

										}

									}
								}

							} else {
								objResult.put("status", status);
								objResult.put("reason", "Sheet Names Not Matched With Check List Program Template");

								return objResult;
							}
						}

					}

					if (objCIQDetailsModelList.size() > 0 && !columnsMatchstatus) {

						String dbCollectionName = CommonUtil.createMongoDbFileNameCheckList(programDetailId,
								file.getOriginalFilename(), ciqFileName);
						if (isAllowDuplicate) {
							// objFileUploadRepository.deleteCiqDetailsByFilename(file.getOriginalFilename());
							objFileUploadRepository.deleteCiqDetailsByFilename(dbCollectionName);
						}
						for (CheckListDetailsModel objCIQDetailsModelLoc : objCIQDetailsModelList) {
							objCIQDetailsModelLoc.setId(objCounterServiceImpl.getNextSequence(dbCollectionName));
							CheckListDetailsModel objnew = ciqUploadRepository.saveCheckList(objCIQDetailsModelLoc,
									dbCollectionName);
							if (objnew != null) {
								status = true;
							} else {
								falsestatus = false;

							}
						}
					}

					if (status && falsestatus) {
						status = true;
						objResult.put("status", status);
					} else {
						objResult.put("status", status);
						objResult.put("reason",
								GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CHECKILST_FILE));
					}

				}
			} else {

				objResult.put("status", status);
				objResult.put("reason", GlobalInitializerListener.faultCodeMap
						.get(FaultCodes.PROVIDE_CHECKLIST_PROGRAM_TEMPLATE_DETAILS));
				return objResult;
			}
		} catch (Exception e) {
			objResult.put("status", status);
			objResult.put("reason",
					GlobalInitializerListener.faultCodeMap.get(FaultCodes.FAILED_TO_UPLOAD_CHECKILST_FILE));
			logger.error(
					"Exception  processExcelData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return objResult;
	}

	@Override
	public Set<String> getCheckListSheetNames(String fileName, String dbCollectionName) {
		// TODO Auto-generated method stub
		List<CheckListDetailsModel> getCheckListSheetNames = null;
		Set<String> objSheetList = null;

		try {
			getCheckListSheetNames = ciqUploadRepository.getCheckListSheetNames(fileName, dbCollectionName);

			if (getCheckListSheetNames != null && getCheckListSheetNames.size() > 0) {
				objSheetList = getCheckListSheetNames.stream().map(X -> X.getSheetName()).collect(Collectors.toSet());

				/*
				 * if(objSheetList!=null && objSheetList.size()>0) { for(String ) }
				 */
			}
		} catch (Exception e) {
			logger.error("Exception  getCheckListSheetNames() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objSheetList;
	}

	@Override
	public Set<String> getCheckListAllSheetNames(String fileName, String dbCollectionName) {
		// TODO Auto-generated method stub
		List<CheckListDetailsModel> getCheckListSheetNames = null;
		Set<String> objSheetList = null;

		try {
			getCheckListSheetNames = ciqUploadRepository.getCheckListAllSheetNames(fileName, dbCollectionName);

			if (getCheckListSheetNames != null && getCheckListSheetNames.size() > 0) {
				objSheetList = getCheckListSheetNames.stream().map(X -> X.getSheetName()).collect(Collectors.toSet());
			}
		} catch (Exception e) {
			logger.error("Exception  getCheckListSheetNames() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return objSheetList;
	}

	@Override
	public Map<String, Object> getCheckListSheetDisply(String fileName, String sheetName, int page, int count) {
		// TODO Auto-generated method stub
		Map<String, Object> CiqSheetDetails = null;

		try {
			CiqSheetDetails = ciqUploadRepository.getCheckListSheetDisply(fileName, sheetName, page, count);

		} catch (Exception e) {
			logger.error("Exception  getCheckListSheetDisply() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return CiqSheetDetails;
	}

	@Override
	public Map<String, Object> getAllCheckListSheetDisply(String fileName, String sheetName, int page, int count,
			String enodeName, int runTestId) {
		// TODO Auto-generated method stub
		Map<String, Object> CiqSheetDetails = null;

		try {
			CiqSheetDetails = ciqUploadRepository.getAllCheckListSheetDisply(fileName, sheetName, page, count,
					enodeName, runTestId);

		} catch (Exception e) {
			logger.error("Exception  getCheckListSheetDisply() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return CiqSheetDetails;
	}

	/**
	 * This method will create CIQ File details
	 * 
	 * @param createCiqEntity
	 * @return boolean
	 */
	@Override
	public boolean saveCheckListFileDetaiils(CheckListDetailsModel createCheckListEntity, String dbCollectionName) {
		boolean status = false;
		try {
			if (createCheckListEntity.getId() != null && createCheckListEntity.getId() > 0) {
				status = ciqUploadRepository.updateCheckListFileDetaiils(createCheckListEntity, dbCollectionName);

			} else {
				createCheckListEntity.setId(objCounterServiceImpl.getNextSequence(dbCollectionName));
				status = ciqUploadRepository.saveCheckListFileDetaiils(createCheckListEntity, dbCollectionName);
			}
		} catch (Exception e) {
			logger.error("Exception saveCheckListFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean updateCheckListFileDetails(CheckListModel createCheckListEntity, String dbCollectionName,
			String enodeName) {
		boolean status = false;
		try {
			status = ciqUploadRepository.updateCheckListFileDetails(createCheckListEntity, dbCollectionName, enodeName);
		} catch (Exception e) {
			logger.error("Exception saveCheckListFileDetaiils() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean deleteCheckListRowDetails(int id, String dbCollectionName) {
		// TODO Auto-generated method stub
		boolean status = false;

		try {
			status = ciqUploadRepository.deleteCheckListRowDetails(id, dbCollectionName);
		} catch (Exception e) {
			status = false;
			logger.error("Exception deleteCheckListRowDetails() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public CiqUploadAuditTrailDetEntity getLatestCheckListByProgram(Integer programId) {
		CiqUploadAuditTrailDetEntity ciqUploadAuditTrailDetEntity = null;
		try {
			ciqUploadAuditTrailDetEntity = objFileUploadRepository.getLatestCheckListByProgram(programId);
		} catch (Exception e) {
			logger.error("Exception getLatestCheckListByProgram() in FileUploadServiceImpl "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return ciqUploadAuditTrailDetEntity;
	}

	@Override
	public Map<String, Object> insertChecklistDetails(String fileName, String sheetName, String enodeName,
			String remarks, int runTestId) {
		// TODO Auto-generated method stub
		Map<String, Object> CiqSheetDetails = null;

		try {
			CiqSheetDetails = ciqUploadRepository.insertChecklistDetails(fileName, sheetName, enodeName, remarks,
					runTestId);

		} catch (Exception e) {
			logger.error("Exception  getCheckListSheetDisply() in  FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return CiqSheetDetails;
	}

	@Override
	public boolean duplicateEnbDetails(NeMappingModel neMappingModel) {
		boolean status = false;
		try {
			status = ciqUploadRepository.duplicateEnbDetails(neMappingModel);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in duplicateProgaramTemplate()   FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean saveEnbDetails(NeMappingEntity neMappingEntity) {
		boolean status = false;
		try {
			status = ciqUploadRepository.saveEnbDetails(neMappingEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in duplicateProgaramTemplate()   FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public ProgramTemplateEntity getProgramTemplate(Integer programId, String scriptStoreTemplate) {
		ProgramTemplateEntity programTemplateEntity = null;
		try {
			programTemplateEntity = objFileUploadRepository.getProgramTemplate(programId, scriptStoreTemplate);
		} catch (Exception e) {
			logger.error(
					"Exception in getProgramTemplate()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return programTemplateEntity;
	}

	@Override
	public Map<String, Object> validationCiqDetails(String dbCollectionName, Integer programId) {
		ObjectMapper mapper = new ObjectMapper();
		List<ErrorDisplayModel> objErrorMap = new ArrayList<>();
		// Map<Integer, List<ErrorDisplayModel>> objErrorMap = new LinkedHashMap<>();
		Map<String, Object> objMapretun = new LinkedHashMap<>();
		try {

			ProgramTemplateEntity objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.PARAMETERS_VALIDATE_TEMPLATE);

			JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());

			List<ValidationTemplateModel> myValidationTemplateModelList = mapper
					.readValue(objData.get("sheets").toString(), new TypeReference<List<ValidationTemplateModel>>() {
					});

			if (myValidationTemplateModelList != null && myValidationTemplateModelList.size() > 0) {

				for (ValidationTemplateModel objValidationTemplateModels : myValidationTemplateModelList) {

					if (objValidationTemplateModels != null
							&& StringUtils.isNotEmpty(objValidationTemplateModels.getSheetName())) {

						List<CIQDetailsModel> objListData = objFileUploadRepository.getRanConfigDetailsValidation(
								dbCollectionName, objValidationTemplateModels.getSheetName(),
								objValidationTemplateModels.getSubSheetName());

						
						List<ValidationTemplateColumnModel> listofValidationColumns = objValidationTemplateModels
								.getValidationColumns();

						if (listofValidationColumns != null && listofValidationColumns.size() > 0) {
							for (ValidationTemplateColumnModel objValTemColModel : listofValidationColumns) {

								List<CIQDetailsModel> objvalidateListData = objListData.stream()
										.filter(x -> x.getCiqMap() != null
												&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
										.collect(Collectors.toList());
								
									validateParamsValues(objValTemColModel, objvalidateListData,objErrorMap);

								//we have to 
								if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "no".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

/*									List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());
*/
									if ("==".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (!objValTemColModel.getColumnValue()
													.equals(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ " value should be " + objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("any".equalsIgnoreCase(objValTemColModel.getOperator())
											&& StringUtils.isNotEmpty(objValTemColModel.getColumnValue())) {

										String[] arraStringValues = objValTemColModel.getColumnValue().split(",");

										List<String> listStringValues = new ArrayList<>(
												Arrays.asList(arraStringValues));

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (!listStringValues.contains(objlocalCIQDetailsModel.getCiqMap()
													.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be any values of these"
														+ objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("<".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) < Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be less than " + objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if (">".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) > Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(
														objValTemColModel.getColumnName() + "  would be greter than "
																+ objValTemColModel.getColumnValue());
												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if (">=".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) >= Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be greter than or equal "
														+ objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("<=".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) <= Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be less than or equal "
														+ objValTemColModel.getColumnValue());
												objErrorMap.add(objErrorDisplayModel);

											}

										}

									}

								} else if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "yes".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

									/*List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());*/

									if ("==".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (objValTemColModel.getColumnValue()
													.equals(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												List<ValidationDependColumnModel> objDependeList = objValTemColModel
														.getDependsColumnsList();

												for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

													if ("==".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())) {

														if (!objValidationDependColumnModel.getColumnValue()
																.equals(objlocalCIQDetailsModel.getCiqMap().get(
																		objValidationDependColumnModel.getColumnName())
																		.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value should be "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " value is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);

														}

													} else if ("any".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())
															&& StringUtils.isNotEmpty(
																	objValidationDependColumnModel.getColumnValue())) {

														String[] arraStringValues = objValidationDependColumnModel
																.getColumnValue().split(",");

														List<String> listStringValues = new ArrayList<>(
																Arrays.asList(arraStringValues));

														if (!listStringValues.contains(objlocalCIQDetailsModel
																.getCiqMap()
																.get(objValidationDependColumnModel.getColumnName())
																.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value would be any values of these "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " value is "
																			+ objValTemColModel.getColumnValue());

															objErrorMap.add(objErrorDisplayModel);

														}

													}

												}

											}

										}

									} else if ("any".equalsIgnoreCase(objValTemColModel.getOperator())) {

										String[] globalarraStringValues = objValTemColModel.getColumnValue().split(",");

										List<String> globallistStringValues = new ArrayList<>(
												Arrays.asList(globalarraStringValues));

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (globallistStringValues.contains(objlocalCIQDetailsModel.getCiqMap()
													.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												List<ValidationDependColumnModel> objDependeList = objValTemColModel
														.getDependsColumnsList();

												for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

													if ("==".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())) {

														if (!objValidationDependColumnModel.getColumnValue()
																.equals(objlocalCIQDetailsModel.getCiqMap().get(
																		objValidationDependColumnModel.getColumnName())
																		.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value should be  "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " values is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);

														}

													} else if ("any".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())
															&& StringUtils.isNotEmpty(
																	objValidationDependColumnModel.getColumnValue())) {

														String[] arraStringValues = objValidationDependColumnModel
																.getColumnValue().split(",");

														List<String> listStringValues = new ArrayList<>(
																Arrays.asList(arraStringValues));

														if (!listStringValues.contains(objlocalCIQDetailsModel
																.getCiqMap()
																.get(objValidationDependColumnModel.getColumnName())
																.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value would be any values of these  "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " values is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);
														}

													}

												}

											}

										}

									}

								} else if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "compare".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

									/*List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());*/

									for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

										List<ValidationDependColumnModel> objDependeList = objValTemColModel
												.getDependsColumnsList();

										for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

											if ("<".equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue < mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " lessThan the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											} else if (">"
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue > mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " greaterThan the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);
												}

											} else if ("<="
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue <= mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());

														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " lessThan or equal the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											} else if (">="
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue >= mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " greater Than  or equal the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											}

										}

									}

								}
							}

						}

					}

				}

			}

		} catch (Exception e) {
			objMapretun.put("validationDetails", objErrorMap);
			logger.error("Exception in validationCiqDetails()   FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		if (objErrorMap != null && objErrorMap.size() > 0) {
			objErrorMap = objErrorMap.stream().sorted(Comparator.comparing(ErrorDisplayModel::getRowId))
					.collect(Collectors.toList());
		}
		objMapretun.put("validationDetails", objErrorMap);
		return objMapretun;
	}

	private void validateParamsValues(ValidationTemplateColumnModel objValTemColModel, List<CIQDetailsModel> objvalidateListData,
			List<ErrorDisplayModel> objErrorMap) {
		logger.error("parameter validation start");
		for (CIQDetailsModel ciqDetailsModel : objvalidateListData) {
			commonValidator.validate(objValTemColModel, ciqDetailsModel,objErrorMap);
		}
		logger.error("parameter validation end");
		
	}

	@Override
	public Map<String, Object> validationEnbDetails(String dbCollectionName, Integer programId, String enbName,
			String enbId) {
		ObjectMapper mapper = new ObjectMapper();
		List<ErrorDisplayModel> objErrorMap = new ArrayList<>();
		// Map<Integer, List<ErrorDisplayModel>> objErrorMap = new LinkedHashMap<>();
		Map<String, Object> objMapretun = new LinkedHashMap<>();
		try {

			ProgramTemplateEntity objProgramTemplateEntity = objFileUploadRepository.getProgramTemplate(programId,
					Constants.PARAMETERS_VALIDATE_TEMPLATE);

			JsonObject objData = CommonUtil.parseRequestDataToJson(objProgramTemplateEntity.getValue());

			List<ValidationTemplateModel> myValidationTemplateModelList = mapper
					.readValue(objData.get("sheets").toString(), new TypeReference<List<ValidationTemplateModel>>() {
					});

			if (myValidationTemplateModelList != null && myValidationTemplateModelList.size() > 0) {

				for (ValidationTemplateModel objValidationTemplateModels : myValidationTemplateModelList) {

					if (objValidationTemplateModels != null
							&& StringUtils.isNotEmpty(objValidationTemplateModels.getSheetName())) {

						/*List<CIQDetailsModel> objListData = objFileUploadRepository.getRanConfigDetailsEnbValidation(
								dbCollectionName, objValidationTemplateModels.getSheetName(),
								objValidationTemplateModels.getSubSheetName(), enbName, enbId);*/
						
						List<CIQDetailsModel> objListData = objFileUploadRepository.getRanConfigDetailsValidation(
								dbCollectionName, objValidationTemplateModels.getSheetName(),
								objValidationTemplateModels.getSubSheetName());
						
						 objListData=objListData.stream().filter(x->enbId.contains(x.geteNBId())).collect(Collectors.toList());
						List<ValidationTemplateColumnModel> listofValidationColumns = objValidationTemplateModels
								.getValidationColumns();

						if (listofValidationColumns != null && listofValidationColumns.size() > 0) {
							for (ValidationTemplateColumnModel objValTemColModel : listofValidationColumns) {
								List<CIQDetailsModel> objvalidateListData = objListData.stream()
										.filter(x -> x.getCiqMap() != null
												&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
										.collect(Collectors.toList());
								
								validateParamsValues(objValTemColModel, objvalidateListData,objErrorMap);

								if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "no".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

									/*List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());*/

									if ("==".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (!objValTemColModel.getColumnValue()
													.equals(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ " value should be " + objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("any".equalsIgnoreCase(objValTemColModel.getOperator())
											&& StringUtils.isNotEmpty(objValTemColModel.getColumnValue())) {

										String[] arraStringValues = objValTemColModel.getColumnValue().split(",");

										List<String> listStringValues = new ArrayList<>(
												Arrays.asList(arraStringValues));

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (!listStringValues.contains(objlocalCIQDetailsModel.getCiqMap()
													.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be any values of these"
														+ objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("<".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) < Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be less than " + objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if (">".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) > Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(
														objValTemColModel.getColumnName() + "  would be greter than "
																+ objValTemColModel.getColumnValue());
												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if (">=".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) >= Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be greter than or equal "
														+ objValTemColModel.getColumnValue());

												objErrorMap.add(objErrorDisplayModel);

											}

										}

									} else if ("<=".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											boolean templateValue = StringUtils
													.isNumeric(objValTemColModel.getColumnValue());
											boolean tableValue = StringUtils
													.isNumeric(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());

											if (!(templateValue && tableValue
													&& Integer.valueOf(objValTemColModel.getColumnValue()) <= Integer
															.valueOf(objlocalCIQDetailsModel.getCiqMap()
																	.get(objValTemColModel.getColumnName())
																	.getHeaderValue()))) {

												ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
												objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
												objErrorDisplayModel
														.setSheetName(objlocalCIQDetailsModel.getSheetName());
												objErrorDisplayModel
														.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
												objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
														.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
												objErrorDisplayModel.setEnbName(objlocalCIQDetailsModel.geteNBName());

												objErrorDisplayModel.setPropertyName(objValTemColModel.getColumnName());
												objErrorDisplayModel.setErrorMessage(objValTemColModel.getColumnName()
														+ "  would be less than or equal "
														+ objValTemColModel.getColumnValue());
												objErrorMap.add(objErrorDisplayModel);

											}

										}

									}

								} else if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "yes".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

									/*List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());*/

									if ("==".equalsIgnoreCase(objValTemColModel.getOperator())) {

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (objValTemColModel.getColumnValue()
													.equals(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												List<ValidationDependColumnModel> objDependeList = objValTemColModel
														.getDependsColumnsList();

												for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

													if ("==".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())) {

														if (!objValidationDependColumnModel.getColumnValue()
																.equals(objlocalCIQDetailsModel.getCiqMap().get(
																		objValidationDependColumnModel.getColumnName())
																		.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value should be "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " value is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);

														}

													} else if ("any".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())
															&& StringUtils.isNotEmpty(
																	objValidationDependColumnModel.getColumnValue())) {

														String[] arraStringValues = objValidationDependColumnModel
																.getColumnValue().split(",");

														List<String> listStringValues = new ArrayList<>(
																Arrays.asList(arraStringValues));

														if (!listStringValues.contains(objlocalCIQDetailsModel
																.getCiqMap()
																.get(objValidationDependColumnModel.getColumnName())
																.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value would be any values of these "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " value is "
																			+ objValTemColModel.getColumnValue());

															objErrorMap.add(objErrorDisplayModel);

														}

													}

												}

											}

										}

									} else if ("any".equalsIgnoreCase(objValTemColModel.getOperator())) {

										String[] globalarraStringValues = objValTemColModel.getColumnValue().split(",");

										List<String> globallistStringValues = new ArrayList<>(
												Arrays.asList(globalarraStringValues));

										for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

											if (globallistStringValues.contains(objlocalCIQDetailsModel.getCiqMap()
													.get(objValTemColModel.getColumnName()).getHeaderValue())) {

												List<ValidationDependColumnModel> objDependeList = objValTemColModel
														.getDependsColumnsList();

												for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

													if ("==".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())) {

														if (!objValidationDependColumnModel.getColumnValue()
																.equals(objlocalCIQDetailsModel.getCiqMap().get(
																		objValidationDependColumnModel.getColumnName())
																		.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value should be  "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " values is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);

														}

													} else if ("any".equalsIgnoreCase(
															objValidationDependColumnModel.getOperator())
															&& StringUtils.isNotEmpty(
																	objValidationDependColumnModel.getColumnValue())) {

														String[] arraStringValues = objValidationDependColumnModel
																.getColumnValue().split(",");

														List<String> listStringValues = new ArrayList<>(
																Arrays.asList(arraStringValues));

														if (!listStringValues.contains(objlocalCIQDetailsModel
																.getCiqMap()
																.get(objValidationDependColumnModel.getColumnName())
																.getHeaderValue())) {

															ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
															objErrorDisplayModel
																	.setRowId(objlocalCIQDetailsModel.getSheetId());
															objErrorDisplayModel.setSheetName(
																	objlocalCIQDetailsModel.getSheetName());
															objErrorDisplayModel.setSubSheetName(
																	objlocalCIQDetailsModel.getSubSheetName());
															objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																	.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																	.getHeaderValue());
															objErrorDisplayModel
																	.setEnbName(objlocalCIQDetailsModel.geteNBName());

															objErrorDisplayModel.setPropertyName(
																	objValidationDependColumnModel.getColumnName());
															objErrorDisplayModel.setErrorMessage(
																	objValidationDependColumnModel.getColumnName()
																			+ " value would be any values of these  "
																			+ objValidationDependColumnModel
																					.getColumnValue()
																			+ " as " + objValTemColModel.getColumnName()
																			+ " values is "
																			+ objValTemColModel.getColumnValue());
															objErrorMap.add(objErrorDisplayModel);
														}

													}

												}

											}

										}

									}

								} else if (StringUtils.isNotEmpty(objValTemColModel.getDependToOtherColumn())
										&& "compare".equalsIgnoreCase(objValTemColModel.getDependToOtherColumn())) {

									/*List<CIQDetailsModel> objvalidateListData = objListData.stream()
											.filter(x -> x.getCiqMap() != null
													&& x.getCiqMap().containsKey(objValTemColModel.getColumnName()))
											.collect(Collectors.toList());*/

									for (CIQDetailsModel objlocalCIQDetailsModel : objvalidateListData) {

										List<ValidationDependColumnModel> objDependeList = objValTemColModel
												.getDependsColumnsList();

										for (ValidationDependColumnModel objValidationDependColumnModel : objDependeList) {

											if ("<".equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue < mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " lessThan the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											} else if (">"
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue > mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " greaterThan the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);
												}

											} else if ("<="
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue <= mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());

														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " lessThan or equal the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											} else if (">="
													.equalsIgnoreCase(objValidationDependColumnModel.getOperator())) {
												boolean mainValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValTemColModel.getColumnName())
														.getHeaderValue());

												boolean subValueStatus = isNumericString(objlocalCIQDetailsModel
														.getCiqMap().get(objValidationDependColumnModel.getColumnName())
														.getHeaderValue());
												if (mainValueStatus && subValueStatus) {
													int mainValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValTemColModel.getColumnName()).getHeaderValue());
													int subValue = Integer.valueOf(objlocalCIQDetailsModel.getCiqMap()
															.get(objValidationDependColumnModel.getColumnName())
															.getHeaderValue());

													if (!(subValue >= mainValue)) {

														ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
														objErrorDisplayModel
																.setRowId(objlocalCIQDetailsModel.getSheetId());
														objErrorDisplayModel
																.setSheetName(objlocalCIQDetailsModel.getSheetName());
														objErrorDisplayModel.setSubSheetName(
																objlocalCIQDetailsModel.getSubSheetName());
														objErrorDisplayModel.setCellId(objlocalCIQDetailsModel
																.getCiqMap().get(Constants.VZ_GROW_Cell_ID)
																.getHeaderValue());
														objErrorDisplayModel
																.setEnbName(objlocalCIQDetailsModel.geteNBName());

														objErrorDisplayModel.setPropertyName(
																objValidationDependColumnModel.getColumnName());
														objErrorDisplayModel.setErrorMessage(
																objValidationDependColumnModel.getColumnName()
																		+ " greater Than  or equal the value of "
																		+ objValTemColModel.getColumnName());
														objErrorMap.add(objErrorDisplayModel);

													}
												} else {

													ErrorDisplayModel objErrorDisplayModel = new ErrorDisplayModel();
													objErrorDisplayModel.setRowId(objlocalCIQDetailsModel.getSheetId());
													objErrorDisplayModel
															.setSheetName(objlocalCIQDetailsModel.getSheetName());
													objErrorDisplayModel
															.setSubSheetName(objlocalCIQDetailsModel.getSubSheetName());
													objErrorDisplayModel.setCellId(objlocalCIQDetailsModel.getCiqMap()
															.get(Constants.VZ_GROW_Cell_ID).getHeaderValue());
													objErrorDisplayModel
															.setEnbName(objlocalCIQDetailsModel.geteNBName());

													objErrorDisplayModel.setPropertyName(
															objValidationDependColumnModel.getColumnName());
													objErrorDisplayModel
															.setErrorMessage("These columns should be numeric "
																	+ objValidationDependColumnModel.getColumnName()
																	+ " , " + objValTemColModel.getColumnName());
													objErrorMap.add(objErrorDisplayModel);

												}

											}

										}

									}

								}
							}

						}

					}

				}

			}

		} catch (Exception e) {
			objMapretun.put("validationDetails", objErrorMap);
			logger.error("Exception in validationEnbDetails()   FileUploadServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		if (objErrorMap != null && objErrorMap.size() > 0) {
			objErrorMap = objErrorMap.stream().sorted(Comparator.comparing(ErrorDisplayModel::getRowId))
					.collect(Collectors.toList());
		}
		objMapretun.put("validationDetails", objErrorMap);
		return objMapretun;
	}

	private boolean isNumericString(String data) {
		boolean status = false;

		try {
			if (StringUtils.isNotEmpty(data)) {
				status = StringUtils.isNumeric(data);

			} else {
				status = false;
			}

		} catch (Exception e) {
			logger.error(
					"Exception in isNumericString()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

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
				BigDecimal bd = new BigDecimal(e1Val.toString().trim());
				long lonVal = bd.longValue();
				return String.valueOf(lonVal).trim();
			case Cell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue().toString().trim();
			}
		}

		return "";
	}

	/**
	 * This api returns Ne Mapping status
	 * 
	 * @param List,Integer,String
	 * @return boolean
	 */
	public NetworkConfigEntity getNeMappingEntity(String enbId, Integer programId) {
		boolean status = false;
		NetworkConfigEntity objNetworkConfigEntity = null;
		try {

			List<NetworkConfigEntity> listNetworkConfigEntity = objFileUploadRepository
					.getNeConfigVersionMarketBased(programId);
			if ((enbId.startsWith("70") || enbId.startsWith("73"))) {
				List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
						.filter(x -> (x.getNeVersionEntity() != null && "Rochester".equalsIgnoreCase(x.getNeName())
								&& "Upstate NY".equalsIgnoreCase(x.getNeMarket())
								&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
						.collect(Collectors.toList());
				if (!individualNwDet.isEmpty()) {
					objNetworkConfigEntity = individualNwDet.get(0);
				}

			} else if ((enbId.startsWith("71") || enbId.startsWith("72") || enbId.startsWith("74"))) {

				List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
						.filter(x -> (x.getNeVersionEntity() != null && "East_Syracuse".equalsIgnoreCase(x.getNeName())
								&& "Upstate NY".equalsIgnoreCase(x.getNeMarket())
								&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
						.collect(Collectors.toList());
				if (!individualNwDet.isEmpty()) {
					objNetworkConfigEntity = individualNwDet.get(0);
				}
			} else if ((enbId.startsWith("56") || enbId.startsWith("57") || enbId.startsWith("58")
					|| enbId.startsWith("61") || enbId.startsWith("62"))) {

				List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
						.filter(x -> (x.getNeVersionEntity() != null
								&& "WestBorough_Medium".equalsIgnoreCase(x.getNeName())
								&& "New England".equalsIgnoreCase(x.getNeMarket())
								&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
						.collect(Collectors.toList());
				if (!individualNwDet.isEmpty()) {
					objNetworkConfigEntity = individualNwDet.get(0);
				}
			} else if ((enbId.startsWith("59") || enbId.startsWith("60") || enbId.startsWith("64")
					|| enbId.startsWith("65") || enbId.startsWith("66") || enbId.startsWith("68"))) {

				List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
						.filter(x -> (x.getNeVersionEntity() != null && "Windsor_Medium".equalsIgnoreCase(x.getNeName())
								&& "New England".equalsIgnoreCase(x.getNeMarket())
								&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
						.collect(Collectors.toList());
				if (!individualNwDet.isEmpty()) {
					objNetworkConfigEntity = individualNwDet.get(0);
				}
			}

			status = true;
		} catch (Exception e) {
			logger.error(
					"Exception  getNeMappingEntity() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objNetworkConfigEntity;

	}

	/**
	 * This api returns Ne Mapping status
	 * 
	 * @param List,Integer,String
	 * @return boolean
	 */
	public boolean saveNeMappingConfig(List<NeMappingEntity> neMappingEntities, Integer programId, String programName) {
		boolean status = false;
		try {
			if (neMappingEntities != null && neMappingEntities.size() > 0) {
				List<NeMappingEntity> newNeMappingEntityList = neMappingEntities.stream()
						.filter(x -> (x.getNetworkConfigEntity() == null)).collect(Collectors.toList());

				List<NetworkConfigEntity> listNetworkConfigEntity = objFileUploadRepository
						.getNeConfigVersionMarketBased(programId);

				NetworkConfigEntity Series70 = null;
				NetworkConfigEntity Series71 = null;
				NetworkConfigEntity Series56 = null;
				NetworkConfigEntity Series59 = null;

				if (!newNeMappingEntityList.isEmpty()) {
					for (NeMappingEntity locneMappingEntity : newNeMappingEntityList) {

						if ((locneMappingEntity.getEnbId().startsWith("70")
								|| locneMappingEntity.getEnbId().startsWith("73"))) {
							if (Series70 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series70);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "Rochester".equalsIgnoreCase(x.getNeName())
												&& "Upstate NY".equalsIgnoreCase(x.getNeMarket())
												&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());
								if (!individualNwDet.isEmpty()) {
									Series70 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series70);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}

						} else if ((locneMappingEntity.getEnbId().startsWith("71")
								|| locneMappingEntity.getEnbId().startsWith("72")
								|| locneMappingEntity.getEnbId().startsWith("74"))) {

							if (Series71 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series71);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "East_Syracuse".equalsIgnoreCase(x.getNeName())
												&& "Upstate NY".equalsIgnoreCase(x.getNeMarket())
												&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());
								if (!individualNwDet.isEmpty()) {
									Series71 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series71);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}
						} else if ((locneMappingEntity.getEnbId().startsWith("56")
								|| locneMappingEntity.getEnbId().startsWith("57")
								|| locneMappingEntity.getEnbId().startsWith("58")
								|| locneMappingEntity.getEnbId().startsWith("61")
								|| locneMappingEntity.getEnbId().startsWith("62"))) {

							if (Series56 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series56);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "WestBorough_Medium".equalsIgnoreCase(x.getNeName())
												&& "New England".equalsIgnoreCase(x.getNeMarket())
												&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());

								if (!individualNwDet.isEmpty()) {
									Series56 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series56);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}
						} else if ((locneMappingEntity.getEnbId().startsWith("59")
								|| locneMappingEntity.getEnbId().startsWith("60")
								|| locneMappingEntity.getEnbId().startsWith("64")
								|| locneMappingEntity.getEnbId().startsWith("65")
								|| locneMappingEntity.getEnbId().startsWith("66")
								|| locneMappingEntity.getEnbId().startsWith("68"))) {

							if (Series59 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series59);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "Windsor_Medium".equalsIgnoreCase(x.getNeName())
												&& "New England".equalsIgnoreCase(x.getNeMarket())
												&& "20.A.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());
								if (!individualNwDet.isEmpty()) {
									Series59 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series59);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}
						}

					}
				}
			}
			status = true;
		} catch (Exception e) {
			logger.error("Exception  saveNeConfig() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	/**
	 * This api returns Ne Mapping status
	 * 
	 * @param List,Integer,String
	 * @return boolean
	 */
	public boolean saveNeversion20BMappingConfig(List<NeMappingEntity> neMappingEntities, Integer programId,
			String programName) {
		boolean status = false;
		try {
			if (neMappingEntities != null && neMappingEntities.size() > 0) {
				List<NeMappingEntity> newNeMappingEntityList = neMappingEntities;

				List<NetworkConfigEntity> listNetworkConfigEntity = objFileUploadRepository
						.getNeConfigVersionMarketBased(programId);
				List<String> ne20bList = getNeList();
				NetworkConfigEntity Series7 = null;
				NetworkConfigEntity Series57 = null;
				NetworkConfigEntity Series59 = null;

				if (!newNeMappingEntityList.isEmpty()) {
					for (NeMappingEntity locneMappingEntity : newNeMappingEntityList) {

						if ((ne20bList.contains(locneMappingEntity.getEnbId())
								&& locneMappingEntity.getEnbId().startsWith("7"))) {
							if (Series7 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series7);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "Rochester".equalsIgnoreCase(x.getNeName())
												&& "Upstate NY".equalsIgnoreCase(x.getNeMarket())
												&& "20.B.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());
								if (!individualNwDet.isEmpty()) {
									Series7 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series7);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								} else {
									locneMappingEntity.setNetworkConfigEntity(null);
									locneMappingEntity.setSiteConfigType(null);
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}

						} else if ((ne20bList.contains(locneMappingEntity.getEnbId())
								&& locneMappingEntity.getEnbId().startsWith("57"))) {

							if (Series57 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series57);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "WestBorough_Medium".equalsIgnoreCase(x.getNeName())
												&& "New England".equalsIgnoreCase(x.getNeMarket())
												&& "20.B.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());

								if (!individualNwDet.isEmpty()) {
									Series57 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series57);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								} else {
									locneMappingEntity.setNetworkConfigEntity(null);
									locneMappingEntity.setSiteConfigType(null);
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}
						} else if ((ne20bList.contains(locneMappingEntity.getEnbId()))
								&& (locneMappingEntity.getEnbId().startsWith("59")
										|| locneMappingEntity.getEnbId().startsWith("61")
										|| locneMappingEntity.getEnbId().startsWith("66"))) {

							if (Series59 != null) {
								locneMappingEntity.setNetworkConfigEntity(Series59);
								locneMappingEntity.setSiteConfigType("NB-IoT Add");
								ciqUploadRepository.saveEnbDetails(locneMappingEntity);
							} else {
								List<NetworkConfigEntity> individualNwDet = listNetworkConfigEntity.parallelStream()
										.filter(x -> (x.getNeVersionEntity() != null
												&& "WestBorough_Tiny".equalsIgnoreCase(x.getNeName())
												&& "New England".equalsIgnoreCase(x.getNeMarket())
												&& "20.B.0".equalsIgnoreCase(x.getNeVersionEntity().getNeVersion())))
										.collect(Collectors.toList());
								if (!individualNwDet.isEmpty()) {
									Series59 = individualNwDet.get(0);
									locneMappingEntity.setNetworkConfigEntity(Series59);
									locneMappingEntity.setSiteConfigType("NB-IoT Add");
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								} else {
									locneMappingEntity.setNetworkConfigEntity(null);
									locneMappingEntity.setSiteConfigType(null);
									ciqUploadRepository.saveEnbDetails(locneMappingEntity);
								}
							}
						}

					}
				}
			}
			status = true;
		} catch (Exception e) {
			logger.error("Exception  saveNeConfig() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;

	}

	List<String> getNeList() {
		StringBuilder builderString = new StringBuilder();
		builderString.append(
				"57075,57282,59417,61029,61163,61181,61192,61195,61202,61210,61287,61297,61452,66036,66089,66142,70050,70052,70113,70282,70351,73006,73020,73026,73033,73034,73035,73056,73067,73086,73090,73109,73110,73117,73182,73183,73187,73189,");
		builderString.append(
				"73190,73215,73217,73225,73248,73270,73271,73550,73553,73847,73873,73883,73884,73906,73907,73921,73922,73923,73924,73927,73929,73994,73998,57003,57013,57015,57019,57020,57023,57025,57041,57069,57142,57189,57192,57215,57258,57311,57322,57324,57333,57339,");
		builderString.append(
				"57708,57711,57001,57016,57032,57039,57044,57047,57096,57102,57111,57122,57124,57143,57152,57153,57178,57179,57214,57267,57306,57402,57415,57497,57709");
		List<String> listData = Arrays.asList(builderString.toString().split(","));
		return listData;
	}

	@Override
	public Map<String, List<String>> getipDUidList(List<Map<String, String>> objList, String id, String fileName,
			String dbcollectionFileName) {
		Map<String, List<String>> ipDuidList = new HashMap<>();
		try {
			List<Map<String, String>> emsipList = getEnbDetailssheet(id, fileName, "vDUHELM(Day0)Orchestrator",
					dbcollectionFileName);
			if (!ObjectUtils.isEmpty(objList)) {
				for (Map<String, String> enb : objList) {
					List<CIQDetailsModel> neidList = getCiqDetailsForRuleValidationsheet(enb.get("eNBId"),
							dbcollectionFileName, "vDUGrowSiteLevel(Day1)CQ", "eNBId");
					if (!ObjectUtils.isEmpty(neidList) && neidList.get(0).getCiqMap().containsKey("NEID")) {
						String neid = neidList.get(0).getCiqMap().get("NEID").getHeaderValue();
						for (Map<String, String> emsip : emsipList) {
							if (emsip.get("eNBId").equalsIgnoreCase(neid)) {
								if (ipDuidList.containsKey(emsip.get("eNBName"))) {
									ipDuidList.get(emsip.get("eNBName")).add(enb.get("eNBId"));
								} else {
									ArrayList<String> enbid = new ArrayList<>();
									enbid.add(enb.get("eNBId"));
									ipDuidList.put(emsip.get("eNBName"), enbid);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception in getipDUidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipDuidList;
	}

	public List<Map<String, String>> getEnbDetailssheet(String id, String fileName, String sheetname,
			String dbcollectionFileName) {
		List<Map<String, String>> objCiqenodeBEntity = null;

		try {
			objCiqenodeBEntity = objFileUploadRepository.getEnbDetailssheet(id, fileName, sheetname,
					dbcollectionFileName);
		} catch (Exception e) {
			logger.error(
					"Exception  getenodebDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqenodeBEntity;

	}

	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname) {
		List<CIQDetailsModel> resultList = null;
		try {
			resultList = objFileUploadRepository.getCiqDetailsForRuleValidationsheet(enbId, dbcollectionFileName,
					sheetname, idname);

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

		}
		return resultList;
	}

	@Override
	public List<CIQDetailsModel> getsheetData(String fileName, String sheetName, String dbcollectionFileName) {
		List<CIQDetailsModel> resultData = null;

		try {
			resultData = objFileUploadRepository.getsheetData(fileName, sheetName, dbcollectionFileName);
		} catch (Exception e) {
			logger.error("Exception  getsheetData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultData;
	}

	@Override
	public List<CIQDetailsModel> getsheetData4G(String fileName, String sheetName, String dbcollectionFileName) {
		List<CIQDetailsModel> resultData = null;

		try {
			resultData = objFileUploadRepository.getsheetData4G(fileName, sheetName, dbcollectionFileName);
		} catch (Exception e) {
			logger.error("Exception  getsheetData() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return resultData;
	}

	@Override
	public Map<String, List<String>> getipGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey) {
		Map<String, List<String>> ipgnbidList = new HashMap<>();
		try {
			if (!ObjectUtils.isEmpty(gnbDataList)) {
				for (CIQDetailsModel gnbData : gnbDataList) {
					if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey(enbIdKey)) {
						String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
						if (ipgnbidList.containsKey(ip)) {
							ipgnbidList.get(ip).add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
						} else {
							List<String> gnbidlist = new ArrayList<>();
							gnbidlist.add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
							ipgnbidList.put(ip, gnbidlist);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipgnbidList;
	}

	@Override
	public Map<String, List<String>> getUSMIPList(List<CIQDetailsModel> enbDataList) {
		Map<String, List<String>> ipenbidList = new HashMap<>();
		LinkedList<String> enbList = new LinkedList<String>();
		try {
			// List<String> ipList=new LinkedList<String>();
			if (!ObjectUtils.isEmpty(enbDataList)) {
				for (CIQDetailsModel gnbData : enbDataList) {

					if (gnbData.getCiqMap().containsKey("USM_IP")
							&& gnbData.getCiqMap().containsKey("Samsung_eNB_ID")) {
						String usmip = gnbData.getCiqMap().get("USM_IP").getHeaderValue();
						int enbCount = Collections.frequency(enbList,
								gnbData.getCiqMap().get("Samsung_eNB_ID").getHeaderValue().toString());

						if (ipenbidList.containsKey(usmip)) {
							/*
							 * if (enbList.stream().noneMatch(enb ->
							 * enb.equals(gnbData.getCiqMap().get("Samsung_eNB_ID")
							 * .getHeaderValue().replaceAll("^0+(?!$)", "")))
							 */

							if (enbCount <= 1) {
								enbList.add(gnbData.getCiqMap().get("Samsung_eNB_ID").getHeaderValue()
										.replaceAll("^0+(?!$)", ""));

								ipenbidList.get(usmip).add(gnbData.getCiqMap().get("Samsung_eNB_ID").getHeaderValue()
										.replaceAll("^0+(?!$)", ""));
							}

						} else {
							/*
							 * if (enbList.stream().noneMatch(enb ->
							 * enb.equals(gnbData.getCiqMap().get("Samsung_eNB_ID")
							 * .getHeaderValue().replaceAll("^0+(?!$)", ""))) ||
							 */
							if (enbCount <= 1) {
								List<String> enbidlist = new LinkedList<String>();
								enbList.add(gnbData.getCiqMap().get("Samsung_eNB_ID").getHeaderValue()
										.replaceAll("^0+(?!$)", ""));
								enbidlist.add(gnbData.getCiqMap().get("Samsung_eNB_ID").getHeaderValue()
										.replaceAll("^0+(?!$)", ""));
								ipenbidList.put(usmip, enbidlist);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipenbidList;
	}

	@Override
	public Map<String, List<Map<String, String>>> getEnbDetails5GMM(String id, String fileName,
			String dbcollectionFileName) {
		Map<String, List<Map<String, String>>> objCiqenodeBEntity = null;

		try {
			objCiqenodeBEntity = objFileUploadRepository.getEnbDetails5GMM(id, fileName, dbcollectionFileName);
		} catch (Exception e) {
			logger.error(
					"Exception  getenodebDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return objCiqenodeBEntity;

	}
	
	@Override
	public boolean upDateCiqNameInNeMapping(String ciqName, CopyOnWriteArraySet<String> setNeids,Integer programId) {
		// TODO Auto-generated method stub
		boolean status=false;
		try {
			status = objFileUploadRepository.upDateCiqNameInNeMapping(ciqName, setNeids,programId);
		} catch (Exception e) {
			logger.error(
					"Exception  getenodebDetails() in  FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public Map<String, List<NeMappingEntity>> getCiqWithNeMappinDetails(HashSet<String> setNeIds,String programId) {
		Map<String, List<NeMappingEntity>> objLinkedHashMap=new LinkedHashMap<>();
		int pID=Integer.parseInt(programId);
		try {
			List<NeMappingEntity> entityList = objFileUploadRepository.getCiqWithNeMappinDetails(setNeIds,pID);
			if(!ObjectUtils.isEmpty(entityList))
			{
				objLinkedHashMap= entityList.stream().filter(neMappingEntity->StringUtils.isNotEmpty(neMappingEntity.getCiqName())).collect(Collectors.groupingBy(NeMappingEntity::getCiqName, Collectors.toList()));
			}
		} catch (Exception e) {
			logger.error( ExceptionUtils.getFullStackTrace(e));
		}
		return objLinkedHashMap;
	}
	
	@Override
	public Map<String, String> getNeversionList(List<CIQDetailsModel> enbDataList, String gnbIdName, String neVersionKey) {
		Map<String, String> neVerionList = new HashMap<>();
		try {
			if (!ObjectUtils.isEmpty(enbDataList)) {
				for (CIQDetailsModel gnbData : enbDataList) {

					if (gnbData.getCiqMap().containsKey(neVersionKey)
							&& gnbData.getCiqMap().containsKey(gnbIdName)) {
						String neVersion = gnbData.getCiqMap().get(neVersionKey).getHeaderValue().toUpperCase();
						String enbId = gnbData.getCiqMap().get(gnbIdName).getHeaderValue().replaceAll("^0+(?!$)", "");
						if(!neVerionList.containsKey(enbId))
							neVerionList.put(enbId, neVersion);
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getNeversionList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return neVerionList;
	}
	
	@Override
	public Map<String, List<String>> getipEnbGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey, Map<String, List<String>> ipgnbidList) {
		try {
			if (!ObjectUtils.isEmpty(gnbDataList)) {
				for (CIQDetailsModel gnbData : gnbDataList) {
					if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey(enbIdKey)) {
						String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
						if (ipgnbidList.containsKey(ip)) {
							ipgnbidList.get(ip).add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
						} else {
							List<String> gnbidlist = new ArrayList<>();
							gnbidlist.add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
							ipgnbidList.put(ip, gnbidlist);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipgnbidList;
	}
	@Override
	public Map<String, List<String>> getipAupfGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey, Map<String, List<String>> ipgnbidList) {
		try {
			if (!ObjectUtils.isEmpty(gnbDataList)) {
				for (CIQDetailsModel gnbData : gnbDataList) {
					if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey(enbIdKey)) {
						String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
						if (ipgnbidList.containsKey(ip)) {
							ipgnbidList.get(ip).add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
						} else {
							List<String> gnbidlist = new ArrayList<>();
							gnbidlist.add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
							ipgnbidList.put(ip, gnbidlist);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipgnbidList;
	}
	@Override
	public Map<String, List<String>> getDSSipAupfGnbidList(List<Map<String, String>> objList3,List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey, Map<String, List<String>> ipgnbidList) {
		try {
			if(!ObjectUtils.isEmpty(objList3)){
				for (Map<String, String> obj : objList3) {
					
				String Neid=obj.get("NEID");
					if (!ObjectUtils.isEmpty(gnbDataList)) {
						for (CIQDetailsModel gnbData : gnbDataList) {
							if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey("NE_ID")) {
								if( (gnbData.getCiqMap().get("NE_ID").getHeaderValue()).equals(Neid)) {
								String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
								if (ipgnbidList.containsKey(ip)) {
									ipgnbidList.get(ip).add(obj.get("eNBId")
									.replaceAll("^0+(?!$)", ""));
								} else {
							List<String> gnbidlist = new ArrayList<>();
							gnbidlist.add(obj.get("eNBId")
									.replaceAll("^0+(?!$)", ""));
							ipgnbidList.put(ip, gnbidlist);
						}
					}
							}
				}
			}
			}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipgnbidList;
	}
	@Override
	public Map<String, List<String>> getipAcpfGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey, Map<String, List<String>> ipgnbidList) {
		try {
			if (!ObjectUtils.isEmpty(gnbDataList)) {
				for (CIQDetailsModel gnbData : gnbDataList) {
					if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey(enbIdKey)) {
						String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
						if (ipgnbidList.containsKey(ip)) {
							if(!ipgnbidList.get(ip).contains(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""))) {
								ipgnbidList.get(ip).add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
										.replaceAll("^0+(?!$)", ""));
							}
							
						} else {
							List<String> gnbidlist = new ArrayList<>();
							gnbidlist.add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
									.replaceAll("^0+(?!$)", ""));
							ipgnbidList.put(ip, gnbidlist);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return ipgnbidList;
	}
	@Override
	public List<Map<String, String>> getCBandEnbList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
		List<Map<String, String>> enbList = new ArrayList<>();
		try {
			if (!ObjectUtils.isEmpty(gnbDataList)) {
				for (CIQDetailsModel gnbData : gnbDataList) {
					if (gnbData.getCiqMap().containsKey(enbIdKey)) {
						Map<String, String> tempMap = new HashMap<>();
						tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
						
						enbList.add(tempMap);
						
					}
				}
			}
		} catch (Exception e) {
			logger.error(
					"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
		}
		return enbList;
	}

@Override
public List<Map<String, String>> getCBandAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public List<Map<String, String>> getDSSAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					//tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public List<Map<String, String>> getDSSNemappingrowAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					//tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public List<Map<String, String>> getDSSNemappingFSUList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					//tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public Map<String, List<String>> getipGnbidListFsu(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey,Map<String,List<String>> ipgnbidList) {
	
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(ipKey) && gnbData.getCiqMap().containsKey(enbIdKey)) {
					String ip = gnbData.getCiqMap().get(ipKey).getHeaderValue();
					if (ipgnbidList.containsKey(ip)) {
						ipgnbidList.get(ip).add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
								.replaceAll("^0+(?!$)", ""));
					} else {
						List<String> gnbidlist = new ArrayList<>();
						gnbidlist.add(gnbData.getCiqMap().get(enbIdKey).getHeaderValue()
								.replaceAll("^0+(?!$)", ""));
						ipgnbidList.put(ip, gnbidlist);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return ipgnbidList;
} 	
@Override
public List<Map<String, String>> getCBandACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public List<Map<String, String>> getDSSACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
@Override
public List<Map<String, String>> getDSSrowforNemappingACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey) {
	List<Map<String, String>> enbList = new ArrayList<>();
	try {
		if (!ObjectUtils.isEmpty(gnbDataList)) {
			for (CIQDetailsModel gnbData : gnbDataList) {
				if (gnbData.getCiqMap().containsKey(enbIdKey)) {
					Map<String, String> tempMap = new HashMap<>();
					tempMap.put("eNBId", gnbData.getCiqMap().get(enbIdKey).getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					//tempMap.put("NEID", gnbData.getCiqMap().get("NEID").getHeaderValue().trim().replaceAll("^0+(?!$)", ""));
					if(!enbList.contains(tempMap))
					{
					enbList.add(tempMap);
					}
				}
			}
		}
	} catch (Exception e) {
		logger.error(
				"Exception in getipGnbidList()   FileUploadServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
	}
	return enbList;
}
}
