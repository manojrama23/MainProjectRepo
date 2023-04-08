package com.smart.rct.premigration.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.models.FetchDetailsModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.CheckListModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.usermanagement.models.User;

public interface FileUploadService {

	public boolean uploadMultipartFile(MultipartFile file, String uploadPath) throws RctException;

	public boolean unzipFile(String sourcePath, String destinationPath) throws RctException;

	public Map<String, Object> processCiq(MultipartFile file, String uploadPath, boolean isAllowDuplicate,
			Integer programId);

	public Map<String, Object> process5GCiq(MultipartFile file, String uploadPath, boolean isAllowDuplicate,
			Integer programId, String programName);

	public Map<String, Object> processChecklist(MultipartFile file, String uploadPath, boolean isAllowDuplicate,
			Integer programId, String ciqFileName);

	public boolean deleteCiq(CiqUploadAuditTrailDetModel ciqAuditdetails);

	public boolean deleteCheckList(CiqUploadAuditTrailDetModel ciqAuditdetails);

	public boolean createCiqAudit(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity);

	public boolean updateCiqAuditDetails(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity);

	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate);

	public Map<String, Object> retriveCiqDetails(String fileName, int page, int count);

	public boolean updateCIQDetailsBasedOnId(CIQDetailsModel ciqDetailsModel);

	public boolean updateCiqFileDetaiils(CIQDetailsModel upDateModel,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user);

	public boolean deleteCiqRowDetails(Integer id, CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user);

	public List<Map<String, String>> getEnbDetails(String id, String fileName, String dbcollectionFileName);

	public List<Map<String, String>> getEnbDetails5G(String id, String fileName, String dbcollectionFileName);

	public boolean createCiqFileDetaiils(CIQDetailsModel createCiqEntity);

	public Map<String, Object> getEnbTableDetails(String programId, String fileName, String enbId, String enbName,
			String menuName, int page, int count, String dbcollectionFileName);

	public ArrayList<String> getEnbDetailsFilename(String fileName, String enbId);

	public boolean updateCiqFileDetaiilsEndBased(CIQDetailsModel uploadedCiq, String menuName,
			CiqUploadAuditTrailDetEntity uploadedCiqAuditEntity, User user);

	public void scheduleTime(String time, int iMethodCall, JSONObject CSVObject);

	public Map<String, Set<String>> getCiqSheetNames(String fileName, String dbCollectionName);

	public Map<String, Object> getCiqSheetDisply(String fileName, String sheetName, String subSheetName,
			Map<String, String> ciqSearchMap, int page, int count);

	public Map<String, Object> getCiqAuditDetails(CiqUploadAuditTrailDetModel ciqAuditTrailModel, int page, int count);

	public Map<String, Object> fetchFileFromServer(NetworkConfigEntity networkConfigEntity,
			Map<String, Object> fileInfo, String marketName, FetchDetailsModel fetchDetailsModel, String fileType)
			throws RctException;

	public Set<String> getCheckListSheetNames(String fileName, String dbCollectionName);

	public Map<String, Object> getCheckListSheetDisply(String fileName, String sheetName, int page, int count);

	public Set<String> getCheckListAllSheetNames(String fileName, String dbCollectionName);

	public Map<String, Object> insertChecklistDetails(String fileName, String sheetName, String enodeName,
			String remarks, int runTestId);

	public Map<String, Object> getAllCheckListSheetDisply(String fileName, String sheetName, int page, int count,
			String enodeName, int runTestId);

	public boolean updateCheckListFileDetails(CheckListModel createCiqEntity, String dbCollectionName,
			String enodeName);

	public boolean saveCheckListFileDetaiils(CheckListDetailsModel createCiqEntity, String dbCollectionName);

	public boolean deleteCheckListRowDetails(int id, String dbCollectionName);

	public CiqUploadAuditTrailDetEntity getLatestCheckListByProgram(Integer programId);

	public boolean duplicateEnbDetails(NeMappingModel neMappingModel);

	public boolean saveEnbDetails(NeMappingEntity neMappingEntity);

	public Map<String, Object> validationCiqDetails(String dbCollectionName, Integer programId);

	public Map<String, Object> validationEnbDetails(String dbCollectionName, Integer programId, String enbName,
			String enbId);

	public Map<String, Set<String>> getCiqSheetNamesBasedOnEnb(String fileName, String dbCollectionName, String enbName,
			String enbId);

	public ProgramTemplateEntity getProgramTemplate(Integer programId, String scriptStoreTemplate);

	public String getEnBDataByPath(String collectionName, String sheetName, String enbId, String path);

	NetworkConfigEntity getNeMappingEntity(String enbId, Integer programId);

	boolean saveNeMappingConfig(List<NeMappingEntity> neMappingEntities, Integer programId, String programName);

	boolean saveNeversion20BMappingConfig(List<NeMappingEntity> neMappingEntities, Integer programId,
			String programName);

	public List<Map<String, String>> getEnbDetailssheet(String id, String fileName, String sheetname,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname);

	public Map<String, List<String>> getipDUidList(List<Map<String, String>> objList, String id, String fileName,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getsheetData(String fileName, String sheetName, String dbcollectionFileName);

	public Map<String, List<String>> getipGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey);

	public Map<String, List<Map<String, String>>> getEnbDetails5GMM(String id, String fileName,
			String dbcollectionFileName);

	public Map<String, List<String>> getUSMIPList(List<CIQDetailsModel> enbDataList);

	public List<CIQDetailsModel> getsheetData4G(String fileName, String sheetName, String dbcollectionFileName);
	public boolean upDateCiqNameInNeMapping(String ciqName,CopyOnWriteArraySet<String> setNeids,Integer programId);
	Map<String, List<NeMappingEntity>> getCiqWithNeMappinDetails(HashSet<String> setNeIds, String programId);


	Map<String, String> getNeversionList(List<CIQDetailsModel> enbDataList, String gnbIdName, String neVersionKey);

	Map<String, List<String>> getipEnbGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey,
			Map<String, List<String>> ipgnbidList);

	List<Map<String, String>> getCBandEnbList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	Map<String, List<String>> getipAupfGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey,
			Map<String, List<String>> ipgnbidList);
	Map<String, List<String>> getipAcpfGnbidList(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey,
			Map<String, List<String>> ipgnbidList);

	List<Map<String, String>> getCBandAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	List<Map<String, String>> getCBandACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	List<Map<String, String>> getDSSACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);
	List<Map<String, String>> getDSSAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	public Map<String, List<String>> getDSSipAupfGnbidList(List<Map<String, String>> objList3,
			List<CIQDetailsModel> gnbDataListday0, String string, String string2, Map<String, List<String>> ipidList);

	List<Map<String, String>> getDSSrowforNemappingACPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	List<Map<String, String>> getDSSNemappingrowAUPFList(List<CIQDetailsModel> gnbDataList, String enbIdKey);

	List<Map<String, String>> getDSSNemappingFSUList(List<CIQDetailsModel> gnbDataList, String enbIdKey);


	Map<String, List<String>> getipGnbidListFsu(List<CIQDetailsModel> gnbDataList, String ipKey, String enbIdKey,
			Map<String, List<String>> ipgnbidList);

	

}