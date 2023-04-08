package com.smart.rct.migration.serviceImpl;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.smart.rct.common.entity.CustomerDetailsEntity;
import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.ShellCmdRuleBuilderEntity;
import com.smart.rct.migration.model.ShellCmdRuleBuilderModel;
import com.smart.rct.migration.repository.ShellCmdRuleBuilderRepository;
import com.smart.rct.migration.service.ShellCmdRuleBuilderService;
import com.smart.rct.usermanagement.models.User;

@Service
public class ShellCmdRuleBuilderServiceImpl implements ShellCmdRuleBuilderService {

	private static final Logger logger = LoggerFactory.getLogger(CmdRuleBuilderServiceImpl.class);
	@Autowired
	ShellCmdRuleBuilderRepository shellCmdRuleBuilderRepository;

	@Override
	public boolean createCmdRule(ShellCmdRuleBuilderEntity cmdRuleEntity) {
		boolean status = false;
		try {
			status = shellCmdRuleBuilderRepository.createCmdRule(cmdRuleEntity);
		} catch (Exception e) {
			logger.error("Exception createCmdRule: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * 
	 * this method will search the command rule builder
	 * 
	 * @param searchBy,searchParameter,page,count
	 * @return map
	 */

	@Override
	public Map<String, Object> searchCmdRule(int customerId, int page, int count, String migrationType, int programId,
			String subType, User user) {
		Map<String, Object> searchFileRule = null;
		try {
			searchFileRule = shellCmdRuleBuilderRepository.loadCmdRuleBuilderSearchDetails(customerId, page, count,
					migrationType, programId, subType, user);
		} catch (Exception e) {
			logger.error("Exception searchFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return searchFileRule;
	}

	/**
	 * this method will update command rule builder details
	 * 
	 * @param cmdRuleEntity
	 * @return boolean
	 */

	@Override
	public boolean updateCmdRuleBuilder(ShellCmdRuleBuilderEntity cmdRuleEntity) {
		boolean status = false;
		try {
			status = shellCmdRuleBuilderRepository.updateCmdRuleBuilder(cmdRuleEntity);
		} catch (Exception e) {
			logger.error("Exception updateCmdRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will delete the command rule builder Details.
	 * 
	 * @param id
	 * @return boolean
	 * @throws RctException
	 */
	@Override
	public boolean deleteCmdRule(int id) throws RctException {
		boolean status = false;
		try {
			status = shellCmdRuleBuilderRepository.deleteCmdRule(id);
		} catch (Exception e) {
			logger.error("Exception deleteFileRule: " + ExceptionUtils.getFullStackTrace(e));
			if (e instanceof DataIntegrityViolationException)
				throw new RctException(
						"Operation Failed : Command Rule Builder is already associated in other places.");
			else {
				throw new RctException("Operation Failed : Failed to Delete Command Rule Builder");
			}
		}
		return status;
	}

	/**
	 * this method will check the rule name is exist or not in command rule builder
	 * 
	 * @param ruleName
	 * @return String
	 */
	@Override
	public boolean duplicateFileName(String ruleName, String migrationType, int programId, String userRole,String subType) {
		boolean status = false;
		try {
			status = shellCmdRuleBuilderRepository.findByRuleName(ruleName, migrationType, programId, userRole,subType);
		} catch (Exception e) {
			logger.error("Exception duplicateFileName: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@Override
	public CustomerDetailsEntity getCustomerDetailsEntity(int customerDetailsId) {
		CustomerDetailsEntity customerDetailsEntity = null;
		try {
			customerDetailsEntity = shellCmdRuleBuilderRepository.getCustomerDetailsEntity(customerDetailsId);
		} catch (Exception e) {
			logger.error("Exception duplicateFileName: " + ExceptionUtils.getFullStackTrace(e));
		}
		return customerDetailsEntity;
	}

	@Override
	public Map<String, Object> searchCmdRule(ShellCmdRuleBuilderModel cmdRuleBuilderModel, int customerId, int page,
			int count, String migrationType, int programId, String subType, User user) {
		Map<String, Object> searchFileRule = null;
		try {
			searchFileRule = shellCmdRuleBuilderRepository.loadCmdRuleBuilderSearchDetails(cmdRuleBuilderModel, customerId,
					page, count, migrationType, programId, subType, user);
		} catch (Exception e) {
			logger.error("Exception searchFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return searchFileRule;
	}
}
