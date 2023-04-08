package com.smart.rct.scheduling.serviceImpl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;
import com.smart.rct.scheduling.repository.SchedulingRepository;
import com.smart.rct.scheduling.service.SchedulingService;

@Service
public class SchedulingServiceImpl implements SchedulingService{
	
	final static Logger logger = LoggerFactory.getLogger(SchedulingServiceImpl.class);
	
	@Autowired
	SchedulingRepository objSchedulingRepository;
	

	@Override
	public boolean createSchedule(SchedulingEntity schedulingEntity) {
		
		boolean status = false;
		try {
			status = objSchedulingRepository.createSchedule(schedulingEntity);
		} catch (Exception e) {
			status = false;
			logger.error("createSchedule() SchedulingServiceImpl" + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}
	
	@Override
	public boolean duplicateScheduling(SchedulingModel objSchedulingModel) {
		boolean status = false;
		try {
			status = objSchedulingRepository.duplicateScheduling(objSchedulingModel);
		} catch (Exception e) {
			logger.error("Exception duplicateScheduling: " + ExceptionUtils.getFullStackTrace(e));
		}
		return status;
	}

}
