package com.smart.rct.migration.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.migration.dto.UseCaseBuilderDto;
import com.smart.rct.migration.entity.UseCaseBuilderEntity;
import com.smart.rct.migration.entity.XmlElementEntity;
import com.smart.rct.migration.entity.XmlRootEntity;
import com.smart.rct.migration.entity.XmlRuleBuilderEntity;
import com.smart.rct.migration.model.XmlElementModel;
import com.smart.rct.migration.model.XmlRootModel;
import com.smart.rct.migration.model.XmlRuleBuilderModel;
import com.smart.rct.migration.model.XmlSerachModel;
import com.smart.rct.migration.repository.CmdRuleBuilderRepository;
import com.smart.rct.migration.repository.XmlRuleBuilderRepository;
import com.smart.rct.migration.service.XmlRuleBuilderService;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.PaginationModel;

@Service
public class XmlRuleBuilderServiceImpl implements XmlRuleBuilderService {

	static final  Logger logger = LoggerFactory.getLogger(XmlRuleBuilderServiceImpl.class);

	@Autowired
	XmlRuleBuilderRepository xmlRuleBuilderRepository;

	@Autowired
	CmdRuleBuilderRepository cmdRuleBuilderRepository;

	@Autowired
	UseCaseBuilderDto useCaseBuilderDto;

	@Override
	public boolean createXmlRuleBuilder(XmlRuleBuilderModel xmlRuleBuilderModel, String migrationType, int programId,
			String subType, String sessionId, int customerId) {
		boolean createXmlRuleStatus = false;
		XmlRuleBuilderEntity xmlRuleBuilderEntity = null;
		try {

			xmlRuleBuilderEntity = new XmlRuleBuilderEntity();
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			xmlRuleBuilderEntity.setCreatedBy(user.getUserName());
			xmlRuleBuilderEntity.setRootName(xmlRuleBuilderModel.getRootName());
			xmlRuleBuilderEntity.setRuleName(xmlRuleBuilderModel.getRuleName());
			xmlRuleBuilderEntity.setSubRootName(xmlRuleBuilderModel.getSubRootName());
			xmlRuleBuilderEntity.setCustomerDetailsEntity(customerDetailsEntity);
			xmlRuleBuilderEntity.setRemarks(xmlRuleBuilderModel.getRemarks());
			xmlRuleBuilderEntity.setUseCount(0);
			xmlRuleBuilderEntity.setCreationDate(new Date());
			xmlRuleBuilderEntity.setCustomerId(customerId);
			xmlRuleBuilderEntity.setMigrationType(migrationType);
			xmlRuleBuilderEntity.setSubType(subType);
			xmlRuleBuilderEntity.setLoopType(xmlRuleBuilderModel.getLoopType());
			xmlRuleBuilderEntity.setCmdName(xmlRuleBuilderModel.getCmdName());
			xmlRuleBuilderEntity.setPrompt(xmlRuleBuilderModel.getPrompt());
			xmlRuleBuilderEntity.setStatus(xmlRuleBuilderModel.getStatus());

			List<XmlRootModel> xmlRootModelList = xmlRuleBuilderModel.getRootDetails();
			List<XmlElementModel> xmlElementModelList = xmlRuleBuilderModel.getElementDetails();
			createXmlRuleStatus = xmlRuleBuilderRepository.createXmlRuleBuilder(xmlRuleBuilderEntity, xmlRootModelList,
					xmlElementModelList, programId);

		} catch (Exception ex) {
			logger.error(" createXmlRuleBuilder() : " + ExceptionUtils.getFullStackTrace(ex));

		}

		return createXmlRuleStatus;
	}

