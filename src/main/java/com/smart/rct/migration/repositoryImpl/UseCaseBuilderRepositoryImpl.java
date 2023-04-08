package com.smart.rct.migration.repositoryImpl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.LsmEntity;
import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.entity.NetworkConfigEntity;
import com.smart.rct.common.entity.NetworkTypeDetailsEntity;
import com.smart.rct.constants.Constants;
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
import com.smart.rct.migration.model.UploadFileModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.migration.model.XmlRuleModel;
import com.smart.rct.migration.repository.UseCaseBuilderRepository;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.usermanagement.entity.UserRoleDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.DateUtil;
import com.smart.rct.util.FileUtil;
import com.smart.rct.util.LoadPropertyFiles;

@Transactional
@Repository
public class UseCaseBuilderRepositoryImpl implements UseCaseBuilderRepository {

	final static Logger logger = LoggerFactory.getLogger(UseCaseBuilderRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UploadFileRepositoryImpl UploadFileRepositoryImpl;
	
	
	@Autowired
	CmdRuleBuilderRepositoryImpl cmdRuleBuilderRepositoryImpl;

	/*@Override
	public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity, List<UseCaseScriptsModel> scriptList,
			Integer programId, String neName, String neVersion, String migrationType, String subType) {
		boolean saveStatus = false;
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		try {

			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
				
				CheckListScriptDetEntity checkListScriptDetEntity = getCheckListDetails(programId);
				
				if(useCaseScriptsModel.getScriptSequence()!=null && !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					//do nothing
				}else {
					CheckListScriptDetails = getExeseq(programId,useCaseScriptsModel.getScript().get("scriptName"),"",checkListScriptDetEntity.getCheckListFileName());
				}
				
				UseCaseBuilderParamEntity useCaseBuilderParamEntity = new UseCaseBuilderParamEntity();

				UploadFileEntity uploadFileEntity = getUploadFileEntity(neName, neVersion, programId,
						useCaseBuilderEntity.getMigrationType(), useCaseScriptsModel.getScript().get("scriptName"),useCaseScriptsModel.getScript().get("scriptFileId"),
						subType);
				useCaseBuilderParamEntity.setScriptsDetails(uploadFileEntity);
				if(useCaseScriptsModel.getScriptSequence()!=null && !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					useCaseBuilderParamEntity
					.setExecutionSequence(Integer.parseInt(useCaseScriptsModel.getScriptSequence()));
				}else {
					useCaseBuilderParamEntity
					.setExecutionSequence(CheckListScriptDetails.get(0).getScriptExeSeq());
				}
				useCaseBuilderParamEntity.setUseCaseBuilderEntity(useCaseBuilderEntity);
				useCaseBuilderParamEntity.setScriptRemarks(useCaseScriptsModel.getScriptRemarks());
				
				CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepositoryImpl.getCustomerDetailsEntity(programId);
				useCaseBuilderParamEntity.setCustomerDetailsEntity(customerDetailsEntity);
				boolean sta = saveparamEntity(useCaseBuilderParamEntity);

				List<CmdRuleModel> cmdRuleList = useCaseScriptsModel.getCmdRules();
				for (CmdRuleModel cmdRuleModel : cmdRuleList) {
					String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
					String cmdSequence = cmdRuleModel.getCmdSequence();
					String cmdRemarks = cmdRuleModel.getCmdRemarks();
					CmdRuleBuilderEntity cmdRuleBuilderEntity = getCommandRuleEntity(cmdName, programId, migrationType,
							subType);
					UseCaseCmdRuleEntity useCaseCmdRuleEntity = new UseCaseCmdRuleEntity();
					useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
					useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
					useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
					useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
					saveCmdRuleEntity(useCaseCmdRuleEntity);
				}

				List<ShellRuleModel> shellRuleList = useCaseScriptsModel.getShellRules();
				for (ShellRuleModel shellRuleModel : shellRuleList) {
					String shellName = shellRuleModel.getShellDetails().get("shellCmdName");
					String shellSequence = shellRuleModel.getShellRuleSequence();
					String shellRemarks = shellRuleModel.getShellRuleRemarks();
					ShellCmdRuleBuilderEntity shellRuleBuilderEntity = getShellRuleEntity(shellName, programId, migrationType,
							subType);
					UseCaseShellRuleEntity useCaseShellRuleEntity = new UseCaseShellRuleEntity();
					useCaseShellRuleEntity.setShellRuleBuilderEntity(shellRuleBuilderEntity);
					useCaseShellRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
					useCaseShellRuleEntity.setShellRemarks(shellRemarks);
					useCaseShellRuleEntity.setShellRuleSequence(Integer.parseInt(shellSequence));
					saveCaseShell(useCaseShellRuleEntity);
				}

				
				List<XmlRuleModel> xmlRuleList = useCaseScriptsModel.getXmlRules();
				for (XmlRuleModel xmlRuleModel : xmlRuleList) {
					String xmlRuleName = xmlRuleModel.getXmlDetails().get("xmlName");
					String xmlSequence = xmlRuleModel.getXmlSequence();
					String xmlRemarks = xmlRuleModel.getXmlRemarks();
					XmlRuleBuilderEntity xmlRuleBuilderEntity = getXmlRuleBuilderEntity(xmlRuleName, programId,
							migrationType, subType);
					UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
					useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
					useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
					useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
					useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
					saveCaseXml(useCaseXmlRuleEntity);
				}

				List<FileRuleModel> fileRuleList = useCaseScriptsModel.getFileRules();
				if (!fileRuleList.isEmpty()) {
					for (FileRuleModel fileRuleModel : fileRuleList) {
						String fileRuleName = fileRuleModel.getFileDetails().get("fileRuleName");
						FileRuleBuilderEntity fileRuleBuilderEntity = getFileRuleEntity(fileRuleName, programId,
								migrationType, subType);
						UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
						useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
						useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
						useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
						useCaseFileRuleEntity
								.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
						saveCaseFile(useCaseFileRuleEntity);
					}
				}

			}
			saveStatus = true;

		} catch (Exception e) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return saveStatus;
	}*/

	@Override
	public XmlRuleBuilderEntity getXmlRuleBuilderEntity(String xmlRuleName, Integer programId, String migrationType,
			String subType) {
		XmlRuleBuilderEntity xmlRuleBuilderEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<XmlRuleBuilderEntity> query = cb.createQuery(XmlRuleBuilderEntity.class);
			Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
			query.select(root).where(cb.and(cb.equal(root.get("ruleName"), xmlRuleName),
					cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType)));
			TypedQuery<XmlRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			xmlRuleBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getCommandRuleEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlRuleBuilderEntity;
	}
	
	
	public CheckListScriptDetEntity getCheckListDetails(int programId) {

		CheckListScriptDetEntity checkListScriptDetEntity = null;

		try {

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CheckListScriptDetEntity.class);

			Conjunction objConjunction = Restrictions.conjunction();

			objConjunction.add(Restrictions.eq("programDetailsEntity.id", programId));
			
			criteria.add(objConjunction);
			criteria.addOrder(Order.desc("id"));
			
			checkListScriptDetEntity = (CheckListScriptDetEntity) criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list().stream().findFirst()
				    .orElse(null);

		} catch (Exception e) {
			logger.error(" getCheckListDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return checkListScriptDetEntity;
	}
	
	
	@Override
	public List<CheckListScriptDetEntity> getExeseq(Integer programId, String scriptName,String configType, String checkListName) {
		List<CheckListScriptDetEntity> checkListScriptDetEntity = null;
		List<CheckListScriptDetEntity> checkListScriptDetEntity1 = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CheckListScriptDetEntity> query = cb.createQuery(CheckListScriptDetEntity.class);
			Root<CheckListScriptDetEntity> root = query.from(CheckListScriptDetEntity.class);
			//query.select(root).where(cb.and(cb.equal(root.get("scriptName"), scriptName),
			//		cb.equal(root.get("programDetailsEntity"), customerDetailsEntity),cb.equal(root.get("checkListFileName"),checkListName)));
			
			if(StringUtils.isNotEmpty(configType) && configType.length()>0){
				query.select(root).where(cb.and(
						cb.equal(root.get("programDetailsEntity"), customerDetailsEntity),cb.equal(root.get("checkListFileName"),checkListName), cb.equal(root.get("configType"),configType)));
			}else{
				query.select(root).where(cb.and(
						cb.equal(root.get("programDetailsEntity"), customerDetailsEntity),cb.equal(root.get("checkListFileName"),checkListName)));
			}
			TypedQuery<CheckListScriptDetEntity> typedQuery = entityManager.createQuery(query);
			checkListScriptDetEntity = typedQuery.getResultList();
			
			String patrn = "";
			String finalScriptName = null;
			for(CheckListScriptDetEntity entity : checkListScriptDetEntity) {
				if(entity.getScriptName().equals(scriptName)) {
					finalScriptName = scriptName;
					break;
				}
				 if(entity.getScriptName().contains("*")) {
					   patrn = entity.getScriptName().replaceAll("\\*", ".*").trim();
					   if(Pattern.matches(patrn, scriptName)) {
						   finalScriptName = entity.getScriptName();
						   //break;
					   }
				  }
			}
			if(finalScriptName==null) {
				finalScriptName = scriptName;
			}
			
			
			CriteriaBuilder cb1 = entityManager.getCriteriaBuilder();
			CriteriaQuery<CheckListScriptDetEntity> query1 = cb1.createQuery(CheckListScriptDetEntity.class);
			Root<CheckListScriptDetEntity> root1 = query1.from(CheckListScriptDetEntity.class);
			
			
			if(StringUtils.isNotEmpty(configType) && configType.length()>0){
				
				query.select(root1).where(cb1.and(cb1.equal(root1.get("scriptName"), finalScriptName),
						cb1.equal(root1.get("programDetailsEntity"), customerDetailsEntity),cb1.equal(root1.get("checkListFileName"),checkListName), cb.equal(root.get("configType"),configType)));
				
			}else{
				query.select(root1).where(cb1.and(cb1.equal(root1.get("scriptName"), finalScriptName),
						cb1.equal(root1.get("programDetailsEntity"), customerDetailsEntity),cb1.equal(root1.get("checkListFileName"),checkListName)));
			}
			
			TypedQuery<CheckListScriptDetEntity> typedQuery1 = entityManager.createQuery(query);
			checkListScriptDetEntity1 = typedQuery1.getResultList();
			
			logger.info("getExeseq() scriptName:"+scriptName+", finalScriptName:"+finalScriptName+", configType:"+configType);
			
		} catch (Exception e) {
			logger.error(" getExeseq() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return checkListScriptDetEntity1;
	}

	@Override
	public List<String> getNwTypeList() {
		List<String> networkTypeList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> query = cb.createQuery(String.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root.<String>get("networkType"));

			TypedQuery<String> typedQuery = entityManager.createQuery(query);
			networkTypeList = (List<String>) typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(" getNwTypeList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkTypeList;
	}

	@Override
	public List<LsmEntity> getLsmNameList() {

		List<LsmEntity> lsmDetailsList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = cb.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root);
			TypedQuery<LsmEntity> typedQuery = entityManager.createQuery(query);
			lsmDetailsList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getLsmNameList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmDetailsList;
	}

	@Override
	public List<LsmEntity> getLsmEntityList(NetworkTypeDetailsEntity networkTypeDetailsEntity) {

		List<LsmEntity> lsmDetailsList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = cb.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("networkTypeDetailsEntity"), networkTypeDetailsEntity));
			TypedQuery<LsmEntity> typedQuery = entityManager.createQuery(query);
			lsmDetailsList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getLsmNameList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmDetailsList;
	}

