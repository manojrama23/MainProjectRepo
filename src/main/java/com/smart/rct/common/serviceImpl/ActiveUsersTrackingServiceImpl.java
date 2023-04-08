package com.smart.rct.common.serviceImpl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.common.entity.ActiveUsersTracking;
import com.smart.rct.common.entity.AuditTrailEntity;
import com.smart.rct.common.models.AuditTrailModel;
import com.smart.rct.common.repository.ActiveUsersTrackingRepository;
import com.smart.rct.common.repository.AuditTrailRepository;
import com.smart.rct.common.service.ActiveUsersTrackingService;
import com.smart.rct.common.service.AuditService;

@Service
public class ActiveUsersTrackingServiceImpl implements ActiveUsersTrackingService {

	final static Logger logger = LoggerFactory.getLogger(ActiveUsersTrackingServiceImpl.class);

	@Autowired
	ActiveUsersTrackingRepository activeUserTrackingRepo;

	/**
	 * This api will save AuditTrailDetails
	 * 
	 * @param auditTrailEntity
	 * @return boolean
	 */
	@Override
	public boolean savedetail(ActiveUsersTracking activeTracking) {
		boolean status = false;
		try {
			status = activeUserTrackingRepo.savedetail(activeTracking);
		} catch (Exception e) {
			logger.error("Exception in AuditTrailServiceImpl.savedetail(): " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

}
