package com.smart.rct.scheduling.service;

import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;

public interface SchedulingService {
	
	boolean createSchedule(SchedulingEntity schedulingEntity);
	
	boolean duplicateScheduling(SchedulingModel objSchedulingModel);

}
