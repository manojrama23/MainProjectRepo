package com.smart.rct.common.service;

import java.util.Map;

import com.smart.rct.common.models.TrackLatiTudeModel;

public interface EnodeBViewMapService {

	Map<String, TrackLatiTudeModel> getMapEnodeBDetails(Integer customerId);

	public Map<String, Object> getNeCommissionData();

	public Map<String, Object> getReasonsChartData();

	public Map<String, Object> getRepChartData();
}
