package com.smart.rct.premigration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.EnbPreGrowAuditModel;
import com.smart.rct.premigration.models.GenerateInfoAuditModel;

public interface GenerateCsvService {

	public boolean csvFileGeneration(String ciqFileName, String pathCsv, Integer customerId, Integer networkTypeId,
			String lsmVersion, String sessionId);

	public Map<String, Object> getCsvAuditDetails(GenerateInfoAuditModel csvModel, int page, int count);

	public JSONObject transferCiqFile(List<GenerateInfoAuditModel> csvInfo, int iLsmId, JSONObject resultMap);

	public boolean ciqFileValidation(String ciqFileName, String pathCsv, Integer customerId, Integer networkTypeId,
			String lsmVersion, String sessionId);

	public List<GenerateInfoAuditModel> getCsvFilesList(int customerId);

	public List<UseCaseBuilderModel> getUseCaseDetails(Integer customerId);

	public JSONObject transferCiqFileAndGrow(EnbPreGrowAuditModel csvInfo, JSONObject resultMap);

	public Map<String, Object> generateFilesListSearch(GenerateInfoAuditModel objCsvInfoAuditModel, int customerId, int page,
			int count);

	public JSONObject envFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath,String fileType, String sessionId, NeMappingEntity neMappingEntity, String remarks, Boolean supportCA);

	public JSONObject csvFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, Boolean supportCA, String dummy_IP);

	public JSONObject commissionScriptFileGeneration(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks);

	public boolean deleteGeneratedFileDetails(GenerateInfoAuditModel csvAuditdetails);

	public boolean updateGeneratedFileDetails(GenerateInfoAuditEntity generateInfoAuditEntity);

	public GenerateInfoAuditEntity getGenerateInfoAuditById(Integer id);
	
	public JSONObject saveCsvAudit(GenerateInfoAuditEntity objInfo);

	public JSONObject deleteUploadScriptFileDetails(String neId, String filePath, String sessionId);

	public JSONObject csvFileGeneration(String ciqFileName, String enbName, String enbName2, Integer programId) throws IOException;

	

	public JSONObject envFileGenerationForFsu(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
Integer programId, String filePath, String sessionId,String ovType, String fileType, String remarks, String neVersion, String fsuType, List<CIQDetailsModel> listCIQDetailsModel);

	public JSONObject csvFileGenerationForFsu(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String ovFileType, String fileType, String remarks,String neVersion, String fsuType, List<CIQDetailsModel> listCIQDetailsModel);

	public String[] dataCiqIndexs(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowDspCellIndex,
			String vzGrowCiqupstateny);
	
	public JSONObject envFileGenerationFor5G(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, 
			String remarks);
	

	public JSONObject routeFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks,String tempFolder,String date,String validation);
	
	public List<CIQDetailsModel> getCIQDetailsModelList(String enbId, String dbcollectionFileName);
	
	public JSONObject envFileGenerationFor5G28ghz(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,
			String remarks,String programName);

	public JSONObject a1a2ConfigFileGeneration(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,
			String remarks,String tempFolder,String date,String validation);

	public JSONObject a1a2CreateFileGeneration(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,
			String remarks,String tempFolder,String date,String validation);
	
	public JSONObject generateTemplates(String ciqFileName, String version, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId,
			String remarks, String releaseVer, NeMappingEntity neMappingEntity);

	public JSONObject cslTemplete(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, Set<String> neList,String tempFolder,String dateString,String validation);

	public JSONObject additionalTemplete(String version, String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String string, String sessionId, String string2,
			NeMappingEntity neMappingEntity, String remarks,String tempFolder,String date,String validation);

	public JSONObject endcTemplate(String nrfreq,String folderName,String enbid, String version, String ciqFileName, String enbId2, String enbName,
			String dbcollectionFileName, Integer programId, String filePath, String sessionId, String fileType,
			NeMappingEntity neMappingEntity, String remarks,String validation);

	public String folderName(String gnbidd);


	public JSONObject offsetFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation);

	public JSONObject tilt(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation);

	JSONObject gpScriptFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, String tempFolder, String date, String validation);

	public JSONObject cpriFileGeneration(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String string, String sessionId, NeMappingEntity neMappingEntity, String remarks);

	public boolean checkEmptyValues(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowCiqupstateny);

	public boolean checkEnbExistence(List<CIQDetailsModel> listCIQDetailsModel, String vzGrowCiqupstateny);

	JSONObject cpriFileGenerationFSU(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, NeMappingEntity neMappingEntity, String remarks, String fsuType, List<CIQDetailsModel> listCIQDetailsModel);

	JSONObject csvGenerationCBand(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks);

	JSONObject csvGenerationDSS(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String string, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks);

	JSONObject csvGenerationDSSAu(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks);

	JSONObject csvGenerationDSSAuPf(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks);

	JSONObject csvGenerationCBandCell(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId,String filetype, NeMappingEntity neMappingEntity, String remarks);

	JSONObject csvGenerationCBandADPF(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String filetype, NeMappingEntity neMappingEntity,
			String remarks);

	 JSONObject commissionScriptFileGenerationFSU(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String string, String sessionId,
			String growCommScriptFileTypeXml, NeMappingEntity neMappingEntity, String remarks);

	public JSONObject commissionScriptFileGenerationNTPFSU(String ciqFileName, String enbId, String enbName,
			String dbcollectionFileName, Integer programId, String string, String sessionId,
			String growCommScriptFileTypeXml, NeMappingEntity neMappingEntity, String remarks);
	
	//file with dummy suffix
	public JSONObject envFileGenerationDummyIP(String ciqFileName, String enbId, String enbName, String dbcollectionFileName,
			Integer programId, String filePath, String sessionId, String fileType, NeMappingEntity neMappingEntity,
			String remarks, Boolean supportCA);


	public JSONObject envUploadToOVFromPremigration(JSONObject ovUpdateJson, GenerateInfoAuditEntity generateInfoAuditEntity);
	public StringBuffer checkEmptyValuesForPTP(String dbcollectionFileName,String enbId);//ptp
	
	
	
}
