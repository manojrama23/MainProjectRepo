package com.smart.rct.common.serviceImpl;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.NeVersionEntity;
import com.smart.rct.common.models.NeVersionModel;
import com.smart.rct.common.repository.NeVersionRepository;
import com.smart.rct.common.service.NeVersionService;
import com.smart.rct.constants.FaultCodes;
import com.smart.rct.exception.RctException;
import com.smart.rct.interceptor.GlobalInitializerListener;

@Service
public class NeVersionServiceImpl implements NeVersionService {

	final static Logger logger = LoggerFactory.getLogger(NeVersionServiceImpl.class);

	@Autowired
	NeVersionRepository neVersionRepository;

	/**
	 * This api will createNeVersion
	 * 
	 * @param neEntity
	 * @return boolean
	 */
	@Override
	public boolean createNeVersion(NeVersionEntity neEntity) {
		boolean status = false;
		try {
			status = neVersionRepository.createNeVersion(neEntity);
		} catch (Exception e) {
			status = false;
			logger.error(
					"Exception NeVersionServiceImpl.createNetworkConfig(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getNeVersionList
	 * 
	 * @param neVersionModel
	 * @return NeVersionEntity
	 */
	@Override
	public List<NeVersionEntity> getNeVersionList(NeVersionModel neVersionModel) {
		List<NeVersionEntity> neVersionEntities = null;
		try {
			neVersionEntities = neVersionRepository.getNeVersionList(neVersionModel);
		} catch (Exception e) {
			logger.error("Exception NeVersionServiceImpl.getNeVersionList(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return neVersionEntities;
	}

	/**
	 * This api will deleteNeVersionDetails
	 * 
	 * @param neVersionDetailId
	 * @return boolean
	 */
	@Override
	public boolean deleteNeVersionDetails(int neVersionDetailId) throws RctException {
		boolean status = false;
		try {
			status = neVersionRepository.deleteNeVersionDetails(neVersionDetailId);
		} catch (Exception e) {
			if (e instanceof DataIntegrityViolationException) {
				throw new RctException(
						GlobalInitializerListener.faultCodeMap.get(FaultCodes.NETWORK_TYPE_DETAILS_ASSOSIATED));
			}
			status = false;
			logger.error(
					"Exception NeVersionServiceImpl.deleteNeVersionDetails(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will duplicateNeVersion
	 * 
	 * @param neVersionModel
	 * @return boolean
	 */
	@Override
	public boolean duplicateNeVersion(NeVersionModel neVersionModel) {
		boolean status = false;
		try {
			status = neVersionRepository.duplicateNeVersion(neVersionModel);
		} catch (Exception e) {
			status = false;
			logger.error("Exception NeVersionServiceImpl.duplicateNeVersion(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

	/**
	 * This api will getNeVersionById
	 * 
	 * @param neVersionDetailId
	 * @return NeVersionEntity
	 */
	@Override
	public NeVersionEntity getNeVersionById(int neVersionDetailId) {
		NeVersionEntity neVersionEntity = null;
		try {
			neVersionEntity = neVersionRepository.getNeVersionById(neVersionDetailId);
		} catch (Exception e) {
			logger.error("Exception NeVersionServiceImpl.getNeVersionById(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return neVersionEntity;
	}

}
