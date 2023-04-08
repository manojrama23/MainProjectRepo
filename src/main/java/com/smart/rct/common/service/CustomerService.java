package com.smart.rct.common.service;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.SnrGeneralEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.KeyValuesModel;
import com.smart.rct.common.models.MileStonesModel;
import com.smart.rct.common.models.OvAutomationModel;
import com.smart.rct.common.models.OvInteractionModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.usermanagement.models.User;

public interface CustomerService {
	
	public List<CustomerEntity> getCustomerList(boolean addAllRecord, boolean addInActiveCustomers);

	public CustomerEntity getCustomerById(Integer customerId);

	public CustomerEntity getCustomerByName(String customerName);

	public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws RctException;

	public boolean deleteCustomer(Integer customerId);
	
	public boolean inActivateCustomer(Integer customerId);

	public boolean deleteCustomerDetails(Integer customerDetailId) throws RctException;;

	public boolean saveProgramTemplate(ProgramTemplateEntity objProgramTemplateEntity);

	public List<ProgramTemplateEntity> getProgTemplateDetails(ProgramTemplateModel programTemplateModel);

	public boolean duplicateProgaramTemplate(ProgramTemplateModel programTemplateModel);

	public List<String> getProgramList(Integer customerId);

	public List<CustomerDetailsEntity> getCustomerDetailsList(CustomerDetailsModel customerDetailsModel);

	public List<CustomerDetailsEntity> getProgramDetailsList(User user);

	public boolean saveProgramGenerateFileEntity(ProgramGenerateFileEntity programGenerateFileEntity);

	public List<ProgramGenerateFileEntity> getProgramGenerateFileDetails(Integer programId);

	public boolean deleteProgramTemplate(Integer programTemplateId);

	public boolean deleteProgramGenerateFileEntity(Integer programTemplateId);

	public boolean duplicateProgramGenerateFileDetails(Integer programTemplateId);

	public List<ProgramTemplateModel> getProgTemplateDetails(List<ProgramTemplateModel> configList,
			List<CustomerDetailsEntity> objCustList, String configType);

	public List<ProgramTemplateModel> getComboBoxList(List<ProgramTemplateModel> configList,
			List<CustomerEntity> custList);

	public boolean saveSchedulingTemplate(SchedulingReportsTemplateEntity schedulingReportsTemplateEntity);
	
	public List<CustomerDetailsEntity> getAllProgramList(CustomerDetailsModel customerDetailsModel);
	List<ProgramTemplateModel> getOvTemplateDetails(List<ProgramTemplateModel> configList, String configType);
	boolean saveOvTemplate(OvGeneralEntity objOvGeneralEntity);
	
	OvAutomationModel getOvAutomationTemplate();

	OvInteractionModel getOvInteractionTemplate();
	MileStonesModel getMileStonesTemplate(ProgramTemplateModel programTemplateModel);
	KeyValuesModel getKeyValuesTemplate(ProgramTemplateModel programTemplateModel);
	List<ProgramTemplateModel> getSnrConfigList(List<ProgramTemplateModel> configList);

	boolean saveSnrTemplate(SnrGeneralEntity objSnrGeneralEntity); 
}