	@Override
	public Map<String,Object> loadXmlRuleBuilderSearchDetails(PaginationModel paginationModel,
			XmlSerachModel searchModel, int programId, String migrationType, String subType, User user, int customerId) {
		Map<String,Object> xmlRuleBuilderEntityList =null;
		try {
			 xmlRuleBuilderEntityList = xmlRuleBuilderRepository
					.loadXmlRuleBuilderSearchDetails(Integer.parseInt(paginationModel.getPage()),
							Integer.parseInt(paginationModel.getCount()), searchModel, migrationType, programId,
							subType, user, customerId);
			
		} catch (Exception e) {
			logger.error(" loadXmlRuleBuilderSearchDetails service : " + ExceptionUtils.getFullStackTrace(e));
		}
		return xmlRuleBuilderEntityList;
	}

	@Override
	public  Map<String, Object> loadXmlRuleBuilderDetails(PaginationModel paginationModel, int programId,
			String migrationType, String subType, User user, int customerId) {
		 Map<String, Object> xmlRuleBuilderEntityList =  null;
		try {
			xmlRuleBuilderEntityList = xmlRuleBuilderRepository.loadXmlRuleBuilderDetails(
					Integer.parseInt(paginationModel.getPage()), Integer.parseInt(paginationModel.getCount()),
					migrationType, programId, subType, user, customerId);
		} catch (Exception e) {
			logger.error(" loadUseCaseBuilderDetails service : " + ExceptionUtils.getFullStackTrace(e));
		}
		return xmlRuleBuilderEntityList;
	}

