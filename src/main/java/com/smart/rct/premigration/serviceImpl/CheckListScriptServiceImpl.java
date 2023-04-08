package com.smart.rct.premigration.serviceImpl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.premigration.repository.CheckListScriptRepository;
import com.smart.rct.premigration.service.CheckListScriptService;

@Service
public class CheckListScriptServiceImpl implements CheckListScriptService{

	
	final static Logger logger = LoggerFactory.getLogger(CheckListScriptServiceImpl.class);

	@Autowired
	CheckListScriptRepository checkListScriptRepository;
	
	@Override
	public List<CheckListScriptDetEntity> getCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel) {
		List<CheckListScriptDetEntity> checkListScriptDetList = null;
		try {
			checkListScriptDetList = checkListScriptRepository.getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptServiceImpl.getCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return checkListScriptDetList;
	}

	@Override
	public boolean isDuplicateScriptNameExist(CheckListScriptDetModel checkListScriptDetModel) {
		boolean status = false;
		try {
			status = checkListScriptRepository.isDuplicateScriptNameExist(checkListScriptDetModel);
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptServiceImpl.isDuplicateScriptNameExist(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@Override
	public boolean isDuplicateSeqExist(CheckListScriptDetModel checkListScriptDetModel) {
		boolean status = false;
		try {
			status = checkListScriptRepository.isDuplicateSeqExist(checkListScriptDetModel);
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptServiceImpl.isDuplicateSeqExist(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public CheckListScriptDetEntity saveCheckListBasedScriptExecutionDetails(CheckListScriptDetEntity checkListScriptDetEntity) throws RctException {
		try{
			checkListScriptDetEntity = checkListScriptRepository.saveCheckListBasedScriptExecutionDetails(checkListScriptDetEntity);
		}catch (org.springframework.dao.DataIntegrityViolationException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new RctException(GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_NAME_ALREADY_USED));
			}
		} catch (JpaSystemException e) {
			if (e.getRootCause() instanceof org.hibernate.TransactionException) {
				throw new RctException(GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_NAME_ALREADY_USED));
			}
		} catch (Exception e) {
			if (e.getCause() instanceof RctException) {
				throw new RctException(GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_NAME_ALREADY_USED));
			}
			logger.error("Exception in CheckListScriptServiceImpl.saveCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return checkListScriptDetEntity;
	}

	@Override
	public boolean deleteCheckListBasedScriptExecutionDetails(Integer checkListScriptDetId) {
		boolean status = false;
		try {
			status = checkListScriptRepository.deleteCheckListBasedScriptExecutionDetails(checkListScriptDetId);
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptServiceImpl.deleteCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	@Override
	public boolean deleteCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel, Set<Integer> idList) {
		boolean status = false;
		try {
			status = checkListScriptRepository.deleteCheckListBasedScriptExecutionDetails(checkListScriptDetModel, idList);
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptServiceImpl.deleteCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	
}
