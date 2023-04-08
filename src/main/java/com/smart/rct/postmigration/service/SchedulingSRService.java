package com.smart.rct.postmigration.service;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.smart.rct.postmigration.entity.SchedulingSprintEntity;
import com.smart.rct.postmigration.entity.SchedulingVerizonEntity;
import com.smart.rct.postmigration.models.SchedulingSRModel;
import com.smart.rct.postmigration.models.SchedulingSprintModel;
import com.smart.rct.postmigration.models.SchedulingVerizonModel;

public interface SchedulingSRService {

	boolean saveVerizonSchedulingDetails(SchedulingVerizonEntity schedulingVerizonEntity);

	boolean saveSprintSchedulingDetails(SchedulingSprintEntity schedulingSprintEntity);

	boolean deleteVerizonDetails(int parseInt);

	boolean deleteSprintDetails(int parseInt);

	Map<String, Object> getVerizonSchedulingDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId);

	Map<String, Object> getSprintSchedulingDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	boolean getSchedulingDetailsToCreateExcel(SchedulingVerizonModel schedulingVerizonModel);

	boolean getSchedulingSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel);

	public JSONObject importVerizonSchedulingDetails(MultipartFile file, String sessionId);

	public JSONObject importSprintSchedulingDetails(MultipartFile file, String sessionId);

	Map<String, Object> getVerizonOverallReportsDetails(SchedulingVerizonModel schedulingVerizonModel, int page,
			int count, int customerId);

	Map<String, Object> getSprintOverallReportsDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	boolean getOverallDetailsToCreateExcel(SchedulingVerizonModel schedulingVerizonModel);

	boolean getOverallSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel);

	public JSONObject importVerizonOverallDetails(MultipartFile file, String sessionId);

	Map<String, Object> getVerizonEodDetails(SchedulingVerizonModel schedulingVerizonModel, int page, int count,
			int customerId);

	Map<String, Object> getSprintEodDetails(SchedulingSprintModel schedulingSprintModel, int page, int count,
			int customerId);

	boolean getEodVerizonToCreateExcel(SchedulingVerizonModel schedulingVerizonModel);

	boolean getEodSprintToCreateExcel(SchedulingSprintModel schedulingSprintModel);

	public JSONObject importSprintOverallDetails(MultipartFile file, String sessionId);

	Map<String, Object> getCustomerIdList();
	
}
