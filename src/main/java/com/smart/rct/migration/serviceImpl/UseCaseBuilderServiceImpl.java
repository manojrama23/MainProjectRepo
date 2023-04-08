package com.smart.rct.migration.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.common.models.NetworkTypeDetailsModel;
import com.smart.rct.common.repository.LsmRepository;
import com.smart.rct.common.repository.NetworkTypeDetailsRepository;
import com.smart.rct.constants.Constants;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.dto.UseCaseBuilderDto;
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
import com.smart.rct.migration.model.CmdRuleModel;
import com.smart.rct.migration.model.FileRuleModel;
import com.smart.rct.migration.model.SearchModel;
import com.smart.rct.migration.model.ShellRuleModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.migration.model.XmlRuleModel;
import com.smart.rct.migration.repository.CmdRuleBuilderRepository;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.migration.repositoryImpl.CmdRuleBuilderRepositoryImpl;
import com.smart.rct.migration.repositoryImpl.UseCaseBuilderRepositoryImpl;
import com.smart.rct.migration.service.UseCaseBuilderService;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;
import com.smart.rct.util.PaginationModel;

@Service
public class UseCaseBuilderServiceImpl implements UseCaseBuilderService {

	static final Logger logger = LoggerFactory.getLogger(UseCaseBuilderRepositoryImpl.class);

	@Autowired
	UseCaseBuilderRepository useCaseBuilderRepository;

	@Autowired
	UseCaseBuilderDto useCaseBuilderDto;

	@Autowired
	CmdRuleBuilderRepository cmdRuleBuilderRepository;

	@Autowired
	LsmRepository lsmRepository;

	@Autowired
	NetworkTypeDetailsRepository networkTypeDetailsRepository;

	@Autowired
	CmdRuleBuilderRepositoryImpl cmdRuleBuilderRepositoryImpl;

