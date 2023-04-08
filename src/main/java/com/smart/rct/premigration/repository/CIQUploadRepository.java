package com.smart.rct.premigration.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.models.CIQDetailsModel;
import com.smart.rct.premigration.models.CheckListDetailsModel;
import com.smart.rct.premigration.models.CheckListModel;
import com.smart.rct.premigration.models.NeMappingModel;

public interface CIQUploadRepository {

	public CIQDetailsModel save(CIQDetailsModel entity, String dbCollectionName);

	public List<CIQDetailsModel> findAll(String fileName);

	public Page<CIQDetailsModel> findByFileName(String fileName, Pageable pageableRequest);

	public boolean updateCiqFileDetails(CIQDetailsModel objpublic);

	public boolean createCiqFileDetaiils(CIQDetailsModel createCiqEntity);

	public CIQDetailsModel getEnbTableDetailsById(String fileName, Integer id, String dbCollcetionDbName);

	public boolean updateCiqFileDetailsEnbs(CIQDetailsModel entity);

	public List<CIQDetailsModel> getCiqSheetNames(String fileName, String dbCollectionName);

	public Map<String, Object> getCiqSheetDisply(String fileName, String sheetName,String subSheetName,Map<String,String> ciqSearchMap, int page, int count);

	public List<CIQDetailsModel> getCiqFileDetails(String fileName);

	public CheckListDetailsModel saveCheckList(CheckListDetailsModel entity, String dbCollectionName);

	public List<CheckListDetailsModel> getCheckListSheetNames(String fileName, String dbCollectionName);
	
	public List<CheckListDetailsModel> getCheckListAllSheetNames(String fileName, String dbCollectionName);

	public Map<String, Object> getCheckListSheetDisply(String programFileName, String sheetName, int page, int count);
	
	public Map<String, Object> getAllCheckListSheetDisply(String programFileName, String sheetName, int page, int count,String enodeName,int runTestId);
	
	public List<CheckListModel> getCheckListDetails(String programFileName,String enodeName,int runTestId,int stepIndex);
	
	public Map<String, Object> insertChecklistDetails(String programFileName, String sheetName,String enodeName,String remarks,int runTestId);

	public boolean saveCheckListFileDetaiils(CheckListDetailsModel createCiqEntity, String dbCollectionName);

	public boolean updateCheckListFileDetaiils(CheckListDetailsModel createCheckListEntity, String dbCollectionName);
	
	public boolean updateCheckListFileDetails(CheckListModel createCheckListEntity, String dbCollectionName,String enodeName);

	public boolean deleteCheckListRowDetails(int id, String dbCollectionName);

	public boolean duplicateEnbDetails(NeMappingModel neMappingModel);

	public boolean saveEnbDetails(NeMappingEntity neMappingEntity);
	
	public List<CIQDetailsModel> getCiqSheetNamesBasedOnEnb(String fileName,String dbCollectionName,String enbName,String enbId);
}
