package com.smart.rct.migration.serviceImpl;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smart.rct.exception.RctException;
import com.smart.rct.migration.entity.FileRuleBuilderEntity;
import com.smart.rct.migration.model.FileRuleBuilderModel;
import com.smart.rct.migration.repository.FileRuleBuilderRepository;
import com.smart.rct.migration.service.FileRuleBuilderService;
import com.smart.rct.usermanagement.models.User;

@Service
public class FileRuleBuilderServiceImpl implements FileRuleBuilderService {
	
	private static final Logger logger = LoggerFactory.getLogger(FileRuleBuilderServiceImpl.class);
	@Autowired
	FileRuleBuilderRepository fileRuleBuilderRepository;
	
	@PersistenceContext	
	private EntityManager entityManager;

	/**
	 * 
	 * this method will create the new file rule builder
	 * 
	 * @param fileRuleBuilderEntity
	 * @return boolean
	 */

	@Override
	public boolean createFileRuleBuilder(FileRuleBuilderEntity fileRuleBuilderEntity) {
		boolean status = false;
		try
		{
			status = fileRuleBuilderRepository.createFileRuleBuilder(fileRuleBuilderEntity);
		}
		catch(Exception e)
		{
			logger.error("Exception createFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	/**
	 * 
	 * this method will search the  file rule builder
	 * 
	 * @param searchBy,searchParameter,page,count
	 * @return map
	 */

	@Override
	public Map<String, Object> searchFileRuleBuilder(int customerId, int page, int count, String migrationType,
			int programId, String subType, User user) {
		Map<String, Object> searchFileRule = null;
		try {
			searchFileRule = fileRuleBuilderRepository.loadFileRuleBuilderSearchDetails(customerId, page, count,
					migrationType, programId, subType, user);
		} catch (Exception e) {
			logger.error("Exception searchFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return searchFileRule;
	}
	
	/**
	 * this method will delete the file rule builder Details.
	 * 
	 * @param id
	 * @return boolean
	 * @throws RctException 
	 */
	@Override
	public boolean deleteFileRule(int id) throws RctException {
		boolean status = false;
		try {
			status = fileRuleBuilderRepository.deleteFileRule(id);
		} catch (Exception e) {
			logger.error("Exception deleteFileRule: " + ExceptionUtils.getFullStackTrace(e));
			if (e instanceof DataIntegrityViolationException)
				throw new RctException("Operation Failed : File Rule Builder is already associated in other places.");
			else {
				throw new RctException("Operation Failed : Failed to Delete File Rule Builder");
			}
		}
		return status;
	}

	/**
	 * this method will update file rule builder details
	 * 
	 * @param fileRuleEntity
	 * @return boolean
	 */

	@Override
	public boolean updateFileRuleBuilder(FileRuleBuilderEntity fileRuleEntity) {
		boolean status = false;
		try {
			status = fileRuleBuilderRepository.updateFileRuleBuilder(fileRuleEntity);
		} catch (Exception e) {
			logger.error("Exception updateFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api get the Role details
	 * 
	 * @param
	 * @return List
	 *//*
	@Override
	public List<FileRuleBuilderEntity> getFileRuleBuilderList() {
		
			List<FileRuleBuilderEntity> objList = null;
			try {
				objList = fileRuleBuilderRepository.getFileRuleBuilderList();
			} catch (Exception e) {
				logger.error(
						"Exception in getRoleList()   UserRoleDetailsServiceImpl:" + ExceptionUtils.getFullStackTrace(e));
			}
			return objList;
		}*/

	/**
	 * this method will check the rule name is exist or not in file rule builder
	 * 
	 * @param ruleName
	 * @return String
	 */
	@Override
	public boolean duplicateFileName(String ruleName,int customerId, String migrationType, int programId, String userRole,String subType) {
		boolean status = false;
		try {
			status = fileRuleBuilderRepository.findByRuleName(ruleName,customerId, migrationType, programId, userRole,subType);
		} catch (Exception e) {
			logger.error("Exception duplicateFileName: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public Map<String, Object> searchFileRuleBuilder(FileRuleBuilderModel fileRuleBuilderModel, int customerId,
			int page, int count, String migrationType, int programId, String subType, User user) {
		Map<String, Object> searchFileRule = null;
		try {
			searchFileRule = fileRuleBuilderRepository.loadFileRuleBuilderSearchDetails(fileRuleBuilderModel,
					customerId, page, count, migrationType, programId, subType, user);
		} catch (Exception e) {
			logger.error("Exception searchFileRuleBuilder: " + ExceptionUtils.getFullStackTrace(e));
		}
		return searchFileRule;
	}

}
