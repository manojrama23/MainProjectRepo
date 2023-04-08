package com.smart.rct.premigration.repositoryImpl;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;
import com.smart.rct.premigration.dto.CheckListScriptDetDto;
import com.smart.rct.premigration.entity.CheckListScriptDetEntity;
import com.smart.rct.premigration.models.CheckListScriptDetModel;
import com.smart.rct.premigration.repository.CheckListScriptRepository;
import com.smart.rct.util.CommonUtil;

@Repository
@Transactional
public class CheckListScriptRepositoryImpl implements CheckListScriptRepository{

	final static Logger logger = LoggerFactory.getLogger(CheckListScriptRepositoryImpl.class);

	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	CheckListScriptDetDto checkListScriptDetDto;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CheckListScriptDetEntity> getCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel) {
		List<CheckListScriptDetEntity> list = null;
		try {
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CheckListScriptDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			criteria.add(Restrictions.eq("programDetailsEntity.id", checkListScriptDetModel.getProgramDetailsEntity().getId()));
			if(checkListScriptDetModel.getCheckListFileName() != null && StringUtils.isNotEmpty(checkListScriptDetModel.getCheckListFileName())){
				criteria.add(Restrictions.ilike("checkListFileName", checkListScriptDetModel.getCheckListFileName(), MatchMode.ANYWHERE));
			}
			if(checkListScriptDetModel.getStepIndex() != null){
				criteria.add(Restrictions.eq("stepIndex", checkListScriptDetModel.getStepIndex()));
			}
			if(StringUtils.isNotEmpty(checkListScriptDetModel.getSheetName())){
				criteria.add(Restrictions.eq("sheetName", checkListScriptDetModel.getSheetName()));
			}
			criteria.addOrder(Order.asc("scriptExeSeq"));
			logger.info("CheckListScriptRepositoryImpl.getCheckListBasedScriptExecutionDetails criteria: "+criteria.toString());
			list = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptRepositoryImpl.getCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return list;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean isDuplicateSeqExist(CheckListScriptDetModel checkListScriptDetModel) {
		boolean  isduplicateExist= false;
		try {
			List<CheckListScriptDetEntity> list = null;
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CheckListScriptDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			if(CommonUtil.isValidObject(checkListScriptDetModel.getId())){
			criteria.add(Restrictions.ne("id", checkListScriptDetModel.getId()));
			}
			criteria.add(Restrictions.eq("programDetailsEntity.id", checkListScriptDetModel.getProgramDetailsEntity().getId()));
			criteria.add(Restrictions.ilike("checkListFileName", checkListScriptDetModel.getCheckListFileName(), MatchMode.ANYWHERE));
			criteria.add(Restrictions.eq("sheetName", checkListScriptDetModel.getSheetName()));
			criteria.add(Restrictions.eq("configType", checkListScriptDetModel.getConfigType()));
			criteria.add(Restrictions.eq("scriptExeSeq", checkListScriptDetModel.getScriptExeSeq()));
			list = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			if(list!=null && list.size() >0 ){
				isduplicateExist = true;
			}
			
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptRepositoryImpl.isDuplicateSeqExist(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return isduplicateExist;
	}

	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean isDuplicateScriptNameExist(CheckListScriptDetModel checkListScriptDetModel) {
		boolean  isduplicateExist= false;
		try {
			List<CheckListScriptDetEntity> list = null;
			Criteria criteria = entityManager.unwrap(Session.class).createCriteria(CheckListScriptDetEntity.class);
			criteria.createAlias("programDetailsEntity", "programDetailsEntity");
			if(CommonUtil.isValidObject(checkListScriptDetModel.getId())){
			criteria.add(Restrictions.ne("id", checkListScriptDetModel.getId()));
			}
			criteria.add(Restrictions.eq("programDetailsEntity.id", checkListScriptDetModel.getProgramDetailsEntity().getId()));
			criteria.add(Restrictions.ilike("checkListFileName", checkListScriptDetModel.getCheckListFileName(), MatchMode.ANYWHERE));
			criteria.add(Restrictions.eq("scriptName", checkListScriptDetModel.getScriptName()));
			criteria.add(Restrictions.eq("configType", checkListScriptDetModel.getConfigType()));
			list = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
			if(list!=null && list.size() >0 ){
				isduplicateExist = true;
			}
			
		} catch (Exception e) {
			logger.error("Exception in CheckListScriptRepositoryImpl.isDuplicateScriptNameExist(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return isduplicateExist;
	}
	
	@Override
	public CheckListScriptDetEntity saveCheckListBasedScriptExecutionDetails(CheckListScriptDetEntity checkListScriptDetEntity) throws RctException {
		boolean exceptionStatus = false;
		try {
			checkListScriptDetEntity = entityManager.merge(checkListScriptDetEntity);
		} catch (Exception e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				exceptionStatus = true;
				throw new RctException(GlobalInitializerListener.faultCodeMap.get(FaultCodes.SCRIPT_NAME_ALREADY_USED));
			}
			logger.error("Exception in CheckListScriptRepositoryImpl.saveCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		} finally {
			if (!exceptionStatus) {
				entityManager.flush();
			}
			entityManager.clear();
		}
		return checkListScriptDetEntity;
	}
	
	@Override
	public boolean deleteCheckListBasedScriptExecutionDetails(CheckListScriptDetModel checkListScriptDetModel, Set<Integer> idList) {
		boolean status = false; 
		try{
			logger.info("CheckListScriptRepositoryImpl.deleteCheckListBasedScriptExecutionDetails() idList from UI: "+idList.toString());
			List<CheckListScriptDetEntity> list = getCheckListBasedScriptExecutionDetails(checkListScriptDetModel);
			for(CheckListScriptDetEntity entity: list){
				logger.info("CheckListScriptRepositoryImpl.deleteCheckListBasedScriptExecutionDetails() id from DB: "+entity.getId());
				if(!idList.contains(entity.getId())){
					entityManager.remove(entity);
				}
			}
			status = true;
		}catch(Exception e){
			status = false;
			logger.error("Exception in CheckListScriptRepositoryImpl.deleteCheckListBasedScriptExecutionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		
		return status;
	}
	
	@Override
	public boolean deleteCheckListBasedScriptExecutionDetails(Integer checkListScriptDetId) {
		boolean status = false;
		try {
			CheckListScriptDetEntity entity = getCheckListBasedScriptExecutionDetailsById(checkListScriptDetId);
			if (entity != null) {
				entityManager.remove(entity);
				status = true;
			}
		} catch (Exception e) {
			status = false;
			logger.error("Exception CheckListScriptRepositoryImpl.deleteCheckListBasedScriptExecutionDetails() :" + ExceptionUtils.getFullStackTrace(e));
		} finally {
			entityManager.flush();
			entityManager.clear();
		}
		return status;
	}

	private CheckListScriptDetEntity getCheckListBasedScriptExecutionDetailsById(Integer checkListScriptDetId) {
		return entityManager.find(CheckListScriptDetEntity.class, checkListScriptDetId);
	}
	
}
