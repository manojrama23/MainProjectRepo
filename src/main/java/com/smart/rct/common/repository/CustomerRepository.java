package com.smart.rct.common.repository;

import java.util.List;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.entity.DuoGeneralConfigEntity;
import com.smart.rct.common.entity.OvGeneralEntity;
import com.smart.rct.common.entity.ProgramGenerateFileEntity;
import com.smart.rct.common.entity.ProgramTemplateEntity;
import com.smart.rct.common.entity.SchedulingReportsTemplateEntity;
import com.smart.rct.common.entity.SnrGeneralEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.ProgramTemplateModel;
import com.smart.rct.exception.RctException;
import com.smart.rct.usermanagement.entity.UserDetailsEntity;
import com.smart.rct.usermanagement.models.User;

public interface CustomerRepository {
	

	public List<CustomerEntity> getCustomerList(boolean addAllRecord, boolean addInActiveCustomers);

	public CustomerEntity getCustomerById(Integer customerId);

	public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws RctException;

	public boolean deleteCustomer(Integer customerId);
	
	public boolean inActivateCustomer(Integer customerId);

	public List<CustomerDetailsEntity> getCustomerDetails(Integer customerId);

	public boolean deleteCustomerDetails(Integer customerDetailId) throws RctException;

	public CustomerEntity getCustomerByName(String customerName);

	public boolean saveProgramTemplate(ProgramTemplateEntity objProgramTemplateEntity);

	public boolean duplicateProgaramTemplate(ProgramTemplateModel programTemplateModel);

	public List<ProgramTemplateEntity> getProgramTemplate(String configType);

	public List<String> getProgramList(Integer customerId);

	public List<CustomerDetailsEntity> getCustomerDetailsList(CustomerDetailsModel customerDetailsModel);

	public List<CustomerDetailsEntity> getProgramDetailsList(User user);

	public List<ProgramTemplateEntity> getProgTemplateDetails(ProgramTemplateModel programTemplateModel);

	public boolean saveProgramGenerateFileEntity(ProgramGenerateFileEntity programGenerateFileEntity);

	public boolean duplicateProgramGenerateFileDetails(Integer customerDetailId);

	public List<ProgramGenerateFileEntity> getProgramGenerateFileDetails(Integer programId);

	public boolean deleteProgramTemplate(Integer programTemplateId);

	public boolean deleteProgramGenerateFileEntity(Integer programTemplateId);

	public List<SchedulingReportsTemplateEntity> getComboBoxList();

	public boolean saveSchedulingTemplate(SchedulingReportsTemplateEntity schedulingReportsTemplateEntity);

	public List<CustomerDetailsEntity> getAllProgramList(CustomerDetailsModel customerDetailsModel);
	List<OvGeneralEntity> getOvTemplate(String configType);
	boolean saveOvTemplate(OvGeneralEntity objOvGeneralEntity);
	OvGeneralEntity getOvlabelTemplate(String label);

	List<SnrGeneralEntity> getSnrGeneralConfigList();
	ProgramTemplateEntity getKeyValuesTemplate(ProgramTemplateModel programTemplateModel, String label);
	
	ProgramTemplateEntity getMileStonesTemplate(ProgramTemplateModel programTemplateModel, String label);
	boolean saveSnrTemplate(SnrGeneralEntity objSnrGeneralEntity);
}
