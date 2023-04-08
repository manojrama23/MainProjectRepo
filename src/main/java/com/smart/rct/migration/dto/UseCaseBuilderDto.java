package com.smart.rct.migration.dto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.smart.rct.constants.Constants;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.UseCaseBuilderParamEntity;
import com.smart.rct.migration.entity.UseCaseCmdRuleEntity;
import com.smart.rct.migration.entity.UseCaseFileRuleEntity;
import com.smart.rct.migration.entity.UseCaseShellRuleEntity;
import com.smart.rct.migration.entity.UseCaseXmlRuleEntity;
import com.smart.rct.migration.entity.XmlElementEntity;
import com.smart.rct.migration.entity.XmlRootEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.CmdRuleModel;
import com.smart.rct.migration.model.FileRuleModel;
import com.smart.rct.migration.model.ShellRuleModel;
import com.smart.rct.migration.model.UseCaseBuilderModel;
import com.smart.rct.migration.model.UseCaseScriptsModel;
import com.smart.rct.migration.model.XmlElementModel;
import com.smart.rct.migration.model.XmlRootModel;
import com.smart.rct.migration.model.XmlRuleBuilderModel;
import com.smart.rct.migration.model.XmlRuleModel;

@Component
public class UseCaseBuilderDto {

	public List<UseCaseBuilderModel> convertUseCaseBuilderEntityToModel(
			List<UseCaseBuilderEntity> useCaseBuilderEntityList) {
		List<UseCaseBuilderModel> useCaseBuilderModelList = new ArrayList<>();
		LinkedHashSet<UseCaseBuilderParamEntity> useCaseBuilderParamEntitySet;
		for (UseCaseBuilderEntity useCaseBuilderEntity : useCaseBuilderEntityList) {
			List<UseCaseScriptsModel> scriptList = new ArrayList<>();
			UseCaseBuilderModel useCaseBuilderModel = new UseCaseBuilderModel();
			useCaseBuilderModel.setId(String.valueOf(useCaseBuilderEntity.getId()));
			if(useCaseBuilderEntity.getNetworkConfigEntity()!=null) {
				useCaseBuilderModel.setLsmName(useCaseBuilderEntity.getNetworkConfigEntity().getNeName());
				useCaseBuilderModel
						.setLsmVersion(useCaseBuilderEntity.getNetworkConfigEntity().getNeVersionEntity().getNeVersion());
			}else if(useCaseBuilderEntity.getNeVersion()!=null) {
				useCaseBuilderModel.setLsmVersion(useCaseBuilderEntity.getNeVersion().getNeVersion());
			}
			useCaseBuilderModel.setUseCaseName(useCaseBuilderEntity.getUseCaseName());
			useCaseBuilderModel.setRemarks(useCaseBuilderEntity.getRemarks());
			useCaseBuilderModel.setExecutionSequence(String.valueOf(useCaseBuilderEntity.getExecutionSequence()));
			useCaseBuilderModel.setUseCount(useCaseBuilderEntity.getUseCount());
			useCaseBuilderModel.setCustomerId(useCaseBuilderEntity.getCustomerId());
			useCaseBuilderModel.setMigrationType(useCaseBuilderEntity.getMigrationType());
			useCaseBuilderModel.setCreatedBy(useCaseBuilderEntity.getCreatedBy());
			Date date = useCaseBuilderEntity.getUseCaseCreationDate();
			String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
			useCaseBuilderModel.setTimeStamp(dateFormat);
			useCaseBuilderParamEntitySet = useCaseBuilderEntity.getUseCaseBuilderParamEntity().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
			for (UseCaseBuilderParamEntity useCaseBuilderParamEntity : useCaseBuilderParamEntitySet) {
				List<CmdRuleModel> useCaseCMDList = new ArrayList<>();
				List<ShellRuleModel> useCaseShellList = new ArrayList<>();
				List<XmlRuleModel> useCaseXmlList = new ArrayList<>();
				List<FileRuleModel> fileRuleModelList = new ArrayList<>();
				UseCaseScriptsModel useCaseScriptsModel = new UseCaseScriptsModel();
				useCaseScriptsModel.setScriptId(String.valueOf(useCaseBuilderParamEntity.getId()));
				useCaseScriptsModel.setScriptName(useCaseBuilderParamEntity.getScriptsDetails().getFileName());
				useCaseScriptsModel
						.setScriptSequence(String.valueOf(useCaseBuilderParamEntity.getExecutionSequence()));
				useCaseScriptsModel.setScriptRemarks(useCaseBuilderParamEntity.getScriptRemarks());
				useCaseScriptsModel.setFilePath(useCaseBuilderParamEntity.getScriptsDetails().getFilePath()+useCaseBuilderEntity.getUseCaseName().trim().replaceAll(" ", "_") + "/");
				
				LinkedHashSet<UseCaseCmdRuleEntity> useCaseCmdRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseCmdRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
				for (UseCaseCmdRuleEntity useCaseCmdRuleEntity : useCaseCmdRuleEntitySet) {
					Map<String, String> cmdRuleMap = new HashMap<>();
					CmdRuleModel cmdRuleModel = new CmdRuleModel();

					String cmdName = useCaseCmdRuleEntity.getCmdRuleBuilderEntity().getRuleName();
					String cmdId = String.valueOf(useCaseCmdRuleEntity.getId());
					String cmdSequence = String.valueOf(useCaseCmdRuleEntity.getCommandRuleSequence());
					String cmdRemarks = useCaseCmdRuleEntity.getCmdRemarks();
					String cmdRuleBuilderId = String.valueOf(useCaseCmdRuleEntity.getCmdRuleBuilderEntity().getId());
					cmdRuleMap.put("cmdId", cmdId);
					cmdRuleMap.put("cmdName", cmdName);
					cmdRuleMap.put("cmdSequence", cmdSequence);
					cmdRuleMap.put("cmdRemarks", cmdRemarks);
					cmdRuleMap.put("cmdRuleBuilderId", cmdRuleBuilderId);
					cmdRuleModel.setCmdId(cmdId);
					cmdRuleModel.setCmdSequence(cmdSequence);
					cmdRuleModel.setCmdRemarks(cmdRemarks);
					Map<String, String> cmdMap = new HashMap<>();
					cmdMap.put("cmdName", cmdName);
					cmdMap.put("cmdRuleBuilderId", cmdRuleBuilderId);
					cmdRuleModel.setCmdDetails(cmdMap);
					useCaseCMDList.add(cmdRuleModel);
				}
				
				LinkedHashSet<UseCaseShellRuleEntity> useCaseShellRuleEntitySet = useCaseBuilderParamEntity.getUseCaseShellRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
				for (UseCaseShellRuleEntity useCaseShellRuleEntity : useCaseShellRuleEntitySet) {
					Map<String, String> shellRuleMap = new HashMap<>();
					ShellRuleModel shellRuleModel = new ShellRuleModel();

					String shellRuleName = useCaseShellRuleEntity.getShellRuleBuilderEntity().getRuleName();
					String shellRuleId = String.valueOf(useCaseShellRuleEntity.getId());
					String shellRuleSequence = String.valueOf(useCaseShellRuleEntity.getShellRuleSequence());
					String shellRuleRemarks = useCaseShellRuleEntity.getShellRemarks();
					String shellRuleBuilderId = String.valueOf(useCaseShellRuleEntity.getShellRuleBuilderEntity().getId());
					shellRuleMap.put("shellRuleId", shellRuleId);
					shellRuleMap.put("shellRuleName", shellRuleName);
					shellRuleMap.put("shellRuleSequence", shellRuleSequence);
					shellRuleMap.put("shellRuleRemarks", shellRuleRemarks);
					shellRuleMap.put("shellRuleBuilderId", shellRuleBuilderId);
					shellRuleModel.setShellRuleId(shellRuleId);
					shellRuleModel.setShellRuleSequence(shellRuleSequence);
					shellRuleModel.setShellRuleRemarks(shellRuleRemarks);
					Map<String, String> shellMap = new HashMap<>();
					shellMap.put("shellCmdName", shellRuleName);
					shellMap.put("shellRuleBuilderId", shellRuleBuilderId);
					shellRuleModel.setShellDetails(shellMap);
					useCaseShellList.add(shellRuleModel);
				}
				
				LinkedHashSet<UseCaseXmlRuleEntity> useCaseXmlRuleEntitySet = useCaseBuilderParamEntity.getUseCaseXmlRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
				for (UseCaseXmlRuleEntity useCaseXmlRuleEntity : useCaseXmlRuleEntitySet) {
					Map<String, String> xmlRuleMap = new HashMap<>();
					XmlRuleModel xmlRuleModel = new XmlRuleModel();

					String xmlName = useCaseXmlRuleEntity.getXmlRuleBuilderEntity().getRuleName();
					String xmlId = String.valueOf(useCaseXmlRuleEntity.getId());
					String xmlSequence = String.valueOf(useCaseXmlRuleEntity.getXmlRuleSequence());
					String xmlRemarks = useCaseXmlRuleEntity.getXmlRemarks();
					String xmlRuleBuilderId = String.valueOf(useCaseXmlRuleEntity.getXmlRuleBuilderEntity().getId());
					xmlRuleMap.put("xmlId", xmlId);
					xmlRuleMap.put("xmlName", xmlName);
					xmlRuleMap.put("xmlSequence", xmlSequence);
					xmlRuleMap.put("xmlRemarks", xmlRemarks);
					xmlRuleMap.put("xmlRuleBuilderId", xmlRuleBuilderId);
					xmlRuleModel.setXmlId(xmlId);
					xmlRuleModel.setXmlSequence(xmlSequence);
					xmlRuleModel.setXmlRemarks(xmlRemarks);
					Map<String, String> xmlMap = new HashMap<>();
					xmlMap.put("xmlName", xmlName);
					xmlMap.put("xmlRuleBuilderId", xmlRuleBuilderId);
					xmlRuleModel.setXmlDetails(xmlMap);
					useCaseXmlList.add(xmlRuleModel);
				}
				
				LinkedHashSet<UseCaseFileRuleEntity> useCaseFileRuleEntitySet = useCaseBuilderParamEntity
						.getUseCaseFileRuleEntitySet().parallelStream().sorted((p1, p2)->p1.getId().compareTo(p2.getId())).collect((Collectors.toCollection( LinkedHashSet::new ) ));;
				for (UseCaseFileRuleEntity useCaseFileRuleEntity : useCaseFileRuleEntitySet) {
					Map<String, String> fileRuleMap = new HashMap<>();
					FileRuleModel fileRuleModel = new FileRuleModel();

					String fileRuleName = useCaseFileRuleEntity.getFileRuleBuilderEntity().getRuleName();

					String fileRuleId = String.valueOf(useCaseFileRuleEntity.getId());
					String fileRuleSequence = String.valueOf(useCaseFileRuleEntity.getFileRuleSequence());
					String fileRuleRemarks = useCaseFileRuleEntity.getFileRemarks();
					String fileRuleBuilderId = String.valueOf(useCaseFileRuleEntity.getFileRuleBuilderEntity().getId());
					fileRuleMap.put("fileRuleId", fileRuleId);
					fileRuleMap.put("fileRuleName", fileRuleName);
					fileRuleMap.put("fileRuleSequence", fileRuleSequence);
					fileRuleMap.put("fileRuleRemarks", fileRuleRemarks);
					fileRuleMap.put("fileRuleBuilderId", fileRuleBuilderId);
					fileRuleModel.setFileRuleId(fileRuleId);
					fileRuleModel.setFileRuleSequence(fileRuleSequence);
					fileRuleModel.setFileRuleRemarks(fileRuleRemarks);
					Map<String, String> fileMap = new HashMap<>();
					fileMap.put("fileRuleName", fileRuleName);
					fileMap.put("fileRuleBuilderId", fileRuleBuilderId);
					fileRuleModel.setFileDetails(fileMap);
					fileRuleModelList.add(fileRuleModel);
				}
				useCaseScriptsModel.setCmdRules(useCaseCMDList);
				useCaseScriptsModel.setFileRules(fileRuleModelList);
				useCaseScriptsModel.setXmlRules(useCaseXmlList);
				useCaseScriptsModel.setShellRules(useCaseShellList);
				Map<String, String> scriptFileMap = new HashMap<>();
				scriptFileMap.put("scriptName", useCaseBuilderParamEntity.getScriptsDetails().getFileName());
				scriptFileMap.put("scriptFileId",
						String.valueOf(useCaseBuilderParamEntity.getScriptsDetails().getId()));
				useCaseScriptsModel.setScript(scriptFileMap);
				scriptList.add(useCaseScriptsModel);
			}
			useCaseBuilderModel.setScriptList(scriptList);
			useCaseBuilderModelList.add(useCaseBuilderModel);
		}

		return useCaseBuilderModelList;
	}

