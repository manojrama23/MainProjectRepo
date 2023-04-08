package com.smart.rct.common.repository;

import java.util.Map;

import com.smart.rct.common.models.TrackLatiTudeModel;

public interface EnodeBViewMapRepository {

	Map<String, TrackLatiTudeModel> getMapEnodeBDetails(Integer customerId);

}
