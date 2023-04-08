package com.smart.rct.premigration.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.premigration.entity.CiqUploadAuditTrailDetEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CiqUploadAuditTrailDetModel;
import com.smart.rct.usermanagement.models.User;

public interface FileUploadRepository {

	public boolean createCiqAudit(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity);

	public boolean deleteCiq(int fileId);

	public Map<String, Object> getCiqAuditDetails(int page, int count, Integer customerId);

	public boolean updateCiqAuditDetails(CiqUploadAuditTrailDetEntity objCiqUploadAuditTrailEntity);

	public boolean deleteCiqDetailsByFilename(String fileName);

	public List<CiqUploadAuditTrailDetEntity> getCiqList(User user, String programId, String fromDate, String toDate);

	public boolean deleteCiqRowDetails(int id, String fileName);

	public List<Map<String, String>> getEnbDetails(String id, String fileName, String dbcollectionFileName);

	public List<Map<String, String>> getEnbDetails5G(String id, String fileName, String dbcollectionFileName);

	public List<CIQDetailsModel> getEnbTableDetails(String fileName, String enbId, String enbName,
			String dbcollectionFileName);
	
	public List<CIQDetailsModel> getCIQDetailsModelList(String enbId,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getEnbTableDetailsRanConfig(String fileName, String enbId, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName);

	public ArrayList<String> getEnbDetailsFilename(String fileName, String enbId);

	public Map<String, Object> getCiqAuditDetails(CiqUploadAuditTrailDetModel ciqAuditTrailModel, int page, int count);

	public CiqUploadAuditTrailDetEntity getCiqAuditBasedONFileNameAndProgram(String fileName, Integer programId);

	public boolean deleteCheckListDetailsByFilename(String checkListDbeName);

	public ProgramTemplateEntity getProgramTemplate(Integer programId, String paramName);

	public String getEnBDataByPath(String collectionName, String sheetName, String enbId, String path);

	public List<CIQDetailsModel> getEnBData(String collectionName, String sheetName, String enbId);

	public CiqUploadAuditTrailDetEntity getLatestCheckListByProgram(Integer programId);

	public List<GrowConstantsEntity> getGrowConstantsDetails();

	public List<CIQDetailsModel> getCiqRowsByCellId(String collectionName, String sheetName, String enbId,
			String cellId);

	public List<CIQDetailsModel> getRanConfigDetailsValidation(String dbcollectionFileName, String sheetName,
			String subSheetName);

	public List<CIQDetailsModel> getRanConfigDetailsEnbValidation(String dbcollectionFileName, String sheetName,
			String subSheetName, String enbName, String enbId);

	public boolean saveGrowConstant(GrowConstantsEntity entity);

	public List<CIQDetailsModel> getEnbDetails(String enbId);

	public List<CIQDetailsModel> getEnbTableSheetDetails(String fileName, String sheetAliasName, String enbId,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getEnbTableSheetDetailss(String fileName, String sheetAliasName, String enbId,
			String dbcollectionFileName);

	public List<NetworkConfigEntity> getNeConfigVersionMarketBased(Integer programId);

	public List<CIQDetailsModel> getEnbTableDetailss(String ciqFileName, String sheetName, String enbName,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getCiqDetailsForRuleValidationsheet(String enbId, String dbcollectionFileName,
			String sheetname, String idname);

	public List<Map<String, String>> getEnbDetailssheet(String id, String fileName, String sheetname,
			String dbcollectionFileName);

	public List<CIQDetailsModel> getsheetData(String fileName, String sheetName, String dbcollectionFileName);

	public Map<String, List<Map<String, String>>> getEnbDetails5GMM(String id, String fileName,
			String dbcollectionFileName);

	public boolean updateInfo(String uniqFetchId, String info);

	public List<CIQDetailsModel> getsheetData4G(String fileName, String sheetName, String dbcollectionFileName);
	boolean upDateCiqNameInNeMapping(String ciqName, CopyOnWriteArraySet<String> setNeids,Integer programId);

	 List<NeMappingEntity> getCiqWithNeMappinDetails(HashSet<String> setNeIds, int pID);
	public Integer getciqDetails(String string);

	 public List<CIQDetailsModel> getEnbTableSheetDetailsss(String fileName, String sheetAliasName, String eNB4G,
			String dbcollectionFileName);

	List<CIQDetailsModel> getEnbTableDetailsRanConfigg(String fileName, String NEID, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName);

	List<CIQDetailsModel> getEnbTableDetailsRanConfig2(String fileName, String gNBID, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName);

	List<CIQDetailsModel> getEnbTableDetailsRanConfigg3(String fileName, String eNB4G, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName);

	List<CIQDetailsModel> getEnbTableDetailsRanConfigBySiteName(String fileName, String SiteName, String enbName,
			String dbcollectionFileName, String sheetAliasName, String subSheetAliasName);
}