	public List<XmlRuleBuilderModel> convertXmlRuleBuilderEntityToModel(
			List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList) {

		List<XmlRuleBuilderModel> xmlRuleBuilderModelList = new ArrayList<>();

		for (XmlRuleBuilderEntity xmlRuleBuilderEntity : xmlRuleBuilderEntityList) {
			List<XmlRootModel> xmlRootModelList = new LinkedList<>();
			List<XmlElementModel> xmlElementModelList = new LinkedList<>();
			XmlRuleBuilderModel xmlRuleBuilderModel = new XmlRuleBuilderModel();
			xmlRuleBuilderModel.setId(String.valueOf(xmlRuleBuilderEntity.getId()));
			xmlRuleBuilderModel.setCreatedBy(xmlRuleBuilderEntity.getCreatedBy());
			xmlRuleBuilderModel.setRemarks(xmlRuleBuilderEntity.getRemarks());
			xmlRuleBuilderModel.setRuleName(xmlRuleBuilderEntity.getRuleName());
			xmlRuleBuilderModel.setRootName(xmlRuleBuilderEntity.getRootName());
			xmlRuleBuilderModel.setSubRootName(xmlRuleBuilderEntity.getSubRootName());
			xmlRuleBuilderModel.setLoopType(xmlRuleBuilderEntity.getLoopType());
			xmlRuleBuilderModel.setCmdName(xmlRuleBuilderEntity.getCmdName());
			xmlRuleBuilderModel.setPrompt(xmlRuleBuilderEntity.getPrompt());
			xmlRuleBuilderModel.setStatus(xmlRuleBuilderEntity.getStatus());
			
			Date date = xmlRuleBuilderEntity.getCreationDate();
			String dateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS).format(date);
			xmlRuleBuilderModel.setTimeStamp(dateFormat);

			Set<XmlRootEntity> xmlRootEntitySet = xmlRuleBuilderEntity.getXmlRootEntitySet();
			for (XmlRootEntity xmlRootEntity : xmlRootEntitySet) {
				XmlRootModel xmlRootModel = new XmlRootModel();
				xmlRootModel.setRootId(String.valueOf(xmlRootEntity.getId()));
				xmlRootModel.setRootKey(xmlRootEntity.getRootKey());
				xmlRootModel.setRootValue(xmlRootEntity.getRootValue());
				xmlRootModelList.add(xmlRootModel);
			}
			Set<XmlElementEntity> xmlElementEntitySet = xmlRuleBuilderEntity.getXmlElementEntitySet();
			for (XmlElementEntity xmlElementEntity : xmlElementEntitySet) {
				XmlElementModel xmlElementModel = new XmlElementModel();
				xmlElementModel.setElementId(String.valueOf(xmlElementEntity.getId()));
				xmlElementModel.setElementName(xmlElementEntity.getElementName());
				xmlElementModel.setElementValue(xmlElementEntity.getElementValue());
				xmlElementModel.setOperator(xmlElementEntity.getOperator());
				xmlElementModelList.add(xmlElementModel);
			}
			xmlRuleBuilderModel.setRootDetails(xmlRootModelList);
			xmlRuleBuilderModel.setElementDetails(xmlElementModelList);
			xmlRuleBuilderModelList.add(xmlRuleBuilderModel);
		}

		return xmlRuleBuilderModelList;
	}

}
