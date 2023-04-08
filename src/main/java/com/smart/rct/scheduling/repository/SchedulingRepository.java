package com.smart.rct.scheduling.repository;

import com.smart.rct.scheduling.entity.SchedulingEntity;
import com.smart.rct.scheduling.model.SchedulingModel;

public interface SchedulingRepository {
	
	boolean createSchedule(SchedulingEntity schedulingEntity);
	
	boolean duplicateScheduling(SchedulingModel objSchedulingModel);

}
