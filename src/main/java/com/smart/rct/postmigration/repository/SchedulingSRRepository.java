package com.smart.rct.postmigration.repository;

import java.util.List;
import java.util.Map;

import com.smart.rct.common.entity.CustomerEntity;
import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;

public interface SchedulingSRRepository {

	boolean saveVerizonSchedulingDetails(SchedulingVerizonEntity schedulingVerizonEntity);

	boolean saveSprintSchedulingDetails(SchedulingSprintEntity schedulingSprintEntity);

	boolean deleteVerizonDetails(int id);

	boolean deleteSprintDetails(int id);

	Map<String, Object> getVerizonSchedulingDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId);

	Map<String, Object> getSprintSchedulingDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	List<SchedulingVerizonEntity> getSchedulingDetailsToExPort(SchedulingVerizonModel schedulingVerizonModel);

	List<SchedulingSprintEntity> getSchedulingDetailsToExPort(SchedulingSprintModel schedulingSprintModel);

	Map<String, Object> getVerizonOverallReportsDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId);

	Map<String, Object> getSprintOverallReportsDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	Map<String, Object> getVerizonEodDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId);

	Map<String, Object> getSprintEodDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	Map<String, Object> getCustomerIdList();

	Map<String, Object> getNeDetailsForMap(SchedulingSRModel schedulingSRModel, List<CustomerEntity> customerEntities);

}
