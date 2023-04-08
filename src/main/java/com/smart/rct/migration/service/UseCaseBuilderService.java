package com.smart.rct.migration.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.model.SearchModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.PaginationModel;

public interface UseCaseBuilderService {

	public boolean createUseCaseBuilder(UseCaseBuilderModel useCaseBuilderModel, Integer customerId,
			String migrationType, int programId, String subType, String sessionId) throws RctException;

	public List<String> getNwTypeList();

	public Map<String, List<String>> getLsmNameList();

	public Map<Integer, String> getScriptList(Integer customerId);

	public List<Map<String, String>> getCommandRuleList(Integer customerId,String migrationType,String subType);
	
	public List<Map<String, String>> getShellRuleList(int programId, String migrationType, String subType);

	public List<Map<String, String>> getFileRuleList(Integer customerId,String migrationType,String subType);

	public int loadUseCaseBuilderDetails(Integer programId, String migrationType, String subType);

	public List<UseCaseBuilderModel> loadUseCaseBuilderDetails(PaginationModel paginationModel, Integer customerId,
			String migrationType, int programId, String subType, User user);

	public Map<String,Object> loadUseCaseBuilderSearchDetails(PaginationModel paginationModel,
			SearchModel searchModel, Integer customerId, String migrationType, int programId, String subType, User user);

	public boolean deleteUseCaseBuilder(Integer id);

	public boolean updateUseCaseBuilder(UseCaseBuilderModel useCaseBuilderModel, Integer customerId,
			String migrationType, int programId, String subType, String sessionId) throws RctException;

	public JSONObject getScriptDetails(List<NetworkTypeDetailsModel> neList);

	public int loadUseCaseBuilderDetails(SearchModel searchModel, Integer customerId, String migrationType, String subType);

	public Map<String, List<String>> getSmList(int programId);

	public Map<String, Map<String, List<Map<String, String>>>> getSmScriptList(int programId, String migrationType,String subType);

	public List<Map<String, String>> getXmlRuleList(int programId,String migrationType,String subType);

	public List<Map<String, String>> getScriptList(int programId, String migrationType);
	
	public List<Map<String, String>> scriptInfoWithoutVersionName(int programId, String migrationType,String subType);

	public boolean duplicateUseCaseName(String useCaseName, int customerId, String migrationType, int programId,
			String role,String subType);
	
	public UseCaseBuilderEntity getUseCaseByName(String useCaseName, String migrationType, int programId, String subType);

	public Map<String, List<Map<String, String>>> getScriptListWithoutSM(int programId, String migrationType,String subType);
	
	public UseCaseBuilderEntity duplicateExecutionSequence(String executionSequence, int programId, String userRole);
	
	public UseCaseBuilderParamEntity duplicateScriptExecutionSequence(String executionSequence, int programId);

	public Object getPageCount(int page, int count, int customerId, String migrationType,
			int programId, String subType, User user);
	
	public UseCaseBuilderEntity getUseCaseBuilderEntity(String useCaseName);
	
	public UseCaseBuilderEntity getUseCaseBuilderEntity(Integer useCaseId);
	
	public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity);
	
	public UseCaseBuilderParamEntity getUseCaseBuilderParam(int useCaseId,int uploadId);
	
	public List<UseCaseBuilderEntity> getUseCaseBuilderEntityList(int programId);

	public List<RunTestEntity> getRunTestDetails(String useCaseName);
	
}
