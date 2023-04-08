package com.smart.rct.common.serviceImpl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.repository.AuditTrailRepository;
import com.smart.rct.common.service.AuditService;

@Service
public class AuditTrailServiceImpl implements AuditService {

	final static Logger logger = LoggerFactory.getLogger(AuditTrailServiceImpl.class);

	@Autowired
	AuditTrailRepository auditTrailRepository;

	/**
	 * This api will save AuditTrailDetails
	 * 
	 * @param auditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean savedetail(AuditTrailEntity auditTrailEntity) {
		boolean status = false;
		try {
			status = auditTrailRepository.savedetail(auditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception in AuditTrailServiceImpl.savedetail(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * this method will return getAuditDetails
	 * 
	 * @param page,count
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getAuditDetails(int page, int count) {
		Map<String, Object> auditTrailEntity = null;
		try {
			auditTrailEntity = auditTrailRepository.getAuditDetails(page, count);
		} catch (Exception e) {
			logger.error("Exception AuditTrailServiceImpl.getAuditDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return auditTrailEntity;
	}

	/**
	 * This api gets AuditTrailfilters
	 * 
	 * @param searchStatus,auditTrailEntity
	 * @return List
	 */
	@Override
	public List<String> getAuditFilters(String searchStatus, AuditTrailEntity auditTrailEntity) {
		List<String> filterList = null;
		try {
			filterList = auditTrailRepository.getAuditFilters(searchStatus, auditTrailEntity);
		} catch (Exception e) {
			logger.error("Exception AuditTrailServiceImpl.getAuditFilters(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return filterList;
	}

	/**
	 * This api gets AuditDetailsOnSearchs
	 * 
	 * @param auditTrailModel,page,count,timeZone
	 * @return Map
	 */
	@Override
	public Map<String, Object> getAuditDetailsOnSearch(AuditTrailModel auditTrailModel, int page, int count) {
		Map<String, Object> auditTrailEntity = null;
		try {
			auditTrailEntity = auditTrailRepository.getAuditDetailsOnSearch(auditTrailModel, page, count);

		} catch (Exception e) {
			logger.error("Exception AuditTrailServiceImpl.getAuditDetailsOnSearch(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return auditTrailEntity;
	}

}