	@Override
	public List<UploadFileEntity> getScriptList(Integer customerId) {

		List<UploadFileEntity> uploadFileEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerId"), customerId));
			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntityList = (List<UploadFileEntity>) typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntityList;
	}

	@Override
	public List<CmdRuleBuilderEntity> getCommandRuleList(Integer programId, String migrationType, String subType) {

		List<CmdRuleBuilderEntity> cmdNameList = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CmdRuleBuilderEntity> query = cb.createQuery(CmdRuleBuilderEntity.class);
			Root<CmdRuleBuilderEntity> root = query.from(CmdRuleBuilderEntity.class);
			// query.select(root.<String>get("ruleName"));

			query.where(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType));
			TypedQuery<CmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			cmdNameList = (List<CmdRuleBuilderEntity>) typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getCommandRuleList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return cmdNameList;
	}
	
	@Override
	public List<ShellCmdRuleBuilderEntity> getShellCommandRuleList(Integer programId, String migrationType, String subType) {

		List<ShellCmdRuleBuilderEntity> shellCmdNameList = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<ShellCmdRuleBuilderEntity> query = cb.createQuery(ShellCmdRuleBuilderEntity.class);
			Root<ShellCmdRuleBuilderEntity> root = query.from(ShellCmdRuleBuilderEntity.class);
			// query.select(root.<String>get("ruleName"));

			query.where(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType));
			TypedQuery<ShellCmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			shellCmdNameList = (List<ShellCmdRuleBuilderEntity>) typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getShellCommandRuleList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return shellCmdNameList;
	}

	@Override
	public CmdRuleBuilderEntity getCommandRuleEntity(String cmdRuleName, Integer programId, String migrationType,
			String subType) {

		CmdRuleBuilderEntity cmdRuleBuilderEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<CmdRuleBuilderEntity> query = cb.createQuery(CmdRuleBuilderEntity.class);
			Root<CmdRuleBuilderEntity> root = query.from(CmdRuleBuilderEntity.class);
			query.select(root).where(cb.and(cb.equal(root.get("ruleName"), cmdRuleName),
					cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType)));
			TypedQuery<CmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			cmdRuleBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getCommandRuleEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return cmdRuleBuilderEntity;
	}
	
	@Override
	public ShellCmdRuleBuilderEntity getShellRuleEntity(String cmdRuleName, Integer programId, String migrationType,
			String subType) {

		ShellCmdRuleBuilderEntity shellRuleBuilderEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<ShellCmdRuleBuilderEntity> query = cb.createQuery(ShellCmdRuleBuilderEntity.class);
			Root<ShellCmdRuleBuilderEntity> root = query.from(ShellCmdRuleBuilderEntity.class);
			query.select(root).where(cb.and(cb.equal(root.get("ruleName"), cmdRuleName),
					cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType)));
			TypedQuery<ShellCmdRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			shellRuleBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getShellRuleEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return shellRuleBuilderEntity;
	}

	/*public UploadFileEntity getUploadFileEntity(String neName, String neVersion, Integer programId,
			String migrationType, String scriptName, String subType) {

		UploadFileEntity uploadFileEntity = null;
		NetworkConfigEntity networkConfigEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			if (neVersion != null && !neVersion.isEmpty() && neName != null && !neName.isEmpty()) {
				CriteriaBuilder neVersioncb = entityManager.getCriteriaBuilder();
				CriteriaQuery<NeVersionEntity> neVersionquery = neVersioncb.createQuery(NeVersionEntity.class);
				Root<NeVersionEntity> neVersionroot = neVersionquery.from(NeVersionEntity.class);
				neVersionquery.select(neVersionroot)
						.where(neVersioncb.and(neVersioncb.equal(neVersionroot.get("neVersion"), neVersion),neVersioncb.equal(neVersionroot.get("programDetailsEntity"), customerDetailsEntity)));
				TypedQuery<NeVersionEntity> neVersiontypedQuery = entityManager.createQuery(neVersionquery);
				NeVersionEntity neVersionEntity = neVersiontypedQuery.getSingleResult();

				CriteriaBuilder netCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NetworkConfigEntity> netQuery = netCB.createQuery(NetworkConfigEntity.class);
				Root<NetworkConfigEntity> netRoot = netQuery.from(NetworkConfigEntity.class);
				netQuery.select(netRoot)
						.where(netCB.and(netCB.equal(netRoot.get("programDetailsEntity"), customerDetailsEntity),
								netCB.equal(netRoot.get("neName"), neName),
								netCB.equal(netRoot.get("neVersionEntity"), neVersionEntity)));
				TypedQuery<NetworkConfigEntity> netTypedQuery = entityManager.createQuery(netQuery);
				networkConfigEntity = netTypedQuery.getSingleResult();
			}

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			if (networkConfigEntity != null) {
				query.select(root)
						.where(cb.and(cb.equal(root.get("neListEntity"), networkConfigEntity),
								cb.equal(root.get("migrationType"), migrationType),
								cb.equal(root.get("subType"), subType), cb.equal(root.get("fileName"), scriptName)));
			} else {
				query.select(root)
						.where(cb.and(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
								cb.equal(root.get("migrationType"), migrationType),
								cb.equal(root.get("subType"), subType), cb.equal(root.get("fileName"), scriptName)));
			}
			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getUploadFileEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntity;
	}*/
	
	@Override
	public UploadFileEntity getUploadFileEntity(String neName, String neVersion, Integer programId,
			String migrationType, String scriptName, String scriptFileId, String subType) {

		UploadFileEntity uploadFileEntity = null;
		try {
			logger.info("getUploadFileEntity() scriptName:"+scriptName+", migrationType: "+migrationType+", subType:"+subType+", scriptFileId:"+scriptFileId);
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			
				query.select(root)
						.where(cb.and(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
								cb.equal(root.get("migrationType"), migrationType),cb.equal(root.get("id"), scriptFileId),
								cb.equal(root.get("subType"), subType), cb.equal(root.get("fileName"), scriptName)));
			
			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getUploadFileEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntity;
	}

	@Override
	public List<UploadFileEntity> getUploadFileEntityList(NetworkConfigEntity networkConfigEntity) {

		List<UploadFileEntity> uploadFileEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root).where(cb.equal(root.get("lsmEntity"), networkConfigEntity));
			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntityList = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getUploadFileEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntityList;
	}

	@Override
	public FileRuleBuilderEntity getFileRuleEntity(String fileRuleName, Integer programId, String migrationType,
			String subType) {

		FileRuleBuilderEntity fileRuleBuilderEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FileRuleBuilderEntity> query = cb.createQuery(FileRuleBuilderEntity.class);
			Root<FileRuleBuilderEntity> root = query.from(FileRuleBuilderEntity.class);
			query.select(root).where(cb.and(cb.equal(root.get("ruleName"), fileRuleName),
					cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType)));
			TypedQuery<FileRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			fileRuleBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getFileRuleEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return fileRuleBuilderEntity;
	}

	@Override
	public List<FileRuleBuilderEntity> getFileRuleList(Integer programId, String migrationType, String subType) {

		List<FileRuleBuilderEntity> fileRuleList = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FileRuleBuilderEntity> query = cb.createQuery(FileRuleBuilderEntity.class);
			Root<FileRuleBuilderEntity> root = query.from(FileRuleBuilderEntity.class);
			// query.select(root.<String>get("ruleName"));
			query.where(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType));
			TypedQuery<FileRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			fileRuleList = (List<FileRuleBuilderEntity>) typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getFileRuleList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return fileRuleList;
	}

	@Override
	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(SearchModel searchModel, Integer programId,
			String migrationType, String subType) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;

		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);

			/*
			 * if (StringUtils.isNotEmpty(searchModel.getSmName())) {
			 * query.where(cb.equal(root.get("customerId"), customerId)); }
			 */
			if (StringUtils.isNotEmpty(searchModel.getSmVersion()) && StringUtils.isNotEmpty(searchModel.getSmName())) {

				CriteriaBuilder versionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NeVersionEntity> versionQuery = versionCB.createQuery(NeVersionEntity.class);
				Root<NeVersionEntity> versionRoot = versionQuery.from(NeVersionEntity.class);
				versionQuery.where(
						versionCB.and(versionCB.equal(versionRoot.get("neVersion"), searchModel.getSmVersion())));
				TypedQuery<NeVersionEntity> versionTypedQuery = entityManager.createQuery(versionQuery);
				NeVersionEntity neVersionEntity = versionTypedQuery.getSingleResult();

				CriteriaBuilder smVersionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NetworkConfigEntity> smVersionQuery = smVersionCB.createQuery(NetworkConfigEntity.class);
				Root<NetworkConfigEntity> smVersionRoot = smVersionQuery.from(NetworkConfigEntity.class);
				smVersionQuery
						.where(smVersionCB.and(smVersionCB.equal(smVersionRoot.get("neName"), searchModel.getSmName()),
								smVersionCB.equal(smVersionRoot.get("neVersionEntity"), neVersionEntity)));
				TypedQuery<NetworkConfigEntity> smVersionTypedQuery = entityManager.createQuery(smVersionQuery);
				NetworkConfigEntity networkConfigEntity = smVersionTypedQuery.getSingleResult();

				query.where(cb.equal(root.get("networkConfigEntity"), networkConfigEntity));
			}
			if (StringUtils.isNotEmpty(searchModel.getUseCaseName())) {
				query.where(cb.equal(root.get("useCaseName"), searchModel.getUseCaseName()));
			}
			if (StringUtils.isNotEmpty(searchModel.getCreatedBy())) {
				query.where(cb.equal(root.get("createdBy"), searchModel.getCreatedBy()));
			}
			if (StringUtils.isNotEmpty(subType)) {
				query.where(cb.and(cb.equal(root.get("migrationType"), migrationType),
						cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						cb.equal(root.get("subType"), subType)));
			} else {
				query.where(cb.and(cb.equal(root.get("migrationType"), migrationType),
						cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			}
			query.orderBy(cb.desc(root.get("useCaseCreationDate")));
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

	@Override
	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(Integer programId, String migrationType,
			String subType) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;

		try {

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			query.select(root).where(cb.and(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType)));

			query.orderBy(cb.desc(root.get("useCaseCreationDate")));
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

	@Override
	public List<UseCaseBuilderEntity> loadUseCaseBuilderDetails(int page, int count, Integer customerId,
			String migrationType, int programId, String subType, User user) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;
		int startIndex = 0;
		int maxResults = 0;
		try {
			if (page > 0) {
				startIndex = ((page - 1) * count);
				maxResults =  count;
			}

			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
					user.getRoleId());

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				query.where(cb.equal(root.get("customerId"), customerId));
			}

			if (StringUtils.isNotEmpty(subType)) {
				query.select(root)
						.where(cb.and(cb.equal(root.get("migrationType"), migrationType),
								cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
								cb.equal(root.get("subType"), subType)));
			} else {
				query.select(root).where(cb.and(cb.equal(root.get("migrationType"), migrationType),
						cb.notEqual(root.get("subType"), "PREAUDIT"),
						cb.notEqual(root.get("subType"), "NESTATUS"),
						cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			}
			query.orderBy(cb.desc(root.get("useCaseCreationDate")));
			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			typedQuery.setFirstResult(startIndex);
			typedQuery.setMaxResults(maxResults);
			useCaseBuilderEntityList = typedQuery.getResultList();

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntityList;
	}

	@Override
	public Map<String, Object> loadUseCaseBuilderSearchDetails(int page, int count, SearchModel searchModel,
			Integer customerId, String migrationType, int programId, String subType, User user) {

		Map<String, Object> objMap = new HashMap<String, Object>();
		List<UseCaseBuilderModel> cmdRuleBuilderList = new ArrayList<>();
		double result = 0;
		int paginationNumber = 0;
		NetworkConfigEntity networkConfigEntity = null;

		try {

			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
					user.getRoleId());

			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UseCaseBuilderEntity.class);

			Conjunction objConjunction = Restrictions.conjunction();

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);

			/*if (StringUtils.isNotEmpty(searchModel.getSmVersion()) && StringUtils.isNotEmpty(searchModel.getSmName())) {

				CriteriaBuilder versionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NeVersionEntity> versionQuery = versionCB.createQuery(NeVersionEntity.class);
				Root<NeVersionEntity> versionRoot = versionQuery.from(NeVersionEntity.class);
				versionQuery.where(
						versionCB.and(versionCB.equal(versionRoot.get("neVersion"), searchModel.getSmVersion()),
								versionCB.equal(versionRoot.get("programDetailsEntity"),customerDetailsEntity)));
				TypedQuery<NeVersionEntity> versionTypedQuery = entityManager.createQuery(versionQuery);
				NeVersionEntity neVersionEntity = versionTypedQuery.getSingleResult();

				CriteriaBuilder smVersionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NetworkConfigEntity> smVersionQuery = smVersionCB.createQuery(NetworkConfigEntity.class);
				Root<NetworkConfigEntity> smVersionRoot = smVersionQuery.from(NetworkConfigEntity.class);
				smVersionQuery
						.where(smVersionCB.and(smVersionCB.equal(smVersionRoot.get("neName"), searchModel.getSmName()),
								smVersionCB.equal(smVersionRoot.get("neVersionEntity"), neVersionEntity),
								smVersionCB.equal(smVersionRoot.get("programDetailsEntity"), customerDetailsEntity)));
				TypedQuery<NetworkConfigEntity> smVersionTypedQuery = entityManager.createQuery(smVersionQuery);
				networkConfigEntity = smVersionTypedQuery.getSingleResult();

				// query.where(cb.equal(root.get("networkConfigEntity"), networkConfigEntity));
				objConjunction.add(Restrictions.eq("networkConfigEntity", networkConfigEntity));
			}*/
			if(StringUtils.isNotEmpty(searchModel.getSmVersion())){
				
				List<NeVersionEntity> neVersionEntity = null;
				
				CriteriaBuilder versionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NeVersionEntity> versionQuery = versionCB.createQuery(NeVersionEntity.class);
				Root<NeVersionEntity> versionRoot = versionQuery.from(NeVersionEntity.class);
				versionQuery.where(
						//versionCB.and(versionCB.equal(versionRoot.get("neVersion"), searchModel.getSmVersion()),
						versionCB.and(versionCB.like(versionRoot.get("neVersion"), "%" +searchModel.getSmVersion() + "%"),
								
								versionCB.equal(versionRoot.get("programDetailsEntity"),customerDetailsEntity)));
				TypedQuery<NeVersionEntity> versionTypedQuery = entityManager.createQuery(versionQuery);
				//NeVersionEntity neVersionEntity = versionTypedQuery.getSingleResult();
				neVersionEntity = versionTypedQuery.getResultList();
				
				objConjunction.add(Restrictions.in("neVersion", neVersionEntity));
			}if(StringUtils.isNotEmpty(searchModel.getSmName())){
				
				List<NetworkConfigEntity> networkConfigEntityList = null;
				
				CriteriaBuilder smVersionCB = entityManager.getCriteriaBuilder();
				CriteriaQuery<NetworkConfigEntity> smVersionQuery = smVersionCB.createQuery(NetworkConfigEntity.class);
				Root<NetworkConfigEntity> smVersionRoot = smVersionQuery.from(NetworkConfigEntity.class);
				smVersionQuery
						.where(smVersionCB.and(smVersionCB.like(smVersionRoot.get("neName"),  "%" + searchModel.getSmName() + "%"),
								smVersionCB.equal(smVersionRoot.get("programDetailsEntity"), customerDetailsEntity)));
				TypedQuery<NetworkConfigEntity> smVersionTypedQuery = entityManager.createQuery(smVersionQuery);
				networkConfigEntityList = smVersionTypedQuery.getResultList();
				
				objConjunction.add(Restrictions.in("networkConfigEntity", networkConfigEntityList));
				
				
				/*criteria.createAlias("networkConfigEntity", "networkConfigEntity");
				Criterion eventprogramName = Restrictions.eq("networkConfigEntity.neName", searchModel.getSmName());
				objConjunction.add(eventprogramName);*/
				
			}

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				objConjunction.add(Restrictions.eq("customerId", customerId));
			}

			if (StringUtils.isNotEmpty(searchModel.getUseCaseName())) {
				objConjunction.add(
						Restrictions.ilike("useCaseName", searchModel.getUseCaseName().trim(), MatchMode.ANYWHERE));
				// objConjection.add(Restrictions.ilike("fileName",
				// fileName,MatchMode.ANYWHERE))
			}
			if (StringUtils.isNotEmpty(searchModel.getCreatedBy())) {
				objConjunction
						.add(Restrictions.ilike("createdBy", searchModel.getCreatedBy().trim(), MatchMode.ANYWHERE));
			}

			objConjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));
			objConjunction.add(Restrictions.eq("migrationType", migrationType));
			if (StringUtils.isNotEmpty(subType)) {
				objConjunction.add(Restrictions.eq("subType", subType));
			}
			criteria.add(objConjunction);

			criteria.setFirstResult((page - 1) * count);
			criteria.setMaxResults(count);
			criteria.addOrder(Order.desc("useCaseCreationDate"));
			criteria.setFetchMode("networkConfigEntity", FetchMode.LAZY);
			criteria.setFetchMode("useCaseBuilderParamEntity", FetchMode.LAZY);
			criteria.setFetchMode("customerDetailsEntity", FetchMode.LAZY);
			
			List<UseCaseBuilderEntity> cmdRuleBuilderEntityList = criteria
					.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			if (cmdRuleBuilderEntityList != null && cmdRuleBuilderEntityList.size() > 0)
				for (UseCaseBuilderEntity objCmdRuleBuilderEntity : cmdRuleBuilderEntityList) {

					Date date = objCmdRuleBuilderEntity.getUseCaseCreationDate();
					String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
					UseCaseBuilderModel objCmdRuleBuilderModel = new UseCaseBuilderModel();

					objCmdRuleBuilderModel.setUseCaseName(objCmdRuleBuilderEntity.getUseCaseName());
					objCmdRuleBuilderModel
							.setExecutionSequence(objCmdRuleBuilderEntity.getExecutionSequence().toString());
					objCmdRuleBuilderModel.setCreatedBy(objCmdRuleBuilderEntity.getCreatedBy());
					objCmdRuleBuilderModel.setTimeStamp(dateFormat);
					objCmdRuleBuilderModel.setRemarks(objCmdRuleBuilderEntity.getRemarks());
					if (objCmdRuleBuilderEntity.getNetworkConfigEntity() != null) {
						objCmdRuleBuilderModel.setLsmName(objCmdRuleBuilderEntity.getNetworkConfigEntity().getNeName());
					}
					if (objCmdRuleBuilderEntity.getNeVersion() != null) {
						objCmdRuleBuilderModel.setLsmVersion(objCmdRuleBuilderEntity.getNeVersion().getNeVersion());
					}

					cmdRuleBuilderList.add(objCmdRuleBuilderModel);
				}

			Criteria criteriaCount = entityManager.unwrap(Session.class).createCriteria(UseCaseBuilderEntity.class);
			criteriaCount.add(objConjunction);
			criteriaCount.setProjection(Projections.rowCount());
			Long totCount = (Long) criteriaCount.uniqueResult();
			double size = totCount;
			result = Math.ceil(size / count);
			paginationNumber = (int) result;

			objMap.put("cmdRuleBuilderData", cmdRuleBuilderEntityList);
			objMap.put("totalCount", paginationNumber);

		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderSearchDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return objMap;
	}

	@Override
	public String getNwTypeName(int id) {
		String nwTypeName = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkTypeDetailsEntity> query = cb.createQuery(NetworkTypeDetailsEntity.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<NetworkTypeDetailsEntity> typedQuery = entityManager.createQuery(query);
			NetworkTypeDetailsEntity networkTypeDetailsEntity = typedQuery.getSingleResult();
			nwTypeName = networkTypeDetailsEntity.getNetworkType();
		} catch (Exception e) {
			logger.error(" getNwTypeName() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return nwTypeName;
	}

	@Override
	public Map<String, String> getlsmDetails(int id) {
		String lsmName = null;
		String lsmVersion = null;
		Map<String, String> lsmDetails = new HashMap<>();
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<LsmEntity> query = cb.createQuery(LsmEntity.class);
			Root<LsmEntity> root = query.from(LsmEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<LsmEntity> typedQuery = entityManager.createQuery(query);
			LsmEntity lsmDetailsEntity = typedQuery.getSingleResult();
			lsmName = lsmDetailsEntity.getLsmName();
			lsmVersion = lsmDetailsEntity.getLsmVersion();
			lsmDetails.put(lsmVersion, lsmName);
		} catch (Exception e) {
			logger.error(" getlsmDetails() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return lsmDetails;
	}

	@Override
	public NetworkTypeDetailsEntity getNwTypeEntity(String nwType) {
		NetworkTypeDetailsEntity networkTypeDetailsEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkTypeDetailsEntity> query = cb.createQuery(NetworkTypeDetailsEntity.class);
			Root<NetworkTypeDetailsEntity> root = query.from(NetworkTypeDetailsEntity.class);
			query.select(root).where(cb.equal(root.get("networkType"), nwType));

			TypedQuery<NetworkTypeDetailsEntity> typedQuery = entityManager.createQuery(query);
			networkTypeDetailsEntity = typedQuery.getSingleResult();

		} catch (Exception e) {
			logger.error(" getNwTypeEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkTypeDetailsEntity;
	}

	@Override
	public NetworkConfigEntity getLsmEntity(String lsmName, String lsmVersion,int programId) {
		NetworkConfigEntity networkConfigEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			
			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("neVersion"), lsmVersion),criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			NeVersionEntity neVersionEntity = crTypedQuery.getSingleResult();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = cb.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root).where(cb.and(cb.equal(root.get("neName"), lsmName),
					cb.equal(root.get("neVersionEntity"), neVersionEntity)));

			TypedQuery<NetworkConfigEntity> typedQuery = entityManager.createQuery(query);
			networkConfigEntity = typedQuery.getSingleResult();

		} catch (Exception e) {
			//logger.error(" getLsmEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigEntity;
	}

	@Override
	public boolean updateUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity, List<UseCaseScriptsModel> scriptList,
			Integer customerId, Integer programId, String migrationType, String subType) {

		boolean updateStatus = false;
		List<CheckListScriptDetEntity> CheckListScriptDetails = null;
		try {
			entityManager.merge(useCaseBuilderEntity);
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepositoryImpl.getCustomerDetailsEntity(programId);
			for (UseCaseScriptsModel useCaseScriptsModel : scriptList) {
				
				CheckListScriptDetEntity checkListScriptDetEntity = getCheckListDetails(programId);
				
				if(useCaseScriptsModel.getScriptSequence()!=null && !useCaseScriptsModel.getScriptSequence().isEmpty()) {
					//do nothing
				}else {
					CheckListScriptDetails = getExeseq(programId,useCaseScriptsModel.getScript().get("scriptName"),"",checkListScriptDetEntity.getCheckListFileName());
				}

				UseCaseBuilderParamEntity useCaseBuilderParamEntity;
				if (useCaseScriptsModel.getScriptId() != null) {
					useCaseBuilderParamEntity = getUseCaseBuilderParamEntity(
							Integer.parseInt(useCaseScriptsModel.getScriptId()));

					UploadFileEntity uploadFileEntity ;
					if (useCaseBuilderEntity.getNetworkConfigEntity() != null) {
						uploadFileEntity = getUploadFileEntity(
								useCaseBuilderEntity.getNetworkConfigEntity().getNeName(),
								useCaseBuilderEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion(),
								programId, useCaseBuilderEntity.getMigrationType(),
								useCaseScriptsModel.getScript().get("scriptName"),useCaseScriptsModel.getScript().get("scriptFileId"), subType);
					} else {
						uploadFileEntity = getUploadFileEntity(null, null, programId,
								useCaseBuilderEntity.getMigrationType(),
								useCaseScriptsModel.getScript().get("scriptName"),useCaseScriptsModel.getScript().get("scriptFileId"), subType);
					}

					if (useCaseBuilderParamEntity != null) {
						useCaseBuilderParamEntity.setScriptsDetails(uploadFileEntity);
						if(useCaseScriptsModel.getScriptSequence()!=null && !useCaseScriptsModel.getScriptSequence().isEmpty()) {
							useCaseBuilderParamEntity
							.setExecutionSequence(Integer.parseInt(useCaseScriptsModel.getScriptSequence()));
						}else {
							useCaseBuilderParamEntity
							.setExecutionSequence((CheckListScriptDetails.get(0).getScriptExeSeq()));
						}
						
						useCaseBuilderParamEntity.setUseCaseBuilderEntity(useCaseBuilderEntity);
						useCaseBuilderParamEntity.setScriptRemarks(useCaseScriptsModel.getScriptRemarks());
						useCaseBuilderParamEntity.setCustomerDetailsEntity(customerDetailsEntity);

						entityManager.merge(useCaseBuilderParamEntity);
						
						//Local path file creation
						UseCaseBuilderModel useCaseBuilderModelforPath = new UseCaseBuilderModel();
						useCaseBuilderModelforPath.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
						if(useCaseBuilderModelforPath.getUseCaseName().contains(Constants.RF_USECASE) || useCaseBuilderModelforPath.getUseCaseName().contains(Constants.COMMISION_USECASE)) {
							//do nothing
						}else {
							createScriptPathForUseCase(useCaseBuilderModelforPath, scriptList);
						}
						//createScriptPathForUseCase(useCaseBuilderModelforPath, scriptList);
					}

					
					
					LinkedHashSet<UseCaseShellRuleEntity> useCaseShellRuleEntitySet = useCaseBuilderParamEntity
							.getUseCaseShellRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
					int useCaseShellEntitySize = 0;
					if (useCaseShellRuleEntitySet != null && !useCaseShellRuleEntitySet.isEmpty()) {
						List<UseCaseShellRuleEntity> useCaseShellRuleEntityList = new ArrayList<UseCaseShellRuleEntity>();
						useCaseShellRuleEntityList.addAll(useCaseShellRuleEntitySet);
						List<ShellRuleModel> shellRuleList = useCaseScriptsModel.getShellRules();
						for (ShellRuleModel shellRuleModel : shellRuleList) {
							String shellName = shellRuleModel.getShellDetails().get("shellCmdName");
							String shellSequence = shellRuleModel.getShellRuleSequence();
							String shellRemarks = shellRuleModel.getShellRuleRemarks();
							ShellCmdRuleBuilderEntity shellRuleBuilderEntity = getShellRuleEntity(shellName, programId,
									migrationType, subType);

							if (useCaseShellRuleEntityList.size() > useCaseShellEntitySize) {
								UseCaseShellRuleEntity useCaseShellRuleEntity = useCaseShellRuleEntityList
										.get(useCaseShellEntitySize);
								if (useCaseShellEntitySize < useCaseShellRuleEntityList.size()) {
									useCaseShellEntitySize++;
								}
								useCaseShellRuleEntity.setShellRuleBuilderEntity(shellRuleBuilderEntity);
								useCaseShellRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
								useCaseShellRuleEntity.setShellRemarks(shellRemarks);
								useCaseShellRuleEntity.setShellRuleSequence(Integer.parseInt(shellSequence));
								entityManager.merge(useCaseShellRuleEntity);
							} else {
								UseCaseShellRuleEntity useCaseShellRuleEntity = new UseCaseShellRuleEntity();
								useCaseShellRuleEntity.setShellRuleBuilderEntity(shellRuleBuilderEntity);
								useCaseShellRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
								useCaseShellRuleEntity.setShellRemarks(shellRemarks);
								useCaseShellRuleEntity.setShellRuleSequence(Integer.parseInt(shellSequence));
								entityManager.persist(useCaseShellRuleEntity);
							}
						}
					} else {
						List<ShellRuleModel> shellRuleList = useCaseScriptsModel.getShellRules();
						for (ShellRuleModel shellRuleModel : shellRuleList) {
							String shellName = shellRuleModel.getShellDetails().get("shellCmdName");
							String shellSequence = shellRuleModel.getShellRuleSequence();
							String shellRemarks = shellRuleModel.getShellRuleRemarks();
							ShellCmdRuleBuilderEntity shellRuleBuilderEntity = getShellRuleEntity(shellName, programId,
									migrationType, subType);
							UseCaseShellRuleEntity useCaseShellRuleEntity = new UseCaseShellRuleEntity();
							useCaseShellRuleEntity.setShellRuleBuilderEntity(shellRuleBuilderEntity);
							useCaseShellRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
							useCaseShellRuleEntity.setShellRemarks(shellRemarks);
							useCaseShellRuleEntity.setShellRuleSequence(Integer.parseInt(shellSequence));
							entityManager.persist(useCaseShellRuleEntity);
						}
					}
					
					
					LinkedHashSet<UseCaseCmdRuleEntity> useCaseCmdRuleEntitySet = useCaseBuilderParamEntity
							.getUseCaseCmdRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
					int useCaseCmdEntitySize = 0;
					if (useCaseCmdRuleEntitySet != null && !useCaseCmdRuleEntitySet.isEmpty()) {
						List<UseCaseCmdRuleEntity> useCaseCmdRuleEntityList = new ArrayList<UseCaseCmdRuleEntity>();
						useCaseCmdRuleEntityList.addAll(useCaseCmdRuleEntitySet);
						List<CmdRuleModel> cmdRuleList = useCaseScriptsModel.getCmdRules();
						for (CmdRuleModel cmdRuleModel : cmdRuleList) {
							String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
							String cmdSequence = cmdRuleModel.getCmdSequence();
							String cmdRemarks = cmdRuleModel.getCmdRemarks();
							CmdRuleBuilderEntity cmdRuleBuilderEntity = getCommandRuleEntity(cmdName, programId,
									migrationType, subType);

							if (useCaseCmdRuleEntityList.size() > useCaseCmdEntitySize) {
								UseCaseCmdRuleEntity useCaseCmdRuleEntity = useCaseCmdRuleEntityList
										.get(useCaseCmdEntitySize);
								if (useCaseCmdEntitySize < useCaseCmdRuleEntityList.size()) {
									useCaseCmdEntitySize++;
								}
								useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
								useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
								useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
								useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
								entityManager.merge(useCaseCmdRuleEntity);
							} else {
								UseCaseCmdRuleEntity useCaseCmdRuleEntity = new UseCaseCmdRuleEntity();
								useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
								useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
								useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
								useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
								entityManager.persist(useCaseCmdRuleEntity);
							}
						}
					} else {
						List<CmdRuleModel> cmdRuleList = useCaseScriptsModel.getCmdRules();
						for (CmdRuleModel cmdRuleModel : cmdRuleList) {
							String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
							String cmdSequence = cmdRuleModel.getCmdSequence();
							String cmdRemarks = cmdRuleModel.getCmdRemarks();
							CmdRuleBuilderEntity cmdRuleBuilderEntity = getCommandRuleEntity(cmdName, programId,
									migrationType, subType);
							UseCaseCmdRuleEntity useCaseCmdRuleEntity = new UseCaseCmdRuleEntity();
							useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
							useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
							useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
							useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
							entityManager.persist(useCaseCmdRuleEntity);
						}
					}

					
					
					LinkedHashSet<UseCaseXmlRuleEntity> useCaseXmlRuleEntitySet = useCaseBuilderParamEntity
							.getUseCaseXmlRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
					if (useCaseXmlRuleEntitySet != null && !useCaseXmlRuleEntitySet.isEmpty()) {
						List<UseCaseXmlRuleEntity> useCaseXmlRuleEntityList = new ArrayList<UseCaseXmlRuleEntity>();
						useCaseXmlRuleEntityList.addAll(useCaseXmlRuleEntitySet);
						int useCaseXmlEntitySize = 0;
						List<XmlRuleModel> xmlRuleList = useCaseScriptsModel.getXmlRules();
						for (XmlRuleModel xmlRuleModel : xmlRuleList) {
							String xmlRuleName = xmlRuleModel.getXmlDetails().get("xmlName");
							String xmlSequence = xmlRuleModel.getXmlSequence();
							String xmlRemarks = xmlRuleModel.getXmlRemarks();
							XmlRuleBuilderEntity xmlRuleBuilderEntity = getXmlRuleBuilderEntity(xmlRuleName, programId,
									migrationType, subType);
							if (useCaseXmlRuleEntityList.size() > useCaseXmlEntitySize) {
								UseCaseXmlRuleEntity useCaseXmlRuleEntity = useCaseXmlRuleEntityList
										.get(useCaseXmlEntitySize);
								if (useCaseXmlEntitySize < useCaseXmlRuleEntityList.size()) {
									useCaseXmlEntitySize++;
								}
								useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
								useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
								useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
								useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
								entityManager.merge(useCaseXmlRuleEntity);
							} else {
								UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
								useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
								useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
								useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
								useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
								entityManager.persist(useCaseXmlRuleEntity);
							}
						}
					} else {
						List<XmlRuleModel> xmlRuleList = useCaseScriptsModel.getXmlRules();
						for (XmlRuleModel xmlRuleModel : xmlRuleList) {
							String xmlRuleName = xmlRuleModel.getXmlDetails().get("xmlName");
							String xmlSequence = xmlRuleModel.getXmlSequence();
							String xmlRemarks = xmlRuleModel.getXmlRemarks();
							XmlRuleBuilderEntity xmlRuleBuilderEntity = getXmlRuleBuilderEntity(xmlRuleName, programId,
									migrationType, subType);
							UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
							useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
							useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
							useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
							useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
							entityManager.persist(useCaseXmlRuleEntity);
						}
					}

					LinkedHashSet<UseCaseFileRuleEntity> useCaseFileRuleEntitySet = useCaseBuilderParamEntity
							.getUseCaseFileRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
					if (useCaseFileRuleEntitySet != null && !useCaseFileRuleEntitySet.isEmpty()) {
						List<UseCaseFileRuleEntity> useCaseFileRuleEntityList = new ArrayList<UseCaseFileRuleEntity>();
						useCaseFileRuleEntityList.addAll(useCaseFileRuleEntitySet);
						int useCaseFileEntitySize = 0;
						List<FileRuleModel> fileRuleList = useCaseScriptsModel.getFileRules();
						if (!fileRuleList.isEmpty()) {
							for (FileRuleModel fileRuleModel : fileRuleList) {
								String fileRuleName = fileRuleModel.getFileDetails().get("fileRuleName");

								FileRuleBuilderEntity fileRuleBuilderEntity = getFileRuleEntity(fileRuleName, programId,
										migrationType, subType);
								if (useCaseFileRuleEntityList.size() > useCaseFileEntitySize) {
									UseCaseFileRuleEntity useCaseFileRuleEntity = useCaseFileRuleEntityList
											.get(useCaseFileEntitySize);
									if (useCaseFileEntitySize < useCaseFileRuleEntityList.size()) {
										useCaseFileEntitySize++;
									}
									useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
									useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
									useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
									useCaseFileRuleEntity
											.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
									entityManager.merge(useCaseFileRuleEntity);
								} else {
									UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
									useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
									useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
									useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
									useCaseFileRuleEntity
											.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
									entityManager.persist(useCaseFileRuleEntity);
								}

							}
						}
					} else {
						List<FileRuleModel> fileRuleList = useCaseScriptsModel.getFileRules();
						if (!fileRuleList.isEmpty()) {
							for (FileRuleModel fileRuleModel : fileRuleList) {
								String fileRuleName = fileRuleModel.getFileDetails().get("fileRuleName");
								FileRuleBuilderEntity fileRuleBuilderEntity = getFileRuleEntity(fileRuleName, programId,
										migrationType, subType);
								UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
								useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
								useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
								useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
								useCaseFileRuleEntity
										.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
								entityManager.persist(useCaseFileRuleEntity);
							}
						}
					}

				} else {

					useCaseBuilderParamEntity = new UseCaseBuilderParamEntity();

					UploadFileEntity uploadFileEntity = null;
					if (useCaseBuilderEntity.getNetworkConfigEntity() != null) {
						uploadFileEntity = getUploadFileEntity(
								useCaseBuilderEntity.getNetworkConfigEntity().getNeName(),
								useCaseBuilderEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion(),
								programId, useCaseBuilderEntity.getMigrationType(),
								useCaseScriptsModel.getScript().get("scriptName"), useCaseScriptsModel.getScript().get("scriptFileId"), subType);
					} else {
						uploadFileEntity = getUploadFileEntity(null, null, programId,
								useCaseBuilderEntity.getMigrationType(),
								useCaseScriptsModel.getScript().get("scriptName"), useCaseScriptsModel.getScript().get("scriptFileId"), subType);
					}

					useCaseBuilderParamEntity.setScriptsDetails(uploadFileEntity);
					useCaseBuilderParamEntity
							.setExecutionSequence((CheckListScriptDetails.get(0).getScriptExeSeq()));
					useCaseBuilderParamEntity.setUseCaseBuilderEntity(useCaseBuilderEntity);
					useCaseBuilderParamEntity.setScriptRemarks(useCaseScriptsModel.getScriptRemarks());
					useCaseBuilderParamEntity.setCustomerDetailsEntity(customerDetailsEntity);
					entityManager.persist(useCaseBuilderParamEntity);

					List<CmdRuleModel> cmdRuleList = useCaseScriptsModel.getCmdRules();
					for (CmdRuleModel cmdRuleModel : cmdRuleList) {
						String cmdName = cmdRuleModel.getCmdDetails().get("cmdName");
						String cmdSequence = cmdRuleModel.getCmdSequence();
						String cmdRemarks = cmdRuleModel.getCmdRemarks();
						CmdRuleBuilderEntity cmdRuleBuilderEntity = getCommandRuleEntity(cmdName, programId,
								migrationType, subType);
						UseCaseCmdRuleEntity useCaseCmdRuleEntity = new UseCaseCmdRuleEntity();
						useCaseCmdRuleEntity.setCmdRuleBuilderEntity(cmdRuleBuilderEntity);
						useCaseCmdRuleEntity.setUseCaseBuilderParamEntity(useCaseBuilderParamEntity);
						useCaseCmdRuleEntity.setCmdRemarks(cmdRemarks);
						useCaseCmdRuleEntity.setCommandRuleSequence(Integer.parseInt(cmdSequence));
						entityManager.persist(useCaseCmdRuleEntity);
					}

					List<XmlRuleModel> xmlRuleList = useCaseScriptsModel.getXmlRules();
					for (XmlRuleModel xmlRuleModel : xmlRuleList) {
						String xmlRuleName = xmlRuleModel.getXmlDetails().get("xmlName");
						String xmlSequence = xmlRuleModel.getXmlSequence();
						String xmlRemarks = xmlRuleModel.getXmlRemarks();
						XmlRuleBuilderEntity xmlRuleBuilderEntity = getXmlRuleBuilderEntity(xmlRuleName, programId,
								migrationType, subType);
						UseCaseXmlRuleEntity useCaseXmlRuleEntity = new UseCaseXmlRuleEntity();
						useCaseXmlRuleEntity.setXmlRemarks(xmlRemarks);
						useCaseXmlRuleEntity.setXmlRuleBuilderEntity(xmlRuleBuilderEntity);
						useCaseXmlRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
						useCaseXmlRuleEntity.setXmlRuleSequence(Integer.parseInt(xmlSequence));
						entityManager.persist(useCaseXmlRuleEntity);
					}

					List<FileRuleModel> fileRuleList = useCaseScriptsModel.getFileRules();
					if (!fileRuleList.isEmpty()) {
						for (FileRuleModel fileRuleModel : fileRuleList) {
							String fileRuleName = fileRuleModel.getFileDetails().get("fileRuleName");
							FileRuleBuilderEntity fileRuleBuilderEntity = getFileRuleEntity(fileRuleName, programId,
									migrationType, subType);
							UseCaseFileRuleEntity useCaseFileRuleEntity = new UseCaseFileRuleEntity();
							useCaseFileRuleEntity.setFileRuleBuilderEntity(fileRuleBuilderEntity);
							useCaseFileRuleEntity.setUseCaseBuilderScriptsEntity(useCaseBuilderParamEntity);
							useCaseFileRuleEntity.setFileRemarks(fileRuleModel.getFileRuleRemarks());
							useCaseFileRuleEntity
									.setFileRuleSequence(Integer.parseInt(fileRuleModel.getFileRuleSequence()));
							entityManager.persist(useCaseFileRuleEntity);
						}
					}
				}
			}
			updateStatus = true;

		} catch (Exception e) {
			logger.error(" updateUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return updateStatus;
	}
	
	public void createScriptPathForUseCase(UseCaseBuilderModel useCaseBuilderModel,
			List<UseCaseScriptsModel> scriptList) {
		try {
			for (UseCaseScriptsModel useCaseScriptModel : scriptList) {
				if(useCaseScriptModel.getScriptName()!=null) {
					//Do nothing
				}else {
					String scriptId = useCaseScriptModel.getScript().get("scriptFileId");
					UploadFileEntity uploadFileEntity = getUploadFileEntityByScriptId(scriptId);
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
			}
		} catch (Exception e) {
			logger.error(" createScriptPathForUseCase() : " + ExceptionUtils.getFullStackTrace(e));
		}
	}

	@Override
	public UseCaseBuilderEntity getUseCaseBuilderEntity(Integer id) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getUseCaseBuilderEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntity;
	}

	@Override
	public UseCaseBuilderParamEntity getUseCaseBuilderParamEntity(Integer id) {
		UseCaseBuilderParamEntity useCaseBuilderParamEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> query = cb.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> root = query.from(UseCaseBuilderParamEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<UseCaseBuilderParamEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderParamEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getUseCaseBuilderParamEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderParamEntity;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<UseCaseBuilderParamEntity> getUseCaseBuilderParamUseCase(UseCaseBuilderParamEntity useCaseId) {
		List<UseCaseBuilderParamEntity> ciqList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(UseCaseBuilderParamEntity.class);

			criteria.add(Restrictions.eq("useCaseBuilderEntity.id", useCaseId.getUseCaseBuilderEntity().getId()));

			ciqList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception  getUseCaseBuilderParamUseCase() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return ciqList;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<UseCaseCmdRuleEntity> getUseCaseCmdRuleEntityList(Integer id) {
		List<UseCaseCmdRuleEntity> useCaseCmdRuleEntityList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(UseCaseCmdRuleEntity.class);

			criteria.add(Restrictions.eq("useCaseBuilderParamEntity.id", id));

			useCaseCmdRuleEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getUseCaseCmdRuleEntityList() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseCmdRuleEntityList;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<UseCaseShellRuleEntity> getUseCaseShellRuleEntityList(Integer id) {
		List<UseCaseShellRuleEntity> useCaseShellRuleEntityList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(UseCaseShellRuleEntity.class);

			criteria.add(Restrictions.eq("useCaseBuilderParamEntity.id", id));

			useCaseShellRuleEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getUseCaseShellRuleEntityList() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseShellRuleEntityList;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<UseCaseFileRuleEntity> getUseCaseFileRuleEntityList(Integer id) {
		List<UseCaseFileRuleEntity> useCaseFileRuleEntityList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(UseCaseFileRuleEntity.class);

			criteria.add(Restrictions.eq("useCaseBuilderScriptsEntity.id", id));

			useCaseFileRuleEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getUseCaseFileRuleEntityList() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseFileRuleEntityList;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<UseCaseXmlRuleEntity> getUseCaseXmlRuleEntityList(Integer id) {
		List<UseCaseXmlRuleEntity> useCaseXmlRuleEntityList = new ArrayList<>();

		try {
			org.hibernate.Criteria criteria = entityManager.unwrap(Session.class)
					.createCriteria(UseCaseXmlRuleEntity.class);

			criteria.add(Restrictions.eq("useCaseBuilderScriptsEntity.id", id));

			useCaseXmlRuleEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY)
					.list();

		} catch (Exception e) {
			logger.error("Exception  getUseCaseXmlRuleEntityList() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseXmlRuleEntityList;
	}

	public UseCaseCmdRuleEntity getUseCaseCmdRuleEntity(Integer id) {
		UseCaseCmdRuleEntity useCaseCmdRuleEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseCmdRuleEntity> query = cb.createQuery(UseCaseCmdRuleEntity.class);
			Root<UseCaseCmdRuleEntity> root = query.from(UseCaseCmdRuleEntity.class);
			query.select(root).where(cb.equal(root.get("SCRIPTS_ID"), id));

			TypedQuery<UseCaseCmdRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseCmdRuleEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception  getUseCaseCmdRuleEntity() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseCmdRuleEntity;
	}

	public UseCaseFileRuleEntity getUseCaseFileRuleEntity(Integer id) {
		UseCaseFileRuleEntity useCaseFileRuleEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseFileRuleEntity> query = cb.createQuery(UseCaseFileRuleEntity.class);
			Root<UseCaseFileRuleEntity> root = query.from(UseCaseFileRuleEntity.class);
			query.select(root).where(cb.equal(root.get("SCRIPTS_ID"), id));

			TypedQuery<UseCaseFileRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseFileRuleEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error("Exception  getUseCaseFileRuleEntity() in  UseCaseBuilderRepositoryImpl:" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseFileRuleEntity;
	}

	@Override
	public boolean deleteUseCaseBuilder(Integer id) {
		boolean deleteUseCaseStatus = false;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			UseCaseBuilderEntity useCaseBuilderEntity = typedQuery.getSingleResult();
			entityManager.remove(useCaseBuilderEntity);
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}

	@Override
	public boolean deleteUseCaseParamBuilder(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		// public boolean deleteUseCaseParamBuilder(int useCaseBuilderParamEntity) {
		boolean deleteUseCaseStatus = false;
		try {

			entityManager.remove(useCaseBuilderParamEntity);
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}

	@Override
	public boolean deleteUseCaseParamBuilder(int id) {
		boolean deleteUseCaseStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from UseCaseBuilderParamEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}
	
	@Override
	public int getMaxUseCaseId() {
		int maxId = 0;
		try {
			Query query = entityManager.createQuery("select max(use.ExecutionSequence) from UseCaseBuilderEntity use");
			maxId = (int) query.getSingleResult();
			
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return maxId;
	}
	
	@Override
	public int getMaxExeSeqId(int programId) {
		int maxId = 0;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			
			Query query = entityManager.createQuery("select max(use.executionSequence) from UseCaseBuilderParamEntity use where use.useCaseBuilderEntity.customerDetailsEntity = :useCaseBuilderEntity");
			query.setParameter("useCaseBuilderEntity", customerDetailsEntity);
			maxId = (int) query.getSingleResult();
			
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return maxId;
	}

	@Override
	public boolean deleteUseCaseShellRule(int id) {
		boolean deleteUseCaseStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from UseCaseShellRuleEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}
	
	@Override
	public boolean deleteUseCaseCmdRule(int id) {
		boolean deleteUseCaseStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from UseCaseCmdRuleEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}

	@Override
	public boolean deleteUseCaseXmlRule(int id) {
		boolean deleteUseCaseStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from UseCaseXmlRuleEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}

	@Override
	public boolean deleteUseCaseFileRule(int id) {
		boolean deleteUseCaseStatus = false;
		try {
			Query query = entityManager.createQuery("DELETE from UseCaseFileRuleEntity WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
			deleteUseCaseStatus = true;
		} catch (Exception e) {
			logger.error(" deleteUseCaseCmd() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}

		return deleteUseCaseStatus;
	}

	@Override
	public UploadFileEntity getUploadFileEntityByScriptId(String scriptFileId) {
		UploadFileEntity uploadFileEntity = null;
		try {
			int id = Integer.valueOf(scriptFileId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
			query.select(root).where(cb.equal(root.get("id"), id));

			TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
			uploadFileEntity = typedQuery.getSingleResult();
		} catch (Exception e) {
			logger.error(" getUploadFileEntityByScriptId() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntity;
	}

	@Override
	public Map<String, List<String>> getSmList(int programId) {
		List<NeVersionEntity> neVersionIdList = null;
		Map<String, List<String>> neMap = new HashMap<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			// crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"),
			// customerDetailsEntity));
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			neVersionIdList = (List<NeVersionEntity>) crTypedQuery.getResultList();

			for (NeVersionEntity neVersionEntity : neVersionIdList) {
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<String> query = cb.createQuery(String.class);
				Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
				query.select(root.<String>get("neName")).where(cb.equal(root.get("neVersionEntity"), neVersionEntity));
				TypedQuery<String> typedQuery = entityManager.createQuery(query);
				List<String> smName = typedQuery.getResultList();

				neMap.put(neVersionEntity.getNeVersion(), smName);
			}

			/*
			 * CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			 * CriteriaQuery<NetworkConfigEntity> query =
			 * cb.createQuery(NetworkConfigEntity.class); Root<NetworkConfigEntity> root =
			 * query.from(NetworkConfigEntity.class);
			 * query.select(root).where(cb.equal(root.get("programDetailsEntity" ),
			 * customerDetailsEntity));
			 * 
			 * TypedQuery<NetworkConfigEntity> typedQuery =
			 * entityManager.createQuery(query); networkConfigEntityList =
			 * (List<NetworkConfigEntity>) typedQuery.getResultList();
			 */

		} catch (Exception e) {
			logger.error(" getSmList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neMap;
	}

	@Override
	public Map<String, Map<String, List<Map<String, String>>>> getSmScriptList(int programId, String migrationType,
			String subType) {
		List<NeVersionEntity> neVersionIdList = null;
		Map<String, Map<String, List<Map<String, String>>>> neMap = new HashMap<>();

		try {

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity),
					criteria.or(criteria.equal(crRoot.get("status"), "Active"),criteria.equal(crRoot.get("status"), "StandBy")));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			neVersionIdList = (List<NeVersionEntity>) crTypedQuery.getResultList();

			for (NeVersionEntity neVersionEntity : neVersionIdList) {
				CriteriaBuilder neConfCriteriaBldr = entityManager.getCriteriaBuilder();
				CriteriaQuery<NetworkConfigEntity> neConfQuery = neConfCriteriaBldr.createQuery(NetworkConfigEntity.class);
				Root<NetworkConfigEntity> neConfRoot = neConfQuery.from(NetworkConfigEntity.class);
				neConfQuery.select(neConfRoot)
						.where(neConfCriteriaBldr.equal(neConfRoot.get("neVersionEntity"), neVersionEntity));
				TypedQuery<NetworkConfigEntity> neConfTypedQuery = entityManager.createQuery(neConfQuery);
				List<NetworkConfigEntity> smNameList = neConfTypedQuery.getResultList();
				
				String smType = LoadPropertyFiles.getInstance().getProperty("smType");
				
				List<String> smNameListNew = new ArrayList();
				
				for (NetworkConfigEntity networkConfigEntity : smNameList) {
					Map smMap = new HashMap<>();

					if (smType.contains(networkConfigEntity.getNeTypeEntity().getNeType())) {

						smNameListNew.add(networkConfigEntity.getNeName());
					}
				}

				Map<String, List<Map<String, String>>> neScriptmap = new HashMap<>();

				for (String neName : smNameListNew) {
					CriteriaBuilder cbSM = entityManager.getCriteriaBuilder();
					CriteriaQuery<NetworkConfigEntity> querySM = cbSM.createQuery(NetworkConfigEntity.class);
					Root<NetworkConfigEntity> rootSM = querySM.from(NetworkConfigEntity.class);
					querySM.select(rootSM).where(cbSM.and(cbSM.equal(rootSM.get("neVersionEntity"), neVersionEntity),
							cbSM.equal(rootSM.get("neName"), neName)));
					TypedQuery<NetworkConfigEntity> typedQuerySM = entityManager.createQuery(querySM);
					NetworkConfigEntity networkConfigEntity = typedQuerySM.getSingleResult();

					/*CriteriaBuilder scriptCb = entityManager.getCriteriaBuilder();
					CriteriaQuery<UploadFileEntity> scriptQuery = scriptCb.createQuery(UploadFileEntity.class);
					Root<UploadFileEntity> scriptRoot = scriptQuery.from(UploadFileEntity.class);
					scriptQuery.select(scriptRoot)
							.where(scriptCb.and(scriptCb.equal(scriptRoot.get("neListEntity"), networkConfigEntity),
									scriptCb.equal(scriptRoot.get("migrationType"), migrationType),
									scriptCb.equal(scriptRoot.get("subType"), subType)));
					TypedQuery<UploadFileEntity> scriptTypedQuery = entityManager.createQuery(scriptQuery);
					List<UploadFileEntity> uploadFileEntityList = scriptTypedQuery.getResultList();

					List<Map<String, String>> scriptList = new LinkedList<>();

					for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
						Map<String, String> scriptMap = new HashMap<>();
						scriptMap.put("scriptName", uploadFileEntity.getFileName());
						scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
						scriptList.add(scriptMap);
					}

					NetworkConfigEntity neEntity = UploadFileRepositoryImpl.getNeEntity(neVersionEntity.getNeVersion(),
							neName, programId);
					List<Map<String, String>> getAllScriptList = getScriptListWithVersionName(programId, migrationType,
							neEntity, subType); 
					// List<Map<String, String>> getAllScriptList = getScriptList(programId,
					// migrationType);
					
					for (Map map : getAllScriptList) {
						Map<String, String> scriptMap = new HashMap<>();
						scriptMap.put("scriptName", map.get("scriptName").toString());
						scriptMap.put("scriptFileId", map.get("scriptFileId").toString());
						scriptList.add(scriptMap);
					}*/
					
					//New Implementation to fetch data - 23/01/2021
					UploadFileModel uploadfilemodel = new UploadFileModel();
					uploadfilemodel.setMigrationType(migrationType);
					uploadfilemodel.setSubType(subType);
					List<UploadFileEntity> uploadFileEntityList = getuploadFileEntityList(uploadfilemodel, programId, "");
					List<Map<String, String>> scriptList = new LinkedList<>();

					for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
						Map<String, String> scriptMap = new HashMap<>();
						scriptMap.put("scriptName", uploadFileEntity.getFileName());
						scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
						scriptList.add(scriptMap);
					}
					
					uploadfilemodel.setNeList(networkConfigEntity);
					uploadFileEntityList = getuploadFileEntityList(uploadfilemodel, programId, "");
					for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
						Map<String, String> scriptMap = new HashMap<>();
						scriptMap.put("scriptName", uploadFileEntity.getFileName());
						scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
						scriptList.add(scriptMap);
					}
					
					neScriptmap.put(neName, scriptList);
				}
				neMap.put(neVersionEntity.getNeVersion(), neScriptmap);
			}
		} catch (Exception e) {
			logger.error(" getSmScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neMap;
	}
	
	public List<UploadFileEntity> getuploadFileEntityList(UploadFileModel uploadfilemodel, int programId, String type) {
		List<UploadFileEntity> uploadFileEntityList = new ArrayList<>();
		try {
			Conjunction conjunction = Restrictions.conjunction();
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UploadFileEntity.class);
			criteria.setFirstResult(0);
			criteria.addOrder(Order.desc("creationDate"));
			if (uploadfilemodel != null) {

				Date endDate = new Date();
				String toDate = DateUtil.dateToString(endDate, Constants.MM_DD_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				Integer pastHistory = Integer.parseInt(LoadPropertyFiles.getInstance().getProperty(Constants.HISTORY));
				c.add(Calendar.DATE, -pastHistory);
				Date sdate = c.getTime();
				String fromDate = DateUtil.dateToString(sdate, Constants.MM_DD_YYYY);
				Criterion searchStartDate = Restrictions.ge("creationDate",
						DateUtil.stringToDate(fromDate, Constants.MM_DD_YYYY));
				Criterion searchEndDate = Restrictions.le("creationDate",
						DateUtil.stringToDateEndTime(toDate, Constants.MM_DD_YYYY));
				conjunction.add(searchStartDate);
				conjunction.add(searchEndDate);
			}
			if(StringUtils.isNotEmpty(uploadfilemodel.getMigrationType())){
				conjunction.add(Restrictions.eq("migrationType", uploadfilemodel.getMigrationType()));				
			}
			if(StringUtils.isNotEmpty(uploadfilemodel.getSubType())){
				conjunction.add(Restrictions.eq("subType", uploadfilemodel.getSubType()));
			}
			if(uploadfilemodel.getNeList() != null) {
				conjunction.add(Restrictions.eq("neListEntity.id", uploadfilemodel.getNeList().getId()));
			} else {
				conjunction.add(Restrictions.isNull("neListEntity"));
			}
			if(programId > 0) {
				conjunction.add(Restrictions.eq("customerDetailsEntity.id", programId));
			}
			if(type.equalsIgnoreCase("withoutversionname")) {
				conjunction.add(Restrictions.isNull("neVersion"));
			} else if(type.equals("withoutsmversion")) {
				Disjunction or = Restrictions.disjunction();
				or.add(Restrictions.isNull("neVersion"));
				if(uploadfilemodel.getNeVersion() != null) {
					or.add(Restrictions.eq("neVersion.id", uploadfilemodel.getNeVersion().getId()));
				}
				conjunction.add(or);
			}
			

			criteria.add(conjunction);
			uploadFileEntityList = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error(
					"Exception in RunTestRepositoryImpl.getRunTestDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return uploadFileEntityList;
	}

	@Override
	public List<XmlRuleBuilderEntity> getXmlRuleList(int programId, String migrationType, String subType) {

		List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<XmlRuleBuilderEntity> query = cb.createQuery(XmlRuleBuilderEntity.class);
			Root<XmlRuleBuilderEntity> root = query.from(XmlRuleBuilderEntity.class);
			query.where(cb.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					cb.equal(root.get("migrationType"), migrationType), cb.equal(root.get("subType"), subType));
			TypedQuery<XmlRuleBuilderEntity> typedQuery = entityManager.createQuery(query);
			xmlRuleBuilderEntityList = (List<XmlRuleBuilderEntity>) typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getFileRuleList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return xmlRuleBuilderEntityList;
	}

	@Override
	public List<Map<String, String>> getScriptList(int programId, String migrationType) {
		List<Map<String, String>> scriptList = new LinkedList<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder scriptCb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> scriptQuery = scriptCb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> scriptRoot = scriptQuery.from(UploadFileEntity.class);
			scriptQuery.select(scriptRoot)
					.where(scriptCb.and(scriptCb.equal(scriptRoot.get("customerDetailsEntity"), customerDetailsEntity),
							scriptCb.equal(scriptRoot.get("migrationType"), migrationType)));
			TypedQuery<UploadFileEntity> scriptTypedQuery = entityManager.createQuery(scriptQuery);
			List<UploadFileEntity> uploadFileEntityList = scriptTypedQuery.getResultList();

			for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
				Map<String, String> scriptMap = new HashMap<>();
				scriptMap.put("scriptName", uploadFileEntity.getFileName());
				scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
				scriptList.add(scriptMap);
			}

		} catch (Exception e) {
			logger.error(" getSmScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return scriptList;
	}

	public List<Map<String, String>> getScriptListWithVersionName(int programId, String migrationType,
			NetworkConfigEntity neEntity, String subType) {
		List<Map<String, String>> scriptList = new LinkedList<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder scriptCb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> scriptQuery = scriptCb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> scriptRoot = scriptQuery.from(UploadFileEntity.class);
			scriptQuery.select(scriptRoot)
					.where(scriptCb.and(scriptCb.equal(scriptRoot.get("customerDetailsEntity"), customerDetailsEntity),
							scriptCb.equal(scriptRoot.get("subType"), subType),
							scriptCb.isNull(scriptRoot.get("neListEntity")),
							scriptCb.equal(scriptRoot.get("migrationType"), migrationType)));
			TypedQuery<UploadFileEntity> scriptTypedQuery = entityManager.createQuery(scriptQuery);
			List<UploadFileEntity> uploadFileEntityList = scriptTypedQuery.getResultList();

			for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
				Map<String, String> scriptMap = new HashMap<>();
				scriptMap.put("scriptName", uploadFileEntity.getFileName());
				scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
				scriptList.add(scriptMap);
			}

		} catch (Exception e) {
			logger.error(" getSmScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return scriptList;
	}

	@Override
	public List<Map<String, String>> scriptInfoWithoutVersionName(int programId, String migrationType, String subType) {
		List<Map<String, String>> scriptList = new LinkedList<>();
		NetworkConfigEntity NetworkConfigEntity = null;
		try {
			/*
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder scriptCb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UploadFileEntity> scriptQuery = scriptCb.createQuery(UploadFileEntity.class);
			Root<UploadFileEntity> scriptRoot = scriptQuery.from(UploadFileEntity.class);
			scriptQuery.select(scriptRoot)
					.where(scriptCb.and(scriptCb.equal(scriptRoot.get("customerDetailsEntity"), customerDetailsEntity),
							scriptCb.isNull(scriptRoot.get("neListEntity")),
							scriptCb.isNull(scriptRoot.get("neVersion")),
							scriptCb.equal(scriptRoot.get("migrationType"), migrationType),
							scriptCb.equal(scriptRoot.get("subType"), subType)));
			TypedQuery<UploadFileEntity> scriptTypedQuery = entityManager.createQuery(scriptQuery);
			List<UploadFileEntity> uploadFileEntityList = scriptTypedQuery.getResultList();*/
			

			UploadFileModel uploadfilemodel = new UploadFileModel();
			uploadfilemodel.setMigrationType(migrationType);
			uploadfilemodel.setSubType(subType);
			List<UploadFileEntity> uploadFileEntityList = getuploadFileEntityList(uploadfilemodel, programId, "withoutversionname");
			for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
				Map<String, String> scriptMap = new HashMap<>();
				scriptMap.put("scriptName", uploadFileEntity.getFileName());
				scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
				scriptList.add(scriptMap);
			}

		} catch (Exception e) {
			logger.error(" getSmScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return scriptList;
	}

	@Override
	public boolean findByRuleName(String useCaseName, int customerId, String migrationType, int programId,
			String userRole, String subType) {
		UseCaseBuilderEntity dupCmdEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			if (userRole.equalsIgnoreCase(Constants.SUPER_ROLE_ADMIN)) {
				query.where(builder.and(builder.equal(root.get("useCaseName"), useCaseName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			} else {
				query.where(builder.and(builder.equal(root.get("customerId"), customerId),
						builder.equal(root.get("useCaseName"), useCaseName),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
						builder.equal(root.get("subType"), subType),
						builder.equal(root.get("migrationType"), migrationType)));
			}

			TypedQuery<UseCaseBuilderEntity> queryResult = entityManager.createQuery(query);
			dupCmdEntity = (UseCaseBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

			if (dupCmdEntity != null) {
				status = true;
			}
		} catch (Exception e) {
			logger.error("Exception findByRuleName() in UseCaseBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}
	
	
	@Override
	public UseCaseBuilderEntity getUseCaseByName(String useCaseName, String migrationType, int programId, String subType) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			query.where(builder.and(builder.equal(root.get("useCaseName"), useCaseName),
					builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity),
					builder.equal(root.get("subType"), subType),
					builder.equal(root.get("migrationType"), migrationType)));

			TypedQuery<UseCaseBuilderEntity> queryResult = entityManager.createQuery(query);
			useCaseBuilderEntity = (UseCaseBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

		} catch (Exception e) {
			logger.error("Exception getUseCaseByName() in UseCaseBuilderRepositoryImpl :"+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntity;
	}

	@Override
	public UseCaseBuilderEntity findByExecutionSequence(String executionSequence, int programId, String userRole) {
		UseCaseBuilderEntity dupCmdEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = builder.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			if (userRole.equalsIgnoreCase(Constants.SUPER_ROLE_ADMIN)) {
				query.where(builder.and(builder.equal(root.get("ExecutionSequence"), executionSequence),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			} else {
				query.where(builder.and(builder.equal(root.get("ExecutionSequence"), executionSequence),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			}

			TypedQuery<UseCaseBuilderEntity> queryResult = entityManager.createQuery(query);
			dupCmdEntity = (UseCaseBuilderEntity) queryResult.getResultList().stream().findFirst().orElse(null);

			//if (dupCmdEntity != null) {
			//	status = true;
			//}
		} catch (Exception e) {
			logger.error("Exception findByExecutionSequence() in UseCaseBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return dupCmdEntity;
	}
	
	@Override
	public UseCaseBuilderParamEntity findScriptExecutionSequence(String executionSequence, int programId) {
		UseCaseBuilderParamEntity dupCmdEntity = null;
		boolean status = false;
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> query = builder.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> root = query.from(UseCaseBuilderParamEntity.class);
			query.select(root);
			
			query.where(builder.and(builder.equal(root.get("executionSequence"), executionSequence),
						builder.equal(root.get("customerDetailsEntity"), customerDetailsEntity)));
			

			TypedQuery<UseCaseBuilderParamEntity> queryResult = entityManager.createQuery(query);
			dupCmdEntity = (UseCaseBuilderParamEntity) queryResult.getResultList().stream().findFirst().orElse(null);

			//if (dupCmdEntity != null) {
			//	status = true;
			//}
		} catch (Exception e) {
			logger.error("Exception findByExecutionSequence() in UseCaseBuilderRepositoryImpl :"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return dupCmdEntity;
	}

	@Override
	public Map<String, List<Map<String, String>>> getScriptListWithoutSM(int programId, String migrationType,
			String subType) {
		List<NeVersionEntity> neVersionIdList = null;
		Map<String, List<Map<String, String>>> neMap = new HashMap<>();
		try {
			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);

			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("programDetailsEntity"), customerDetailsEntity));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			neVersionIdList = (List<NeVersionEntity>) crTypedQuery.getResultList();

			for (NeVersionEntity neVersionEntity : neVersionIdList) {
				/*
				 * CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				 * CriteriaQuery<NetworkConfigEntity> query =
				 * cb.createQuery(NetworkConfigEntity.class); Root<NetworkConfigEntity> root =
				 * query.from(NetworkConfigEntity.class);
				 * query.select(root).where(cb.equal(root.get("neVersionEntity"),
				 * neVersionEntity)); TypedQuery<NetworkConfigEntity> typedQuery =
				 * entityManager.createQuery(query); List<NetworkConfigEntity>
				 * networkConfigEntityList = typedQuery.getResultList();
				 * 
				 * 
				 * for (NetworkConfigEntity networkConfigEntity : networkConfigEntityList) {
				 * CriteriaBuilder scriptCb = entityManager.getCriteriaBuilder();
				 * CriteriaQuery<UploadFileEntity> scriptQuery =
				 * scriptCb.createQuery(UploadFileEntity.class); Root<UploadFileEntity>
				 * scriptRoot = scriptQuery.from(UploadFileEntity.class);
				 * scriptQuery.select(scriptRoot)
				 * //.where(scriptCb.and(scriptCb.equal(scriptRoot.get("neListEntity"),
				 * networkConfigEntity),
				 * .where(scriptCb.and(scriptCb.equal(scriptRoot.get("neVersion"),
				 * networkConfigEntity.getNeVersionEntity()),
				 * scriptCb.equal(scriptRoot.get("migrationType"), migrationType)));
				 * TypedQuery<UploadFileEntity> scriptTypedQuery =
				 * entityManager.createQuery(scriptQuery); List<UploadFileEntity>
				 * uploadFileEntityList = scriptTypedQuery.getResultList();
				 */
				/*
				List<Map<String, String>> scriptList = new LinkedList<>();
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<UploadFileEntity> query = cb.createQuery(UploadFileEntity.class);
				Root<UploadFileEntity> root = query.from(UploadFileEntity.class);
				query.select(root).where(
						cb.and(cb.or(cb.equal(root.get("neVersion"), neVersionEntity),
								cb.isNull(root.get("neVersion")))),
						cb.isNull(root.get("neListEntity")), cb.equal(root.get("migrationType"), migrationType),
						cb.equal(root.get("subType"), subType));

				TypedQuery<UploadFileEntity> typedQuery = entityManager.createQuery(query);
				List<UploadFileEntity> uploadFileEntityList = typedQuery.getResultList();
				*/
				UploadFileModel uploadfilemodel = new UploadFileModel();
				uploadfilemodel.setMigrationType(migrationType);
				uploadfilemodel.setSubType(subType);
				uploadfilemodel.setNeVersion(neVersionEntity);
				List<UploadFileEntity> uploadFileEntityList = getuploadFileEntityList(uploadfilemodel, programId, "withoutsmversion");
				List<Map<String, String>> scriptList = new LinkedList<>();
				for (UploadFileEntity uploadFileEntity : uploadFileEntityList) {
					Map<String, String> scriptMap = new HashMap<>();
					scriptMap.put("scriptName", uploadFileEntity.getFileName());
					scriptMap.put("scriptFileId", String.valueOf(uploadFileEntity.getId()));
					scriptList.add(scriptMap);
				}

				// }
				neMap.put(neVersionEntity.getNeVersion(), scriptList);
			}
		} catch (Exception e) {
			logger.error(" getSmScriptList() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return neMap;
	}

	@Override
	public NetworkConfigEntity getLsmEntity(String lsmVersion) {
		NetworkConfigEntity networkConfigEntity = null;
		try {
			CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
			CriteriaQuery<NeVersionEntity> crQuery = criteria.createQuery(NeVersionEntity.class);
			Root<NeVersionEntity> crRoot = crQuery.from(NeVersionEntity.class);
			crQuery.select(crRoot).where(criteria.equal(crRoot.get("neVersion"), lsmVersion));
			TypedQuery<NeVersionEntity> crTypedQuery = entityManager.createQuery(crQuery);
			NeVersionEntity neVersionEntity = crTypedQuery.getSingleResult();

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<NetworkConfigEntity> query = cb.createQuery(NetworkConfigEntity.class);
			Root<NetworkConfigEntity> root = query.from(NetworkConfigEntity.class);
			query.select(root).where(cb.equal(root.get("neVersionEntity"), neVersionEntity));

			TypedQuery<NetworkConfigEntity> typedQuery = entityManager.createQuery(query);
			networkConfigEntity = typedQuery.getResultList().get(0);

		} catch (Exception e) {
			logger.error(" getLsmEntity() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return networkConfigEntity;
	}

	/**
	 * this method will return UserDetailsEntity
	 * 
	 * @param articleId
	 * @return UserDetailsEntity
	 */
	public UseCaseBuilderParamEntity getEntity(int articleId) {
		return entityManager.find(UseCaseBuilderParamEntity.class, articleId);
	}

	/*
	 * public UseCaseCmdRuleEntity getCmdEntity(int articleId) { return
	 * entityManager.find(UseCaseCmdRuleEntity.class, articleId); }
	 */

	@Override
	public List<UseCaseCmdRuleEntity> getCmdEntity(int id) {
		List<UseCaseCmdRuleEntity> useCaseCmdRuleEntity = null;
		try {
			// int id = Integer.valueOf(scriptFileId);

			UseCaseBuilderParamEntity useCaseBuilderParamEntity = entityManager.find(UseCaseBuilderParamEntity.class,
					id);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseCmdRuleEntity> query = cb.createQuery(UseCaseCmdRuleEntity.class);
			Root<UseCaseCmdRuleEntity> root = query.from(UseCaseCmdRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseCmdRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseCmdRuleEntity = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getUploadFileEntityByScriptId() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseCmdRuleEntity;
	}
	

	@Override
	public List<UseCaseShellRuleEntity> getShellEntity(int id) {
		List<UseCaseShellRuleEntity> useCaseShellRuleEntity = null;
		try {
			// int id = Integer.valueOf(scriptFileId);

			UseCaseBuilderParamEntity useCaseBuilderParamEntity = entityManager.find(UseCaseBuilderParamEntity.class,
					id);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseShellRuleEntity> query = cb.createQuery(UseCaseShellRuleEntity.class);
			Root<UseCaseShellRuleEntity> root = query.from(UseCaseShellRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderParamEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseShellRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseShellRuleEntity = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getUploadFileEntityByScriptId() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseShellRuleEntity;
	}

	@Override
	public List<UseCaseXmlRuleEntity> getXmlEntity(int id) {
		List<UseCaseXmlRuleEntity> useCaseXmlRuleEntity = null;
		try {
			// int id = Integer.valueOf(scriptFileId);

			UseCaseBuilderParamEntity useCaseBuilderParamEntity = entityManager.find(UseCaseBuilderParamEntity.class,
					id);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseXmlRuleEntity> query = cb.createQuery(UseCaseXmlRuleEntity.class);
			Root<UseCaseXmlRuleEntity> root = query.from(UseCaseXmlRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderScriptsEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseXmlRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseXmlRuleEntity = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getUploadFileEntityByScriptId() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseXmlRuleEntity;
	}

	@Override
	public List<UseCaseFileRuleEntity> getFileEntity(int id) {
		List<UseCaseFileRuleEntity> useCaseFileRuleEntity = null;
		try {
			// int id = Integer.valueOf(scriptFileId);

			UseCaseBuilderParamEntity useCaseBuilderParamEntity = entityManager.find(UseCaseBuilderParamEntity.class,
					id);

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseFileRuleEntity> query = cb.createQuery(UseCaseFileRuleEntity.class);
			Root<UseCaseFileRuleEntity> root = query.from(UseCaseFileRuleEntity.class);
			query.select(root).where(cb.equal(root.get("useCaseBuilderScriptsEntity"), useCaseBuilderParamEntity));

			TypedQuery<UseCaseFileRuleEntity> typedQuery = entityManager.createQuery(query);
			useCaseFileRuleEntity = typedQuery.getResultList();
		} catch (Exception e) {
			logger.error(" getUploadFileEntityByScriptId() : " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseFileRuleEntity;
	}

	@Override
	public Object getPageCount(int page, int count, int customerId, String migrationType, int programId, String subType,
			User user) {
		List<UseCaseBuilderEntity> result =null;
		long pageCount = 0;
		double results = 0;
		int pagecount=0;
		try {
			UserRoleDetailsEntity userRoleDetailsEntity = entityManager.find(UserRoleDetailsEntity.class,
					user.getRoleId());

			CustomerDetailsEntity customerDetailsEntity = entityManager.find(CustomerDetailsEntity.class, programId);
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(UseCaseBuilderEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
		
			criteria.add(conjunction);	

			if (!user.getRole().equalsIgnoreCase(userRoleDetailsEntity.getRole())) {
				conjunction.add(Restrictions.eq("customerId", customerId));
			}

			if (StringUtils.isNotEmpty(subType)) {
				conjunction.add(Restrictions.eq("migrationType", migrationType));
				conjunction.add(Restrictions.eq("customerDetailsEntity", customerDetailsEntity));
				conjunction.add(Restrictions.eq("subType", subType));
			} else {
				conjunction.add(Restrictions.eq("migrationType", migrationType));
				conjunction.add(Restrictions.eq("customerDetailsEntity", customerDetailsEntity));
				
			}
			criteria.add(conjunction);
			criteria.addOrder(Order.desc("useCaseCreationDate"));
			result = criteria.setResultTransformer(org.hibernate.Criteria.DISTINCT_ROOT_ENTITY).list();
			pageCount = result.size();
			double size = pageCount;
			results = Math.ceil(size / count);
			pagecount = (int) results;
		}catch (Exception e) {
			logger.error("Exception getPageCount() in UserDetailsRepositoryImpl :"+ ExceptionUtils.getFullStackTrace(e));
		}
		return pagecount;
	}
	
	@Override
	public UseCaseBuilderEntity getUseCaseBuilderEntity(String useCaseName) {
		UseCaseBuilderEntity useCaseBuilderEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("useCaseName"), useCaseName));

			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntity = typedQuery.getSingleResult();

		} catch (Exception e) {
			//logger.error(
					//"Exception findCommand() in CmdRuleBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntity;
	}
	
	@Override
	public UseCaseBuilderParamEntity getUseCaseBuilderParam(int useCaseId,int uploadId) {
		UseCaseBuilderParamEntity useCaseBuilderParamEntity = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderParamEntity> query = cb.createQuery(UseCaseBuilderParamEntity.class);
			Root<UseCaseBuilderParamEntity> root = query.from(UseCaseBuilderParamEntity.class);

			query.select(root);
			query.where(cb.equal(root.get("useCaseBuilderEntity"), useCaseId),
					cb.equal(root.get("scriptsDetails"), uploadId));

			TypedQuery<UseCaseBuilderParamEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderParamEntity = typedQuery.getSingleResult();

		} catch (Exception e) {
			//logger.error(
					//"Exception findCommand() in CmdRuleBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderParamEntity;
	}
	
	@Override
	public boolean createUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity) {
		boolean status = false;
		try {
			entityManager.persist(useCaseBuilderEntity);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	@Override
	public List<UseCaseBuilderEntity> getUseCaseBuilderEntityList(int programId) {
		List<UseCaseBuilderEntity> useCaseBuilderEntityList = null;
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<UseCaseBuilderEntity> query = cb.createQuery(UseCaseBuilderEntity.class);
			Root<UseCaseBuilderEntity> root = query.from(UseCaseBuilderEntity.class);
			query.select(root);
			query.where(cb.equal(root.get("customerDetailsEntity"), programId));

			TypedQuery<UseCaseBuilderEntity> typedQuery = entityManager.createQuery(query);
			useCaseBuilderEntityList = (List<UseCaseBuilderEntity>) typedQuery.getResultList();

		} catch (Exception e) {
			logger.error("Exception getUseCaseBuilderEntityList() in UseCaseBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return useCaseBuilderEntityList;
	}

	@Override
	public boolean saveUseCaseBuilder(UseCaseBuilderEntity useCaseBuilderEntity) {
		boolean status = false;
		try {
			entityManager.persist(useCaseBuilderEntity);
			status = true;

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;	
		}
	
	@Override
	public boolean saveparamEntity(UseCaseBuilderParamEntity useCaseBuilderParamEntity) {
		boolean status = false;
		try {
			entityManager.persist(useCaseBuilderParamEntity);
			status = true;
		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}	
		return status;	
	}
	
	@Override
	public void saveCmdRuleEntity(UseCaseCmdRuleEntity useCaseCmdRuleEntity) {
		try {
			entityManager.persist(useCaseCmdRuleEntity);

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}		
	}
	@Override

	public void saveCaseShell(UseCaseShellRuleEntity useCaseShellRuleEntity) {
		try {
			entityManager.persist(useCaseShellRuleEntity);

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}		
	}

	@Override
	public void saveCaseXml(UseCaseXmlRuleEntity useCaseXmlRuleEntity) {
		try {
			entityManager.persist(useCaseXmlRuleEntity);

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}		
	}
	
	@Override
	public void saveCaseFile(UseCaseFileRuleEntity useCaseFileRuleEntity) {
		try {
			entityManager.persist(useCaseFileRuleEntity);

		} catch (Exception e) {
			logger.error("Exception  createUploadScript() in  UploadFileRepositoryImpl:"
					+ ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}		
	}


	@Override
	public List<RunTestEntity> getRunTestDetails(String useCaseName) {
		List<RunTestEntity> runTestEntity = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(RunTestEntity.class);
			Conjunction conjunction = Restrictions.conjunction();
			Criterion usecase = Restrictions.ilike("useCaseDetails",
					useCaseName, MatchMode.ANYWHERE);
			conjunction.add(usecase);
			criteria.add(conjunction);
			runTestEntity =  criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			logger.error("Exception getUseCaseBuilderEntityList() in UseCaseBuilderRepositoryImpl :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return runTestEntity;
	}

	@Override
	public void deleteruntestResult(int id) {
	try {
		Query query = entityManager.createQuery("DELETE from RunTestResultEntity WHERE useCaseBuilderEntity.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	} catch (Exception e) {
		logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
	} finally {
		entityManager.flush();
		entityManager.clear();
	}
	}

	


}
