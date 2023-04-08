package com.smart.rct.migration.repositoryImpl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.RunTestInputEntity;
import com.smart.rct.common.entity.WorkFlowManagementEntity;
import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.CmdRuleBuilderEntity;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.entity.OvTestResultEntity;
import com.smart.rct.migration.entity.RunTestEntity;
import com.smart.rct.migration.entity.RunTestResultEntity;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.entity.ShellCommandEntity;
import com.smart.rct.migration.entity.UploadFileEntity;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseCmdRuleEntity;
import com.smart.rct.migration.entity.UseCaseFileRuleEntity;
import com.smart.rct.migration.entity.UseCaseShellRuleEntity;
import com.smart.rct.migration.entity.UseCaseXmlRuleEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.RunTestModel;
import com.smart.rct.migration.repository.RunTestRepository;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.premigration.entity.GenerateInfoAuditEntity;
import com.smart.rct.premigration.entity.NeMappingEntity;
import com.smart.rct.premigration.entity.PremigrationOvUpadteEntity;
import com.smart.rct.premigration.models.NeMappingModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Repository
@Transactional
public class RunTestRepositoryImpl implements RunTestRepository {

	final static Logger logger = LoggerFactory.getLogger(RunTestRepositoryImpl.class);

	/*
	 * @Autowired private EntityManagerFactory entityManagerFactory;
	 */
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	FileUploadRepository fileUploadRepository;

	@Autowired
	UseCaseBuilderService useCaseBuilderService;
	
	@Autowired
	NeMappingService neMappingService;

