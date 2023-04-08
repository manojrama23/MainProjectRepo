package com.smart.rct.migration.repository;

import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;

import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseCmdRuleEntity;
import com.smart.rct.migration.entity.UseCaseFileRuleEntity;
import com.smart.rct.migration.entity.UseCaseShellRuleEntity;
import com.smart.rct.migration.entity.UseCaseXmlRuleEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.SearchModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.usermanagement.models.User;

public interface UseCaseBuilderRepository {

	/*public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity, List<UseCaseScriptsModel> scriptList,
			Integer customerId, String neName, String neVersion,String migrationType,String subType);*/

	public List<String> getNwTypeList();

	public List<LsmEntity> getLsmNameList();

	public List<UploadFileEntity> getScriptList(Integer customerId);

	public List<CmdRuleBuilderEntity> getCommandRuleList(Integer customerId,String migrationType,String subType);
	
	public List<ShellCmdRuleBuilderEntity> getShellCommandRuleList(Integer programId, String migrationType, String subType);

	public List<FileRuleBuilderEntity> getFileRuleList(Integer customerId,String migrationType,String subType);

	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(SearchModel searchModel, Integer programId, String migrationType, String subType);

	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(Integer programId, String migrationType, String subType);

	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(int page, int count, Integer customerId,
			String migrationType, int programId, String subType, User user);

	public Map<String,Object> loadUseCaseBuilderSearchDetails(int page, int count, SearchModel searchModel,
			Integer customerId, String migrationType, int programId, String subType, User user);

	public String getNwTypeName(int id);

	public Map<String, String> getlsmDetails(int id);

	public NetworkTypeDetailsEntity getNwTypeEntity(String nwType);

	public NetworkConfigEntity getLsmEntity(String lsmName, String lsmVersion,int programId);

	public CmdRuleBuilderEntity getCommandRuleEntity(String cmdRuleName, Integer customerId,String migrationType,String subType);
	
	public ShellCmdRuleBuilderEntity getShellRuleEntity(String cmdRuleName, Integer programId, String migrationType,
			String subType);

	public FileRuleBuilderEntity getFileRuleEntity(String fileRuleName, Integer customerId,String migrationType,String subType);

	public boolean updateUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity, List<UseCaseScriptsModel> scriptList,
			Integer customerId, Integer programId,String migrationType,String subType);

	public UseCaseBuilderEntity getUseCaseBuilderEntity(Integer id);

	public boolean deleteUseCaseBuilder(Integer id);

	public UseCaseBuilderParamEntity getUseCaseBuilderParamEntity(Integer id);
	
	public List<UseCaseBuilderParamEntity> getUseCaseBuilderParamUseCase(UseCaseBuilderParamEntity id);
	
	public List<UseCaseCmdRuleEntity> getUseCaseCmdRuleEntityList(Integer id);
	
	public List<UseCaseShellRuleEntity> getUseCaseShellRuleEntityList(Integer id);
	
	public List<UseCaseFileRuleEntity> getUseCaseFileRuleEntityList(Integer id);
	
	public List<UseCaseXmlRuleEntity> getUseCaseXmlRuleEntityList(Integer id);

	public List<UploadFileEntity> getUploadFileEntityList(NetworkConfigEntity networkConfigEntity);

	public List<LsmEntity> getLsmEntityList(NetworkTypeDetailsEntity networkTypeDetailsEntity);

	public UploadFileEntity getUploadFileEntityByScriptId(String scriptFileId);

	public Map<String, List<String>> getSmList(int programId);

	public Map<String, Map<String, List<Map<String, String>>>> getSmScriptList(int programId, String migrationType,String subType);

	public List<XmlRuleBuilderEntity> getXmlRuleList(int programId,String migrationType,String subType);

	public List<Map<String, String>> getScriptList(int programId, String migrationType);
	
	public List<Map<String, String>> scriptInfoWithoutVersionName(int programId, String migrationType,String subType);

	public boolean findByRuleName(String ruleName, int customerId, String migrationType, int programId,
			String userRole,String subType);

	public Map<String, List<Map<String, String>>> getScriptListWithoutSM(int programId, String migrationType,String subType);

	public NetworkConfigEntity getLsmEntity(String lsmVersion);
	
	public UseCaseBuilderParamEntity getEntity(int articleId);
	
	public List<UseCaseCmdRuleEntity> getCmdEntity(int articleId);
	
	public List<UseCaseShellRuleEntity> getShellEntity(int articleId);
	
	public List<UseCaseXmlRuleEntity> getXmlEntity(int articleId);
	
	public List<UseCaseFileRuleEntity> getFileEntity(int articleId);
	
	public boolean deleteUseCaseParamBuilder(int useCaseBuilderParamEntity);
	
	public int getMaxUseCaseId();
	
	public int getMaxExeSeqId(int programId);
	
	public boolean deleteUseCaseCmdRule(int useCaseBuilderCmdEntity);
	
	public boolean deleteUseCaseShellRule(int useCaseBuilderShellEntity);
	
	public boolean deleteUseCaseXmlRule(int useCaseBuilderXmlEntity);
	
	public boolean deleteUseCaseFileRule(int useCaseBuilderFileEntity);
	
	public boolean deleteUseCaseParamBuilder(UseCaseBuilderParamEntity useCaseBuilderParamEntity);
	
	public UseCaseBuilderEntity findByExecutionSequence(String executionSequence, int programId, String userRole);
	
	public UseCaseBuilderParamEntity findScriptExecutionSequence(String executionSequence, int programId);

	public Object getPageCount(int page, int count, int customerId, String migrationType, int programId, String subType,
			User user);
	
	public UseCaseBuilderEntity getUseCaseBuilderEntity(String useCaseName);
	
	public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity);
	
	public UseCaseBuilderParamEntity getUseCaseBuilderParam(int useCaseId,int uploadId);

	public List<UseCaseBuilderEntity> getUseCaseBuilderEntityList(int programId);

	public UseCaseBuilderEntity getUseCaseByName(String useCaseName, String migrationType, int programId,String subType);

	public boolean saveUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity);

	public void saveCmdRuleEntity(UseCaseCmdRuleEntity useCaseCmdRuleEntity);

	public void saveCaseShell(UseCaseShellRuleEntity useCaseShellRuleEntity);

	public void saveCaseXml(UseCaseXmlRuleEntity useCaseXmlRuleEntity);

	public boolean saveparamEntity(UseCaseBuilderParamEntity useCaseBuilderParamEntity);

	public CheckListScriptDetEntity getCheckListDetails(int programId);

	public void saveCaseFile(UseCaseFileRuleEntity useCaseFileRuleEntity);

	public List<CheckListScriptDetEntity> getExeseq(Integer programId, String scriptName, String configType,
			String checkListName);

	public XmlRuleBuilderEntity getXmlRuleBuilderEntity(String xmlRuleName, Integer programId, String migrationType,
			String subType);

	public UploadFileEntity getUploadFileEntity(String neName, String neVersion, Integer programId, String migrationType,
			String scriptName, String scriptFileId, String subType);

	public List<RunTestEntity> getRunTestDetails(String useCaseName);

	public void deleteruntestResult(int id);
		
}
