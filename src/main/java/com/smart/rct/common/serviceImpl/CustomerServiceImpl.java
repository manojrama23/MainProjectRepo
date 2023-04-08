package com.smart.rct.common.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.entity.GrowConstantsEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.SnrGeneralEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.DependentRuleValidationModel;
import com.smart.rct.common.models.KeyValuesModel;
import com.smart.rct.common.models.MileStonesModel;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.common.repository.CustomerRepository;
import com.smart.rct.common.service.CustomerService;
import com.smart.rct.constants.Constants;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.entity.NeConfigTypeEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.premigration.repository.FileUploadRepository;
import com.smart.rct.premigration.service.CheckListScriptService;
import com.smart.rct.premigration.service.NeMappingService;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@Service
public class CustomerServiceImpl implements CustomerService {

	final static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	FileUploadRepository fileUploadRepository;
	
	@Autowired
	CheckListScriptService checkListScriptService;
	
	@Autowired
	NeMappingService neMappingService;

	/**
	 * This api get the CustomerList
	 * 
	 * @param addAllRecord
	 * @return List<CustomerEntity>
	 */
	@Override
	public List<CustomerEntity> getCustomerList(boolean addAllRecord, boolean addInActiveCustomers) {
		List<CustomerEntity> customerEntityList = null;
		try {
			customerEntityList = customerRepository.getCustomerList(addAllRecord, addInActiveCustomers);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getCustomerList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return customerEntityList;
	}

	/**
	 * This api get the CustomerList by customerId
	 * 
	 * @return CustomerEntity
	 */
	@Override
	public CustomerEntity getCustomerById(Integer customerId) {
		CustomerEntity customerEntity = null;
		try {
			customerEntity = customerRepository.getCustomerById(customerId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getCustomerById(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return customerEntity;
	}
	
	public boolean addTemplateForProgram(CustomerDetailsEntity customerDetailsEntity, String templateName, String configType){
		boolean status = false;
		try{
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			programTemplateModel.setProgramDetailsEntity(customerDetailsEntity);
			programTemplateModel.setLabel(templateName);
			if (!duplicateProgaramTemplate(programTemplateModel)) {
				ProgramTemplateEntity objProgramTemplateEntity = new ProgramTemplateEntity();
				objProgramTemplateEntity.setProgramDetailsEntity(customerDetailsEntity);
				objProgramTemplateEntity.setLabel(templateName);
				objProgramTemplateEntity.setValue("");
				objProgramTemplateEntity.setConfigType(configType);
				customerRepository.saveProgramTemplate(objProgramTemplateEntity);
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.addTemplateForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	
	public boolean addGenerateFileInfoForProgram(CustomerDetailsEntity customerDetailsEntity){
		boolean status = false;
		try{
			if (!duplicateProgramGenerateFileDetails(customerDetailsEntity.getId())) {
				ProgramGenerateFileEntity programGenerateFileEntity = new ProgramGenerateFileEntity();
				programGenerateFileEntity.setProgramDetailsEntity(customerDetailsEntity);
				programGenerateFileEntity.setCsv(Constants.ACTIVE);
				programGenerateFileEntity.setEndc(Constants.INACTIVE);
				programGenerateFileEntity.setAll(Constants.INACTIVE);
				programGenerateFileEntity.setEnv(Constants.ACTIVE);
				programGenerateFileEntity.setCommissionScript(Constants.ACTIVE);
				customerRepository.saveProgramGenerateFileEntity(programGenerateFileEntity);
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.addGenerateFileInfoForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	
	public boolean copyGroConstantsForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			List<GrowConstantsEntity> growConstantsEntities = fileUploadRepository.getGrowConstantsDetails();
			growConstantsEntities = growConstantsEntities.stream().filter(X -> X.getProgramDetailsEntity().getId().equals(customerDetailIdToCopy)).collect(Collectors.toList());
			if(CommonUtil.isValidObject(growConstantsEntities) && growConstantsEntities.size() > 0){
				for(GrowConstantsEntity growConstantsEntity: growConstantsEntities){
					GrowConstantsEntity entity = new GrowConstantsEntity();
					entity.setProgramDetailsEntity(customerDetailsEntity);
					entity.setLabel(growConstantsEntity.getLabel());
					entity.setValue(growConstantsEntity.getValue());
					fileUploadRepository.saveGrowConstant(entity);
				}
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyGroConstantsForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	public boolean copyTemplatesForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			ProgramTemplateModel programTemplateModel = new ProgramTemplateModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(customerDetailIdToCopy);
			programTemplateModel.setProgramDetailsEntity(programDetailsEntity);
			List<ProgramTemplateEntity> entities = customerRepository.getProgTemplateDetails(programTemplateModel);
			if(CommonUtil.isValidObject(entities) && entities.size() > 0){
				for(ProgramTemplateEntity programTemplateEntity: entities){
					ProgramTemplateEntity objProgramTemplateEntity = new ProgramTemplateEntity();
					objProgramTemplateEntity.setProgramDetailsEntity(customerDetailsEntity);
					objProgramTemplateEntity.setLabel(programTemplateEntity.getLabel());
					objProgramTemplateEntity.setValue(programTemplateEntity.getValue());
					objProgramTemplateEntity.setConfigType(programTemplateEntity.getConfigType());
					customerRepository.saveProgramTemplate(objProgramTemplateEntity);
				}
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyTemplatesForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	public boolean copyGenerateFileInfoForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			List<ProgramGenerateFileEntity> programGenerateFileDetails = customerRepository.getProgramGenerateFileDetails(customerDetailIdToCopy);
			if(CommonUtil.isValidObject(programGenerateFileDetails) && programGenerateFileDetails.size() > 0){
				for(ProgramGenerateFileEntity programGenerateFileEntity: programGenerateFileDetails){
					ProgramGenerateFileEntity entity = new ProgramGenerateFileEntity();
					entity.setProgramDetailsEntity(customerDetailsEntity);
					entity.setEndc(programGenerateFileEntity.getAll());
					entity.setAll(programGenerateFileEntity.getEndc());
					entity.setCsv(programGenerateFileEntity.getCsv());
					entity.setEnv(programGenerateFileEntity.getEnv());
					entity.setCommissionScript(programGenerateFileEntity.getCommissionScript());
					customerRepository.saveProgramGenerateFileEntity(entity);
				}
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyGenerateFileInfoForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	public boolean copyCheckListScriptInfoForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			
			CheckListScriptDetModel checkListScriptDetModel = new CheckListScriptDetModel();
			CustomerDetailsEntity programDetailsEntity = new CustomerDetailsEntity();
			programDetailsEntity.setId(customerDetailIdToCopy);
			checkListScriptDetModel.setProgramDetailsEntity(programDetailsEntity);
			List<CheckListScriptDetEntity> oldList = checkListScriptService.getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);
			
			for(CheckListScriptDetEntity checkListScriptDetEntity: oldList){
				CheckListScriptDetEntity scriptDetEntity = new CheckListScriptDetEntity();
				scriptDetEntity.setCheckListFileName(checkListScriptDetEntity.getCheckListFileName());
				scriptDetEntity.setProgramDetailsEntity(customerDetailsEntity);
				scriptDetEntity.setSheetName(checkListScriptDetEntity.getSheetName());
				scriptDetEntity.setStepIndex(checkListScriptDetEntity.getStepIndex());
				scriptDetEntity.setScriptName(checkListScriptDetEntity.getScriptName());
				scriptDetEntity.setScriptExeSeq(checkListScriptDetEntity.getScriptExeSeq());
				scriptDetEntity.setCreatedBy(checkListScriptDetEntity.getCreatedBy());
				scriptDetEntity.setCreationDate(checkListScriptDetEntity.getCreationDate());
				checkListScriptService.saveCheckListBasedScriptExecutionDetails(scriptDetEntity);
			}
			
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyCheckListScriptInfoForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	/*public boolean copyCiqValidationRulesForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyCiqValidationRulesForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}*/
	
	public boolean copyNeConfigTypeInfoForProgram(CustomerDetailsEntity customerDetailsEntity, Integer customerDetailIdToCopy){
		boolean status = false;
		try{
			List<NeConfigTypeEntity> neConfigTypeDetailsList = neMappingService.getNeConfigTypeDetails(customerDetailIdToCopy);
			if(neConfigTypeDetailsList != null && neConfigTypeDetailsList.size() > 0){
				for(NeConfigTypeEntity neConfigTypeEntity: neConfigTypeDetailsList){
					NeConfigTypeEntity entity = new NeConfigTypeEntity();
					entity.setProgramDetailsEntity(customerDetailsEntity);
					entity.setNwConfigType(neConfigTypeEntity.getNwConfigType());
					neMappingService.saveNeConfigType(entity);
				}
			}
			status = true;
		}catch(Exception e){
			logger.error("Exception in CustomerServiceImpl.copyNeConfigTypeInfoForProgram(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	/**
	 * This api saves customer and its details
	 * 
	 * @param customerEntity
	 * @return CustomerEntity
	 */
	@Override
	public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws RctException {
		CustomerEntity entity = null;
		try {
			Set<String> newPrograms = customerEntity.getCustomerDetails().stream()
					.filter(objCustomerDetailsEntity -> (!CommonUtil.isValidObject(objCustomerDetailsEntity.getId())))
					.map(x -> x.getProgramName()).collect(Collectors.toSet());
			entity = customerRepository.saveCustomer(customerEntity);
			if (entity != null && entity.getCustomerDetails() != null && entity.getCustomerDetails().size() > 0) {
				List<CustomerDetailsEntity> objList = entity.getCustomerDetails();
				for (CustomerDetailsEntity objCustomerDetailsEntity : objList) {
					if (StringUtils.isNotEmpty(objCustomerDetailsEntity.getProgramName())) {
						if(newPrograms.contains(objCustomerDetailsEntity.getProgramName()) && objCustomerDetailsEntity.getSourceProgramId()!=null && objCustomerDetailsEntity.getSourceprogramName()!=null && objCustomerDetailsEntity.getSourceProgramId() > 0){ 
							logger.info("CustomerServiceImpl.saveCustomer() copying info from source program: "+objCustomerDetailsEntity.getSourceprogramName()+" to destination program: "+objCustomerDetailsEntity.getProgramName());
							copyGroConstantsForProgram(objCustomerDetailsEntity, objCustomerDetailsEntity.getSourceProgramId());
							copyTemplatesForProgram(objCustomerDetailsEntity, objCustomerDetailsEntity.getSourceProgramId());
							copyGenerateFileInfoForProgram(objCustomerDetailsEntity, objCustomerDetailsEntity.getSourceProgramId());
							copyNeConfigTypeInfoForProgram(objCustomerDetailsEntity, objCustomerDetailsEntity.getSourceProgramId());
						}
						// For Program Template Saving
						addTemplateForProgram(objCustomerDetailsEntity,Constants.CIQ_VALIDATE_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.CHECK_LIST_VALIDATE_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.ENB_MENU_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.PARAMETERS_VALIDATE_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SCRIPT_STORE_TEMPLATE,Constants.CONFIG_TYPE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.PROMPT_TEMPLATE,Constants.CONFIG_TYPE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.CIQ_FILE_PATH,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.CIQ_NAME_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SCRIPT_FILE_PATH,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SCRIPT_NAME_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SCRIPT_NAME_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SITE_DETAILS_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.RELEASE_VERSION_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						//new
						addTemplateForProgram(objCustomerDetailsEntity,Constants.NE_STATUS_CONFIG_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						if(objCustomerDetailsEntity.getProgramName().contains("5G-DSS") || objCustomerDetailsEntity.getProgramName().contains("5G-CBAND")
								|| objCustomerDetailsEntity.getProgramName().contains("4G-USM-LIVE")|| objCustomerDetailsEntity.getProgramName().contains("5G-MM")
								|| objCustomerDetailsEntity.getProgramName().contains("4G-FSU")) {
							addTemplateForProgram(objCustomerDetailsEntity,Constants.SEQUENCE_NUMBER_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
							addTemplateForProgram(objCustomerDetailsEntity,Constants.POST_MIGRATION_MILESTONE,Constants.CONFIG_TYPE_PRE_MIGRATION);
							addTemplateForProgram(objCustomerDetailsEntity,Constants.MIGRATION_MILESTONE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						}
						/////For S&R
						addTemplateForProgram(objCustomerDetailsEntity,Constants.SITE_REPORT_INPUTS_TEMPLATE,Constants.CONFIG_TYPE_PRE_MIGRATION);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.MAIL_CONFIGURATION,Constants.CONFIG_TYPE_S_R);
						// For S&R Template Saving
						addTemplateForProgram(objCustomerDetailsEntity,Constants.FETCH_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.PREMIGRATION_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						
						if(objCustomerDetailsEntity.getProgramName().contains("4G-USM-LIVE"))
						{
							addTemplateForProgram(objCustomerDetailsEntity,Constants.SUPPORT_CA,Constants.CONFIG_TYPE_S_R);
						}
						//addTemplateForProgram(objCustomerDetailsEntity,Constants.ENV_EXPORT_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.NE_GROW_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.MIGRATION_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.POST_MIGRATION_AUDIT_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.POST_MIGRATION_RANATP_SCHEDULE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.NE_GROW_AUTOMATION,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.FETCH_FROM_RFDB,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.FETCH_DATE,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.MIGRATION_USECASES,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.NE_GROW_USECASES,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.KEY_VALUES,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.POST_MIG_USECASES,Constants.CONFIG_TYPE_S_R);
						addTemplateForProgram(objCustomerDetailsEntity,Constants.MILESTONES,Constants.CONFIG_TYPE_S_R);
						addGenerateFileInfoForProgram(objCustomerDetailsEntity);
					}
				}
			}
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			}
		} catch (JpaSystemException e) {
			if (e.getRootCause() instanceof org.hibernate.TransactionException) {
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			}
		} catch (Exception e) {

			// TODO: handle exception
			if (e.getCause() instanceof RctException) {
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.PROGRAM_NAME_ALREADY_USED));
			}
			logger.error("Exception in CustomerServiceImpl.saveCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		}

		return entity;
	}

	/**
	 * This api delete the customer and its details
	 * 
	 * @param customerDetails
	 * @return boolean
	 */
	@Override
	public boolean deleteCustomer(Integer customerId) {
		boolean status = false;
		try {
			status = customerRepository.deleteCustomer(customerId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.deleteCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	/**
	 * This api In Activate the customer and its details
	 * 
	 * @param customerDetails
	 * @return boolean
	 */
	@Override
	public boolean inActivateCustomer(Integer customerId) {
		boolean status = false;
		try {
			status = customerRepository.inActivateCustomer(customerId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.inActivateCustomer(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will deleteCustomerDetails
	 * 
	 * @param customerDetailId
	 * @return boolean
	 */
	@Override
	public boolean deleteCustomerDetails(Integer customerDetailId) throws RctException {
		boolean status = false;
		try {
			status = customerRepository.deleteCustomerDetails(customerDetailId);
		} catch (Exception e) {
			if (e instanceof RctException) {
				throw new RctException(e.getMessage());
			}
			logger.error(
					"Exception in CustomerServiceImpl.deleteCustomerDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getCustomerByName
	 * 
	 * @param customerName
	 * @return CustomerEntity
	 */
	@Override
	public CustomerEntity getCustomerByName(String customerName) {
		CustomerEntity entity = null;
		try {
			entity = customerRepository.getCustomerByName(customerName);
		} catch (Exception e) {
			logger.error(
					"Exception in CustomerServiceImpl.getCustomerByName(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return entity;
	}

	/**
	 * This api will getProgramList
	 * 
	 * @param customerId
	 * @return List<String>
	 */
	@Override
	public List<String> getProgramList(Integer customerId) {
		List<String> programList = null;
		try {
			programList = customerRepository.getProgramList(customerId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getProgramList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return programList;
	}

	/**
	 * This api will saveOvTemplate
	 * 
	 * @param objProgramTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveOvTemplate(OvGeneralEntity objOvGeneralEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			status = customerRepository.saveOvTemplate(objOvGeneralEntity);
		} catch (Exception e) {
			status = false;
			// TODO: handle exception
			logger.error(
					"Exception in CustomerServiceImpl.saveOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@Override
	public boolean saveSnrTemplate(SnrGeneralEntity objSnrGeneralEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			status = customerRepository.saveSnrTemplate(objSnrGeneralEntity);
		} catch (Exception e) {
			status = false;
			// TODO: handle exception
			logger.error(
					"Exception in CustomerServiceImpl.saveOvTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	/**
	 * This api will saveProgramTemplate
	 * 
	 * @param objProgramTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveProgramTemplate(ProgramTemplateEntity objProgramTemplateEntity) {
		// TODO Auto-generated method stub
		boolean status = false;
		try {
			status = customerRepository.saveProgramTemplate(objProgramTemplateEntity);
		} catch (Exception e) {
			status = false;
			// TODO: handle exception
			logger.error(
					"Exception in CustomerServiceImpl.saveProgramTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will duplicateProgaramTemplate
	 * 
	 * @param programTemplateModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateProgaramTemplate(ProgramTemplateModel programTemplateModel) {
		boolean status = false;
		try {
			status = customerRepository.duplicateProgaramTemplate(programTemplateModel);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in CustomerServiceImpl.duplicateProgaramTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getProgTemplateDetails
	 * 
	 * @param configList,objCustList
	 * @return List<ProgramTemplateModel>
	 */
	@Override
	public List<ProgramTemplateModel> getProgTemplateDetails(List<ProgramTemplateModel> configList,
			List<CustomerDetailsEntity> objCustList, String configType) {
		List<ProgramTemplateEntity> objList = null;
		try {
			objList = customerRepository.getProgramTemplate(configType);
			if (objCustList != null && objCustList.size() > 0) {
				Set<Integer> programIdList = objCustList.stream().map(x -> x.getId()).collect(Collectors.toSet());
				objList = objList.stream().filter(x -> (programIdList.contains(x.getProgramDetailsEntity().getId())))
						.collect(Collectors.toList());
			}
			if (objList != null && objList.size() > 0) {
				ProgramTemplateModel configDetailModel = new ProgramTemplateModel();
				for (ProgramTemplateEntity templateEntity : objList) {
					configDetailModel = new ProgramTemplateModel();
					configDetailModel.setId(templateEntity.getId());
					configDetailModel.setProgramDetailsEntity(templateEntity.getProgramDetailsEntity());
					configDetailModel.setLabel(templateEntity.getLabel());
					configDetailModel.setValue(templateEntity.getValue());
					configDetailModel.setType("PROGRAM TEMPLATE");
					configDetailModel.setConfigType(templateEntity.getConfigType());
					if(StringUtils.isNotEmpty(templateEntity.getConfigType()) && "s&r".equalsIgnoreCase(templateEntity.getConfigType()))
					{
						if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.FETCH_SCHEDULE.equalsIgnoreCase(templateEntity.getLabel()))
						{
							configDetailModel.setInputType("time");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.FETCH_FROM_RFDB.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.MAIL_CONFIGURATION.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.NE_GROW_AUTOMATION.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.FETCH_DATE.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.MIGRATION_USECASES.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.NE_GROW_USECASES.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.KEY_VALUES.equalsIgnoreCase(templateEntity.getLabel())) {
								configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.POST_MIG_USECASES.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.SUPPORT_CA.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}else if(StringUtils.isNotEmpty(templateEntity.getLabel()) && Constants.MILESTONES.equalsIgnoreCase(templateEntity.getLabel())) {
							configDetailModel.setInputType("");
						}
						else {
							configDetailModel.setInputType("dropdown");
						}
					}
						configList.add(configDetailModel);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.getProgTemplateDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return configList;
	}
	
	/**
	 * This api will getProgTemplateDetails
	 * 
	 * @param configList,objCustList
	 * @return List<ProgramTemplateModel>
	 */
	@Override
	public List<ProgramTemplateModel> getOvTemplateDetails(List<ProgramTemplateModel> configList, String configType) {
		List<OvGeneralEntity> objList = null;
		try {
			objList = customerRepository.getOvTemplate(configType);
			
			if (objList != null && objList.size() > 0) {
				ProgramTemplateModel configDetailModel = new ProgramTemplateModel();
				for (OvGeneralEntity templateEntity : objList) {
					configDetailModel = new ProgramTemplateModel();
					configDetailModel.setId(templateEntity.getId());
					configDetailModel.setLabel(templateEntity.getLabel());
					configDetailModel.setValue(templateEntity.getValue());
					configDetailModel.setType("GENERAL OV");
					configDetailModel.setConfigType(templateEntity.getConfigType());
					configList.add(configDetailModel);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.getOvTemplateDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return configList;
	}

	/**
	 * This api will getCustomerDetailsList
	 * 
	 * @param customerDetailsModel
	 * @return List<CustomerDetailsEntity>
	 */
	@Override
	public List<CustomerDetailsEntity> getCustomerDetailsList(CustomerDetailsModel customerDetailsModel) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			customerDetailsEntities = customerRepository.getCustomerDetailsList(customerDetailsModel);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getCustomerDetailsList(): "+ ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntities;
	}

	/**
	 * This api will getProgramDetailsList
	 * 
	 * @param user
	 * @return List<CustomerDetailsEntity>
	 */
	@Override
	public List<CustomerDetailsEntity> getProgramDetailsList(User user) {
		List<CustomerDetailsEntity> customerDetailsEntities = null;
		try {
			customerDetailsEntities = customerRepository.getProgramDetailsList(user);
		} catch (Exception e) {
			logger.error(
					"Exception in CustomerServiceImpl.getProgramDetailsList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntities;
	}

	/**
	 * This api will getProgTemplateDetails
	 * 
	 * @param programTemplateModel
	 * @return List<ProgramTemplateEntity>
	 */
	@Override
	public List<ProgramTemplateEntity> getProgTemplateDetails(ProgramTemplateModel programTemplateModel) {
		List<ProgramTemplateEntity> programTemplateEntities = null;
		try {
			programTemplateEntities = customerRepository.getProgTemplateDetails(programTemplateModel);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getProgTemplateDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return programTemplateEntities;
	}

	/**
	 * This api will duplicateProgramGenerateFileDetails
	 * 
	 * @param customerDetailId
	 * @return boolean
	 */
	@Override
	public boolean duplicateProgramGenerateFileDetails(Integer customerDetailId) {
		boolean status = false;
		try {
			status = customerRepository.duplicateProgramGenerateFileDetails(customerDetailId);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in CustomerServiceImpl.duplicateProgramGenerateFileDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will saveProgramGenerateFileEntity
	 * 
	 * @param programGenerateFileEntity
	 * @return boolean
	 */
	@Override
	public boolean saveProgramGenerateFileEntity(ProgramGenerateFileEntity programGenerateFileEntity) {
		boolean status = false;
		try {
			status = customerRepository.saveProgramGenerateFileEntity(programGenerateFileEntity);
		} catch (Exception e) {
			status = false;
			logger.error("Exception in CustomerServiceImpl.saveProgramGenerateFileEntity(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getProgramGenerateFileDetails
	 * 
	 * @param programId
	 * @return List<ProgramGenerateFileEntity>
	 */
	@Override
	public List<ProgramGenerateFileEntity> getProgramGenerateFileDetails(Integer programId) {
		List<ProgramGenerateFileEntity> programGenerateFileEntities = null;
		try {
			programGenerateFileEntities = customerRepository.getProgramGenerateFileDetails(programId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getProgramGenerateFileDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return programGenerateFileEntities;
	}

	/**
	 * This api will deleteProgramTemplate
	 * 
	 * @param programTemplateId
	 * @return boolean
	 */
	@Override
	public boolean deleteProgramTemplate(Integer programTemplateId) {
		boolean status = false;
		try {
			status = customerRepository.deleteProgramTemplate(programTemplateId);
		} catch (Exception e) {
			logger.error(
					"Exception in CustomerServiceImpl.deleteProgramTemplate(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will deleteProgramGenerateFileEntity
	 * 
	 * @param programTemplateId
	 * @return boolean
	 */
	@Override
	public boolean deleteProgramGenerateFileEntity(Integer programTemplateId) {
		boolean status = false;
		try {
			status = customerRepository.deleteProgramGenerateFileEntity(programTemplateId);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.deleteProgramGenerateFileEntity(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getComboBoxList
	 * 
	 * @param configList,custList
	 * @return List<ProgramTemplateModel>
	 */
	@Override
	public List<ProgramTemplateModel> getComboBoxList(List<ProgramTemplateModel> configList,
			List<CustomerEntity> custList) {
		List<SchedulingReportsTemplateEntity> objList = null;
		try {
			objList = customerRepository.getComboBoxList();
			if (custList != null && custList.size() > 0) {
				Set<Integer> custIdList = custList.stream().map(x -> x.getId()).collect(Collectors.toSet());
				objList = objList.stream().filter(x -> (custIdList.contains(x.getCustomerEntity().getId())))
						.collect(Collectors.toList());
			}
			if (objList != null && objList.size() > 0) {
				ProgramTemplateModel configDetailModel = new ProgramTemplateModel();
				for (SchedulingReportsTemplateEntity templateEntity : objList) {
					configDetailModel = new ProgramTemplateModel();
					configDetailModel.setId(templateEntity.getId());
					configDetailModel.setCustomerEntity(templateEntity.getCustomerEntity());
					configDetailModel.setLabel(templateEntity.getLabel());
					configDetailModel.setValue(templateEntity.getValue());
					configDetailModel.setType("S & R");
					configDetailModel.setConfigType(Constants.CONFIG_TYPE_S_R);
					configList.add(configDetailModel);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getProgTemplateDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return configList;
	}

	/**
	 * This api will saveSchedulingTemplate
	 * 
	 * @param schedulingReportsTemplateEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSchedulingTemplate(SchedulingReportsTemplateEntity schedulingReportsTemplateEntity) {
		boolean status = false;
		try {
			status = customerRepository.saveSchedulingTemplate(schedulingReportsTemplateEntity);
		} catch (Exception e) {
			status = false;
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.saveSchedulingTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public List<CustomerDetailsEntity> getAllProgramList(CustomerDetailsModel customerDetailsModel) {
		List<CustomerDetailsEntity> programList = null;
		try {
			programList = customerRepository.getAllProgramList(customerDetailsModel);
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getAllProgramList(): "+ ExceptionUtils.getFullStackTrace(e));
		}
		return programList;
	}
	
	
	/**
	 * This api will getProgTemplateDetails
	 * 
	 * @return OvInteractionModel
	 */
	@Override
	public OvInteractionModel getOvInteractionTemplate() {
		OvInteractionModel ovInteractionModel =null;
		try {
			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_INTRACTION);
			
			if (objOvGeneralEntity != null && StringUtils.isNotEmpty(objOvGeneralEntity.getValue())) {
				ObjectMapper mapper = new ObjectMapper();
				
				JsonObject objData = CommonUtil.parseRequestDataToJson(objOvGeneralEntity.getValue());

				 ovInteractionModel = mapper
						.readValue(objData.get("ovInteraction").toString(), new TypeReference<OvInteractionModel>() {
						});
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.getOvInteractionTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return ovInteractionModel;
	}

@Override
	public KeyValuesModel getKeyValuesTemplate(ProgramTemplateModel programTemplateModel) {
		KeyValuesModel KeyValuesModel =null;
		
		try {
			ProgramTemplateEntity objOvGeneralEntity = customerRepository.getKeyValuesTemplate( programTemplateModel,Constants.KEY_VALUES);

				
			if (objOvGeneralEntity != null && StringUtils.isNotEmpty(objOvGeneralEntity.getValue())) {
				ObjectMapper mapper = new ObjectMapper();
				
				JsonObject objData = CommonUtil.parseRequestDataToJson(objOvGeneralEntity.getValue());

				KeyValuesModel = mapper.readValue(objData.get("KeyValues").toString(), new TypeReference<KeyValuesModel>() {
						});
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.getOvInteractionTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return KeyValuesModel;
	}
	

 @Override
 public MileStonesModel getMileStonesTemplate(ProgramTemplateModel programTemplateModel) {
	 MileStonesModel MileStonesModel =null;
	
	try {
		ProgramTemplateEntity objOvGeneralEntity = customerRepository.getMileStonesTemplate( programTemplateModel,Constants.MILESTONES);
		if (objOvGeneralEntity != null && StringUtils.isNotEmpty(objOvGeneralEntity.getValue())) {
			ObjectMapper mapper = new ObjectMapper();
			JsonObject objData = CommonUtil.parseRequestDataToJson(objOvGeneralEntity.getValue());
			MileStonesModel = mapper.readValue(objData.get("MileStones").toString(), new TypeReference<MileStonesModel>() {
				
			});
		}
	} catch (Exception e) {
		// TODO: handle exception
		logger.error("Exception in CustomerServiceImpl.getMileStonesTemplate(): "
				+ ExceptionUtils.getFullStackTrace(e));
	}
	return MileStonesModel;
}
 
	@Override
	public OvAutomationModel getOvAutomationTemplate() {
		OvAutomationModel ovAutomationModel =null;
		try {
			OvGeneralEntity objOvGeneralEntity = customerRepository.getOvlabelTemplate(Constants.OV_AUTOMATION);
			
			if (objOvGeneralEntity != null && StringUtils.isNotEmpty(objOvGeneralEntity.getValue())) {
				ObjectMapper mapper = new ObjectMapper();
				
				JsonObject objData = CommonUtil.parseRequestDataToJson(objOvGeneralEntity.getValue());

				ovAutomationModel = mapper
						.readValue(objData.get("automation").toString(), new TypeReference<OvAutomationModel>() {
						});
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception in CustomerServiceImpl.getOvInteractionTemplate(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return ovAutomationModel;
	}

	@Override
	public List<ProgramTemplateModel> getSnrConfigList(List<ProgramTemplateModel> configList) {
		List<SnrGeneralEntity> objList = null;
		try {
			objList = customerRepository.getSnrGeneralConfigList();
			if (objList != null && !objList.isEmpty()) {
				ProgramTemplateModel configDetailModel;
				for (SnrGeneralEntity templateEntity : objList) {
					configDetailModel = new ProgramTemplateModel();
					configDetailModel.setId(templateEntity.getId());
					configDetailModel.setLabel(templateEntity.getLabel());
					configDetailModel.setValue(templateEntity.getValue());
					configDetailModel.setType("GENERAL");
					configDetailModel.setConfigType(Constants.CONFIG_TYPE_GENERAL);
					configList.add(configDetailModel);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in CustomerServiceImpl.getDuoGeneralConfigList(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return configList;
	}
	
}
