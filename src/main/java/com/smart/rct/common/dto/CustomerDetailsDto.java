package com.smart.rct.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.common.models.CustomerDetailsModel;
import com.smart.rct.common.models.CustomerModel;
import com.smart.rct.usermanagement.entity.UserSessionPool;
import com.smart.rct.usermanagement.models.User;
import com.smart.rct.util.CommonUtil;

@Component
public class CustomerDetailsDto {

	final static Logger logger = LoggerFactory.getLogger(CustomerDetailsDto.class);

	/**
	 * This method will set the CustomerEntity with values from customerModel.
	 * 
	 * @param customerModel
	 * @return customerEntity
	 */
	public static CustomerEntity getCustomerEntity(CustomerModel customerModel, String sessionId) {
		CustomerEntity customerEntity = null;
		try {
			customerEntity = new CustomerEntity();
			customerEntity.setId(customerModel.getId());
			customerEntity.setCustomerName(customerModel.getCustomerName());
			customerEntity.setStatus(customerModel.getStatus());
			customerEntity.setCustomerShortName(customerModel.getCustomerShortName());
			List<CustomerDetailsEntity> customerEntityDetails = new ArrayList<CustomerDetailsEntity>();
			CustomerDetailsEntity customerDetailsEntity = null;
			if(CommonUtil.isValidObject(customerModel.getCustomerDetails())){
				for (CustomerDetailsModel customerDetailsModel : customerModel.getCustomerDetails()) {
					customerDetailsEntity = getCustomerEntityDetails(customerDetailsModel, sessionId);
					customerDetailsEntity.setCustomerEntity(customerEntity);
					customerEntityDetails.add(customerDetailsEntity);
				}
			}
			customerEntity.setCustomerDetails(customerEntityDetails);
		} catch (Exception e) {
			logger.error("Excpetion in CustomerDetailsDto.getCustomerEntity(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return customerEntity;
	}

	/**
	 * This method will set the CustomerDetailsEntity with values from
	 * customerDetailsModel.
	 * 
	 * @param customerDetailsModel
	 * @return customerDetailsEntity
	 */
	private static CustomerDetailsEntity getCustomerEntityDetails(CustomerDetailsModel customerDetailsModel,
			String sessionId) {
		CustomerDetailsEntity customerDetailsEntity = new CustomerDetailsEntity();
		try {
			customerDetailsEntity.setId(customerDetailsModel.getId());
			customerDetailsEntity.setCustomerEntity(customerDetailsModel.getCustomerEntity());
			customerDetailsEntity.setNetworkTypeDetailsEntity(customerDetailsModel.getNetworkTypeDetailsEntity());
			customerDetailsEntity.setProgramName(customerDetailsModel.getProgramName());
			customerDetailsEntity.setProgramDescription(customerDetailsModel.getProgramDescription());
			customerDetailsEntity.setSourceProgramId(customerDetailsModel.getSourceProgramId());
			customerDetailsEntity.setSourceprogramName(customerDetailsModel.getSourceprogramName());
			customerDetailsEntity.setStatus(customerDetailsModel.getStatus());
			customerDetailsEntity.setCreationDate(new Date());
			User user = UserSessionPool.getInstance().getSessionUser(sessionId);
			customerDetailsEntity.setCreatedBy(user.getUserName());
		} catch (Exception e) {
			logger.error("Excpetion in CustomerDetailsDto.getCustomerEntityDetails(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return customerDetailsEntity;
	}

	/**
	 * This method will set the CustomerModel with values from customerEntity.
	 * 
	 * @param customerEntity
	 * @return customerModel
	 */
	public static CustomerModel getCustomerModel(CustomerEntity customerEntity, String sessionId) {
		CustomerModel customerModel = null;
		try {
			customerModel = new CustomerModel();
			customerModel.setId(customerEntity.getId());
			customerModel.setCustomerName(customerEntity.getCustomerName());
			customerModel.setStatus(customerEntity.getStatus());
			customerModel.setIconPath(customerEntity.getIconPath());
			customerModel.setCustomerShortName(customerEntity.getCustomerShortName());
			List<CustomerDetailsModel> customerDetailsModels = new ArrayList<CustomerDetailsModel>();
			CustomerDetailsModel customerDetailsModel = null;
			if(CommonUtil.isValidObject(customerEntity.getCustomerDetails())){
				for (CustomerDetailsEntity customerDetailsEntity : customerEntity.getCustomerDetails()) {
					customerDetailsModel = getCustomerModelDetails(customerDetailsEntity, sessionId);
					customerDetailsModel.setCustomerEntity(customerEntity);
					customerDetailsModels.add(customerDetailsModel);
				}
			}
			customerModel.setCustomerDetails(customerDetailsModels);
		} catch (Exception e) {
			logger.error("Excpetion in CustomerDetailsDto.getCustomerModel(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return customerModel;
	}

	/**
	 * This method will set the CustomerDetailsModel with values from
	 * customerDetailsEntity.
	 * 
	 * @param customerDetailsEntity
	 * @return customerDetailsModel
	 */
	private static CustomerDetailsModel getCustomerModelDetails(CustomerDetailsEntity customerDetailsEntity,
			String sessionId) {
		CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
		try {
			customerDetailsModel.setId(customerDetailsEntity.getId());
			customerDetailsModel.setCustomerEntity(customerDetailsEntity.getCustomerEntity());
			customerDetailsModel.setNetworkTypeDetailsEntity(customerDetailsEntity.getNetworkTypeDetailsEntity());
			customerDetailsModel.setProgramName(customerDetailsEntity.getProgramName());
			customerDetailsModel.setProgramDescription(customerDetailsEntity.getProgramDescription());
			customerDetailsModel.setStatus(customerDetailsEntity.getStatus());
			customerDetailsModel.setSourceProgramId(customerDetailsEntity.getSourceProgramId());
			customerDetailsModel.setSourceprogramName(customerDetailsEntity.getSourceprogramName());
			customerDetailsModel.setCreationDate(customerDetailsEntity.getCreationDate());
			customerDetailsModel.setCreatedBy(customerDetailsEntity.getCreatedBy());
		} catch (Exception e) {
			logger.error("Excpetion in CustomerDetailsDto.getCustomerModelDetails(): " + ExceptionUtils.getFullStackTrace(e));
			e.printStackTrace();
		}
		return customerDetailsModel;
	}

}
