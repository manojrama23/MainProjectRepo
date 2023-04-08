package com.smart.rct.migration.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.RunTestInputEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.entity.ShellCommandEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseCmdRuleEntity;
import com.smart.rct.migration.entity.UseCaseFileRuleEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.NeMappingModel;

public interface RunTestRepository {

	public RunTestEntity createRunTest(RunTestEntity runTestEntity);

	public List<RunTestEntity> getRunTestDetails(int page, int count, Integer customerId, Integer programId,
			String migrationStatus, String migrationSubStatus, int days);

	public List<RunTestEntity> getRunTestSearchDetails(RunTestModel runTestModel, int page, int count,
			Integer customerId, Integer programId, String migrationStatus, String migrationSubStatus, int days);

	public boolean updateRunTest(RunTestEntity runTestEntity);
	
	public boolean updateWfhRunTest(WorkFlowManagementEntity workFlowManagementEntity);
	
	public boolean updateRunTestov(OvTestResultEntity ovTestResultEntity);

	// public List<RunTestEntity> getrunTestDetails();

	public boolean deleterunTest(int runTestId);

	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails();

	// public List<UploadFileEntity> getScriptInfo();

	public List<UseCaseCmdRuleEntity> getCommandRules(UseCaseBuilderParamEntity useCaseBuilderParamEntity);

	public List<CmdRuleBuilderEntity> geMigtCommandRules(UseCaseCmdRuleEntity cmdRuleEnt);

	public List<UseCaseFileRuleEntity> getFileRules(UseCaseBuilderParamEntity useCaseBuilderParamEntity);

	public int getRunTestId(Date creationDate);

	boolean updateRunTest(Integer runTestId);

	public List<UseCaseBuilderParamEntity> getScriptDetails(int usecaseID);

	UploadFileEntity getScriptInfo(int scriptID);

	public UseCaseBuilderEntity getUseCaseEntity(int useCaseId);

	public List<RunTestEntity> loadRunTestDetails(int programId, String migrationType, String migrationSubType,
			int customerId, int history, RunTestModel runTestModels);

	public boolean updateUseCountForUseCase(int useCaseId, String action, int Count);

	public RunTestEntity getRunTestEntity(Integer runTestId);
	
	public WorkFlowManagementEntity getWFMTestEntity(Integer wfmTestId);
	
	public NetworkConfigEntity getNeType(int lsmId);

	public List<Map> getSmList(int programId);

	public CmdRuleBuilderEntity getCommandRuleById(int id);

	public ShellCmdRuleBuilderEntity getshellRuleById(int id);

	public FileRuleBuilderEntity getFileRuleById(int id);

	public List<Map> getUseCaseList(int programId, String migrationType, String subType, String ciqFName,
			List<Map> neList);

	public List<Map> getUseCaseList(int programId, String migrationType, String subType);

	public String loadRunningLog(Integer runtestId);

	public XmlRuleBuilderEntity getXmlRuleById(int id);

	public String getNeRelVer(int pgmId, String lsmVersion);

	public boolean getRuntestEnbProgressStatus(String neName);

	public boolean getRunTestEntity(int programId, String migrationType, String subType, String testname);

	public RunTestEntity getRunTestEntityDetails(int programId, String migrationType, String subType, String testname,
			String neName);

	public boolean getInProgressRunTestDetails(int programId, String migrationType, String subType);

	public List<ShellCommandEntity> getShellCommandDetails(String shellCmdName);

	public Map<String, Object> getRunTestDetails(RunTestModel runTestModel, int page, int count, Integer programId,
			String migrationType, String migrationSubType,boolean wfmKey);

	NetworkConfigEntity getNetWorkEntityDetails(NeMappingModel neMappingModel);
	List<Map> getMigrationUseCaseList(int programId, String migrationType, String subType,String ciqFileName,List<String> enbId, String programName);

	RunTestInputEntity getInputRuntestJson(int runTestId);

	boolean insertRunTestInputDetails(RunTestInputEntity runTestInputEntity);

	List<Map> getMigrationUseCaseListWFM(int programId, String migrationType, String subType, String ciqFileName,
			String enbId, String programName);

	public NetworkConfigEntity getNEConfigEntity(String lsmVersion, String lsmName, CustomerDetailsEntity programDetailsEntityversion);

	List<Map> getUseCaseListPostMig(int programId, String migrationType, String subType, List<String> enbIdList);

       public boolean getusecaseDetails(int programId, String migrationType, String subType, String useCaseName);

        public String loadneOplogs(Integer runTestId);
         public boolean updatePreMigrationOv(PremigrationOvUpadteEntity premigrationOvUpadteEntity);

        GenerateInfoAuditEntity getGenerateInfoAuditEntityEntity(Integer runTestId);
  
}