	/**
	 * This method will rSave the RunTest Detials and return Status
	 * 
	 * @param RunTestEntity
	 * @return boolean
	 */
	@Override
	public RunTestEntity createRunTest(RunTestEntity runTestEntity) {
		try {
			entityManager.persist(runTestEntity);
		} catch (Exception e) {
			logger.error(
					"Exception in  createRunTest() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}

	/**
	 * This method will return updateNetwork Details
	 * 
	 * @Param RunTestEntity
	 * @return boolean
	 */
	@Override
	public boolean updateRunTest(RunTestEntity runTestEntity) {
		boolean status = false;
		try {
			runTestEntity.setCreationDate(new Date());
			entityManager.merge(runTestEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRunTest() RunTestRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	@Override
	public boolean updateWfhRunTest(WorkFlowManagementEntity workFlowManagementEntity) {
		boolean status = false;
		try {
			workFlowManagementEntity.setCreationDate(new Date());
			entityManager.merge(workFlowManagementEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRunTest() RunTestRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	@Override
	public boolean updateRunTestov(OvTestResultEntity ovTestResultEntity) {
		boolean status = false;
		try {
			ovTestResultEntity.setCreationDate(new Date());
			entityManager.merge(ovTestResultEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRunTest() RunTestRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	public boolean updatePreMigrationOv(PremigrationOvUpadteEntity premigrationOvUpadteEntity) {
		boolean status = false;
		try {
			premigrationOvUpadteEntity.setCreationDate(new Date());
			entityManager.merge(premigrationOvUpadteEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in updateRunTest() RunTestRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	public List<Map> getSmList(int programId) {

		Map<NeVersionEntity, Map<NetworkConfigEntity, List<UseCaseBuilderEntity>>> neVersionNameUSecaseMap = new HashMap<>();
		Map<NetworkConfigEntity, List<UseCaseBuilderEntity>> neNameUsecaseMap = new HashMap<>();
		List<NeVersionEntity> neVersionIdList = new ArrayList<>();

		List<Map> neVersionLst = new ArrayList<>();

		// List<Map> smLst = new ArrayList<>();

		List<Map> ucbLst = new ArrayList<>();

		String smType = LoadPropertyFiles.getInstance().getProperty("smType");

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder neVerCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<NeVersionEntity> neVerCrQuery = neVerCriteriaBldr.createQuery(NeVersionEntity.class);
		Root<NeVersionEntity> neVerCrRoot = neVerCrQuery.from(NeVersionEntity.class);
		// crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"),
		// customerDetailsEntity));
		neVerCrQuery.select(neVerCrRoot).where(
				neVerCriteriaBldr.equal(neVerCrRoot.get("programDetailsEntity"), customerDetailsEntity),neVerCriteriaBldr.or(neVerCriteriaBldr.equal(neVerCrRoot.get("status"), "Active"),neVerCriteriaBldr.equal(neVerCrRoot.get("status"), "StandBy"))
				);
		TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(neVerCrQuery);
		neVersionIdList = (List<NeVersionEntity>) crTypedQuery.getResultList(); // fetch neversion

		//System.out.println("neVersionIdList : " + neVersionIdList);
		for (NeVersionEntity neVersionEntity : neVersionIdList) {
			Map neVersionMap = new HashMap<>();
			List<Map> smLst = new ArrayList<>();

			neVersionMap.put("id", neVersionEntity.getId());
			neVersionMap.put("name", neVersionEntity.getNeVersion());
			neVersionLst.add(neVersionMap);
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			conjunction.add(Restrictions.eq("neVersionEntity", neVersionEntity));
			criteria.add(conjunction);
			List<NetworkConfigEntity> smNameList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			// System.out.println("smNameList : "+smNameList);

			for (NetworkConfigEntity networkConfigEntity : smNameList) {
				Map smMap = new HashMap<>();

				if (smType.contains(networkConfigEntity.getNeTypeEntity().getNeType())) {

					smMap.put("id", networkConfigEntity.getId());
					smMap.put("name", networkConfigEntity.getNeName());
					smLst.add(smMap);
				}
				neVersionMap.put("smNameList", smLst);

				// comment starts here
				Conjunction conjunctions = Restrictions.conjunction();
				Criteria criteriaa = entityManager.unwrap(Session.class).createCriteria(UseCaseBuilderEntity.class);
				conjunctions.add(Restrictions.eq("networkConfigEntity", networkConfigEntity));
				criteriaa.add(conjunctions);
				List<UseCaseBuilderEntity> usecaseBldrList = criteriaa
						.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

				for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

					Map ucbMap = new HashMap<>();
					ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
					ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
					ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());

					ucbLst.add(ucbMap);

					smMap.put("useCaseList", ucbLst);
				}

				// comment ends here

				// neNameUsecaseMap.put(networkConfigEntity, usecaseBldrList);

			}

			// neVersionNameUSecaseMap.put(neVersionEntity, neNameUsecaseMap);
		}

		JSONObject tmp = new JSONObject();
		tmp.put("smVersion", neVersionLst);

		// System.out.println("neVersionMap : "+tmp);

		return neVersionLst;

	}

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public String loadRunningLog(Integer runtestId) {

		CriteriaBuilder runTestCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> runTestQuery = runTestCriteriaBldr.createQuery(String.class);
		Root<RunTestEntity> runTestRoot = runTestQuery.from(RunTestEntity.class);
		runTestQuery.select(runTestRoot.get("outputFilepath"))
				.where(runTestCriteriaBldr.equal(runTestRoot.get("id"), runtestId));

		TypedQuery<String> runTestTypedQuery = entityManager.createQuery(runTestQuery);
		String filePath = runTestTypedQuery.getSingleResult();

		return filePath;
	}
	
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public String loadneOplogs(Integer runtestId) {

		CriteriaBuilder runTestCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<String> runTestQuery = runTestCriteriaBldr.createQuery(String.class);
		Root<RunTestEntity> runTestRoot = runTestQuery.from(RunTestEntity.class);
		runTestQuery.select(runTestRoot.get("resultFilePath"))
				.where(runTestCriteriaBldr.equal(runTestRoot.get("id"), runtestId));

		TypedQuery<String> runTestTypedQuery = entityManager.createQuery(runTestQuery);
		String filePath = runTestTypedQuery.getSingleResult();

		return filePath;
	}

	@Override
	public List<ShellCommandEntity> getShellCommandDetails(String shellCmdName) {

		CriteriaBuilder shellCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<ShellCommandEntity> shellQuery = shellCriteriaBldr.createQuery(ShellCommandEntity.class);
		Root<ShellCommandEntity> shellRoot = shellQuery.from(ShellCommandEntity.class);
		shellQuery.select(shellRoot).where(shellCriteriaBldr.equal(shellRoot.get("cmdName"), shellCmdName));
		shellQuery.orderBy(shellCriteriaBldr.asc(shellRoot.get("columnOrder")));
		TypedQuery<ShellCommandEntity> shellTypedQuery = entityManager.createQuery(shellQuery);
		List<ShellCommandEntity> shellList = shellTypedQuery.getResultList();

		return shellList;
	}

	/**
	 * This method will return list of usecase based on program ID
	 * 
	 * @return List<Map>
	 */

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<Map> getUseCaseList(int programId, String migrationType, String subType, String ciqFName,
			List<Map> neList) {

		List<Map> ucbLst = new ArrayList<>();

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType));
		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();

		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

			Map ucbMap = new HashMap<>();
			ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
			ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
			ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());
			ucbMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			CriteriaBuilder usecaseBldrParamCriteriaBldr = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> usecaseBldrParamQuery = usecaseBldrParamCriteriaBldr
					.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> usecaseBldrParamRoot = usecaseBldrParamQuery
					.from(UseCaseBuilderParamEntity.class);
			usecaseBldrParamQuery.select(usecaseBldrParamRoot).where(usecaseBldrParamCriteriaBldr
					.equal(usecaseBldrParamRoot.get("useCaseBuilderEntity"), useCaseBuilderEntity));
			TypedQuery<UseCaseBuilderParamEntity> usecaseBldrParamTypedQuery = entityManager
					.createQuery(usecaseBldrParamQuery);
			List<UseCaseBuilderParamEntity> usecaseBldrParamList = usecaseBldrParamTypedQuery.getResultList();

			List<Map> ucbParamLst = new ArrayList<>();
			for (UseCaseBuilderParamEntity usecaseBldrParam : usecaseBldrParamList) {

				CriteriaBuilder uploadFileCriteriaBldr = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> uploadFileQuery = uploadFileCriteriaBldr.createQuery(String.class);
				Root<UploadFileEntity> uploadFileRoot = uploadFileQuery.from(UploadFileEntity.class);
				uploadFileQuery.select(uploadFileRoot.get("fileName")).where(uploadFileCriteriaBldr
						.equal(uploadFileRoot.get("id"), usecaseBldrParam.getScriptsDetails().getId()));
				TypedQuery<String> uploadFileTypedQuery = entityManager.createQuery(uploadFileQuery);
				String scriptFileName = uploadFileTypedQuery.getSingleResult();

				Map ucbParamMap = new HashMap<>();
				ucbParamMap.put("scriptId", usecaseBldrParam.getScriptsDetails().getId());
				ucbParamMap.put("scriptExeSequence", usecaseBldrParam.getExecutionSequence());
				ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
				ucbParamMap.put("scriptName", scriptFileName);
				ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);

				ucbParamLst.add(ucbParamMap);
			}

			ucbMap.put("scripts", ucbParamLst);

			ucbLst.add(ucbMap);
		}

		for (Map neid : neList) {

			String neId = neid.get("neId").toString();
			String neName = neid.get("neName").toString();
			// for rf
			Map ucbrfMap = new HashMap<>();
			ucbrfMap.put("useCaseName", Constants.RF_USECASE + "_" + neId);
			ucbrfMap.put("useCaseId", "0");
			ucbrfMap.put("executionSequence", 1);
			ucbrfMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			StringBuilder constantRfScriptsFilePath = new StringBuilder();

			constantRfScriptsFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.SEPARATOR)
					.append(Constants.PRE_MIGRATION_SCRIPT.replace("filename", ciqFName).replaceAll(" ", "_"))
					.append(Constants.SEPARATOR).append(neId);

			File directory = new File(constantRfScriptsFilePath.toString());
			if (directory.exists()) {
				String[] filename = directory.list();
				TreeSet<String> bashfilename = new TreeSet<>();

				for (String singleFile : filename) {

					if (singleFile.endsWith(".sh")) {
						String fileName = singleFile.substring(8);
						bashfilename.add(fileName);
					}
				}

				int seq = 1;
				List<Map> ucbRfParamLst = new ArrayList<>();
				for (String file : bashfilename) {
					Map ucbRfParamMap = new HashMap<>();
					ucbRfParamMap.put("scriptId", seq);
					ucbRfParamMap.put("scriptExeSequence", seq);
					ucbRfParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
					ucbRfParamMap.put("scriptName", "BASH_RF_" + file);
					ucbRfParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);
					ucbRfParamLst.add(ucbRfParamMap);
					seq++;
				}

				ucbrfMap.put("scripts", ucbRfParamLst);
				ucbLst.add(ucbrfMap);
			}
			// for commission
			Map ucbCommissionMap = new HashMap<>();
			ucbCommissionMap.put("useCaseName", Constants.COMMISION_USECASE + "_" + neId);
			ucbCommissionMap.put("useCaseId", "0");
			ucbCommissionMap.put("executionSequence", 1);
			ucbCommissionMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			StringBuilder constantCommissionScriptsFilePath = new StringBuilder();

			constantCommissionScriptsFilePath.append(LoadPropertyFiles.getInstance().getProperty("BASE_PATH"))
					.append(Constants.CUSTOMER).append(Constants.SEPARATOR).append(programId)
					.append(Constants.PRE_MIGRATION_COMMISSIONING_SCRIPT.replace("filename", ciqFName)
							.replace("enbId", neId).replaceAll(" ", "_"));

			File comDirectory = new File(constantRfScriptsFilePath.toString());
			if (comDirectory.exists()) {
				String[] comFilename = comDirectory.list();
				/*
				 * TreeSet<String> comBashfilename = new TreeSet<>();
				 * 
				 * for (String singleFile : comFilename) {
				 * 
				 * if (singleFile.endsWith(".sh")) { String fileName = singleFile.substring(8);
				 * comBashfilename.add(fileName); } }
				 */

				int comSeq = 1;
				List<Map> ucbParamLst = new ArrayList<>();
				for (String file : comFilename) {
					Map ucbParamMap = new HashMap<>();
					ucbParamMap.put("scriptId", comSeq);
					ucbParamMap.put("scriptExeSequence", comSeq);
					ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
					ucbParamMap.put("scriptName", "BASH_RF_" + file);
					ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);
					ucbParamLst.add(ucbParamMap);
					comSeq++;
				}

				ucbCommissionMap.put("scripts", ucbParamLst);
				ucbLst.add(ucbCommissionMap);

			}
		}
		return ucbLst;

	}

	/**
	 * This method will return list of usecase based on program ID
	 * 
	 * @return List<Map>
	 */

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<Map> getUseCaseList(int programId, String migrationType, String subType) {

		List<Map> ucbLst = new ArrayList<>();

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType));
		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();

		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

			Map ucbMap = new HashMap<>();
			ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
			ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
			ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());
			ucbMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			CriteriaBuilder usecaseBldrParamCriteriaBldr = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> usecaseBldrParamQuery = usecaseBldrParamCriteriaBldr
					.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> usecaseBldrParamRoot = usecaseBldrParamQuery
					.from(UseCaseBuilderParamEntity.class);
			usecaseBldrParamQuery.select(usecaseBldrParamRoot).where(usecaseBldrParamCriteriaBldr
					.equal(usecaseBldrParamRoot.get("useCaseBuilderEntity"), useCaseBuilderEntity));
			TypedQuery<UseCaseBuilderParamEntity> usecaseBldrParamTypedQuery = entityManager
					.createQuery(usecaseBldrParamQuery);
			List<UseCaseBuilderParamEntity> usecaseBldrParamList = usecaseBldrParamTypedQuery.getResultList();

			List<Map> ucbParamLst = new ArrayList<>();
			for (UseCaseBuilderParamEntity usecaseBldrParam : usecaseBldrParamList) {

				CriteriaBuilder uploadFileCriteriaBldr = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> uploadFileQuery = uploadFileCriteriaBldr.createQuery(String.class);
				Root<UploadFileEntity> uploadFileRoot = uploadFileQuery.from(UploadFileEntity.class);
				uploadFileQuery.select(uploadFileRoot.get("fileName")).where(uploadFileCriteriaBldr
						.equal(uploadFileRoot.get("id"), usecaseBldrParam.getScriptsDetails().getId()));
				TypedQuery<String> uploadFileTypedQuery = entityManager.createQuery(uploadFileQuery);
				String scriptFileName = uploadFileTypedQuery.getSingleResult();

				Map ucbParamMap = new HashMap<>();
				ucbParamMap.put("scriptId", usecaseBldrParam.getScriptsDetails().getId());
				ucbParamMap.put("scriptExeSequence", usecaseBldrParam.getExecutionSequence());
				ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
				ucbParamMap.put("scriptName", scriptFileName);
				ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);

				ucbParamLst.add(ucbParamMap);
			}

			ucbMap.put("scripts", ucbParamLst);

			ucbLst.add(ucbMap);
		}

		return ucbLst;

	}

