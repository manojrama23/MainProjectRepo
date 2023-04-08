package com.smart.rct.postmigration.serviceImpl;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.postmigration.entity.SiteDataEntity;
import com.smart.rct.postmigration.models.SiteDataModel;
import com.smart.rct.postmigration.repository.SiteDataRepository;
import com.smart.rct.postmigration.service.SiteDataService;

@Service
public class SiteDataServiceImpl implements SiteDataService {

	final static Logger logger = LoggerFactory.getLogger(SiteDataServiceImpl.class);

	@Autowired
	SiteDataRepository siteDataRepository;

	/**
	 * This method will saveSiteDataAudit
	 * 
	 * @param siteDataEntity
	 * @return boolean
	 */
	@Override
	public boolean saveSiteDataAudit(SiteDataEntity siteDataEntity) {
		boolean status = false;
		try {
			status = siteDataRepository.saveSiteDataAudit(siteDataEntity);
		} catch (Exception e) {
			logger.error("Exception Exception SiteDataServiceImpl.saveSiteDataAudit(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will deleteSiteDataDetails
	 * 
	 * @param siteDataModel
	 * @return boolean
	 */
	@Override
	public boolean deleteSiteDataDetails(SiteDataModel siteDataModel) {
		boolean status = false;
		try {
			status = siteDataRepository.deleteSiteDataDetails(siteDataModel);
		} catch (Exception e) {
			logger.error("Exception Exception SiteDataServiceImpl.deleteSiteDataDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This method will getSiteDataDetails
	 * 
	 * @param siteDataModel,page,count
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> getSiteDataDetails(SiteDataModel siteDataModel, int page, int count) {
		Map<String, Object> map = null;
		try {
			map = siteDataRepository.getSiteDataDetails(siteDataModel, page, count);
		} catch (Exception e) {
			logger.error("Exception Exception SiteDataServiceImpl.getSiteDataDetails(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return map;
	}

	/**
	 * This method will getSiteDataDetailsById
	 * 
	 * @param id
	 * @return SiteDataEntity
	 */
	@Override
	public SiteDataEntity getSiteDataDetailsById(Integer id) {
		SiteDataEntity siteDataEntity = null;
		try {
			siteDataEntity = siteDataRepository.getSiteDataDetailsById(id);
		} catch (Exception e) {
			logger.error("Exception Exception SiteDataServiceImpl.getSiteDataDetailsById(): "
					+ ExceptionUtils.getFullStackTrace(e));
		}
		return siteDataEntity;
	}

}