	@Override
	public boolean createUseCaseBuilder(UseCaseBuilderModel useCaseBuilderModel, Integer customerId,
			String migrationType, int programId, String subType, String sessionId) throws RctException {
		boolean createUseCaseStatus = false;
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {

			List<UseCaseScriptsModel> scriptList = useCaseBuilderModel.getScriptList();

			if (useCaseBuilderModel.getUseCaseName().contains(Constants.RF_USECASE)
					|| useCaseBuilderModel.getUseCaseName().contains(Constants.COMMISION_USECASE))
			/*
			 * || useCaseBuilderModel.getUseCaseName().contains(Constants.GROW_CELL_USECASE)
			 * || useCaseBuilderModel.getUseCaseName().contains(Constants.GROW_ENB_USECASE)
			 * || useCaseBuilderModel.getUseCaseName().contains(Constants.PNP_USECASE) ||
			 * useCaseBuilderModel.getUseCaseName().contains("AU_CaCell_20B_UseCase") ||
			 * useCaseBuilderModel.getUseCaseName().contains("AU_CaCell_20A_UseCase") ||
			 * useCaseBuilderModel.getUseCaseName().contains("AU_20B_UseCase") ||
			 * useCaseBuilderModel.getUseCaseName().contains("AU_20A_UseCase") ||
			 * useCaseBuilderModel.getUseCaseName().contains("pnp_20B") ||
			 * useCaseBuilderModel.getUseCaseName().contains("pnp_20A"))
			 */ {
				// do nothing
			} else {
				createScriptPathForUseCase(useCaseBuilderModel, scriptList);
			}

			boolean checkStatus = true;
			Map<String, String> result = new HashMap<>();
			if (!useCaseBuilderModel.getScriptList().isEmpty()) {
				result = checkScriptContents(useCaseBuilderModel, migrationType, subType, programId);
				checkStatus = Boolean.valueOf(result.get("scriptStatus"));
			}
			if (checkStatus) {
				useCaseBuilderEntity = new UseCaseBuilderEntity();
				useCaseBuilderEntity.setCustomerId(useCaseBuilderModel.getCustomerId());
				NetworkConfigEntity networkConfigEntity = null;
				if (!StringUtils.isEmpty(useCaseBuilderModel.getLsmVersion())
						&& !StringUtils.isEmpty(useCaseBuilderModel.getLsmName())) {
					networkConfigEntity = useCaseBuilderRepository.getLsmEntity(useCaseBuilderModel.getLsmName(),
							useCaseBuilderModel.getLsmVersion(), programId);
				}
				// else if (!StringUtils.isEmpty(useCaseBuilderModel.getLsmVersion())) {
				// networkConfigEntity =
				// useCaseBuilderRepository.getLsmEntity(useCaseBuilderModel.getLsmVersion());
				// }

				/*
				 * List<UseCaseScriptsModel> scriptList = useCaseBuilderModel.getScriptList();
				 * 
				 * if (useCaseBuilderModel.getUseCaseName().contains(Constants.RF_USECASE) ||
				 * useCaseBuilderModel.getUseCaseName().contains(Constants.COMMISION_USECASE)) {
				 * // do nothing } else { createScriptPathForUseCase(useCaseBuilderModel,
				 * scriptList); }
				 */

				CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository
						.getCustomerDetailsEntity(programId);
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				useCaseBuilderEntity.setCreatedBy(user.getUserName());
				useCaseBuilderEntity.setNetworkConfigEntity(networkConfigEntity);
				useCaseBuilderEntity.setUseCaseName(useCaseBuilderModel.getUseCaseName());
				useCaseBuilderEntity.setRemarks(useCaseBuilderModel.getRemarks());
				useCaseBuilderEntity.setExecutionSequence(Integer.parseInt(useCaseBuilderModel.getExecutionSequence()));
				useCaseBuilderEntity.setUseCount(0);
				useCaseBuilderEntity.setUseCaseCreationDate(new Date());
				useCaseBuilderEntity.setMigrationType(migrationType);
				useCaseBuilderEntity.setCustomerDetailsEntity(customerDetailsEntity);
				/*
				 * if (migrationType.equalsIgnoreCase("PreMigration")) { subType = ""; }
				 */
				useCaseBuilderEntity.setSubType(subType);
				useCaseBuilderEntity.setNeVersion(useCaseBuilderModel.getNeVersion());
				useCaseBuilderEntity.setCiqFileName(useCaseBuilderModel.getCiqFileName());
				boolean stat = useCaseBuilderRepository.saveUseCaseBuilder(useCaseBuilderEntity);
				createUseCaseStatus = createUseCaseBuilders(useCaseBuilderEntity, scriptList, programId,
						useCaseBuilderModel.getLsmName(), useCaseBuilderModel.getLsmVersion(), migrationType, subType);

			} else {
				String command = result.get("command");
				String scriptName = result.get("scriptName");
				throw new RctException(command + " Command is not exist for " + scriptName + " script");
			}
		} catch (Exception e) {
			logger.error("Exception in createUseCaseBuilder() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
			if (e instanceof RctException)
				throw new RctException(e.getMessage());
			else if (e instanceof FileNotFoundException) {
				throw new RctException(e.getMessage());
			}
		}

		return createUseCaseStatus;
	}

	private boolean createUseCaseBuilders(UseCaseBuilderEntity useCaseBuilderEntity,
			List<UseCaseScriptsModel> scriptList, int programId, String lsmName, String lsmVersion,
			String migrationType, String subType) {
		boolean saveStatus = false;
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		/*
		 * if (migrationType.equalsIgnoreCase("PreMigration")) { UseCaseScriptsModel
		 * useCaseScriptsModel1 = scriptList.get(0); }
		 */
		try {

			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {

				CheckListScriptDetEntity checkListScriptDetEntity = useCaseBuilderRepository
						.getCheckListDetails(programId);

				if (useCaseScriptsModel.getScriptSequence() != null
						&& !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					// do nothing
				} else {
					CheckListScriptDetails = useCaseBuilderRepository.getExeseq(programId,
							useCaseScriptsModel.getScript().get("scriptName"), "",
							checkListScriptDetEntity.getCheckListFileName());
				}

				UseCaseBuilderParamEntity useCaseBuilderParamEntity = new UseCaseBuilderParamEntity();
				String ScriptName=useCaseScriptsModel.getScript().get("scriptName");
				System.out.println(ScriptName);
				UploadFileEntity uploadFileEntity = useCaseBuilderRepository.getUploadFileEntity(lsmName, lsmVersion,
						programId, useCaseBuilderEntity.getMigrationType(),
						useCaseScriptsModel.getScript().get("scriptName"),
						useCaseScriptsModel.getScript().get("scriptFileId"), subType);
				useCaseBuilderParamEntity.setScriptsDetails(uploadFileEntity);
				if (useCaseScriptsModel.getScriptSequence() != null
						&& !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					useCaseBuilderParamEntity
							.setExecutionSequence(Integer.parseInt(useCaseScriptsModel.getScriptSequence()));
				} else {
					useCaseBuilderParamEntity.setExecutionSequence(CheckListScriptDetails.get(0).getScriptExeSeq());
				}
				useCaseBuilderParamEntity.setUseCaseBuilderEntity(useCaseBuilderEntity);
				useCaseBuilderParamEntity.setScriptRemarks(useCaseScriptsModel.getScriptRemarks());

				CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepositoryImpl
						.getCustomerDetailsEntity(programId);
				useCaseBuilderParamEntity.setCustomerDetailsEntity(customerDetailsEntity);
				boolean sta = useCaseBuilderRepository.saveparamEntity(useCaseBuilderParamEntity);

				List<CmdRuleModel> cmdRuleList = useCaseScriptsModel.getCmdRules();
				for (CmdRuleModel cmdRuleModel : cmdRuleList) {
					String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
					String cmdSequence = cmdRuleModel.getCmdSequence();
					String cmdRemarks = cmdRuleModel.getCmdRemarks();
					CmdRuleBuilderEntity cmdRuleBuilderEntity = useCaseBuilderRepository.getCommandRuleEntity(cmdName,
							programId, migrationType, subType);
					UseCaseCmdRuleEntity useCaseCmdRuleEntity = new UseCaseCmdRuleEntity();
					useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
					useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
					useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
					useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
					useCaseBuilderRepository.saveCmdRuleEntity(useCaseCmdRuleEntity);
				}

				List<ShellRuleModel> shellRuleList = useCaseScriptsModel.getShellRules();
				for (ShellRuleModel shellRuleModel : shellRuleList) {
					String shellName = shellRuleModel.getShellDetails().get("shellCmdName");
					String shellSequence = shellRuleModel.getShellRuleSequence();
					String shellRemarks = shellRuleModel.getShellRuleRemarks();
					ShellCmdRuleBuilderEntity shellRuleBuilderEntity = useCaseBuilderRepository
							.getShellRuleEntity(shellName, programId, migrationType, subType);
					UseCaseShellRuleEntity useCaseShellRuleEntity = new UseCaseShellRuleEntity();
					useCaseShellRuleEntity.setShellRuleBuilderEntity(shellRuleBuilderEntity);
					useCaseShellRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
					useCaseShellRuleEntity.setShellRemarks(shellRemarks);
					useCaseShellRuleEntity.setShellRuleSequence(Integer.parseInt(shellSequence));
					useCaseBuilderRepository.saveCaseShell(useCaseShellRuleEntity);
				}

				List<XmlRuleModel> xmlRuleList = useCaseScriptsModel.getXmlRules();
				for (XmlRuleModel xmlRuleModel : xmlRuleList) {
					String xmlRuleName = xmlRuleModel.getXmlDetails().get("xmlName");
					String xmlSequence = xmlRuleModel.getXmlSequence();
					String xmlRemarks = xmlRuleModel.getXmlRemarks();
					XmlRuleBuilderEntity xmlRuleBuilderEntity;

					if (migrationType.equalsIgnoreCase("PreMigration")) {
						String migrationType1 = "PreMigration";
						String subType1 = "";	
						if(ScriptName.contains("BASH_CA")) {
							migrationType1="Migration";
							subType1="PreCheck";
							xmlRuleBuilderEntity = useCaseBuilderRepository.getXmlRuleBuilderEntity(xmlRuleName, programId,
									migrationType1, subType1);
						}else {
						xmlRuleBuilderEntity = useCaseBuilderRepository.getXmlRuleBuilderEntity(xmlRuleName, programId,
								migrationType1, subType1);
						}
					} else {
						xmlRuleBuilderEntity = useCaseBuilderRepository.getXmlRuleBuilderEntity(xmlRuleName, programId,
								migrationType, subType);

					}

					/*
					 * xmlRuleBuilderEntity =
					 * useCaseBuilderRepository.getXmlRuleBuilderEntity(xmlRuleName, programId,
					 * migrationType, subType);
					 */
					UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
					useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
					useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
					useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
					useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
					useCaseBuilderRepository.saveCaseXml(useCaseXmlRuleEntity);
					/*
					 * } else { XmlRuleBuilderEntity xmlRuleBuilderEntity = useCaseBuilderRepository
					 * .getXmlRuleBuilderEntity(xmlRuleName, programId, migrationType, subType);
					 * 
					 * UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
					 * useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
					 * useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
					 * useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity
					 * ); useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
					 * useCaseBuilderRepository.saveCaseXml(useCaseXmlRuleEntity); }
					 * 
					 */

				}

				List<FileRuleModel> fileRuleList = useCaseScriptsModel.getFileRules();
				if (!fileRuleList.isEmpty()) {
					for (FileRuleModel fileRuleModel : fileRuleList) {
						String fileRuleName = fileRuleModel.getFileDetails().get("fileRuleName");
						FileRuleBuilderEntity fileRuleBuilderEntity = useCaseBuilderRepository
								.getFileRuleEntity(fileRuleName, programId, migrationType, subType);
						UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
						useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
						useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
						useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
						useCaseFileRuleEntity
								.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
						useCaseBuilderRepository.saveCaseFile(useCaseFileRuleEntity);
					}
				}
				/*
				 * if (migrationType.equalsIgnoreCase("PreMigration")) {
				 * 
				 * FileRuleModel fileRuleModel=null;
				 * 
				 * @SuppressWarnings("null") String fileRuleName =
				 * fileRuleModel.getFileDetails().get("fileRuleName"); FileRuleBuilderEntity
				 * fileRuleBuilderEntity = useCaseBuilderRepository
				 * .getFileRuleEntity(fileRuleName, programId, migrationType, subType);
				 * UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
				 * useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
				 * 
				 * UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
				 * useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(
				 * useCaseBuilderParamEntity); //
				 * useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks()); //
				 * useCaseFileRuleEntity //
				 * .setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
				 * useCaseBuilderRepository.saveCaseFile(useCaseFileRuleEntity);
				 * 
				 * break; }
				 */ }

			saveStatus = true;
		} catch (

		Exception e) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		}
		// TODO Auto-generated method stub
		return saveStatus;
	}