	@Override
	public boolean updateXmlRuleBuilder(XmlRuleBuilderModel xmlRuleBuilderModel, String migrationType, int programId,
			String subType, String sessionId) {
		boolean updateXmlRuleStatus = false;
		XmlRuleBuilderEntity xmlRuleBuilderEntity = null;
		ArrayList<Integer> elementId = new ArrayList();
		ArrayList<Integer> rootId = new ArrayList();
		try {
			//Delete root details
			List<XmlRootEntity> xmlRootEntityList = xmlRuleBuilderRepository.getXmlRootEntity(Integer.parseInt(xmlRuleBuilderModel.getId()));
			for(XmlRootEntity XmlRootEntity:xmlRootEntityList) {
				rootId.add(XmlRootEntity.getId());
			}
			
			List<XmlRootModel> iXmlRootModelList = xmlRuleBuilderModel.getRootDetails();
			for (XmlRootModel xmlRootModel : iXmlRootModelList) {
				if (xmlRootModel.getRootId() != null && !xmlRootModel.getRootId().trim().isEmpty()) {
					if (rootId.contains(Integer.parseInt(xmlRootModel.getRootId()))) {
						int location = rootId.indexOf(Integer.parseInt(xmlRootModel.getRootId()));
						rootId.remove(location);
					} else {
						// do nothing
					}
				}
			}
			
			for(int xmlRootId : rootId) {
				
				xmlRuleBuilderRepository.deleteXmlRootById(xmlRootId);
			}
			
			//Delete Element details
			List<XmlElementEntity> xmlElementEntityList = xmlRuleBuilderRepository.getXmlElementEntity(Integer.parseInt(xmlRuleBuilderModel.getId()));
			for(XmlElementEntity XmlElementEntity:xmlElementEntityList) {
				elementId.add(XmlElementEntity.getId());
			}
			
			List<XmlElementModel> iXmlElementModelList = xmlRuleBuilderModel.getElementDetails();
			for (XmlElementModel xmlElementModel : iXmlElementModelList) {
				if (xmlElementModel.getElementId() != null && !xmlElementModel.getElementId().trim().isEmpty()) {
					if (elementId.contains(Integer.parseInt(xmlElementModel.getElementId()))) {
						int location = elementId.indexOf(Integer.parseInt(xmlElementModel.getElementId()));
						elementId.remove(location);
					} else {
						// do nothing
					}
				}
			}
			
			for(int xmlElementId : elementId) {
				xmlRuleBuilderRepository.deleteXmlElementById(xmlElementId);
			}
			
			
			xmlRuleBuilderEntity = xmlRuleBuilderRepository
					.getXmlRuleBuilderEntity(Integer.parseInt(xmlRuleBuilderModel.getId()));
			CustomerDetailsEntity customerDetailsEntity = cmdRuleBuilderRepository.getCustomerDetailsEntity(programId);
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			xmlRuleBuilderEntity.setCreatedBy(user.getUserName());
			xmlRuleBuilderEntity.setRootName(xmlRuleBuilderModel.getRootName());
			xmlRuleBuilderEntity.setRuleName(xmlRuleBuilderModel.getRuleName());
			xmlRuleBuilderEntity.setSubRootName(xmlRuleBuilderModel.getSubRootName());
			xmlRuleBuilderEntity.setCustomerDetailsEntity(customerDetailsEntity);
			xmlRuleBuilderEntity.setRemarks(xmlRuleBuilderModel.getRemarks());
			xmlRuleBuilderEntity.setUseCount(0);
			xmlRuleBuilderEntity.setCreationDate(new Date());
			xmlRuleBuilderEntity.setMigrationType(migrationType);
			xmlRuleBuilderEntity.setSubType(subType);
			xmlRuleBuilderEntity.setLoopType(xmlRuleBuilderModel.getLoopType());
			xmlRuleBuilderEntity.setCmdName(xmlRuleBuilderModel.getCmdName());
			xmlRuleBuilderEntity.setPrompt(xmlRuleBuilderModel.getPrompt());
			xmlRuleBuilderEntity.setStatus(xmlRuleBuilderModel.getStatus());
			
			
			List<XmlRootModel> xmlRootModelList = xmlRuleBuilderModel.getRootDetails();
			List<XmlElementModel> xmlElementModelList = xmlRuleBuilderModel.getElementDetails();
			updateXmlRuleStatus = xmlRuleBuilderRepository.updateXmlRuleBuilder(xmlRuleBuilderEntity, xmlRootModelList,
					xmlElementModelList, programId);
		} catch (Exception ex) {
			logger.error(" createUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(ex));
		}
		return updateXmlRuleStatus;
	}

	@Override
	public boolean deleteXmlRuleBuilder(int xmlRuleBuilderId) {
		boolean deleteXmlRuleStatus = false;
		try {
			deleteXmlRuleStatus = xmlRuleBuilderRepository.deleteXmlRuleBuilder(xmlRuleBuilderId);
		} catch (Exception e) {
			logger.error(" deleteUseCaseBuilder() : " + ExceptionUtils.getFullStackTrace(e));
		}
		return deleteXmlRuleStatus;
	}
	
	@Override
	public boolean duplicateFileName(String ruleName, int customerId, String migrationType, int programId,
			String userRole,String subType) {
		boolean status = false;
		try {
			status = xmlRuleBuilderRepository.findByRuleName(ruleName, customerId, migrationType, programId, userRole,subType);
		} catch (Exception e) {
			logger.error("Exception duplicateFileName: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<XmlRuleBuilderEntity> getXmlRuleBuilderEntityList(int programId) {
		List<XmlRuleBuilderEntity> xmlRuleBuilderEntityList = null;
		try {
			xmlRuleBuilderEntityList = xmlRuleBuilderRepository.getXmlRuleBuilderEntityList(programId);
		} catch (Exception e) {
			logger.info("Exception in getXmlRuleBuilderEntityList() in XmlRuleBuilderServiceImpl"+ ExceptionUtils.getFullStackTrace(e));
		}
		return xmlRuleBuilderEntityList;
	}

	@Override
	public XmlRuleBuilderEntity findByRuleName(int programId, String ruleName) {
		XmlRuleBuilderEntity xmlRuleBuilderEntity = null;
		try {
			xmlRuleBuilderEntity = xmlRuleBuilderRepository.findByRuleName(programId, ruleName);
		} catch (Exception e) {
			logger.info("Exception in findByRuleName() in XmlRuleBuilderServiceImpl"+ ExceptionUtils.getFullStackTrace(e));
		}
		return xmlRuleBuilderEntity;
	}

}