	/**
	 * This method will return runTestEntityList
	 * 
	 * @return RunTestEntity
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<RunTestEntity> getRunTestSearchDetails(RunTestModel runTestModel, int page, int count,
			Integer customerId, Integer programId, String migrationStatus, String migrationSubStatus, int days) {
		List<RunTestEntity> runTestEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runTestResultEntity", FetchMode.LAZY);
			if (runTestModel != null) {
				if (StringUtils.isNotEmpty(runTestModel.getLsmVersion())) {
					Criterion lsmVersion = Restrictions.ilike("lsmVersion", runTestModel.getLsmVersion().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmVersion);
				}

				if (StringUtils.isNotEmpty(runTestModel.getTestName())) {
					Criterion testName = Restrictions.ilike("testName", runTestModel.getTestName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(testName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getCiqName())) {
					Criterion ciqName = Restrictions.ilike("ciqName", runTestModel.getCiqName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", runTestModel.getNeName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getLsmName())) {
					Criterion lsmName = Restrictions.ilike("lsmName", runTestModel.getLsmName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmName);
				}

				if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())
						&& runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				} else if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
				} else if (runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchEndDate);
				}
			}

			conjunction.add(Restrictions.eq("migrationType", migrationStatus));
			conjunction.add(Restrictions.eq("migrationSubType", migrationSubStatus));
			conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));

			criteria.add(conjunction);

			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error(" getRunTestDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntityList;
	}

	/**
	 * This method will return runTestEntityList
	 * 
	 * @return RunTestEntity
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<RunTestEntity> getRunTestDetails(int page, int count, Integer customerId, Integer programId,
			String migrationStatus, String migrationSubStatus, int days) {
		List<RunTestEntity> runTestEntityList = null;
		int startIndex = 0;
		int maxResults = 0;
		try {
			if (page > 0) {
				startIndex = ((page - 1) * count);
				maxResults = (page * count);
			}

			long today = System.currentTimeMillis();
			long nDays = days * 24 * 60 * 60 * 1000;
			long nDaysAgo = today - nDays;
			Date nDaysAgoDate = new Date(nDaysAgo);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.and(
					// cb.equal(root.get("customerId"), customerId),
					cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("migrationType"), migrationStatus),
					cb.equal(root.get("migrationSubType"), migrationSubStatus),
					cb.greaterThanOrEqualTo(root.get("creationDate"), nDaysAgoDate)));
			query.orderBy(cb.desc(root.get("creationDate")));
			TypedQuery<RunTestEntity> typedQuery = entityManager.createQuery(query);
			typedQuery.setFirstResult(startIndex);
			typedQuery.setMaxResults(count);
			runTestEntityList = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(" getRunTestDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntityList;
	}

	/**
	 * This method will delete RunTest by id
	 * 
	 * @param runTestId
	 * @return boolean
	 */
	@Override
	public boolean deleterunTest(int runTestId) {
		boolean status = false;
		try {
			entityManager.remove(getRunTestId(runTestId));
			status = true;
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception in deleterunTest() in  RunTestRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	/**
	 * this method will return UploadedFileEntity
	 * 
	 * @param nwTypeID
	 * @return NetworkTypeDetailsEntity
	 */
	public RunTestEntity getRunTestId(int runTestId) {
		return entityManager.find(RunTestEntity.class, runTestId);
	}

	@Override
	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails() {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntityList = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntityList;
	}

	/**
	 * This method will return scripts details associated with the given usecase ID
	 * 
	 * @return UseCaseBuilderParamEntity
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UseCaseBuilderParamEntity> getScriptDetails(int usecaseID) {

		List<UseCaseBuilderParamEntity> usecaseBuilderScriptList = null;
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {

			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);

			query.select(root).where(builder.equal(root.get("id"), usecaseID));

			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntity = typedQuery.getSingleResult();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> queryScript = cb.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> rootScript = queryScript.from(UseCaseBuilderParamEntity.class);

			queryScript.select(rootScript)
					.where(cb.equal(rootScript.get("useCaseBuilderEntity"), useCaseBuilderEntity));

			TypedQuery<UseCaseBuilderParamEntity> typedQueryScript = entityManager.createQuery(queryScript);
			usecaseBuilderScriptList = typedQueryScript.getResultList();

			/*
			 * CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			 * CriteriaQuery<NetworkTypeDetailsEntity> query =
			 * builder.createQuery(NetworkTypeDetailsEntity.class);
			 * Root<NetworkTypeDetailsEntity> root =
			 * query.from(NetworkTypeDetailsEntity.class); query.select(root);
			 * query.where(builder.equal(root.get("networkType"), networkType));
			 * TypedQuery<NetworkTypeDetailsEntity> queryResult =
			 * entityManager.createQuery(query);
			 */

		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getScriptDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return usecaseBuilderScriptList;

	}

	/**
	 * This method will return scripts details associated with the given usecase ID
	 * 
	 * @return UseCaseBuilderParamEntity
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadFileEntity getScriptInfo(int scriptID) {

		UploadFileEntity ScriptList = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = builder.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root).where(builder.equal(root.get("id"), scriptID));
			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			ScriptList = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getScriptInfo() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ScriptList;

	}

	@SuppressWarnings("unchecked")
	@Override
	public UseCaseBuilderEntity getUseCaseEntity(int useCaseId) {

		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root).where(builder.equal(root.get("id"), useCaseId));
			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getScriptInfo() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntity;

	}

	@Override
	public List<UseCaseCmdRuleEntity> getCommandRules(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		List<UseCaseCmdRuleEntity> UseCaseCmdRuleEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseCmdRuleEntity> query = cb.createQuery(UseCaseCmdRuleEntity.class);
			Root<UseCaseCmdRuleEntity> root = query.from(UseCaseCmdRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseCmdRuleEntity> typedQuery = entityManager.createQuery(query);

			UseCaseCmdRuleEntityList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getCommandRules() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return UseCaseCmdRuleEntityList;
	}

	@Override
	public List<UseCaseFileRuleEntity> getFileRules(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		List<UseCaseFileRuleEntity> useCaseFileRuleEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseFileRuleEntity> query = cb.createQuery(UseCaseFileRuleEntity.class);
			Root<UseCaseFileRuleEntity> root = query.from(UseCaseFileRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseFileRuleEntity> typedQuery = entityManager.createQuery(query);

			useCaseFileRuleEntityList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getCommandRules() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return useCaseFileRuleEntityList;
	}

	@Override
	public List<CmdRuleBuilderEntity> geMigtCommandRules(UseCaseCmdRuleEntity cmdRuleEnt) {
		List<CmdRuleBuilderEntity> cmdRuleBuilderEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CmdRuleBuilderEntity> query = cb.createQuery(CmdRuleBuilderEntity.class);
			Root<CmdRuleBuilderEntity> root = query.from(CmdRuleBuilderEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"), cmdRuleEnt));

			TypedQuery<CmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);

			cmdRuleBuilderEntityList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" Error in RunTestRepositoryImpl getCommandRules() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return cmdRuleBuilderEntityList;
	}

	@Override
	public int getRunTestId(Date creationDate) {
		List<RunTestEntity> runTestEntity = null;
		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			TimeZone toTimeZone = DateUtil.getUserTimeZone("Asia/Kolkata");
			Date fromDate = DateUtil.convertDateFromUserTimeZonetoUTC(
					CommonUtil.dateToString(creationDate, Constants.YYYY_MM_DD_HH_MM_SS), toTimeZone,
					Constants.YYYY_MM_DD_HH_MM_SS);
			criteria.add(Restrictions.eq("creationDate", fromDate));
			runTestEntity = criteria.list();

		} catch (Exception e) {
			logger.error("Exception getRunTestId :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity.get(0).getId();
	}

	/**
	 * This method will update details
	 * 
	 * @param runTestId
	 * @return boolean
	 */
	@Override
	public boolean updateRunTest(Integer runTestId) {
		boolean status = false;
		try {
			/*
			 * Query query = new Query();
			 * query.addCriteria(Criteria.where("id").is(entity.getId())); Update update =
			 * new Update(); update.set("fileName", entity.getFileName());
			 * update.set("eNBId", entity.geteNBId()); update.set("eNBName",
			 * entity.geteNBName()); update.set("ciqMap", entity.getCiqMap());
			 * mongoTemplate.findAndModify(query, update, CIQDetailsModel.class,
			 * entity.getFileName()); status=true;
			 */

		} catch (Exception e) {
			logger.error("Exception updateRunTest() in runTestRepoImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/*
	 * public List<CmdRuleBuilderEntity> geMigtCommandRules(UseCaseCmdRuleEntity
	 * cmdRuleEnt) { List<CmdRuleBuilderEntity> cmdRuleBuilderEntityList = null; try
	 * { CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	 * CriteriaQuery<CmdRuleBuilderEntity> query =
	 * cb.createQuery(CmdRuleBuilderEntity.class); Root<CmdRuleBuilderEntity> root =
	 * query.from(CmdRuleBuilderEntity.class);
	 * query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"),
	 * cmdRuleEnt));
	 * 
	 * TypedQuery<CmdRuleBuilderEntity> typedQuery =
	 * entityManager.createQuery(query);
	 * 
	 * cmdRuleBuilderEntityList = typedQuery.getResultList(); } catch (Exception e)
	 * { logger.error(" Error in RunTestRepositoryImpl getCommandRules() : " +
	 * ExceptionUtils.getFullStackTrace(e)); } finally { entityManager.flush();
	 * entityManager.clear(); }
	 * 
	 * return cmdRuleBuilderEntityList; }
	 */

	/*
	 * @Override public List<RunTestEntity> loadRunTestDetails(int programId, String
	 * migrationType, String migrationSubType, int customerId, int history,
	 * RunTestModel runTestModels) { List<RunTestEntity> runTestEntityList = null;
	 * 
	 * try {
	 * 
	 * long today = System.currentTimeMillis(); long nDays = history * 24 * 60 * 60
	 * * 1000; long nDaysAgo = today - nDays; Date nDaysAgoDate = new
	 * Date(nDaysAgo);
	 * 
	 * CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	 * CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
	 * Root<RunTestEntity> root = query.from(RunTestEntity.class);
	 * query.select(root); query.where(cb.and(cb.equal(root.get("customerId"),
	 * customerId), cb.equal(root.get("customerDetailsEntity"), programId),
	 * cb.equal(root.get("migrationType"), migrationType),
	 * cb.equal(root.get("migrationSubType"), migrationSubType)
	 * //,cb.greaterThanOrEqualTo(root.get("creationDate"), nDaysAgoDate) )); if
	 * (runTestModels != null) { if
	 * (StringUtils.isNotEmpty(runTestModels.getLsmVersion())) {
	 * query.where(cb.like(root.get("lsmVersion"),
	 * runTestModels.getLsmVersion().trim())); }
	 * 
	 * if (StringUtils.isNotEmpty(runTestModels.getTestName())) {
	 * query.where(cb.like(root.get("testName"),
	 * runTestModels.getTestName().trim())); } if
	 * (StringUtils.isNotEmpty(runTestModels.getCiqName())) {
	 * query.where(cb.like(root.get("ciqName"), runTestModels.getCiqName().trim()));
	 * } if (StringUtils.isNotEmpty(runTestModels.getNeName())) {
	 * query.where(cb.like(root.get("neName"), runTestModels.getNeName().trim())); }
	 * if (StringUtils.isNotEmpty(runTestModels.getLsmName())) {
	 * query.where(cb.like(root.get("lsmName"), runTestModels.getLsmName().trim()));
	 * } } TypedQuery<RunTestEntity> typedQuery = entityManager.createQuery(query);
	 * runTestEntityList = typedQuery.getResultList(); } catch (Exception e) {
	 * logger.error(" loadUseCaseBuilderDetails() : " +
	 * ExceptionUtils.getFullStackTrace(e)); } finally { entityManager.flush();
	 * entityManager.clear(); } return runTestEntityList; }
	 */

	@Override
	public List<RunTestEntity> loadRunTestDetails(int programId, String migrationType, String migrationSubType,
			int customerId, int history, RunTestModel runTestModels) {
		List<RunTestEntity> runTestEntityList = null;

		try {

			long today = System.currentTimeMillis();
			long nDays = history * 24 * 60 * 60 * 1000;
			long nDaysAgo = today - nDays;
			Date nDaysAgoDate = new Date(nDaysAgo);
			Date todayDate = new Date(today);

			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runTestResultEntity", FetchMode.LAZY);
			if (runTestModels != null) {
				if (StringUtils.isNotEmpty(runTestModels.getLsmVersion())) {
					Criterion lsmVersion = Restrictions.ilike("lsmVersion", runTestModels.getLsmVersion().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmVersion);
				}

				if (StringUtils.isNotEmpty(runTestModels.getTestName())) {
					Criterion testName = Restrictions.ilike("testName", runTestModels.getTestName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(testName);
				}
				if (StringUtils.isNotEmpty(runTestModels.getCiqName())) {
					Criterion ciqName = Restrictions.ilike("ciqName", runTestModels.getCiqName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqName);
				}
				if (StringUtils.isNotEmpty(runTestModels.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", runTestModels.getNeName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(runTestModels.getLsmName())) {
					Criterion lsmName = Restrictions.ilike("lsmName", runTestModels.getLsmName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmName);
				}
				if (runTestModels.getFromDate() != null && !"".equals(runTestModels.getFromDate())
						&& runTestModels.getToDate() != null && !"".equals(runTestModels.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModels.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModels.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				} else if (runTestModels.getFromDate() != null && !"".equals(runTestModels.getFromDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModels.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
				} else if (runTestModels.getToDate() != null && !"".equals(runTestModels.getToDate())) {
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModels.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchEndDate);
				}

			}
			if (StringUtils.isNotEmpty(runTestModels.getLsmVersion())
					|| StringUtils.isNotEmpty(runTestModels.getTestName())
					|| StringUtils.isNotEmpty(runTestModels.getCiqName())
					|| StringUtils.isNotEmpty(runTestModels.getNeName())
					|| StringUtils.isNotEmpty(runTestModels.getLsmName())
					|| StringUtils.isNotEmpty(runTestModels.getFromDate())
					|| StringUtils.isNotEmpty(runTestModels.getToDate())) {
				// Do nothing
			} else {
				DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fromDate = dateFormat1.format(todayDate);
				String toDate = dateFormat1.format(nDaysAgoDate);

				String toDateAfterTrim = toDate.substring(0, 10);
				toDateAfterTrim = toDateAfterTrim + " " + "23:59:59";

				Date finalStartDate = dateFormat1.parse(fromDate);
				Date finalEndDate = dateFormat1.parse(toDateAfterTrim);

				Criterion dateRange = Restrictions.between("creationDate", finalEndDate, finalStartDate);
				conjunction.add(dateRange);
			}

			// conjunction.add(Restrictions.eq("customerId", customerId));
			conjunction.add(Restrictions.eq("migrationType", migrationType));
			conjunction.add(Restrictions.eq("migrationSubType", migrationSubType));
			conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));

			criteria.add(conjunction);

			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntityList;
	}

	@Override
	public boolean updateUseCountForUseCase(int useCaseId, String action, int count) {
		boolean updateUseCountStatus = false;
		try {
			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderService.getUseCaseBuilderEntity(useCaseId);
			logger.info("updateUseCountForUseCase() useCaseId: " + useCaseId + ", action: " + action
					+ ", Exist UseCount: " + useCaseBuilderEntity.getUseCount() + ", count: " + count);
			if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
				useCaseBuilderEntity.setUseCount(useCaseBuilderEntity.getUseCount() + count);
			} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
					&& useCaseBuilderEntity.getUseCount() > 0) {
				useCaseBuilderEntity.setUseCount(useCaseBuilderEntity.getUseCount() - count);
			} else {
				useCaseBuilderEntity.setUseCount(0);
			}

			logger.info("updateUseCountForUseCase() useCaseId: " + useCaseId + ", action: " + action
					+ ", Updating UseCount: " + useCaseBuilderEntity.getUseCount());
			entityManager.merge(useCaseBuilderEntity);

			Set<UseCaseBuilderParamEntity> useCaseBuilderParamEntitySet = useCaseBuilderEntity
					.getUseCaseBuilderParamEntity();
			for (UseCaseBuilderParamEntity useCaseBuilderParamEntity : useCaseBuilderParamEntitySet) {

				UploadFileEntity uploadFileEntity = useCaseBuilderParamEntity.getScriptsDetails();

				if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
					uploadFileEntity.setUseCount(uploadFileEntity.getUseCount() + count);
				} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
						&& uploadFileEntity.getUseCount() > 0) {
					uploadFileEntity.setUseCount(uploadFileEntity.getUseCount() - count);
				} else {
					uploadFileEntity.setUseCount(0);
				}

				entityManager.merge(uploadFileEntity);

				Set<UseCaseCmdRuleEntity> useCaseCmdRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseCmdRuleEntitySet();
				for (UseCaseCmdRuleEntity useCaseCmdRuleEntity : useCaseCmdRuleEntitySet) {
					CmdRuleBuilderEntity cmdRuleBuilderEntity = useCaseCmdRuleEntity.getCmdRuleBuilderEntity();

					if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
						cmdRuleBuilderEntity.setUseCount(cmdRuleBuilderEntity.getUseCount() + count);
					} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
							&& cmdRuleBuilderEntity.getUseCount() > 0) {
						cmdRuleBuilderEntity.setUseCount(cmdRuleBuilderEntity.getUseCount() - count);
					} else {
						cmdRuleBuilderEntity.setUseCount(0);
					}

					entityManager.merge(cmdRuleBuilderEntity);
				}

				Set<UseCaseFileRuleEntity> useCaseFileRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseFileRuleEntitySet();
				for (UseCaseFileRuleEntity useCaseFileRuleEntity : useCaseFileRuleEntitySet) {
					FileRuleBuilderEntity fileRuleBuilderEntity = useCaseFileRuleEntity.getFileRuleBuilderEntity();

					if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
						fileRuleBuilderEntity.setUseCount(fileRuleBuilderEntity.getUseCount() + count);
					} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
							&& fileRuleBuilderEntity.getUseCount() > 0) {
						fileRuleBuilderEntity.setUseCount(fileRuleBuilderEntity.getUseCount() - count);
					} else {
						fileRuleBuilderEntity.setUseCount(0);
					}
					entityManager.merge(fileRuleBuilderEntity);
				}

				Set<UseCaseShellRuleEntity> useCaseShellRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseShellRuleEntitySet();
				for (UseCaseShellRuleEntity useCaseShellRuleEntity : useCaseShellRuleEntitySet) {
					ShellCmdRuleBuilderEntity shellRuleBuilderEntity = useCaseShellRuleEntity
							.getShellRuleBuilderEntity();
					if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
						shellRuleBuilderEntity.setUseCount(shellRuleBuilderEntity.getUseCount() + count);
					} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
							&& shellRuleBuilderEntity.getUseCount() > 0) {
						shellRuleBuilderEntity.setUseCount(shellRuleBuilderEntity.getUseCount() - count);
					} else {
						shellRuleBuilderEntity.setUseCount(0);
					}
					entityManager.merge(shellRuleBuilderEntity);
				}

				Set<UseCaseXmlRuleEntity> useCaseXmlRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseXmlRuleEntitySet();
				for (UseCaseXmlRuleEntity useCaseXmlRuleEntity : useCaseXmlRuleEntitySet) {
					XmlRuleBuilderEntity xmlRuleBuilderEntity = useCaseXmlRuleEntity.getXmlRuleBuilderEntity();
					if (Constants.USECOUNT_INCREMENT.equalsIgnoreCase(action)) {
						xmlRuleBuilderEntity.setUseCount(xmlRuleBuilderEntity.getUseCount() + count);
					} else if (Constants.USECOUNT_DECREMENT.equalsIgnoreCase(action)
							&& xmlRuleBuilderEntity.getUseCount() > 0) {
						xmlRuleBuilderEntity.setUseCount(xmlRuleBuilderEntity.getUseCount() - count);
					} else {
						xmlRuleBuilderEntity.setUseCount(0);
					}
					entityManager.merge(xmlRuleBuilderEntity);
				}

			}
			updateUseCountStatus = true;
		} catch (Exception e) {
			logger.error(" updateUseCountForUseCase() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return updateUseCountStatus;
	}

	@Override
	public RunTestEntity getRunTestEntity(Integer runTestId) {
		RunTestEntity runTestEntity = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			conjunction.add(Restrictions.eq("id", runTestId));
			criteria.add(conjunction);
			runTestEntity = (RunTestEntity) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}
	
	/*
	 * @Override public WorkFlowManagementEntity getWFMRunTestResult(Integer
	 * wfmTestId) { // TODO Auto-generated method stub return null; }
	 */
	
	@Override
	public WorkFlowManagementEntity getWFMTestEntity(Integer wfmTestId) {
		WorkFlowManagementEntity runTestEntity = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(WorkFlowManagementEntity.class);
			conjunction.add(Restrictions.eq("id", wfmTestId));
			criteria.add(conjunction);
			runTestEntity = (WorkFlowManagementEntity) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}
	@Override
	public GenerateInfoAuditEntity getGenerateInfoAuditEntityEntity(Integer runTestId) {
		GenerateInfoAuditEntity generateInfoAuditEntity = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(GenerateInfoAuditEntity.class);
			conjunction.add(Restrictions.eq("id", runTestId));
			criteria.add(conjunction);
			generateInfoAuditEntity = (GenerateInfoAuditEntity) criteria.uniqueResult();

		} catch (Exception e) {
			logger.error(" getGenerateInfoAuditEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return generateInfoAuditEntity;
	}

	@Override
	public boolean getRunTestEntity(int programId, String migrationType, String subType, String testname) {
		List<RunTestEntity> runTestEntity = null;
		boolean isTestNamePresent = true;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("migrationSubType"), subType),
					cb.equal(root.get("testName"), testname));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntity = queryResult.getResultList();

			if (runTestEntity.isEmpty()) {
				isTestNamePresent = false;
			}

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return isTestNamePresent;
	}

	@Override
	public RunTestEntity getRunTestEntityDetails(int programId, String migrationType, String subType, String testname,
			String neName) {
		RunTestEntity runTestEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("migrationSubType"), subType),
					cb.equal(root.get("testName"), testname), cb.equal(root.get("neName"), neName));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}

	@Override
	public boolean getInProgressRunTestDetails(int programId, String migrationType, String subType) {
		List<RunTestEntity> runTestEntity = null;
		boolean isInProgress = false;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("migrationSubType"), subType),
					cb.equal(root.get("progressStatus"), "InProgress"));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntity = queryResult.getResultList();

			if (!runTestEntity.isEmpty()) {
				isInProgress = true;
			}

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return isInProgress;
	}

	/*
	 * @Override public RunTestEntity getRunTestEntity(int runtestId) {
	 * RunTestEntity runTestEntity = null; try { runTestEntity =
	 * entityManager.find(RunTestEntity.class, runtestId); } catch (Exception e) {
	 * logger.error("Exception getRunTestEntity: " +
	 * ExceptionUtils.getFullStackTrace(e)); } return runTestEntity; }
	 */

	/**
	 * this method will return neRelease Version
	 * 
	 * @param programId
	 *            , lsm version
	 * @return ne relversion
	 */
	@Override
	public String getNeRelVer(int pgmId, String lsmVersion) {

		String neRelVer = null;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> query = cb.createQuery(String.class);
			Root<NeVersionEntity> root = query.from(NeVersionEntity.class);

			query.select(root.get("releaseVersion"));
			query.where(cb.and(cb.equal(root.get("neVersion"), lsmVersion),
					cb.equal(root.get("programDetailsEntity"), pgmId)));

			TypedQuery<String> queryResult = entityManager.createQuery(query);
			neRelVer = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getNeRelVer() : " + ExceptionUtils.getFullStackTrace(e));
		}

		return neRelVer;

	}

	public NetworkConfigEntity getNeType(int lsmId) {

		NetworkConfigEntity networkConfigEntity = null;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = cb.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("id"), lsmId));
			TypedQuery<NetworkConfigEntity> queryResult = entityManager.createQuery(query);
			networkConfigEntity = queryResult.getSingleResult();

		} catch (Exception e) {
			logger.error(" getNeType() : " + ExceptionUtils.getFullStackTrace(e));
		}

		return networkConfigEntity;

	}

	/*
	 * public List<NetworkConfigDetailsEntity> getNeconfigDetails(int lsmId){
	 * 
	 * List<NetworkConfigDetailsEntity> networkConfigDeailsEntity= null;
	 * 
	 * try { CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	 * CriteriaQuery<NetworkConfigDetailsEntity> query =
	 * cb.createQuery(NetworkConfigDetailsEntity.class);
	 * Root<NetworkConfigDetailsEntity> root =
	 * query.from(NetworkConfigDetailsEntity.class);
	 * 
	 * query.select(root); // TypedQuery<RunTestEntity> typedQuery =
	 * entityManager.createQuery(query); // runTestEntity =
	 * typedQuery.getSingleResult(); query.where(cb.equal(root.get("id"), lsmId));
	 * query.orderBy(cb.desc(root.get("step")));
	 * TypedQuery<NetworkConfigDetailsEntity> queryResult =
	 * entityManager.createQuery(query); networkConfigDeailsEntity =
	 * queryResult.getResultList();
	 * 
	 * }catch(Exception e ) { logger.error(" getNeconfigDetails() : " +
	 * ExceptionUtils.getFullStackTrace(e)); }
	 * 
	 * 
	 * 
	 * 
	 * return networkConfigDeailsEntity;
	 * 
	 * }
	 */

	/**
	 * 
	 * this method will retrieve id
	 * 
	 * @param id
	 * @return CmdRuleBuilderEntity
	 */
	@Override
	public CmdRuleBuilderEntity getCommandRuleById(int id) {
		return entityManager.find(CmdRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will retrieve id
	 * 
	 * @param id
	 * @return ShellCmdRuleBuilderEntity
	 */
	@Override
	public ShellCmdRuleBuilderEntity getshellRuleById(int id) {
		return entityManager.find(ShellCmdRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will retrieve id
	 * 
	 * @param id
	 * @return CmdRuleBuilderEntity
	 */
	@Override
	public XmlRuleBuilderEntity getXmlRuleById(int id) {
		return entityManager.find(XmlRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will retrieve id
	 * 
	 * @param id
	 * @return CmdRuleBuilderEntity
	 */
	@Override
	public FileRuleBuilderEntity getFileRuleById(int id) {
		return entityManager.find(FileRuleBuilderEntity.class, id);
	}

	/**
	 * 
	 * this method will retrieve nedetails that are in progress status for a
	 * particular ne
	 * 
	 * @param neName
	 * @return RunTestEntity
	 */

	@Override
	public boolean getRuntestEnbProgressStatus(String neName) {

		boolean inProgressStatus = true;
		List<RunTestEntity> runtestEntity = null;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);

			query.select(root);
			query.where(
					cb.and(cb.equal(root.get("neName"), neName), cb.equal(root.get("progressStatus"), "InProgress")));

			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runtestEntity = queryResult.getResultList();

			if (runtestEntity.isEmpty()) {
				inProgressStatus = false;
			}

		} catch (Exception e) {
			logger.error(" Exception RunTestRepositoryImpl in getRuntestEnbProgressStatus()  : "
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return inProgressStatus;

	}

	@Override
	public Map<String, Object> getRunTestDetails(RunTestModel runTestModel, int page, int count, Integer programId,
			String migrationType, String migrationSubType,boolean wfmKey) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		double result = 0;
		int paginationNumber = 0;
		List<RunTestEntity> runTestEntityList = null;
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("creationDate"));
			criteria.setFetchMode("runTestResultEntity", FetchMode.LAZY);
			if (runTestModel != null) {
				if (StringUtils.isNotEmpty(runTestModel.getLsmVersion())) {
					Criterion lsmVersion = Restrictions.ilike("lsmVersion", runTestModel.getLsmVersion().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmVersion);
				}

				if (StringUtils.isNotEmpty(runTestModel.getTestName())) {
					Criterion testName = Restrictions.ilike("testName", runTestModel.getTestName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(testName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getUserName())) {
					Criterion userName = Restrictions.ilike("userName", runTestModel.getUserName(),
							MatchMode.ANYWHERE);
					conjunction.add(userName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getCiqName())) {
					Criterion ciqName = Restrictions.ilike("ciqName", runTestModel.getCiqName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(ciqName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getNeName())) {
					Criterion neName = Restrictions.ilike("neName", runTestModel.getNeName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(neName);
				}
				if (StringUtils.isNotEmpty(runTestModel.getLsmName())) {
					Criterion lsmName = Restrictions.ilike("lsmName", runTestModel.getLsmName().trim(),
							MatchMode.ANYWHERE);
					conjunction.add(lsmName);
				}

				if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())
						&& runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
					conjunction.add(searchEndDate);
				} else if (runTestModel.getFromDate() != null && !"".equals(runTestModel.getFromDate())) {
					Criterion searchStartDate = Restrictions.ge("creationDate",
							DateUtil.stringToDate(runTestModel.getFromDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchStartDate);
				} else if (runTestModel.getToDate() != null && !"".equals(runTestModel.getToDate())) {
					Criterion searchEndDate = Restrictions.le("creationDate",
							DateUtil.stringToDateEndTime(runTestModel.getToDate(), Constants.MM_DD_YYYY));
					conjunction.add(searchEndDate);
				}
			}

			if (wfmKey) {
				criteria.add(Restrictions.or(Restrictions.ilike("migrationType", "premigration"),
						Restrictions.ilike("migrationType", migrationType)));
				
				
				criteria.add(Restrictions.or(Restrictions.ilike("migrationSubType", "PREAUDIT"),
						Restrictions.ilike("migrationSubType", migrationSubType)));
				
				} else {
					conjunction.add(Restrictions.eq("migrationType", migrationType));
					conjunction.add(Restrictions.eq("migrationSubType", migrationSubType));
				}
			
			/*
			 * conjunction.add(Restrictions.eq("migrationType", migrationType));
			 * conjunction.add(Restrictions.eq("migrationSubType", migrationSubType));
			 */
			conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));

			if (StringUtils.isNotEmpty(runTestModel.getUserName())) {
				conjunction.add(Restrictions.ilike("userName", runTestModel.getUserName(),
						MatchMode.ANYWHERE));
			}

			criteria.add(conjunction);
			logger.info("RunTestRepositoryImpl.getRunTestDetails() criteria: " + criteria.toString());
			runTestEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			criteriaCount.setFetchMode("runTestResultEntity", FetchMode.LAZY);
			criteriaCount.add(conjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;
			logger.info("RunTestRepositoryImpl.getRunTestDetails() totCount: " + totCount + ", paginationNumber: "
					+ paginationNumber);
			objMap.put("list", runTestEntityList);
			objMap.put("paginationNumber", paginationNumber);

		} catch (Exception e) {
			logger.error(
					"Exception in RunTestRepositoryImpl.getRunTestDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	/*
	 * @Override public String getEnbId(String dbCollectionName ,String neId) {
	 * List<EnbDetailsModel> resultList = null;
	 * 
	 * String enbIp = null;
	 * 
	 * Query query = new
	 * Query(org.springframework.data.mongodb.core.query.Criteria.where("eNBId").is(
	 * neId));
	 * query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where(
	 * "sheetAliasName").is("IPPLAN")); try {
	 * 
	 * resultList = mongoTemplate.find(query, EnbDetailsModel.class,
	 * dbCollectionName); System.out.println("resultList : "+resultList);
	 * 
	 * 
	 * 
	 * String vlan = fileUploadRepository.getEnBDataByPath(dbCollectionName,
	 * "IPPLAN", neId,"VLAN");
	 * 
	 * System.out.println("vlan  : "+vlan);
	 * 
	 * String oamIp = fileUploadRepository.getEnBDataByPath(dbCollectionName,
	 * "IPPLAN", neId,"eNB_OAM_IP&eNB_S&B_IP");
	 * 
	 * System.out.println("oamIp :: "+oamIp);
	 * 
	 * 
	 * 
	 * 
	 * } catch (Exception e) {
	 * logger.error("Exception getEnbId() in RunTestRepositoryImpl :" +
	 * ExceptionUtils.getFullStackTrace(e));
	 * 
	 * } return enbIp; }
	 * 
	 */

	@Override
	public NetworkConfigEntity getNetWorkEntityDetails(NeMappingModel neMappingModel) {
		List<NeMappingEntity> neMappingEntityList = null;
		NetworkConfigEntity networkConfigEntity = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NeMappingEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			if (neMappingModel != null) {
				if (StringUtils.isNotEmpty(neMappingModel.getEnbId())) {
					Criterion enbId = Restrictions.eq("enbId", neMappingModel.getEnbId());
					conjunction.add(enbId);
				}

				if (neMappingModel.getProgramDetailsEntity().getId() != null) {
					criteria.createAlias("programDetailsEntity", "programDetailsEntity");
					Criterion eventprogramName = Restrictions.eq("programDetailsEntity.id",
							neMappingModel.getProgramDetailsEntity().getId());
					conjunction.add(eventprogramName);
				}
				criteria.add(conjunction);

			}
			neMappingEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

			if (!ObjectUtils.isEmpty(neMappingEntityList)) {
				NeMappingEntity neDetails = neMappingEntityList.get(0);

				networkConfigEntity = neDetails.getNetworkConfigEntity();

			}
		} catch (Exception e) {
			logger.error("Exception getNeMapping() in NeMappingRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		}
		return networkConfigEntity;

	}

	/**
	 * This method will return list of usecase based on program ID
	 * 
	 * @return List<Map>
	 */

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<Map> getMigrationUseCaseList(int programId, String migrationType, String subType, String ciqFileName,
			List<String> enbIds, String programName) {

		List<Map> ucbLst = new ArrayList<>();
		for(String enbId:enbIds) {
		List<Map> ucbenbLst = new ArrayList<>();
		List<UseCaseBuilderEntity> usecaseBldrLists = new ArrayList<>();
		Conjunction conjunction = Restrictions.conjunction();

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
		Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UseCaseBuilderEntity.class);
		conjunction.add(Restrictions.eq("customerDetailsEntity", customerDetailsEntity));
		conjunction.add(Restrictions.eq("migrationType", migrationType));
		conjunction.add(Restrictions.eq("subType", subType));
		conjunction.add(Restrictions.eq("ciqFileName", ciqFileName));
		conjunction.add(Restrictions.ilike("useCaseName", enbId, MatchMode.ANYWHERE));
		criteria.add(conjunction);
		criteria.addOrder(Order.desc("id"));
		List<UseCaseBuilderEntity> usecaseBldrList = criteria
				.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
		/*
		 * if (ObjectUtils.isEmpty(usecaseBldrList)) {
		 * 
		 * customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class,
		 * programId);
		 * 
		 * usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		 * usecaseBldrQuery =
		 * usecaseBldrCriteriaBldr.createQuery(UseCaseBuilderEntity.class);
		 * usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		 * usecaseBldrQuery.select(usecaseBldrRoot).where(
		 * usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"),
		 * customerDetailsEntity),
		 * usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"),
		 * migrationType), usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"),
		 * subType), usecaseBldrCriteriaBldr.like(usecaseBldrRoot.get("useCaseName"),
		 * "%" + enbId + "%"));
		 * usecaseBldrQuery.orderBy(usecaseBldrCriteriaBldr.desc(usecaseBldrRoot.get(
		 * "id"))); usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		 * usecaseBldrList = usecaseBldrTypedQuery.getResultList(); }
		 */

		if (programName.contains("4G") && !subType.equalsIgnoreCase("NEGrow")) {
			// custom usecases
			List<UseCaseBuilderEntity> customUsecasesList = getMigrationCustomUseCases(programId, migrationType,
					subType, ciqFileName, enbId);
			if (!ObjectUtils.isEmpty(customUsecasesList)) {
				if (!ObjectUtils.isEmpty(usecaseBldrList)) {
					usecaseBldrList.addAll(customUsecasesList);
				} else {
					usecaseBldrList = customUsecasesList;
				}
			}

		}
		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {
			int count = 0;
			String usecaseName = StringUtils.substringBeforeLast(useCaseBuilderEntity.getUseCaseName(), "_");
			if (usecaseBldrLists.isEmpty()) {
				usecaseBldrLists.add(useCaseBuilderEntity);
			}
			for (int i = 0; i < usecaseBldrLists.size(); i++) {
				if (usecaseBldrLists.get(i).getUseCaseName().contains(usecaseName)) {
					count++;
				}
			}
			if (count == 0) {
				usecaseBldrLists.add(useCaseBuilderEntity);
			}
		}
		usecaseBldrList = usecaseBldrLists;
		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

			Map ucbMap = new HashMap<>();
			ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
			ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
			ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());
			ucbMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			CriteriaBuilder usecaseBldrParamCriteriaBldr = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> usecaseBldrParamQuery = usecaseBldrParamCriteriaBldr
					.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> usecaseBldrParamRoot = usecaseBldrParamQuery
					.from(UseCaseBuilderParamEntity.class);
			usecaseBldrParamQuery.select(usecaseBldrParamRoot).where(usecaseBldrParamCriteriaBldr
					.equal(usecaseBldrParamRoot.get("useCaseBuilderEntity"), useCaseBuilderEntity));
			TypedQuery<UseCaseBuilderParamEntity> usecaseBldrParamTypedQuery = entityManager
					.createQuery(usecaseBldrParamQuery);
			List<UseCaseBuilderParamEntity> usecaseBldrParamList = usecaseBldrParamTypedQuery.getResultList();

			List<Map> ucbParamLst = new ArrayList<>();
			for (UseCaseBuilderParamEntity usecaseBldrParam : usecaseBldrParamList) {
				Map ucbParamMap = new HashMap<>();
				ucbParamMap.put("scriptId", usecaseBldrParam.getScriptsDetails().getId());
				ucbParamMap.put("scriptExeSequence", usecaseBldrParam.getExecutionSequence());
				ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
				ucbParamMap.put("scriptName", usecaseBldrParam.getScriptsDetails().getFileName());
				ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);
				ucbParamLst.add(ucbParamMap);
			}
			ucbMap.put("scripts", ucbParamLst);
			ucbenbLst.add(ucbMap);
		}
		if(programName.contains("5G-MM")) {
			ucbenbLst = reorderUseCaseList(ucbenbLst);
		}
		ucbLst.addAll(ucbenbLst);
		}
		return ucbLst;

	}

	@SuppressWarnings("rawtypes")
	public List<Map> reorderUseCaseList(List<Map> useCaseLst) {
		List<Map> ucbMapList = new ArrayList<>();
		List<Map> ucbMapListENDC = new ArrayList<>();
		for(Map ucbMaptem : useCaseLst) {
			if(ucbMaptem.get("useCaseName").toString().toUpperCase().contains("ENDC")) {
				ucbMapListENDC.add(ucbMaptem);
			} else if(!ucbMaptem.get("useCaseName").toString().toUpperCase().contains("GP_SCRIPT_USECASE")){
				ucbMapList.add(ucbMaptem);
			}
		}
		ucbMapList.addAll(ucbMapListENDC);
		return ucbMapList;
	}
	
	public List<UseCaseBuilderEntity> getMigrationCustomUseCases(int programId, String migrationType, String subType,
			String ciqFileName, String enbId) {

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "RFUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "EndcUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "CommissionScriptUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "Grow_Cell_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "Grow_Enb_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "pnp_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "pnp_%"));

		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();
		return usecaseBldrList;

	}

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<Map> getMigrationUseCaseListWFM(int programId, String migrationType, String subType, String ciqFileName,
			String enbId, String programName) {

		List<Map> ucbLst = new ArrayList<>();

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("ciqFileName"), ciqFileName),
				usecaseBldrCriteriaBldr.like(usecaseBldrRoot.get("useCaseName"), "%" + enbId + "%"));
		usecaseBldrQuery.orderBy(usecaseBldrCriteriaBldr.desc(usecaseBldrRoot.get("id")));

		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();

		if (ObjectUtils.isEmpty(usecaseBldrList)) {

			customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
			usecaseBldrQuery = usecaseBldrCriteriaBldr.createQuery(UseCaseBuilderEntity.class);
			usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
			usecaseBldrQuery.select(usecaseBldrRoot).where(
					usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
					usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
					usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType),
					usecaseBldrCriteriaBldr.like(usecaseBldrRoot.get("useCaseName"), "%" + enbId + "%"));
			usecaseBldrQuery.orderBy(usecaseBldrCriteriaBldr.desc(usecaseBldrRoot.get("id")));
			usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
			usecaseBldrList = usecaseBldrTypedQuery.getResultList();
		}

		// custom usecases
		List<UseCaseBuilderEntity> customUsecasesList = getMigrationCustomUseCasesWFM(programId, migrationType, subType,
				ciqFileName, enbId);

		if (!ObjectUtils.isEmpty(customUsecasesList) && programName.contains("4G")) {
			if (!ObjectUtils.isEmpty(usecaseBldrList)) {
				usecaseBldrList.addAll(customUsecasesList);
			} else {
				usecaseBldrList = customUsecasesList;
			}

		}

		usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);

		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

			Map ucbMap = new HashMap<>();
			ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
			ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
			ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());
			ucbMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			CriteriaBuilder usecaseBldrParamCriteriaBldr = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> usecaseBldrParamQuery = usecaseBldrParamCriteriaBldr
					.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> usecaseBldrParamRoot = usecaseBldrParamQuery
					.from(UseCaseBuilderParamEntity.class);
			usecaseBldrParamQuery.select(usecaseBldrParamRoot).where(usecaseBldrParamCriteriaBldr
					.equal(usecaseBldrParamRoot.get("useCaseBuilderEntity"), useCaseBuilderEntity));
			TypedQuery<UseCaseBuilderParamEntity> usecaseBldrParamTypedQuery = entityManager
					.createQuery(usecaseBldrParamQuery);
			List<UseCaseBuilderParamEntity> usecaseBldrParamList = usecaseBldrParamTypedQuery.getResultList();

			List<Map> ucbParamLst = new ArrayList<>();
			for (UseCaseBuilderParamEntity usecaseBldrParam : usecaseBldrParamList) {

				CriteriaBuilder uploadFileCriteriaBldr = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> uploadFileQuery = uploadFileCriteriaBldr.createQuery(String.class);
				Root<UploadFileEntity> uploadFileRoot = uploadFileQuery.from(UploadFileEntity.class);
				uploadFileQuery.select(uploadFileRoot.get("fileName")).where(uploadFileCriteriaBldr
						.equal(uploadFileRoot.get("id"), usecaseBldrParam.getScriptsDetails().getId()));
				TypedQuery<String> uploadFileTypedQuery = entityManager.createQuery(uploadFileQuery);
				String scriptFileName = uploadFileTypedQuery.getSingleResult();

				Map ucbParamMap = new HashMap<>();
				ucbParamMap.put("scriptId", usecaseBldrParam.getScriptsDetails().getId());
				ucbParamMap.put("scriptExeSequence", usecaseBldrParam.getExecutionSequence());
				ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
				ucbParamMap.put("scriptName", scriptFileName);
				ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);

				ucbParamLst.add(ucbParamMap);
			}

			ucbMap.put("scripts", ucbParamLst);

			ucbLst.add(ucbMap);
		}

		return ucbLst;

	}

	public List<UseCaseBuilderEntity> getMigrationCustomUseCasesWFM(int programId, String migrationType, String subType,
			String ciqFileName, String enbId) {

		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "RFUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "CommissionScriptUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "Grow_Cell_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "Grow_Enb_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "GrowCell%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "GrowEnb%"),

				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "pnp_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "pnp_%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "GrowvDU%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "GrowFSU%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "CA_Usecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "EndcUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTimeUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNEUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTime21AUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNE21AUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTime21BUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNE21BUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTime21CUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNE21CUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTime21DUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNE21DUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "NeCreationTime22AUsecase%"),
				usecaseBldrCriteriaBldr.notLike(usecaseBldrRoot.get("useCaseName"), "DeleteNE22AUsecase%"));

		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();
		return usecaseBldrList;

	}

	@Override
	@SuppressWarnings("unchecked")
	public RunTestInputEntity getInputRuntestJson(int runTestId) {
		RunTestInputEntity runTestInputEntity = null;

		try {

			Criteria criter = entityManager.unwrap(Session.class).createCriteria(RunTestInputEntity.class);
			criter.add(Restrictions.eq("runTestID", runTestId));
			List<RunTestInputEntity> runTestInputList = criter.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
			if (!ObjectUtils.isEmpty(runTestInputList)) {
				runTestInputEntity = runTestInputList.get(0);
			}

		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestInputEntity;

	}

	/**
	 * This method will Save RunTest InPut Json
	 * 
	 * @Param RunTestEntity
	 * @return boolean
	 */
	@Override
	public boolean insertRunTestInputDetails(RunTestInputEntity runTestInputEntity) {
		boolean status = false;
		try {
			entityManager.merge(runTestInputEntity);
			status = true;

		} catch (Exception e) {
			status = false;
			logger.error("Exception in insertRunTestInputDetails() RunTestRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public NetworkConfigEntity getNEConfigEntity(String lsmVersion, String lsmName,CustomerDetailsEntity neMappingModel) {
		NetworkConfigEntity networkConfigEntity = null;
		List<NetworkConfigEntity> networkConfigEntitys = null;
		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class).createCriteria(NetworkConfigEntity.class);
			criteria.createAlias("neVersionEntity", "neVersionEntity");
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			Conjunction conjunction = Restrictions.conjunction();			
					conjunction.add(Restrictions.eq("neName", lsmName));
					conjunction.add(Restrictions.and(Restrictions.eq("neVersionEntity.neVersion",lsmVersion), Restrictions.eq("neVersionEntity.programDetailsEntity.id",
							neMappingModel.getId())));
				criteria.add(conjunction);
				networkConfigEntitys = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
				System.out.println(networkConfigEntitys);
				networkConfigEntity = (NetworkConfigEntity) criteria.uniqueResult();
			}
		catch (Exception e) {
			logger.error("Exception in insertRunTestInputDetails() RunTestRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigEntity;
	}
	
	
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public List<Map> getUseCaseListPostMig(int programId, String migrationType, String subType, List<String> enbIdList) {

		List<Map> ucbLst = new ArrayList<>();
		NetworkConfigEntity networkConfigEntity = null;
		NeVersionEntity neVersionEntity = null;
		for(String enbId : enbIdList) {
			NeMappingModel neMappingModel = new NeMappingModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(programId);
			neMappingModel.setProgramDetailsEntity(programDetailsEntity);
			neMappingModel.setEnbId(enbId);
			List<NeMappingEntity> neMappingEntities = neMappingService.getNeMapping(neMappingModel);

			if (CommonUtil.isValidObject(neMappingEntities) && !neMappingEntities.isEmpty()) {
				NeMappingEntity neMappingEntity = neMappingEntities.get(0);
				if(CommonUtil.isValidObject(neMappingEntity) && CommonUtil.isValidObject(neMappingEntity.getSiteConfigType())) {
					networkConfigEntity = neMappingEntity.getNetworkConfigEntity();
					break;
				}
			}
		}
		try {
			if(networkConfigEntity!=null) {
				neVersionEntity = networkConfigEntity.getNeVersionEntity();
			}			
		} catch(Exception e) {
			logger.error("Exception in getUseCaseListPostMig() RunTestRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
			neVersionEntity = null;
		}
		CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

		CriteriaBuilder usecaseBldrCriteriaBldr = entityManager.getCriteriaBuilder();
		CriteriaQuery<UseCaseBuilderEntity> usecaseBldrQuery = usecaseBldrCriteriaBldr
				.createQuery(UseCaseBuilderEntity.class);
		Root<UseCaseBuilderEntity> usecaseBldrRoot = usecaseBldrQuery.from(UseCaseBuilderEntity.class);
		usecaseBldrQuery.select(usecaseBldrRoot).where(
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("customerDetailsEntity"), customerDetailsEntity),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("migrationType"), migrationType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("subType"), subType),
				usecaseBldrCriteriaBldr.equal(usecaseBldrRoot.get("neVersion"), neVersionEntity));
		TypedQuery<UseCaseBuilderEntity> usecaseBldrTypedQuery = entityManager.createQuery(usecaseBldrQuery);
		List<UseCaseBuilderEntity> usecaseBldrList = usecaseBldrTypedQuery.getResultList();

		for (UseCaseBuilderEntity useCaseBuilderEntity : usecaseBldrList) {

			Map ucbMap = new HashMap<>();
			ucbMap.put("useCaseName", useCaseBuilderEntity.getUseCaseName());
			ucbMap.put("useCaseId", useCaseBuilderEntity.getId());
			ucbMap.put("executionSequence", useCaseBuilderEntity.getExecutionSequence());
			ucbMap.put("ucSleepInterval", Constants.UC_SLEEPINTERVAL);

			CriteriaBuilder usecaseBldrParamCriteriaBldr = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> usecaseBldrParamQuery = usecaseBldrParamCriteriaBldr
					.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> usecaseBldrParamRoot = usecaseBldrParamQuery
					.from(UseCaseBuilderParamEntity.class);
			usecaseBldrParamQuery.select(usecaseBldrParamRoot).where(usecaseBldrParamCriteriaBldr
					.equal(usecaseBldrParamRoot.get("useCaseBuilderEntity"), useCaseBuilderEntity));
			TypedQuery<UseCaseBuilderParamEntity> usecaseBldrParamTypedQuery = entityManager
					.createQuery(usecaseBldrParamQuery);
			List<UseCaseBuilderParamEntity> usecaseBldrParamList = usecaseBldrParamTypedQuery.getResultList();

			List<Map> ucbParamLst = new ArrayList<>();
			for (UseCaseBuilderParamEntity usecaseBldrParam : usecaseBldrParamList) {

				CriteriaBuilder uploadFileCriteriaBldr = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> uploadFileQuery = uploadFileCriteriaBldr.createQuery(String.class);
				Root<UploadFileEntity> uploadFileRoot = uploadFileQuery.from(UploadFileEntity.class);
				uploadFileQuery.select(uploadFileRoot.get("fileName")).where(uploadFileCriteriaBldr
						.equal(uploadFileRoot.get("id"), usecaseBldrParam.getScriptsDetails().getId()));
				TypedQuery<String> uploadFileTypedQuery = entityManager.createQuery(uploadFileQuery);
				String scriptFileName = uploadFileTypedQuery.getSingleResult();

				Map ucbParamMap = new HashMap<>();
				ucbParamMap.put("scriptId", usecaseBldrParam.getScriptsDetails().getId());
				ucbParamMap.put("scriptExeSequence", usecaseBldrParam.getExecutionSequence());
				ucbParamMap.put("scriptSleepInterval", Constants.SCRIPT_SLEEPINTERVAL);
				ucbParamMap.put("scriptName", scriptFileName);
				ucbParamMap.put("useGeneratedScript", Constants.GENERATED_SCRIPT);

				ucbParamLst.add(ucbParamMap);
			}

			ucbMap.put("scripts", ucbParamLst);

			ucbLst.add(ucbMap);
		}

		return ucbLst;


	}

@Override
	public boolean getusecaseDetails(int programId, String migrationType, String subType, String useCaseName) {
		List<RunTestEntity> runTestEntity = null;
		boolean isTestNamePresent = true;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<RunTestEntity> query = cb.createQuery(RunTestEntity.class);
			Root<RunTestEntity> root = query.from(RunTestEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("migrationSubType"), subType),
					cb.equal(root.get("useCase"), useCaseName));
			TypedQuery<RunTestEntity> queryResult = entityManager.createQuery(query);
			runTestEntity = queryResult.getResultList();

			if (runTestEntity.isEmpty()) {
				isTestNamePresent = false;
			}

		} catch (Exception e) {
			logger.error(" getRunTestEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return isTestNamePresent;
	}
}