	public void createScriptPathForUseCase(UseCaseBuilderModel useCaseBuilderModel,
			List<UseCaseScriptsModel> scriptList) {
		try {
			for (UseCaseScriptsModel useCaseScriptModel : scriptList) {
				String scriptId = useCaseScriptModel.getScript().get("scriptFileId");
				UploadFileEntity uploadFileEntity = useCaseBuilderRepository.getUploadFileEntityByScriptId(scriptId);
				String scriptPath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
						+ uploadFileEntity.getFilePath();
				String scriptName = uploadFileEntity.getFileName();
				StringBuilder sourcePath = new StringBuilder();
				sourcePath = sourcePath.append(scriptPath).append(scriptName);
				File source = new File(sourcePath.toString());
				StringBuilder sb = new StringBuilder();
				sb.append(scriptPath).append(useCaseBuilderModel.getUseCaseName().trim().replaceAll(" ", "_"));
				FileUtil.createDirectory(sb.toString());
				sb.append("/" + scriptName);
				File dest = new File(sb.toString());
				FileUtils.copyFile(source, dest);
			}
		} catch (Exception e) {
			logger.info("Exception in createScriptPathForUseCase() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public List<String> getNwTypeList() {
		List<String> nwTypeList = null;
		try {
			nwTypeList = useCaseBuilderRepository.getNwTypeList();
		} catch (Exception e) {
			logger.info(
					"Exception in getNwTypeList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return nwTypeList;
	}

	@Override
	public Map<String, List<String>> getLsmNameList() {

		Map<String, List<String>> lsmVersionNameMap = new HashMap<>();
		List<LsmEntity> lsmNameEntityList = null;
		List<String> lsmNameList = null;

		try {
			lsmNameEntityList = useCaseBuilderRepository.getLsmNameList();
			for (LsmEntity lsmEntity : lsmNameEntityList) {
				if (lsmVersionNameMap.containsKey(lsmEntity.getLsmVersion())) {
					List<String> lsmName = lsmVersionNameMap.get(lsmEntity.getLsmVersion());
					lsmName.add(lsmEntity.getLsmName());
					lsmVersionNameMap.put(lsmEntity.getLsmVersion(), lsmName);
				} else {
					lsmNameList = new ArrayList<>();
					lsmNameList.add(lsmEntity.getLsmName());
					lsmVersionNameMap.put(lsmEntity.getLsmVersion(), lsmNameList);
				}

			}
		} catch (Exception e) {
			logger.info(
					"Exception in getNsmNameList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return lsmVersionNameMap;
	}

	@Override
	public Map<Integer, String> getScriptList(Integer customerId) {
		Map<Integer, String> scriptMap = new HashMap<>();
		List<UploadFileEntity> scriptList = null;
		try {
			scriptList = useCaseBuilderRepository.getScriptList(customerId);
			for (UploadFileEntity UploadFileEntity : scriptList) {
				scriptMap.put(UploadFileEntity.getId(), UploadFileEntity.getFileName());
			}
		} catch (Exception e) {
			logger.info(
					"Exception in getScriptList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return scriptMap;
	}

	@Override
	public List<Map<String, String>> getCommandRuleList(Integer programId, String migrationType, String subType) {

		List<CmdRuleBuilderEntity> commandRuleList = null;
		List<Map<String, String>> cmdRuleList = new LinkedList<>();
		try {
			commandRuleList = useCaseBuilderRepository.getCommandRuleList(programId, migrationType, subType);

			for (CmdRuleBuilderEntity CmdRuleBuilderEntity : commandRuleList) {
				Map<String, String> cmdRuleMap = new HashMap<>();
				cmdRuleMap.put("cmdRuleBuilderId", String.valueOf(CmdRuleBuilderEntity.getId()));
				cmdRuleMap.put("cmdName", CmdRuleBuilderEntity.getRuleName());
				cmdRuleList.add(cmdRuleMap);
			}
		} catch (Exception e) {
			logger.info("Exception in getCommandRuleList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return cmdRuleList;
	}

	@Override
	public List<Map<String, String>> getShellRuleList(int programId, String migrationType, String subType) {

		List<ShellCmdRuleBuilderEntity> shellCmdRuleEntityList = null;
		List<Map<String, String>> shellCmdRuleList = new LinkedList<>();
		try {
			shellCmdRuleEntityList = useCaseBuilderRepository.getShellCommandRuleList(programId, migrationType,
					subType);
			for (ShellCmdRuleBuilderEntity shellCmdRuleEntity : shellCmdRuleEntityList) {
				Map<String, String> shellCmdRuleMap = new HashMap<>();
				shellCmdRuleMap.put("shellCmdRuleBuilderId", String.valueOf(shellCmdRuleEntity.getId()));
				shellCmdRuleMap.put("shellCmdName", shellCmdRuleEntity.getRuleName());
				shellCmdRuleList.add(shellCmdRuleMap);
			}
		} catch (Exception e) {
			logger.info("Exception in getShellRuleList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return shellCmdRuleList;
	}

	@Override
	public List<Map<String, String>> getFileRuleList(Integer programId, String migrationType, String subType) {

		List<FileRuleBuilderEntity> fileRuleEntityList = null;
		List<Map<String, String>> fileRuleList = new LinkedList<>();
		try {
			fileRuleEntityList = useCaseBuilderRepository.getFileRuleList(programId, migrationType, subType);
			for (FileRuleBuilderEntity fileRuleBuilderEntity : fileRuleEntityList) {
				Map<String, String> fileRuleMap = new HashMap<>();
				fileRuleMap.put("fileRuleBuilderId", String.valueOf(fileRuleBuilderEntity.getId()));
				fileRuleMap.put("fileRuleName", fileRuleBuilderEntity.getRuleName());
				fileRuleList.add(fileRuleMap);
			}
		} catch (Exception e) {
			logger.info("Exception in getFileRuleList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return fileRuleList;
	}

	@Override
	public int loadUseCaseBuilderDetails(SearchModel searchModel, Integer programId, String migrationType,
			String subtype) {
		int useCaseBuilderEntityCount = 0;
		try {
			List<UseCaseBuilderEntity> useCaseBuilderEntityList = useCaseBuilderRepository
					.loadUseCaseBuilderDetails(searchModel, programId, migrationType, subtype);
			useCaseBuilderEntityCount = useCaseBuilderEntityList.size();
		} catch (Exception e) {
			logger.info("Exception in loadUseCaseBuilderDetails() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntityCount;
	}

	@Override
	public int loadUseCaseBuilderDetails(Integer programId, String migrationType, String subType) {
		int useCaseBuilderEntityCount = 0;
		try {
			List<UseCaseBuilderEntity> useCaseBuilderEntityList = useCaseBuilderRepository
					.loadUseCaseBuilderDetails(programId, migrationType, subType);
			useCaseBuilderEntityCount = useCaseBuilderEntityList.size();
		} catch (Exception e) {
			logger.info("Exception in loadUseCaseBuilderDetails() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntityCount;
	}

	@Override
	public List<UseCaseBuilderModel> loadUseCaseBuilderDetails(PaginationModel paginationModel, Integer customerId,
			String migrationType, int programId, String subType, User user) {
		List<UseCaseBuilderModel> useCaseBuilderModelList = new ArrayList<>();
		try {
			List<UseCaseBuilderEntity> useCaseBuilderEntityList = useCaseBuilderRepository.loadUseCaseBuilderDetails(
					Integer.parseInt(paginationModel.getPage()), Integer.parseInt(paginationModel.getCount()),
					customerId, migrationType, programId, subType, user);
			if (!useCaseBuilderEntityList.isEmpty())
				useCaseBuilderModelList = useCaseBuilderDto
						.convertUseCaseBuilderEntityToModel(useCaseBuilderEntityList);
		} catch (Exception e) {
			logger.info("Exception in loadUseCaseBuilderDetails() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderModelList;
	}

	@Override
	public Map<String, Object> loadUseCaseBuilderSearchDetails(PaginationModel paginationModel, SearchModel searchModel,
			Integer customerId, String migrationType, int programId, String subType, User user) {
		List<UseCaseBuilderModel> useCaseBuilderModelList = new ArrayList<>();
		Map<String, Object> useCaseBuilderEntityList = null;
		try {
			useCaseBuilderEntityList = useCaseBuilderRepository.loadUseCaseBuilderSearchDetails(
					Integer.parseInt(paginationModel.getPage()), Integer.parseInt(paginationModel.getCount()),
					searchModel, customerId, migrationType, programId, subType, user);

			List<UseCaseBuilderEntity> useCaseBuilderEntity = (List<UseCaseBuilderEntity>) useCaseBuilderEntityList
					.get("cmdRuleBuilderData");

			if (!useCaseBuilderEntityList.isEmpty())
				useCaseBuilderModelList = useCaseBuilderDto.convertUseCaseBuilderEntityToModel(useCaseBuilderEntity);

			useCaseBuilderEntityList.put("cmdRuleBuilderData", useCaseBuilderModelList);
			useCaseBuilderEntityList.put("totalCount", useCaseBuilderEntityList.get("totalCount"));

		} catch (Exception e) {
			logger.info("Exception in loadUseCaseBuilderSearchDetails() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntityList;
	}

	@Override
	public boolean deleteUseCaseBuilder(Integer id) {

		boolean deleteUseCaseStatus = false;
		try {
			UseCaseBuilderEntity useCaseBuilderEntity = useCaseBuilderRepository.getUseCaseBuilderEntity(id);
			String useCaseName = useCaseBuilderEntity.getUseCaseName();
			if (CommonUtil.isValidObject(useCaseBuilderEntity.getUseCaseBuilderParamEntity())
					&& useCaseBuilderEntity.getUseCaseBuilderParamEntity().size() > 0) {
				UploadFileEntity uploadFileEntity = useCaseBuilderEntity.getUseCaseBuilderParamEntity().iterator()
						.next().getScriptsDetails();
				if (uploadFileEntity != null) {
					String filePath = uploadFileEntity.getFilePath();
					filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath + "/" + useCaseName;
					// if (FileUtil.hasMatchingDir(useCaseName, uploadFileEntity.getFilePath()))
					FileUtil.deleteFileOrFolder(filePath);
				}
			}
			deleteUseCaseStatus = useCaseBuilderRepository.deleteUseCaseBuilder(id);
		} catch (Exception e) {
			logger.info("Exception in deleteUseCaseBuilder() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return deleteUseCaseStatus;
	}

	@Override
	public boolean updateUseCaseBuilder(UseCaseBuilderModel useCaseBuilderModel, Integer customerId,
			String migrationType, int programId, String subType, String sessionId) throws RctException {

		boolean updateUseCaseStatus = false;
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			boolean checkStatus = true;

			Map<String, String> result = new HashMap<>();
			if (!useCaseBuilderModel.getScriptList().isEmpty()) {
				result = checkScriptContents(useCaseBuilderModel, migrationType, subType, programId);
				checkStatus = Boolean.valueOf(result.get("scriptStatus"));
			}
			if (checkStatus) {
				NetworkConfigEntity networkConfigEntity = useCaseBuilderRepository
						.getLsmEntity(useCaseBuilderModel.getLsmName(), useCaseBuilderModel.getLsmVersion(), programId);

				List<UseCaseScriptsModel> scriptList = useCaseBuilderModel.getScriptList();

				int count = 0;
				ArrayList<Integer> scriptId = new ArrayList();
				ArrayList<Integer> cmdId = new ArrayList();
				ArrayList<Integer> shellId = new ArrayList();
				ArrayList<Integer> fileId = new ArrayList();
				ArrayList<Integer> xmlId = new ArrayList();
				for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
					UseCaseBuilderParamEntity useCaseBuilderParamEntity;
					if (useCaseScriptsModel.getScriptId() != null) {
						useCaseBuilderParamEntity = useCaseBuilderRepository
								.getUseCaseBuilderParamEntity(Integer.parseInt(useCaseScriptsModel.getScriptId()));

						List<UseCaseBuilderParamEntity> useCaseBuilderParamEntityUseCase = useCaseBuilderRepository
								.getUseCaseBuilderParamUseCase(useCaseBuilderParamEntity);

						List<UseCaseCmdRuleEntity> useCaseCmdRuleEntity = useCaseBuilderRepository
								.getUseCaseCmdRuleEntityList(Integer.parseInt(useCaseScriptsModel.getScriptId()));

						List<UseCaseFileRuleEntity> useCaseFileRuleEntity = useCaseBuilderRepository
								.getUseCaseFileRuleEntityList(Integer.parseInt(useCaseScriptsModel.getScriptId()));

						List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = useCaseBuilderRepository
								.getUseCaseXmlRuleEntityList(Integer.parseInt(useCaseScriptsModel.getScriptId()));

						List<UseCaseShellRuleEntity> useCaseShellRuleEntity = useCaseBuilderRepository
								.getUseCaseShellRuleEntityList(Integer.parseInt(useCaseScriptsModel.getScriptId()));

						if (count == 0) {
							for (UseCaseBuilderParamEntity useCaseBuilderParam : useCaseBuilderParamEntityUseCase) {
								scriptId.add(useCaseBuilderParam.getId());
							}

							for (UseCaseShellRuleEntity useCaseShellRule : useCaseShellRuleEntity) {
								shellId.add(useCaseShellRule.getId());
							}

							for (UseCaseCmdRuleEntity useCaseCmdRule : useCaseCmdRuleEntity) {
								cmdId.add(useCaseCmdRule.getId());
							}

							for (UseCaseFileRuleEntity useCaseFileRule : useCaseFileRuleEntity) {
								fileId.add(useCaseFileRule.getId());
							}

							for (UseCaseXmlRuleEntity useCaseXmlRule : useCaseXmlRuleEntity) {
								xmlId.add(useCaseXmlRule.getId());
							}

						}

						if (scriptId.contains(Integer.parseInt(useCaseScriptsModel.getScriptId()))) {
							int location = scriptId.indexOf(Integer.parseInt(useCaseScriptsModel.getScriptId()));
							scriptId.remove(location);
						} else {
							// do nothing
						}

						List<CmdRuleModel> cmdList = useCaseScriptsModel.getCmdRules();
						for (CmdRuleModel cmdRuleModel : cmdList) {
							if (cmdRuleModel.getCmdId() != null) {
								if (cmdId.contains(Integer.parseInt(cmdRuleModel.getCmdId()))) {
									int location = cmdId.indexOf(Integer.parseInt(cmdRuleModel.getCmdId()));
									cmdId.remove(location);
								} else {
									// do nothing
								}
							}
						}

						List<ShellRuleModel> shellList = useCaseScriptsModel.getShellRules();
						for (ShellRuleModel shellRuleModel : shellList) {
							if (shellRuleModel.getShellRuleId() != null) {
								if (shellId.contains(Integer.parseInt(shellRuleModel.getShellRuleId()))) {
									int location = shellId.indexOf(Integer.parseInt(shellRuleModel.getShellRuleId()));
									shellId.remove(location);
								} else {
									// do nothing
								}
							}
						}

						List<FileRuleModel> fileList = useCaseScriptsModel.getFileRules();
						for (FileRuleModel fileRuleModel : fileList) {
							if (fileRuleModel.getFileRuleId() != null) {
								if (fileId.contains(Integer.parseInt(fileRuleModel.getFileRuleId()))) {
									int location = fileId.indexOf(Integer.parseInt(fileRuleModel.getFileRuleId()));
									fileId.remove(location);
								} else {
									// do nothing
								}
							}
						}

						List<XmlRuleModel> xmlList = useCaseScriptsModel.getXmlRules();
						for (XmlRuleModel xmlRuleModel : xmlList) {
							if (xmlRuleModel.getXmlId() != null) {
								if (xmlId.contains(Integer.parseInt(xmlRuleModel.getXmlId()))) {
									int location = xmlId.indexOf(Integer.parseInt(xmlRuleModel.getXmlId()));
									xmlId.remove(location);
								} else {
									// do nothing
								}
							}
						}
					}
					count = 1;
				}

				// Delete use case builder
				for (int iscriptId : scriptId) {

					List<UseCaseCmdRuleEntity> cmdEntity = useCaseBuilderRepository.getCmdEntity(iscriptId);
					if (cmdEntity != null) {
						for (UseCaseCmdRuleEntity CmdRuleEntity : cmdEntity) {
							useCaseBuilderRepository.deleteUseCaseCmdRule(CmdRuleEntity.getId());
						}
					}

					List<UseCaseShellRuleEntity> shellEntity = useCaseBuilderRepository.getShellEntity(iscriptId);
					if (shellEntity != null) {
						for (UseCaseShellRuleEntity shellRuleEntity : shellEntity) {
							useCaseBuilderRepository.deleteUseCaseCmdRule(shellRuleEntity.getId());
						}
					}

					List<UseCaseXmlRuleEntity> xmlRuleEntity = useCaseBuilderRepository.getXmlEntity(iscriptId);
					if (xmlRuleEntity != null) {
						for (UseCaseXmlRuleEntity XmlRuleEntity : xmlRuleEntity) {
							useCaseBuilderRepository.deleteUseCaseXmlRule(XmlRuleEntity.getId());
						}
					}

					List<UseCaseFileRuleEntity> fileRuleEntity = useCaseBuilderRepository.getFileEntity(iscriptId);
					if (fileRuleEntity != null) {
						for (UseCaseFileRuleEntity FileRuleEntity : fileRuleEntity) {
							useCaseBuilderRepository.deleteUseCaseFileRule(FileRuleEntity.getId());
						}
					}

					UseCaseBuilderParamEntity entitydaat = useCaseBuilderRepository.getEntity(iscriptId);
					useCaseBuilderRepository.deleteUseCaseParamBuilder(entitydaat.getId());

					String fileName = entitydaat.getScriptsDetails().getFileName();
					String filePath = entitydaat.getScriptsDetails().getFilePath();
					filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH") + filePath + "/"
							+ useCaseBuilderModel.getUseCaseName() + "/" + fileName;
					// if (FileUtil.hasMatchingDir(useCaseName, uploadFileEntity.getFilePath()))
					FileUtil.deleteFileOrFolder(filePath);

				}

				// Delete cmd rule
				for (int icmdId : cmdId) {
					useCaseBuilderRepository.deleteUseCaseCmdRule(icmdId);
				}

				// Delete shell rule
				for (int ishellId : shellId) {
					useCaseBuilderRepository.deleteUseCaseShellRule(ishellId);
				}
				// Delete file rule
				for (int ifileId : fileId) {
					useCaseBuilderRepository.deleteUseCaseFileRule(ifileId);
				}

				// Delete xml rule
				if (!migrationType.equalsIgnoreCase("PreMigration")) {
					for (int ixmlId : xmlId) {
						useCaseBuilderRepository.deleteUseCaseXmlRule(ixmlId);
					}
				}

				// createScriptPathForUseCase(useCaseBuilderModel, scriptList);
				User user = UserSessionPool.getInstance().getSessionUser(sessionId);
				CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository
						.getCustomerDetailsEntity(programId);
				useCaseBuilderEntity = useCaseBuilderRepository
						.getUseCaseBuilderEntity(Integer.parseInt(useCaseBuilderModel.getId()));
				useCaseBuilderEntity.setCreatedBy(user.getUserName());
				useCaseBuilderEntity.setNetworkConfigEntity(networkConfigEntity);

				useCaseBuilderEntity.setUseCaseName(useCaseBuilderModel.getUseCaseName());
				useCaseBuilderEntity.setRemarks(useCaseBuilderModel.getRemarks());
				useCaseBuilderEntity.setExecutionSequence(Integer.parseInt(useCaseBuilderModel.getExecutionSequence()));
				// useCaseBuilderEntity.setUseCount(0);
				useCaseBuilderEntity.setMigrationType(useCaseBuilderModel.getMigrationType());
				useCaseBuilderEntity.setUseCaseCreationDate(new Date());
				useCaseBuilderEntity.setMigrationType(migrationType);
				useCaseBuilderEntity.setCustomerDetailsEntity(customerDetailsEntity);
				useCaseBuilderEntity.setSubType(subType);
				useCaseBuilderEntity.setNeVersion(useCaseBuilderModel.getNeVersion());
				/*useCaseBuilderEntity.setCiqFileName(useCaseBuilderModel.getCiqFileName());
				if (migrationType.equalsIgnoreCase("PreMigration")) {
					String xmlRuleName = "curl";
					String xmlSequence = "1";
					String xmlRemarks = null;
					XmlRuleBuilderEntity xmlRuleBuilderEntity;

					if (migrationType.equalsIgnoreCase("PreMigration")) {
						String migrationType1 = "PreMigration";
						String subType1 = "";

						xmlRuleBuilderEntity = useCaseBuilderRepository.getXmlRuleBuilderEntity(xmlRuleName, programId,
								migrationType1, subType1);
						UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
						useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
						useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						// useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
						useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
						useCaseBuilderRepository.saveCaseXml(useCaseXmlRuleEntity);

					}
				}
*/
				updateUseCaseStatus = useCaseBuilderRepository.updateUseCaseBuilder(useCaseBuilderEntity, scriptList,
						customerId, programId, migrationType, subType);
			} else {
				String command = result.get("command");
				String scriptName = result.get("scriptName");
				throw new RctException(command + " Command is not exist for " + scriptName + " script");
			}

		} catch (Exception e) {
			logger.error("Exception in updateUseCaseBuilder() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
			if (e instanceof RctException)
				throw new RctException(e.getMessage());
			else if (e instanceof FileNotFoundException) {
				throw new RctException(e.getMessage());
			}
		}
		return updateUseCaseStatus;
	}

	public Map<String, String> checkScriptContents(UseCaseBuilderModel useCaseBuilderModel, String migrationType,
			String subType, int programId) throws IOException {
		UploadFileEntity uploadFileEntity = null;
		String scriptName = null;
		String scriptFileId = null;
		BufferedReader br = null;
		boolean result = false;
		Map<String, String> resultMap = new HashMap<>();
		try {
			List<UseCaseScriptsModel> listUseCaseScriptObj = useCaseBuilderModel.getScriptList();

			breakData: for (UseCaseScriptsModel usm : listUseCaseScriptObj) {
				scriptName = usm.getScript().get("scriptName");
				scriptFileId = usm.getScript().get("scriptFileId");
				List<String> listCmdRules = new ArrayList<>();
				List<CmdRuleModel> cmdRuleList = usm.getCmdRules();
				for (CmdRuleModel cmdRuleModel : cmdRuleList) {
					String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
					listCmdRules.add(cmdName);
				}
				List<String> listCmds = new ArrayList<>();

				for (String cmdRules : listCmdRules) {
					String cmd = cmdRuleBuilderRepository.findCommand(cmdRules, migrationType, subType, programId);
					listCmds.add(cmd);
				}
				if (listCmds.isEmpty()) {
					result = true;
					resultMap.put("scriptStatus", Boolean.toString(result));
				} else {
					uploadFileEntity = useCaseBuilderRepository.getUploadFileEntityByScriptId(scriptFileId);
					if (uploadFileEntity != null && listCmdRules != null && !listCmdRules.isEmpty()) {
						// Check file Path related to specific UseCase
						String filePath = LoadPropertyFiles.getInstance().getProperty("BASE_PATH")
								+ uploadFileEntity.getFilePath()
								+ useCaseBuilderModel.getUseCaseName().trim().replaceAll(" ", "_") + "/";
						StringBuilder sb = new StringBuilder();
						sb.append(filePath).append(scriptName);
						String actualFilePath = sb.toString();
						File file = new File(actualFilePath);
						if (file.exists()) {
							br = new BufferedReader(new FileReader(file));
							String content = FileUtils.readFileToString(file);
							if (StringUtils.isNotEmpty(content)) {
								for (int i = 0; i < listCmds.size(); i++) {
									if (content.contains(listCmds.get(i).trim())) {
										result = true;
										resultMap.put("scriptStatus", Boolean.toString(result));

									} else {
										result = false;
										resultMap.put("scriptStatus", Boolean.toString(result));
										resultMap.put("command", listCmds.get(i));
										resultMap.put("scriptName", scriptName);
										break breakData;
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.info("Exception in updateUseCaseBuilder() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
			result = false;
			if (e instanceof FileNotFoundException)
				throw new FileNotFoundException();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return resultMap;
	}

	@Override
	public JSONObject getScriptDetails(List<NetworkTypeDetailsModel> neList) {
		List<LsmEntity> objList = null;
		List<UploadFileEntity> uploadFileEntityList = null;
		Map<String, Map<String, Map<String, List<Map<String, String>>>>> networkScriptMap = new HashMap<>();

		Map<String, String> scriptMap = null;
		JSONObject neJson = new JSONObject();
		try {

			for (NetworkTypeDetailsModel networkTypeDetailsModel : neList) {
				NetworkTypeDetailsEntity networkTypeDetailsEntity = new NetworkTypeDetailsEntity();
				networkTypeDetailsEntity.setId(networkTypeDetailsModel.getId());
				networkTypeDetailsEntity.setCaretedDate(networkTypeDetailsModel.getCaretedDate());
				networkTypeDetailsEntity.setCreatedBy(networkTypeDetailsModel.getCreatedBy());
				networkTypeDetailsEntity.setNetworkType(networkTypeDetailsModel.getNetworkType());
				networkTypeDetailsEntity.setRemarks(networkTypeDetailsModel.getRemarks());
				networkTypeDetailsEntity.setStatus(networkTypeDetailsModel.getStatus());

				objList = useCaseBuilderRepository.getLsmEntityList(networkTypeDetailsEntity);
				Map<String, List<String>> lsmMapList = new HashMap<>();
				for (LsmEntity lsmEntity : objList) {
					if (lsmMapList.get(lsmEntity.getLsmVersion()) != null) {
						List<String> lsmName = lsmMapList.get(lsmEntity.getLsmVersion());
						lsmName.add(lsmEntity.getLsmName());
						lsmMapList.put(lsmEntity.getLsmVersion(), lsmName);
					} else {
						List<String> lsmName = new ArrayList<>();
						lsmName.add(lsmEntity.getLsmName());
						lsmMapList.put(lsmEntity.getLsmVersion(), lsmName);
					}
				}
				Map<String, Map<String, List<Map<String, String>>>> lsmNameVersionMap = new HashMap<>();
				for (Map.Entry<String, List<String>> lsmMap : lsmMapList.entrySet()) {
					String versionKey = lsmMap.getKey();
					List<String> lsmNameList = lsmMap.getValue();

					Map<String, List<Map<String, String>>> lsmNameNameMap = new HashMap<>();
					for (String lsmName : lsmNameList) {
						// NetworkConfigEntity networkConfigEntity =
						// useCaseBuilderRepository.getLsmEntity(lsmName,
						// versionKey);
						// uploadFileEntityList =
						// useCaseBuilderRepository.getUploadFileEntityList(networkConfigEntity);
						List<Map<String, String>> listScript = new ArrayList<>();
						for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
							scriptMap = new HashMap<>();
							scriptMap.put("scriptName", uploadFileEntity.getFileName());
							scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
							listScript.add(scriptMap);
						}
						lsmNameNameMap.put(lsmName, listScript);
					}
					lsmNameVersionMap.put(versionKey, lsmNameNameMap);
				}
				networkScriptMap.put(networkTypeDetailsModel.getNetworkType(), lsmNameVersionMap);
			}
			neJson.putAll(networkScriptMap);

		} catch (Exception e) {
			logger.error("Exception  getUploadScriptDetails() in  UploadFileServiceImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return neJson;
	}

	@Override
	public Map<String, List<String>> getSmList(int programId) {
		Map<String, List<String>> smList = null;
		try {
			smList = useCaseBuilderRepository.getSmList(programId);

		} catch (Exception e) {
			logger.info("Exception in getSmList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	@Override
	public UseCaseBuilderEntity getUseCaseBuilderEntity(String useCaseName) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			useCaseBuilderEntity = useCaseBuilderRepository.getUseCaseBuilderEntity(useCaseName);

		} catch (Exception e) {
			logger.info("Exception in getUseCaseBuilderEntity() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return useCaseBuilderEntity;
	}

	@Override
	public UseCaseBuilderParamEntity getUseCaseBuilderParam(int useCaseId, int uploadId) {
		UseCaseBuilderParamEntity useCaseBuilderParamEntity = null;
		try {
			useCaseBuilderParamEntity = useCaseBuilderRepository.getUseCaseBuilderParam(useCaseId, uploadId);

		} catch (Exception e) {
			logger.info("Exception in getUseCaseBuilderParam() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return useCaseBuilderParamEntity;
	}

	@Override
	public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity) {
		boolean status = false;
		try {
			status = useCaseBuilderRepository.createUseCaseBuilder(useCaseBuilderEntity);

		} catch (Exception e) {
			logger.info("Exception in createUseCaseBuilder() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return status;
	}

	@Override
	public Map<String, Map<String, List<Map<String, String>>>> getSmScriptList(int programId, String migrationType,
			String subType) {
		Map<String, Map<String, List<Map<String, String>>>> smList = null;
		try {
			smList = useCaseBuilderRepository.getSmScriptList(programId, migrationType, subType);

		} catch (Exception e) {
			logger.info("Exception in getSmScriptList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	@Override
	public List<Map<String, String>> getXmlRuleList(int programId, String migrationType, String subType) {

		List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = null;
		List<Map<String, String>> xmlRuleList = new LinkedList<>();
		try {
			xmlRuleBuilderEntityList = useCaseBuilderRepository.getXmlRuleList(programId, migrationType, subType);
			for (XmlRuleBuilderEntity xmlRuleBuilderEntity : xmlRuleBuilderEntityList) {
				Map<String, String> xmlRuleMap = new HashMap<>();
				xmlRuleMap.put("xmlRuleBuilderId", String.valueOf(xmlRuleBuilderEntity.getId()));
				xmlRuleMap.put("xmlName", xmlRuleBuilderEntity.getRuleName());
				xmlRuleList.add(xmlRuleMap);
			}
		} catch (Exception e) {
			logger.info(
					"Exception in getXmlRuleList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return xmlRuleList;
	}

	@Override
	public List<Map<String, String>> getScriptList(int programId, String migrationType) {
		List<Map<String, String>> smList = new LinkedList<>();
		try {
			smList = useCaseBuilderRepository.getScriptList(programId, migrationType);

		} catch (Exception e) {
			logger.info(
					"Exception in getScriptList() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	@Override
	public List<Map<String, String>> scriptInfoWithoutVersionName(int programId, String migrationType, String subType) {
		List<Map<String, String>> smList = new LinkedList<>();
		try {
			smList = useCaseBuilderRepository.scriptInfoWithoutVersionName(programId, migrationType, subType);

		} catch (Exception e) {
			logger.info("Exception in scriptInfoWithoutVersionName() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	@Override
	public boolean duplicateUseCaseName(String ruleName, int customerId, String migrationType, int programId,
			String userRole, String subType) {
		boolean status = false;
		try {
			status = useCaseBuilderRepository.findByRuleName(ruleName, customerId, migrationType, programId, userRole,
					subType);
		} catch (Exception e) {
			logger.info("Exception in duplicateUseCaseName() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public UseCaseBuilderEntity getUseCaseByName(String useCaseName, String migrationType, int programId,
			String subType) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			useCaseBuilderEntity = useCaseBuilderRepository.getUseCaseByName(useCaseName, migrationType, programId,
					subType);
		} catch (Exception e) {
			logger.info("Exception in getUseCaseByName() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntity;
	}

	@Override
	public UseCaseBuilderEntity duplicateExecutionSequence(String executionSequence, int programId, String userRole) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			useCaseBuilderEntity = useCaseBuilderRepository.findByExecutionSequence(executionSequence, programId,
					userRole);
		} catch (Exception e) {
			logger.info("Exception in duplicateExecutionSequence() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntity;
	}

	@Override
	public UseCaseBuilderParamEntity duplicateScriptExecutionSequence(String executionSequence, int programId) {
		UseCaseBuilderParamEntity useCaseBuilderParamEntity = null;
		try {
			useCaseBuilderParamEntity = useCaseBuilderRepository.findScriptExecutionSequence(executionSequence,
					programId);
		} catch (Exception e) {
			logger.info("Exception in duplicateExecutionSequence() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderParamEntity;
	}

	@Override
	public Map<String, List<Map<String, String>>> getScriptListWithoutSM(int programId, String migrationType,
			String subType) {
		Map<String, List<Map<String, String>>> smList = null;
		try {
			smList = useCaseBuilderRepository.getScriptListWithoutSM(programId, migrationType, subType);

		} catch (Exception e) {
			logger.info("Exception in getScriptListWithoutSM() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return smList;
	}

	@Override
	public Object getPageCount(int page, int count, int customerId, String migrationType, int programId, String subType,
			User user) {
		Object paging = null;
		try {
			paging = useCaseBuilderRepository.getPageCount(page, count, customerId, migrationType, programId, subType,
					user);

		} catch (Exception e) {
			logger.info(
					"Exception in getPageCount() in UseCaseBuilderServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return paging;
	}

	@Override
	public UseCaseBuilderEntity getUseCaseBuilderEntity(Integer useCaseId) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			useCaseBuilderEntity = useCaseBuilderRepository.getUseCaseBuilderEntity(useCaseId);
		} catch (Exception e) {
			logger.info("Exception in getUseCaseBuilderEntity() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}

		return useCaseBuilderEntity;
	}

	@Override
	public List<UseCaseBuilderEntity> getUseCaseBuilderEntityList(int programId) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;
		try {
			useCaseBuilderEntityList = useCaseBuilderRepository.getUseCaseBuilderEntityList(programId);
		} catch (Exception e) {
			logger.info("Exception in getUseCaseBuilderEntityList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return useCaseBuilderEntityList;
	}

	@Override
	public List<RunTestEntity> getRunTestDetails(String useCaseName) {
		List<RunTestEntity> runTestEntity = null;
		try {
			runTestEntity = useCaseBuilderRepository.getRunTestDetails(useCaseName);
		} catch (Exception e) {
			logger.info("Exception in getUseCaseBuilderEntityList() in UseCaseBuilderServiceImpl"
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return runTestEntity;
	}

}